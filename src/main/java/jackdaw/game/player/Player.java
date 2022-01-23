package jackdaw.game.player;

import jackdaw.game.DayCycleEvent;
import jackdaw.game.resources.MatStack;
import jackdaw.game.resources.Material;

public class Player {

    private MatStack[] inventory = new MatStack[Material.values().length];
    private DayCycleEvent.NOONEVENTS currentNoonEvent = DayCycleEvent.NOONEVENTS.NONE;
    private String name;

    public Player(String name) {
        this.name = name;
        for (Material mat : Material.values()) {
            if (mat.isSellable())
                addWith(new MatStack(mat, 100));
        }
    }

    public void initInventory() {
        inventory = new MatStack[Material.values().length];
        for (Material mat : Material.values()) {
            if (mat.isSellable())
                addWith(new MatStack(mat, 5));
        }
    }

    public void collect(Material mat, int amount) {
        if (mat == null) // empty plains exist
            return;
        int index = mat.ordinal();
        if (inventory[index] != null)
            inventory[index].merge(new MatStack(mat, amount));
        else
            inventory[index] = new MatStack(mat, amount);
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
                    MatStack slotStack = getStackInSlot(payement.getMat());
                    if (slotStack != null) {
                        if (slotStack.getAmmount() >= payement.getAmmount()) {
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

    public DayCycleEvent.NOONEVENTS getCurrentNoonEvent() {
        return currentNoonEvent;
    }

    public void setCurrentNoonEvent(DayCycleEvent.NOONEVENTS currentNoonEvent) {
        this.currentNoonEvent = currentNoonEvent;
    }

    public String getName() {
        return name;
    }
}
