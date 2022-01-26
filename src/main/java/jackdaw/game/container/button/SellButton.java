package jackdaw.game.container.button;

import framework.window.Window;
import jackdaw.game.resources.Material;

import java.awt.*;

public class SellButton extends ClickButton {
    public Material material;

    public SellButton(int x, int y, Material material, OnClick click) {
        super(x, y, Window.getGameScale(40), Window.getGameScale(30), click);
        this.material = material;
    }

    @Override
    public void draw(Graphics2D g) {
        if (box.x >= Window.getGameScale(250))
            g.setColor(new Color(20, 50, 20, 150));
        else
            g.setColor(new Color(20, 20, 50, 150));

        g.fill(box);
    }

}
