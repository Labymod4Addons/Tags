package de.tags.core;

import de.tags.core.gui.activity.TagActivity;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.entity.player.tag.PositionType;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.widget.widgets.activity.settings.ActivitySettingWidget.ActivitySetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
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

  @SliderSetting(min = 0, max = 1, steps = 0.1F)
  private final ConfigProperty<Float> tagSize = new ConfigProperty<>(0.5F);

  public ConfigProperty<Float> tagSize() {
    return this.tagSize;
  }

  @Exclude
  private final Map<String, Tag> tags = new HashMap<>();

  public Map<String, Tag> getTags() {
    return tags;
  }

  @DropdownSetting
  private final ConfigProperty<TagPosition> position = new ConfigProperty<>(TagPosition.BELOW_NAME);

  public ConfigProperty<TagPosition> position() {
    return this.position;
  }

  @MethodOrder(after = "position")
  @ActivitySetting
  public Activity openNameTags() {
    return new TagActivity(false);
  }


  public void removeInvalidNameTags() {
    this.tags.entrySet()
        .removeIf(entry -> entry.getKey().isEmpty() || entry.getValue().getText().isEmpty());
  }

  public enum TagPosition {
    ABOVE_NAME(PositionType.ABOVE_NAME),
    BELOW_NAME(PositionType.BELOW_NAME);

    private final PositionType type;
    TagPosition(PositionType type) {
      this.type = type;
    }

    public PositionType type() {
      return type;
    }
  }

}
