package de.tags.core;

import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class TagsAddon extends LabyAddon<TagsConfiguration> {

  @Override
  protected void enable() {
    this.registerSettingCategory();
    this.logger().info("Enabled the Addon");
  }

  @Override
  protected Class<TagsConfiguration> configurationClass() {
    return TagsConfiguration.class;
  }
}
