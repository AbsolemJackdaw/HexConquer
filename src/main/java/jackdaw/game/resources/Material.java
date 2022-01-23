package jackdaw.game.resources;

import jackdaw.game.TexLoader;

import java.awt.image.BufferedImage;

public enum Material {
    WOOD(TexLoader.WOOD),
    WOOL(TexLoader.WOOL),
    WHEAT(TexLoader.WHEAT),
    CLAY(TexLoader.CLAY),
    METAL(TexLoader.METAL),
    GOLD(TexLoader.GOLD, false),
    NONE();
    final BufferedImage texture;
    final boolean isSellable;

    Material(BufferedImage texture) {
        this.texture = texture;
        this.isSellable = true;
    }

    Material() {
        this.texture = null;
        this.isSellable = false;
    }

    Material(BufferedImage texture, boolean isSellable) {
        this.texture = texture;
        this.isSellable = isSellable;
    }

    public boolean isSellable() {
        return isSellable;
    }
}

