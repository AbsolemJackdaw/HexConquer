package jackdaw.game.resources;

import jackdaw.game.TexLoader;

import java.awt.image.BufferedImage;

public enum Material {
    WOOD(TexLoader.WOOD),
    WOOL(TexLoader.WOOL),
    WHEAT(TexLoader.WHEAT),
    CLAY(TexLoader.CLAY),
    METAL(TexLoader.METAL),
    GOLD(TexLoader.GOLD);

    final BufferedImage texture;

    Material(BufferedImage texture) {
        this.texture = texture;
    }
}

