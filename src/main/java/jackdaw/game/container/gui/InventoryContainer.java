package jackdaw.game.container.gui;

import framework.window.Window;
import jackdaw.game.Level;
import jackdaw.game.container.BufferedContainer;
import jackdaw.game.container.DrawProvider;
import jackdaw.game.container.button.Button;
import jackdaw.game.container.button.SellButton;
import jackdaw.game.resources.MatStack;
import jackdaw.game.resources.Material;

import java.awt.*;

public class InventoryContainer extends BufferedContainer {
    private final Level level;
    private final Font font = new Font(Font.MONOSPACED, Font.ITALIC | Font.BOLD, Window.getGameScale(32));
    private final int generalOffset = Window.getGameScale(10);

    public InventoryContainer(Level level, int width, int height) {
        super(level, width, height);
        this.level = level;
        setOrigin(0, 0);

        for (Material value : Material.values()) {
            if (!value.equals(Material.GOLD)) {
                int y = (1 + value.ordinal()) * font.getSize();
                buttons.add(new SellButton(getWidth() - font.getSize() * 4, y + generalOffset, value, b -> {
                    if (b instanceof SellButton sell) {
                        MatStack pay = new MatStack(sell.material, 4);
                        if (level.player.canPay(pay)) {
                            level.player.substractWith(pay);
                            level.player.collect(Material.GOLD,1);
                        }
                    }
                }));

                buttons.add(new SellButton(getWidth() - font.getSize() * 2, y + generalOffset, value, b -> {
                    if (b instanceof SellButton sell) {
                        MatStack pay = new MatStack(Material.GOLD, 2); //TODO take values out of trade bonuses
                        if (level.player.canPay(pay)) {
                            level.player.substractWith(pay);
                            level.player.collect(sell.material,1);
                        }
                    }
                }));
            }
        }
    }

    @Override
    public void draw(Graphics2D g, DrawProvider provider) {
        g.setColor(new Color(50, 50, 50, 230));
        g.fill(Level.viewPort);
        g.setColor(Color.orange.darker().darker().darker());
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.white);
        g.setFont(font);
        g.drawString("Resources", generalOffset, font.getSize());

        for (Button button : buttons) {
            button.draw(g);
        }
        g.setColor(Color.white);

        int index = 2;
        for (Material value : Material.values()) {
            MatStack stack = level.player.getStackInSlot(value);
            String info = String.format("%-7s: %d", value.name().toLowerCase(), stack != null ? stack.getAmmount() : 0);
            g.drawString(info, generalOffset, index++ * font.getSize());
        }


    }

    @Override
    public void update() {

        buttons.forEach(Button::update);
    }
}
