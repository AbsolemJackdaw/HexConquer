package jackdaw.game.level.upgrades;

import framework.input.MouseHandler;
import framework.window.Window;
import jackdaw.game.DayCycleEvent;
import jackdaw.game.level.map.Coord;
import jackdaw.game.resources.MatStack;
import jackdaw.game.resources.Material;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Upgrade {
    private boolean onlyCityUpgrade = false;
    private BufferedImage img = null;
    private int mult = 1;
    private Material mat = Material.NONE;
    private int points = 0;
    private DayCycleEvent.Event resist = DayCycleEvent.Event.NONE;
    private MatStack[] cost = new MatStack[0];
    private boolean global = false; //all cities benefit
    private String info = "";

    public DayCycleEvent.Event resists() {
        return resist;
    }

    public int resourceBonusMultiplier(Material material) {
        return material == mat ? mult : 0;
    }

    public BufferedImage img() {
        return img;
    }

    public String getInfo() {
        return info;
    }

    public int getPoints() {
        return points;
    }

    public MatStack[] getCost() {
        return cost;
    }

    public Upgrade image(BufferedImage img) {
        this.img = img;
        return this;
    }

    public Upgrade bonus(Material mat, int mult) {
        this.mult = mult;
        this.mat = mat;
        return this;
    }

    public Upgrade resist(DayCycleEvent.Event event) {
        this.resist = event;
        return this;
    }

    public Upgrade points(int points) {
        this.points = points;
        return this;
    }

    public Upgrade costs(MatStack... cost) {
        this.cost = cost;
        return this;
    }

    public Upgrade cityExclusive() {
        this.onlyCityUpgrade = true;
        return this;
    }

    public Upgrade global() {
        global = true;
        return this;
    }

    public Upgrade info(String formattedInfo) {
        this.info = formattedInfo;
        return this;
    }

    public void drawInfo(Graphics2D g, Font font, Rectangle box) {
        if (box.contains(new Coord((int) MouseHandler.mouseX, (int) MouseHandler.mouseY).point())) {
            String[] sray = getInfo().split("\n");
            int i = 0;
            for (String s : sray) {
                g.setColor(Color.black);
                g.drawString(s, Window.getGameScale(14), Window.getGameScale(498) + (int) (i * font.getSize()));
                g.setColor(Color.white);
                g.drawString(s, Window.getGameScale(16), Window.getGameScale(500) + (int) (i++ * font.getSize()));
            }
        }
    }
}
