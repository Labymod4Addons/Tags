package de.tags.core;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.serializer.legacy.LegacyComponentSerializer;
import java.awt.*;
import java.util.UUID;

public class Tag {
  private String text;

  public Tag(String text) {
    this.text = text;
  }

  public static Tag create(String text) {
    return new Tag(text);
  }

  public static Tag create() {
    return new Tag("");
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Component getComponent() {
    return LegacyComponentSerializer.legacyAmpersand().deserialize(this.text);
  }

}
