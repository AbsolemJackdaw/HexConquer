package jackdaw.game;

import framework.GameStateHandler;
import framework.gamestate.LoadState;
import framework.resourceLoaders.ImageLoader;
import framework.resourceLoaders.ResourceLocation;

import java.awt.image.BufferedImage;

public class TexLoader extends LoadState {
    public static BufferedImage PLAIN_HEX;
    public static BufferedImage WOOD_HEX;
    public static BufferedImage MOUNTAIN_HEX;
    public static BufferedImage PIT_HEX;
    public static BufferedImage FIELD_HEX;
    public static BufferedImage RIVER_HEX;
    public static BufferedImage EMPTY_HEX;
    public static BufferedImage WATER_HEX;

    public static BufferedImage CITY;
    public static BufferedImage VILLAGE_TRANSPARENT;
    public static BufferedImage VILLAGE;
    public static BufferedImage ROAD;
    public static BufferedImage CROSSING;

    public static BufferedImage CITY_GUI;
    public static BufferedImage HOVER;
    public static BufferedImage CITY_POP;
    public static BufferedImage CLOCK;
    public static BufferedImage ROBBERS;
    public static BufferedImage RODENTS;
    public static BufferedImage COMMERCE;

    public static BufferedImage WOOD;
    public static BufferedImage WOOL;
    public static BufferedImage METAL;
    public static BufferedImage CLAY;
    public static BufferedImage GOLD;
    public static BufferedImage WHEAT;

    public static BufferedImage GOODYEAR;
    public static BufferedImage BADYEAR;


    public TexLoader(GameStateHandler gsh) {
        super(gsh);
        ImageLoader.setLoadHandler(getClass());
    }

    @Override
    protected void loadResources() {
        super.loadResources();
        PLAIN_HEX = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/plains.png"));
        WOOD_HEX = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/forest.png"));
        MOUNTAIN_HEX = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/mountain.png"));
        PIT_HEX = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/pits.png"));
        FIELD_HEX = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/fields.png"));
        RIVER_HEX = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/rivers.png"));
        EMPTY_HEX = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/desert.png"));
        WATER_HEX = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hex/water.png"));

        CITY = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/build/city.png"));
        VILLAGE_TRANSPARENT = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/build/village_transparent.png"));
        VILLAGE = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/build/village.png"));
        ROAD = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/build/road.png"));
        CROSSING = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/build/crossing.png"));

        CITY_GUI = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/gui/city.png"));
        CITY_POP = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/gui/city_pop.png"));
        HOVER = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/hover.png"));
        CLOCK = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/gui/clock.png"));
        ROBBERS = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/gui/robbers.png"));
        RODENTS = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/gui/rodents.png"));
        COMMERCE = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/gui/commerce.png"));

        CLAY = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/item/clay.png"));
        GOLD = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/item/gold.png"));
        METAL = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/item/metal.png"));
        WHEAT = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/item/wheat.png"));
        WOOD = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/item/wood.png"));
        WOOL = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/item/wool.png"));

        GOODYEAR = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/events/good_year.png"));
        BADYEAR = ImageLoader.loadSprite(new ResourceLocation("/jackdaw/events/black_death.png"));

    }


    @Override
    public void update() {
        super.update();
        if (isDoneLoadingResources())
            gsh.changeGameState(ConquerStates.LEVEL);
    }
}
