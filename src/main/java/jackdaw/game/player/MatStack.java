package jackdaw.game.player;

import jackdaw.game.resources.Material;

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
        this.ammount += amount;
    }
}
