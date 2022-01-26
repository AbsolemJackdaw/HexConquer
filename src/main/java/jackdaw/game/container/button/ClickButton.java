package jackdaw.game.container.button;

import framework.input.MouseHandler;

import java.awt.*;

public class ClickButton extends Button {
    private final OnClick click;


    public ClickButton(int x, int y, int sx, int sy, OnClick click) {
        super(x, y, sx, sy);
        this.click = click;
    }

    @Override
    public void draw(Graphics2D g) {

    }

    @Override
    public void update() {
        if (MouseHandler.click && this.box.contains(MouseHandler.clicked))
            click.onClick(this);
    }
}
