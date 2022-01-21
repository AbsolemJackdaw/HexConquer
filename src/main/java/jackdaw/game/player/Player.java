package jackdaw.game.player;

import jackdaw.game.resources.Material;

public class Player {

    private MatStack[] inventory = new MatStack[Material.values().length];

    public Player() {
        for (Material mat : Material.values()) {
            if (mat != Material.GOLD)
                addWith(new MatStack(mat, 5));
        }
    }

    public void collect(Material mat) {
        if (mat == null) // empty plains exist
            return;
        int index = mat.ordinal();
        if (inventory[index] != null)
            inventory[index].merge(new MatStack(mat, 1));
        else
            inventory[index] = new MatStack(mat, 1);
    }

    public void updateWith(MatStack stack, boolean add) {
        if (stack != null && stack.getMat() != null) {
            int index = stack.getMat().ordinal();
            if (inventory[index] != null) {
                int amount = stack.getAmmount();
                if (!add)
                    amount *= -1;
                inventory[index].updateAmount(amount);
            } else {
                inventory[index] = stack;
            }
        }
    }

    public void substractWith(MatStack stack) {
        updateWith(stack, false);
    }

    public void addWith(MatStack stack) {
        updateWith(stack, true);
    }

    public MatStack[] getInventory() {
        return inventory;
    }

    public boolean canPay(MatStack... cost) {
        int hits = 0;
        if (cost != null)
            for (MatStack payement : cost) {
                if (payement != null) {
                    MatStack slotStack = getStackInSlot(payement.mat);
                    if (slotStack != null) {
                        if (slotStack.ammount >= payement.ammount) {
                            hits++;
                        }
                    }
                }
            }
        return cost != null && hits == cost.length;
    }

    public MatStack getStackInSlot(Material mat) {
        return inventory[mat.ordinal()];
    }
}
