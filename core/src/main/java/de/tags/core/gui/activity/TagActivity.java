package de.tags.core.gui.activity;

import de.tags.core.Tag;
import de.tags.core.TagsAddon;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.mouse.MutableMouse;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.key.InputType;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.key.MouseButton;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.render.font.TextColorStripper;
import java.awt.Desktop.Action;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Link("manage.lss")
@Link("overview.lss")
@AutoActivity
public class TagActivity extends Activity {


  //private static final Pattern SHORTCUT_REGEX = Pattern.compile("[\\w.:]{0,32}");
  private static final TextColorStripper TEXT_COLOR_STRIPPER = Laby.references()
      .textColorStripper();
  private final TagsAddon addon;
  private final VerticalListWidget<TagWidget> nameTagList;
  private final Map<String, TagWidget> tagWidgets;
  private TagWidget selectedNameTag;
  private ButtonWidget removeButton;
  private ButtonWidget editButton;

  private FlexibleContentWidget inputWidget;
  private String lastUserName;
  private String lastCustomName;

  private Action action;
  private boolean background;

  public TagActivity(boolean background) {
    this.addon = TagsAddon.INSTANCE;
    addon.reloadConfig();
    this.background = background;

    this.tagWidgets = new HashMap<>();
    addon.configuration().getTags().forEach((username, customTag) -> {
      this.tagWidgets.put(username, new TagWidget(username, customTag));
    });

    this.nameTagList = new VerticalListWidget<>();
    this.nameTagList.addId("name-tag-list");
    this.nameTagList.setSelectCallback(shortcutWidget -> {
      TagWidget selectedNameTag = this.nameTagList.session().getSelectedEntry();
      if (Objects.isNull(selectedNameTag)
          || selectedNameTag.getTag() != shortcutWidget.getTag()) {
        this.editButton.setEnabled(true);
        this.removeButton.setEnabled(true);
      }
    });

    this.nameTagList.setDoubleClickCallback(shortcutWidget -> this.setAction(Action.EDIT));
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    FlexibleContentWidget container = new FlexibleContentWidget();
    container.addId("name-tag-container");
    for (TagWidget shortcutWidget : this.tagWidgets.values()) {
      this.nameTagList.addChild(shortcutWidget);
    }

    if (background) {
      DivWidget containerBackground = new DivWidget();
      containerBackground.addId("container-background");
      containerBackground.addChild(container);
      this.document().addChild(containerBackground);
    }

    container.addFlexibleContent(new ScrollWidget(this.nameTagList));

    this.selectedNameTag = this.nameTagList.session().getSelectedEntry();
    HorizontalListWidget menu = new HorizontalListWidget();
    menu.addId("overview-button-menu");

    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.add", () -> this.setAction(Action.ADD)));

    this.editButton = ButtonWidget.i18n("labymod.ui.button.edit",
        () -> this.setAction(Action.EDIT));
    this.editButton.setEnabled(this.selectedNameTag != null);
    menu.addEntry(this.editButton);

    this.removeButton = ButtonWidget.i18n("labymod.ui.button.remove",
        () -> this.setAction(Action.REMOVE));
    this.removeButton.setEnabled(this.selectedNameTag != null);
    menu.addEntry(this.removeButton);

    container.addContent(menu);
    if (!background) {
      this.document().addChild(container);
    }
    if (this.action == null) {
      return;
    }

    DivWidget manageContainer = new DivWidget();
    manageContainer.addId("manage-container");

    Widget overlayWidget;
    switch (this.action) {
      default:
      case ADD:
        TagWidget newCustomNameTag = new TagWidget("", Tag.create());
        overlayWidget = this.initializeManageContainer(newCustomNameTag);
        break;
      case EDIT:
        overlayWidget = this.initializeManageContainer(this.selectedNameTag);
        break;
      case REMOVE:
        overlayWidget = this.initializeRemoveContainer(this.selectedNameTag);
        break;
    }
      manageContainer.addChild(overlayWidget);
    this.document().addChild(manageContainer);
  }


  private FlexibleContentWidget initializeRemoveContainer(TagWidget tagWidget) {
    this.inputWidget = new FlexibleContentWidget();
    this.inputWidget.addId("remove-container");

    ComponentWidget confirmationWidget = ComponentWidget.i18n(
        "playertags.gui.manage.remove.title");
    confirmationWidget.addId("remove-confirmation");
    this.inputWidget.addContent(confirmationWidget);

    TagWidget previewWidget = new TagWidget(tagWidget.getName(), tagWidget.getTag());
    previewWidget.addId("remove-preview");
    this.inputWidget.addContent(previewWidget);

    HorizontalListWidget menu = new HorizontalListWidget();
    menu.addId("remove-button-menu");

    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.remove", () -> {
      this.addon.configuration().getTags().remove(tagWidget.getName());
      this.tagWidgets.remove(tagWidget.getName());
      this.nameTagList.session().setSelectedEntry(null);
      this.setAction(null);
      this.addon.reloadConfig();
    }));

    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.cancel", () -> this.setAction(null)));
    this.inputWidget.addContent(menu);

    return this.inputWidget;
  }

  private DivWidget initializeManageContainer(TagWidget tagWidget) {
    TextFieldWidget customTextField = new TextFieldWidget();
    ButtonWidget doneButton = ButtonWidget.i18n("labymod.ui.button.done");

    DivWidget inputContainer = new DivWidget();
    inputContainer.addId("input-container");

    this.inputWidget = new FlexibleContentWidget();
    this.inputWidget.addId("input-list");

    ComponentWidget labelName = ComponentWidget.i18n("playertags.gui.manage.name");
    labelName.addId("label-name");
    this.inputWidget.addContent(labelName);

    HorizontalListWidget nameList = new HorizontalListWidget();
    nameList.addId("input-name-list");

    TextFieldWidget nameTextField = new TextFieldWidget();
    nameTextField.setText(tagWidget.getName());
    //nameTextField.validator(newValue -> SHORTCUT_REGEX.matcher(newValue).matches());
    nameTextField.updateListener(newValue -> {
      doneButton.setEnabled(
          !newValue.trim().isEmpty() && !this.getStrippedText(customTextField.getText()).isEmpty()
      );
      if (newValue.equals(this.lastUserName)) {
        return;
      }

      this.lastUserName = newValue;

    });
    nameList.addEntry(nameTextField);
    this.inputWidget.addContent(nameList);

    ComponentWidget labelCustomName = ComponentWidget.i18n(
        "playertags.gui.manage.custom.name");
    labelCustomName.addId("label-name");
    this.inputWidget.addContent(labelCustomName);

    HorizontalListWidget customNameList = new HorizontalListWidget();
    customNameList.addId("input-name-list");

    customTextField.setText(tagWidget.getTag().getText());
    //customTextField.validator(newValue -> SHORTCUT_REGEX.matcher(newValue).matches());
    customTextField.updateListener(newValue -> {
      doneButton.setEnabled(
          !this.getStrippedText(newValue).isEmpty() && !nameTextField.getText().trim().isEmpty()
      );
      if (newValue.equals(this.lastCustomName)) {
        return;
      }

      this.lastCustomName = newValue;
    });

    customNameList.addEntry(customTextField);
    this.inputWidget.addContent(customNameList);

    HorizontalListWidget buttonList = new HorizontalListWidget();
    buttonList.addId("edit-button-menu");

    doneButton.setEnabled(
        !nameTextField.getText().trim().isEmpty() && !this.getStrippedText(
            customTextField.getText()).isEmpty()
    );
    doneButton.setPressable(() -> {
      if (tagWidget.getTag().getText().length() == 0) {
        this.tagWidgets.put(nameTextField.getText(), tagWidget);
        this.nameTagList.session().setSelectedEntry(tagWidget);
      }

      this.addon.configuration().getTags().remove(tagWidget.getName());
      Tag customNameTag = tagWidget.getTag();
      customNameTag.setText(customTextField.getText());
      this.addon.configuration().getTags().put(nameTextField.getText(), customNameTag);
      this.addon.configuration().removeInvalidNameTags();

      tagWidget.setName(nameTextField.getText());
      tagWidget.setTag(customNameTag);
      this.setAction(null);

      this.addon.reloadConfig();
    });

    buttonList.addEntry(doneButton);

    buttonList.addEntry(ButtonWidget.i18n("labymod.ui.button.cancel", () -> this.setAction(null)));
    inputContainer.addChild(this.inputWidget);
    this.inputWidget.addContent(buttonList);
    return inputContainer;
  }

  private String getStrippedText(String text) {
    text = text.trim();
    if (text.isEmpty()) {
      return text;
    }
    return TEXT_COLOR_STRIPPER.stripColorCodes(text, '&');
  }

  @Override
  public boolean mouseClicked(MutableMouse mouse, MouseButton mouseButton) {
    try {
      if (this.action != null) {
        return this.inputWidget.mouseClicked(mouse, mouseButton);
      }

      return super.mouseClicked(mouse, mouseButton);
    } finally {
      this.selectedNameTag = this.nameTagList.session().getSelectedEntry();
      this.removeButton.setEnabled(this.selectedNameTag != null);
      this.editButton.setEnabled(this.selectedNameTag != null);
    }
  }

  @Override
  public boolean keyPressed(Key key, InputType type) {
    if (key.getId() == 256 && this.action != null) {
      this.setAction(null);
      return true;
    }

    return super.keyPressed(key, type);
  }

  private void setAction(Action action) {
    this.action = action;
    this.reload();
  }

  public void setBackground(boolean background) {
    this.background = background;
  }

  private enum Action {
    ADD, EDIT, REMOVE
  }

}
