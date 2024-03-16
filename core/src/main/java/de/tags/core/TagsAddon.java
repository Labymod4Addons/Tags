package de.tags.core;

import net.labymod.api.addon.LabyAddon;
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

    this.logger().info("Enabled the Addon");
  }

  public void reloadConfig() {
    this.labyAPI().eventBus().fire(new ConfigurationSaveEvent());
  }

  @Override
  protected Class<TagsConfiguration> configurationClass() {
    return TagsConfiguration.class;
  }
}
