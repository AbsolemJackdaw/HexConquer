package jackdaw.game.level.upgrades;

import jackdaw.game.DayCycleEvent;
import jackdaw.game.TexLoader;
import jackdaw.game.resources.MatStack;
import jackdaw.game.resources.Material;

import java.util.ArrayList;
import java.util.Random;

public class Upgrades {
    public static final Upgrade WATERWORKS = new Upgrade().image(TexLoader.TEST).points(1).resist(DayCycleEvent.Event.PESTILENCE).cityExclusive().global().
            info("Waterworks\nPrevent pestilence in every city.").
            costs(new MatStack(Material.METAL, 15), new MatStack(Material.WOOD, 15), new MatStack(Material.CLAY, 15));
    public static final Upgrade GARISSON = new Upgrade().image(TexLoader.TEST).resist(DayCycleEvent.Event.ROBBERS).
            info("Garrisson\nProtects the village from Robbers stealing resources.").
            costs(new MatStack(Material.WHEAT, 5), new MatStack(Material.WOOL, 5), new MatStack(Material.WOOD, 5));

    public static final Upgrade WHEATFARM = new Upgrade().image(TexLoader.TEST).bonus(Material.WHEAT, 1).
            info("Farm\nIncreases collected Wheat from surounding fields").
            costs(new MatStack(Material.CLAY, 2), new MatStack(Material.WOOL, 2));
    public static final Upgrade WOOLFARM = new Upgrade().image(TexLoader.TEST).bonus(Material.WOOL, 1).
            info("Farm\nIncreases collected Wool from surounding fields").
            costs(new MatStack(Material.CLAY, 2), new MatStack(Material.WOOD, 2));
    public static final Upgrade WOODFARM = new Upgrade().image(TexLoader.TEST).bonus(Material.WOOD, 1).
            info("Farm\nIncreases collected Wood from surounding fields").
            costs(new MatStack(Material.CLAY, 2), new MatStack(Material.WHEAT, 2));

    public static final Upgrade CLAYFARM = new Upgrade().image(TexLoader.TEST).bonus(Material.CLAY, 1).
            info("Farm\nIncreases collected Clay from surounding fields").
            costs(new MatStack(Material.WOOD, 2), new MatStack(Material.WHEAT, 2));
    public static final Upgrade METALFARM = new Upgrade().image(TexLoader.TEST).bonus(Material.METAL, 1).
            info("Farm\nIncreases collected Metal from surounding fields").
            costs(new MatStack(Material.WOOD, 2), new MatStack(Material.WHEAT, 2));
    public static final Upgrade GOLDFARM = new Upgrade().image(TexLoader.TEST).bonus(Material.GOLD, 1).
            info("Farm\nIncreases collected Gold from surounding fields").
            costs(new MatStack(Material.WOOD, 2), new MatStack(Material.WHEAT, 2), new MatStack(Material.METAL, 3));

    public static final Upgrade BARN = new Upgrade().image(TexLoader.TEST).resist(DayCycleEvent.Event.RODENTS).
            info("Barn\nProtects your stock from rodents").
            costs(new MatStack(Material.WOOL, 2), new MatStack(Material.WOOD, 4), new MatStack(Material.CLAY, 4));

    public static final Upgrade CHURCH = new Upgrade().image(TexLoader.TEST).points(2).cityExclusive().
            info("Church\nExpand and teach. Build and Conquer. Faith will prevail").
            costs(new MatStack(Material.WHEAT, 2), new MatStack(Material.CLAY, 5), new MatStack(Material.GOLD, 5));

    public static final ArrayList<Upgrade> UPGRADES = new ArrayList<>();

    static {
        UPGRADES.add(WATERWORKS);
        UPGRADES.add(WHEATFARM);
        UPGRADES.add(WOOLFARM);
        UPGRADES.add(WOODFARM);
        UPGRADES.add(CLAYFARM);
        UPGRADES.add(METALFARM);
        UPGRADES.add(GOLDFARM);
        UPGRADES.add(BARN);
        UPGRADES.add(CHURCH);
        UPGRADES.add(GARISSON);

    }

    public static Upgrade getRandom() {

        return UPGRADES.get(new Random().nextInt(UPGRADES.size()));
    }
}
