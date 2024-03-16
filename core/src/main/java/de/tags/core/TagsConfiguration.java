package de.tags.core;

import de.tags.core.gui.activity.TagActivity;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.widget.widgets.activity.settings.ActivitySettingWidget.ActivitySetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.Exclude;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.util.MethodOrder;
import java.util.HashMap;
import java.util.Map;

@ConfigName("settings")
public class TagsConfiguration extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }

  @Exclude
  public final Map<String, Tag> tags = new HashMap<>();

  public Map<String, Tag> getTags() {
    return tags;
  }

  @MethodOrder(after = "enabled")
  @ActivitySetting
  public Activity openNameTags() {
    return new TagActivity(false);
  }


  public void removeInvalidNameTags() {
    this.tags.entrySet()
        .removeIf(entry -> entry.getKey().isEmpty() || entry.getValue().getText().isEmpty());
  }

}
