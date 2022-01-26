package jackdaw.game.container.gui;

import framework.window.Window;
import jackdaw.game.Level;
import jackdaw.game.container.BufferedContainer;
import jackdaw.game.container.DrawProvider;
import jackdaw.game.container.button.Button;
import jackdaw.game.container.button.ClickButton;
import jackdaw.game.container.button.PayButton;
import jackdaw.game.container.button.SellButton;
import jackdaw.game.level.upgrades.Upgrade;
import jackdaw.game.level.upgrades.Upgrades;
import jackdaw.game.resources.MatStack;
import jackdaw.game.resources.Material;

import java.awt.*;
import java.util.ArrayList;

public class InventoryContainer extends BufferedContainer {
    private final Level level;
    private final int generalOffset = Window.getGameScale(10);
    private ClickButton research;
    private ArrayList<PayButton> upgrades = new ArrayList<>();

    public InventoryContainer(Level level, int width, int height) {
        super(level, width, height);
        this.level = level;
        setOrigin(0, 0);

        for (Material value : Material.values()) {
            if (value.isSellable()) {
                int y = (1 + value.ordinal()) * FONT.getSize();
                buttons.add(new SellButton(getWidth() - FONT.getSize() * 4, y + generalOffset, value, b -> {
                    if (b instanceof SellButton sell) {
                        MatStack pay = new MatStack(sell.material, 4);
                        if (level.player.canPay(pay)) {
                            level.player.substractWith(pay);
                            level.player.collect(Material.GOLD, 1);
                        }
                    }
                }));

                buttons.add(new SellButton(getWidth() - FONT.getSize() * 2, y + generalOffset, value, b -> {
                    if (b instanceof SellButton sell) {
                        MatStack pay = new MatStack(Material.GOLD, 2); //TODO take values out of trade bonuses
                        if (level.player.canPay(pay)) {
                            level.player.substractWith(pay);
                            level.player.collect(sell.material, 1);
                        }
                    }
                }));
            }
        }

        research = new ClickButton(0, 0, 100, 20, b -> {
            level.player.addUpgrade(Upgrades.getRandom());
        }) {
            @Override
            public void draw(Graphics2D g) {
                super.draw(g);
                g.setColor(Color.green);
                g.fill(box);
            }
        };
    }

    @Override
    public void draw(Graphics2D g, DrawProvider provider) {
//        g.setColor(new Color(50, 50, 50, 230));
//        g.fill(Level.viewPort);
        g.setColor(Color.orange.darker().darker().darker());
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.white);
        g.setFont(FONT);
        g.drawString("Resources", generalOffset, FONT.getSize());

        for (Button button : buttons) {
            button.draw(g);
        }
        g.setColor(Color.white);

        int index = 2;
        for (Material value : Material.values()) {
            if (!value.isSellable() && value != Material.GOLD)
                continue;
            MatStack stack = level.player.getStackInSlot(value);
            String info = String.format("%-7s: %d", value.name().toLowerCase(), stack != null ? stack.getAmmount() : 0);
            g.drawString(info, generalOffset, index++ * FONT.getSize());
        }

        for (index = 0; index < level.player.upgradeInventorySize(); index++) {
            Upgrade upgrade = level.player.getUpgrade(index);
            Rectangle box = new Rectangle(
                    (int) originX + Window.getGameScale(level.player.arrayIndex(index).x * 70 + 500),
                    (int) originY + Window.getGameScale(level.player.arrayIndex(index).y * 70 + 16),
                    Window.getGameScale(64),
                    Window.getGameScale(64)
            );
            if (upgrade != null) {
                g.drawImage(
                        upgrade.img(),
                        box.x,
                        box.y,
                        box.width,
                        box.height,
                        null
                );
                upgrade.drawInfo(g, FONT, box);
            }
        }

        research.draw(g);

    }

    @Override
    public void update() {

        buttons.forEach(Button::update);
        research.update();
    }
}
