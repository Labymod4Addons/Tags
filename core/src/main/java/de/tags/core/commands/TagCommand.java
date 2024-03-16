package de.tags.core.commands;

import de.tags.core.Tag;
import de.tags.core.TagsAddon;
import net.labymod.api.client.chat.command.Command;
import net.labymod.api.client.chat.command.SubCommand;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.TextDecoration;

public class TagCommand extends Command {


  public TagCommand() {
    super("tags");
    withSubCommand(new AddCommand());
    withSubCommand(new RemoveCommand());
    withSubCommand(new EditCommand());

  }

  @Override
  public boolean execute(String s, String[] strings) {

    if(strings.length == 0) {
      this.displayMessage("§c/tags <add|remove|edit>");
      return true;
    }

    return true;
  }


  private static class AddCommand extends SubCommand {
    protected AddCommand() {
      super("add");
    }

    @Override
    public boolean execute(String s, String[] strings) {

      if(strings.length < 2) {
        this.displayMessage("§c/tags add <Username> <Text>");
        return true;
      }

      String username = strings[0];
      String text = strings[1];
      Tag newTag = Tag.create(text);

      if(TagsAddon.INSTANCE.configuration().getTags().containsKey(username)) {
        Tag oldTag = TagsAddon.INSTANCE.configuration().getTags().get(username);
        TagsAddon.INSTANCE.displayMessage(Component.translatable("playertags.command.add.already_exists", NamedTextColor.RED).arguments(Component.text(username, NamedTextColor.GOLD), oldTag.getComponent()));
        return true;
      }

      TagsAddon.INSTANCE.configuration().getTags().put(username, newTag);

      TagsAddon.INSTANCE.displayMessage(Component.translatable("playertags.command.add.success", NamedTextColor.GRAY).arguments(newTag.getComponent(),Component.text(username, NamedTextColor.GOLD).decorate(TextDecoration.BOLD)));

      return true;
    }
  }

  private static class RemoveCommand extends SubCommand {
    protected RemoveCommand() {
      super("remove");
    }

    @Override
    public boolean execute(String s, String[] strings) {
      if(strings.length == 0) {
        this.displayMessage("§c/tags remove <Username>");
        return true;
      }

      String username = strings[0];

      if(!TagsAddon.INSTANCE.configuration().getTags().containsKey(username)) {
        TagsAddon.INSTANCE.displayMessage(Component.translatable("playertags.command.not_found", NamedTextColor.RED).argument(Component.text(username, NamedTextColor.GOLD)));
        return true;
      }

      Tag oldTag = TagsAddon.INSTANCE.configuration().getTags().get(username);

      TagsAddon.INSTANCE.displayMessage(Component.translatable("playertags.command.remove.success", NamedTextColor.GRAY).arguments(oldTag.getComponent(),Component.text(username, NamedTextColor.GOLD).decorate(TextDecoration.BOLD)));
      TagsAddon.INSTANCE.configuration().getTags().remove(username);

      return true;
    }
  }

  private static class EditCommand extends SubCommand {
    protected EditCommand() {
      super("edit");
    }

    @Override
    public boolean execute(String s, String[] strings) {
      if (strings.length < 2) {
        this.displayMessage("§c/tags edit <Username> <Text>");
        return true;
      }

      String username = strings[0];

      if (!TagsAddon.INSTANCE.configuration().getTags().containsKey(username)) {
        TagsAddon.INSTANCE.displayMessage(
            Component.translatable("playertags.command.not_found", NamedTextColor.RED)
                .argument(Component.text(username, NamedTextColor.GOLD)));
        return true;
      }

      String text = strings[1];
      Tag oldTag = TagsAddon.INSTANCE.configuration().getTags().get(username);

      Component oldTagComponent = oldTag.getComponent();
      oldTag.setText(text);
      Component newTagComponent = oldTag.getComponent();

      TagsAddon.INSTANCE.displayMessage(
          Component.translatable("playertags.command.edit.success", NamedTextColor.GRAY)
              .arguments(Component.text(username, NamedTextColor.GOLD).decorate(TextDecoration.BOLD), newTagComponent, oldTagComponent));

      return true;
    }
  }

}
