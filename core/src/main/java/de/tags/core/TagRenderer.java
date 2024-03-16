package de.tags.core;

import net.labymod.api.client.entity.player.Player;
import net.labymod.api.client.entity.player.tag.tags.NameTag;
import net.labymod.api.client.render.font.RenderableComponent;
import org.jetbrains.annotations.Nullable;

public class TagRenderer extends NameTag {

  public TagRenderer() {
    super();
  }

  @Override
  public float getScale() {
    return TagsAddon.INSTANCE.configuration().tagSize().get();
  }

  @Override
  protected @Nullable RenderableComponent getRenderableComponent() {
    if(entity == null || !(entity instanceof Player player)) return null;

    if(!TagsAddon.INSTANCE.configuration().getTags().containsKey(player.getName())) return null;
    Tag tag = TagsAddon.INSTANCE.configuration().getTags().get(player.getName());
    return RenderableComponent.of(tag.getComponent());
  }

  @Override
  public boolean isVisible() {
    return (TagsAddon.INSTANCE.configuration().enabled().get() && !this.entity.isCrouching() && super.isVisible() && (
        this.entity instanceof Player player &&
        TagsAddon.INSTANCE.configuration().getTags().containsKey(player.getName()))
    );
  }
}
