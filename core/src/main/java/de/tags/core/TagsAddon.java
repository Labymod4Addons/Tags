package de.tags.core;

import de.tags.core.commands.TagCommand;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.entity.player.tag.PositionType;
import net.labymod.api.event.labymod.config.ConfigurationSaveEvent;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class TagsAddon extends LabyAddon<TagsConfiguration> {
  public static TagsAddon INSTANCE;
  @Override
  protected void enable() {
    INSTANCE = this;
    this.registerSettingCategory();

    this.registerCommand(new TagCommand());

    labyAPI().tagRegistry().register("playertags_tagrender", configuration().position().get().type(), new TagRenderer());

    this.logger().info("Enabled the Addon");

    configuration().position().addChangeListener(tagPosition -> {
      this.labyAPI().tagRegistry().unregister("playertags_tagrender");
      this.labyAPI().tagRegistry().register("playertags_tagrender", tagPosition.type(), new TagRenderer());
    });

  }

  public void reloadConfig() {
    this.labyAPI().eventBus().fire(new ConfigurationSaveEvent());
  }

  @Override
  protected Class<TagsConfiguration> configurationClass() {
    return TagsConfiguration.class;
  }
}
