package jackdaw.game;

import framework.GameStateHandler;
import framework.gamestate.LoadState;
import framework.resourceLoaders.ImageLoader;
import framework.resourceLoaders.ResourceLocation;

import java.awt.image.BufferedImage;

public class TexLoader extends LoadState {
    public static BufferedImage PLAINS;
    public static BufferedImage WOODS;
    public static BufferedImage MOUNTAINS;
    public static BufferedImage PITS;
    public static BufferedImage FIELDS;
    public static BufferedImage RIVER;
    public static BufferedImage EMPTY;
    public static BufferedImage WATER;

    public static BufferedImage CITY;
    public static BufferedImage ROAD;
    public static BufferedImage CROSSING;

    public static BufferedImage CITYGUI;

    public TexLoader(GameStateHandler gsh) {
        super(gsh);
        ImageLoader.setLoadHandler(getClass());
    }

    @Override
    protected void loadResources() {
        super.loadResources();
        PLAINS = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/plains.png"));
        WOODS = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/forest.png"));
        MOUNTAINS = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/mountain.png"));
        PITS = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/pits.png"));
        FIELDS = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/fields.png"));
        RIVER = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/rivers.png"));
        EMPTY = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/desert.png"));
        WATER = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/water.png"));

        CITY = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/build/city.png"));
        ROAD = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/build/road.png"));
        CROSSING = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/build/crossing.png"));

        CITYGUI = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/gui/city.png"));

    }


    @Override
    public void update() {
        super.update();
        if (isDoneLoadingResources())
            gsh.changeGameState(ConquerStates.LEVEL);
    }
}
