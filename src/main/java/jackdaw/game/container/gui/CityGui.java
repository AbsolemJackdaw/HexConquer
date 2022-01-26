package jackdaw.game.container.gui;

import framework.input.MouseHandler;
import framework.window.Window;
import jackdaw.game.Level;
import jackdaw.game.TexLoader;
import jackdaw.game.container.BufferedContainer;
import jackdaw.game.container.DrawProvider;
import jackdaw.game.container.button.PayButton;
import jackdaw.game.level.BuildSpot;
import jackdaw.game.level.map.Coord;
import jackdaw.game.level.map.Hover;
import jackdaw.game.level.upgrades.Upgrade;
import jackdaw.game.resources.MatStack;
import jackdaw.game.resources.Material;

import java.awt.*;
import java.util.ArrayList;

public class CityGui extends BufferedContainer {

    final BuildSpot city;
    PayButton button;
    MatStack[] cost = new MatStack[]{
            new MatStack(Material.CLAY, 3),
            new MatStack(Material.WOOD, 3),
            new MatStack(Material.METAL, 7),
            new MatStack(Material.WHEAT, 7),
            new MatStack(Material.WOOL, 3)
    };
    Level level;
    ArrayList<PayButton> stockedUpgrades = new ArrayList<>();
    private boolean removeButton = false;

    public CityGui(Level level, int width, int height, BuildSpot city) {
        super(level, width, height);
        this.city = city;
        this.level = level;
    }

    public void init() {
        button = new PayButton(
                (int) originX + (16 * getWidth() / TexLoader.CITY_GUI.getWidth()),
                (int) originY + (90 * getHeight() / TexLoader.CITY_GUI.getHeight()),
                Window.getGameScale(75),
                Window.getGameScale(75),
                cost, b -> {
            if (!city.isCity()) {
                if (city.getOwner().canPay(cost)) {
                    city.upgrade();
                    for (MatStack matStack : cost) {
                        city.getOwner().substractWith(matStack);
                    }
                }
            }
        }) {
            @Override
            public void draw(Graphics2D g) {
                g.setColor(new Color(50, 90, 50, 150));
                g.fill(this.box);

                g.setClip(box);
                g.drawImage(TexLoader.CITY, box.x, box.y, box.width, box.height, null);
                g.setClip(Level.viewPort);
                super.draw(g);
            }

            @Override
            public boolean canBuy() {
                return !city.isCity();
            }

            @Override
            public boolean isBought() {
                return city.isCity();
            }
        };


        initUpgradeButtons();
    }

    private void initUpgradeButtons() {
        for (int i = 0; i < level.player.upgradeInventorySize(); i++) {
            final int index = i;
            Upgrade upgrade = level.player.getUpgrade(index);
            stockedUpgrades.add(
                    new PayButton(
                            (int) originX + Window.getGameScale(level.player.arrayIndex(index).x * 70 + 500),
                            (int) originY + Window.getGameScale(level.player.arrayIndex(index).y * 70 + 16),
                            Window.getGameScale(64),
                            Window.getGameScale(64),
                            (upgrade != null ? upgrade.getCost() : new MatStack[0]),
                            b -> {
                                if (upgrade != null && city.canAddUpgrade() && level.player.canPay(upgrade.getCost())) {
                                    city.addUpgrade(upgrade);
                                    level.player.removeUpgrade(index);
                                    for (MatStack stack : upgrade.getCost())
                                        level.player.substractWith(stack);
                                    removeButton = true;
                                }
                            }
                    ) {
                        @Override
                        public void draw(Graphics2D g) {
                            if (upgrade != null) {
                                upgrade.drawInfo(g, FONT, box);
                                g.drawImage(upgrade.img(), box.x, box.y, box.width, box.height, null);
                            } else {
                                g.setColor(Color.darkGray);
                                g.fill(box);
                            }
                            //dont draw hoverable
                            //super.draw(g);

                        }
                    }
            );
        }
    }

    @Override
    public void draw(Graphics2D g, DrawProvider provider) {
        g.setColor(new Color(50, 50, 50, 230));
        g.fill(Level.viewPort);
        g.drawImage(TexLoader.CITY_GUI, (int) originX, (int) originY, getWidth(), getHeight(), null);

        int l = city.getUpgrades().length;
        for (int p = 0; p < l; p++) {
            Upgrade u = city.getUpgrades()[p];
            int x = p - (p >= 2 ? 2 : 0);
            int y = p / 2;
            if (u != null)
                g.drawImage(u.img(), (int) originX + (100 * (x)), (int) originY + (100 * (y)), 90, 90, null);
        }

        if (!city.isCity()) {
            g.setFont(FONT);
            g.setColor(Color.black);
            g.drawString("upgrade to city", -2 + button.box.x + button.box.width, -2 + button.box.y + button.box.height / 2);
            g.setColor(Color.white);
            g.drawString("upgrade to city", button.box.x + button.box.width, button.box.y + button.box.height / 2);
            button.draw(g);
        }

        stockedUpgrades.forEach(payButton -> payButton.draw(g));
        stockedUpgrades.forEach(payButton -> {
            Coord mouse = new Coord((int) MouseHandler.mouseX, (int) MouseHandler.mouseY);
            if (payButton.box.contains(mouse.point())) {
                if (payButton.getHoverable(mouse) != Hover.EMPTY)
                    payButton.getHoverable(mouse).draw(g);

            }
        });

    }

    @Override
    public void update() {
        super.update();
        button.update();
        if (removeButton) {
            stockedUpgrades.clear();
            initUpgradeButtons();
            removeButton = false;
        } else
            stockedUpgrades.forEach(PayButton::update);

    }
}
