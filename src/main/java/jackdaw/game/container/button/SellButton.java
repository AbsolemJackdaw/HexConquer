package jackdaw.game.container.button;

import framework.input.MouseHandler;
import framework.window.Window;
import jackdaw.game.resources.Material;

import java.awt.*;

public class SellButton extends Button {
    private final OnClick click;
    public Material material;

    public SellButton(int x, int y, Material material, OnClick click) {
        super(x, y, Window.getGameScale(40), Window.getGameScale(30));
        this.click = click;
        this.material = material;
    }

    @Override
    public void draw(Graphics2D g) {
        if (box.x >= 250)
            g.setColor(new Color(20, 50, 20, 150));
        else
            g.setColor(new Color(20, 20, 50, 150));

        g.fill(box);
    }

    @Override
    public void update() {
        if (MouseHandler.click && this.box.contains(MouseHandler.clicked))
            click.onClick(this);
    }

}
