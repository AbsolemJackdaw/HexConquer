package jackdaw.game.level;

import jackdaw.game.Level;
import jackdaw.game.TexLoader;
import jackdaw.game.level.map.Coord;
import jackdaw.game.level.map.Element;
import jackdaw.game.resources.Material;
import jackdaw.game.resources.PlainType;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PlainHex extends Element {
    private final PlainType plainType;
    private Polygon hex;

    public PlainHex(Level level, PlainType plainType, Coord coord) {
        super(level, coord);
        if (plainType == null)
            throw new NullPointerException("plain type for a Hex cannot be null");

        this.plainType = plainType;
    }

    /**
     * called on game start, after the central coords have been generated for plains, and after nodes have been created.
     * Uses six surrounding nodes to create a hexagon shape
     */
    public void createHex(Polygon polygon) {
        this.hex = polygon;
    }


    @Override
    public void draw(Graphics2D g) {
        g.setClip(hex);
        BufferedImage img = switch (plainType) {
            case PLAINS -> TexLoader.PLAIN_HEX;
            case WOODS -> TexLoader.WOOD_HEX;
            case MOUNTAINS -> TexLoader.MOUNTAIN_HEX;
            case FIELDS -> TexLoader.FIELD_HEX;
            case PITS -> TexLoader.PIT_HEX;
            case RIVERS -> TexLoader.RIVER_HEX;
            case EMPTY -> TexLoader.EMPTY_HEX;
            case WATER -> TexLoader.WATER_HEX;
        };
        if (img != null)
            g.drawImage(img, getPosition().posX() - (int) Level.bone, getPosition().posY() - (int) Level.bone, (int) Level.bone * 2, (int) Level.bone * 2, null);

        g.setClip(Level.viewPort);
    }

    @Override
    public void update() {

    }

    @Override
    public Shape getBoundingBox() {
        return hex;
    }

    public Material getMaterial() {
        return switch (plainType) {
            case WOODS -> Material.WOOD;
            case PLAINS -> Material.WOOL;
            case FIELDS -> Material.WHEAT;
            case RIVERS -> Material.GOLD;
            case PITS -> Material.CLAY;
            case MOUNTAINS -> Material.METAL;
            case EMPTY, WATER -> Material.NONE;
        };
    }

}
