package de.tags.core.gui.activity;

import de.tags.core.Tag;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import java.util.UUID;

@AutoWidget
public class TagWidget extends SimpleWidget {
  private String name;
  private Tag tag;

  public TagWidget(String name, Tag tag) {
    this.name = name;
    this.tag = tag;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    ComponentWidget iconWidget = ComponentWidget.component(Component.icon(Icon.head(getName())));
    iconWidget.addId("icon");
    this.addChild(iconWidget);

    ComponentWidget nameWidget = ComponentWidget.component(Component.text(getName()));
    nameWidget.addId("name");
    this.addChild(nameWidget);

    ComponentWidget customNameWidget = ComponentWidget.component(this.tag.getComponent());
    customNameWidget.addId("custom-name");
    this.addChild(customNameWidget);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Tag getTag() {
    return tag;
  }

  public void setTag(Tag tag) {
    this.tag = tag;
  }
}
