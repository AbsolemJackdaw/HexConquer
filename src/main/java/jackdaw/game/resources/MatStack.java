package jackdaw.game.resources;

import jackdaw.game.container.BufferedContainer;
import jackdaw.game.container.DrawProvider;

public class MatStack {
    Material mat;
    int ammount;

    public MatStack(Material mat, int ammount) {
        this.mat = mat;
        this.ammount = ammount;
    }

    public Material getMat() {
        return mat;
    }

    public void setMat(Material mat) {
        this.mat = mat;
    }

    public int getAmmount() {
        return ammount;
    }

    public void setAmmount(int ammount) {
        this.ammount = ammount;
    }

    public boolean canMerge(MatStack stack) {
        return stack.getMat().equals(mat);
    }

    public void merge(MatStack stack) {
        if (canMerge(stack)) {
            updateAmount(stack.getAmmount());
        }
    }

    public void updateAmount(int amount) {
        int result = this.ammount + amount;
        this.ammount = Math.max(result, 0);
    }

    public DrawProvider draw(int x, int y, int size) {
        return g ->
        {
            g.drawImage(mat.texture, (int) (5 + x ), y, size, size, null);
            g.drawString("x" + ammount, (int) (5 + x ) + size, y+ BufferedContainer.FONT.getSize());
        };
    }
}
