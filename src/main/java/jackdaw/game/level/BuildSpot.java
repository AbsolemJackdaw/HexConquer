package jackdaw.game.level;

import framework.window.Window;
import jackdaw.game.DayCycleEvent;
import jackdaw.game.Level;
import jackdaw.game.TexLoader;
import jackdaw.game.container.BufferedContainer;
import jackdaw.game.container.gui.CityGui;
import jackdaw.game.level.map.Coord;
import jackdaw.game.level.map.GuiAble;
import jackdaw.game.level.map.Hover;
import jackdaw.game.level.upgrades.Upgrade;
import jackdaw.game.player.Player;
import jackdaw.game.resources.MatStack;
import jackdaw.game.resources.Material;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class BuildSpot extends PlaceableElement implements GuiAble {
    public final Rectangle imageSpace;
    private final Shape boundingBox;
    private final Upgrade[] upgrades = new Upgrade[4];
    private final ArrayList<Material> farms = new ArrayList<>();
    private boolean hasBuildRoadNode;
    private boolean isCity = false;
    private DayCycleEvent.Event currentDayEvent = DayCycleEvent.Event.NONE;

    public BuildSpot(Level level, Coord coord) {
        super(level, coord);
        int size = (int) (15 * Window.getScale());
        boundingBox = new Ellipse2D.Double(coord.posX() - size / 2f, coord.posY() - size / 2f, size, size);
        size = (int) (16 * Window.getScale());
        imageSpace = new Rectangle(getPosition().posX() - (int) (size * 2.5), (int) (getPosition().posY() - size * 3.0), size * 5, size * 5);
    }

    public void addTrackingHex(PlainHex hex) {
        farms.add(hex.getMaterial());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BuildSpot node && node.getPosition().equals(getPosition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPosition().posX(), getPosition().posY());
    }

    @Override
    public void update() {

    }

    @Override
    public Shape getBoundingBox() {
        return boundingBox;
    }

    @Override
    public void buy(Player player) {
        if (canBuy()) {
            if (player.canPay(cost())) {
                purchasePlot(player);
                for (MatStack matStack : cost()) {
                    player.substractWith(matStack);
                }
            }
        }
    }

    public void purchasePlot(Player buyer) {
        bought = true;
        this.setOwner(buyer);
    }

    @Override
    public void draw(Graphics2D g) {
        if (bought) {
            g.setClip(imageSpace);
            BufferedImage img = isCity ? TexLoader.CITY : TexLoader.VILLAGE;
            g.drawImage(img, imageSpace.x, imageSpace.y, imageSpace.width, imageSpace.height, null);

            BufferedImage tex = switch (currentDayEvent) {
                case GOODYEAR -> TexLoader.GOODYEAR;
                case PESTILENCE -> TexLoader.BADYEAR;
                case RODENTS -> TexLoader.RODENTS;
                case ROBBERS -> TexLoader.ROBBERS;
                case COMMERCE -> TexLoader.COMMERCE;
                default -> null;
            };
            if (tex != null)
                g.drawImage(tex, imageSpace.x + imageSpace.width / 4, imageSpace.y, imageSpace.width / 2, imageSpace.height / 2, null);
        } else if (hasBuildRoadNode) {
            g.setColor(Color.DARK_GRAY);
            g.setClip(getBoundingBox());
            g.fill(getBoundingBox());
        }
        g.setClip(Level.viewPort);
    }

    public void attachRoad() {
        hasBuildRoadNode = true;
    }

    public boolean connectsWithRoad() {
        return hasBuildRoadNode;
    }

    @Override
    public MatStack[] cost() {
        return new MatStack[]{new MatStack(Material.WOOL, 5), new MatStack(Material.WOOD, 7), new MatStack(Material.CLAY, 7)};
    }

    @Override
    public BufferedContainer getGui() {
        if (bought) {
            CityGui gui = new CityGui(level, Level.viewPort.width - Window.getGameScale(100), Level.viewPort.height - Window.getGameScale(100), this);
            gui.setOrigin(Window.getGameScale(50), Window.getGameScale(50));
            gui.init();
            return gui;
        }
        return null;
    }

    @Override
    public boolean canBuy() {
        byte isRoadFreeCounter = 0;
        boolean atLeastOneRoadHasBeenBought = false;
        Collection<Road> roads = level.nodeRoads(this);
        if (!roads.isEmpty()) {
            for (Road road : roads) {
                if (!atLeastOneRoadHasBeenBought)
                    atLeastOneRoadHasBeenBought = road.isBought();
                if (!level.isCityBuild(road.getLinkedNodeA()) && !level.isCityBuild(road.getLinkedNodeB()))
                    isRoadFreeCounter++;
            }
        }
        return isRoadFreeCounter == roads.size() && atLeastOneRoadHasBeenBought;
    }

    @Override
    public Hover getHoverable(Coord coord) {
        Hover theSuper = super.getHoverable(coord);
        return new Hover(theSuper).withDraw(g -> {
            Rectangle s = level.getRelativeShape(imageSpace);
            if (canBuy()) {
                g.drawImage(TexLoader.VILLAGE_TRANSPARENT, s.x, s.y, s.width, s.height, null);
            } else if (bought) {
                g.drawImage(TexLoader.CITY_POP, s.x + s.width / 4, s.y + s.height / 4, s.width / 2, s.height / 2, null);
            }
            theSuper.draw(g);
        });
    }

    public void setCurrentDayEvent(DayCycleEvent.Event currentDayEvent) {
        this.currentDayEvent = currentDayEvent;
    }

    public void upgrade() {
        isCity = true;
    }

    public int getBonusMultiplier(Material material) {
        int i = isCity ? 2 : 1;
        for (Upgrade upgrade : upgrades) {
            if (upgrade != null)
                i += upgrade.resourceBonusMultiplier(material);
        }
        return i;
    }

    public boolean isResistant(DayCycleEvent.Event event) {
        boolean flag = false;
        for (Upgrade upgrade : upgrades) {
            if (upgrade != null)
                if (upgrade.resists().equals(event))
                    flag = true;
        }
        return event != DayCycleEvent.Event.NONE && flag;
    }

    public ArrayList<Material> getFields() {
        return farms;
    }

    public boolean isCity() {
        return isCity;
    }

    public Upgrade[] getUpgrades() {
        return upgrades;
    }

    public boolean canAddUpgrade() {
        for (int i = 0; i < (isCity ? upgrades.length : upgrades.length / 2); i++) {
            if (upgrades[i] == null)
                return true;
        }
        return false;
    }

    public void addUpgrade(Upgrade upgrade) {
        for (int i = 0; i < upgrades.length; i++) {
            if (upgrades[i] == null) {
                upgrades[i] = upgrade;
                return;
            }
        }
    }
}
