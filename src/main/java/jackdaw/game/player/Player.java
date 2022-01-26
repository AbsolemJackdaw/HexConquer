package jackdaw.game.player;

import jackdaw.game.level.upgrades.Upgrade;
import jackdaw.game.resources.MatStack;
import jackdaw.game.resources.Material;

import java.awt.*;
import java.util.Set;
import java.util.TreeSet;

public class Player {

    //    public ArrayList<Upgrade> availableUpgrades = new ArrayList<>();
    private MatStack[] inventory = new MatStack[Material.values().length];
    private String name;
    private Upgrade[][] availableUpgrades = new Upgrade[5][2];
    private Set<Integer> markedUpgradesRemoval = new TreeSet<>();

    public Player(String name) {
        this.name = name;
        for (Material mat : Material.values()) {
            if (mat.isSellable())
                addWith(new MatStack(mat, 100));
        }
    }

    public void initInventory() {
        inventory = new MatStack[Material.values().length];
    }

    public void collect(Material mat, int amount) {
        if (mat == Material.NONE) // empty plains exist
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

    public String getName() {
        return name;
    }

    public void addUpgrade(Upgrade upgrade) {
        if (canAddUpgrade()) {
            addInNextFreeSlot(upgrade);
        }
    }

    public boolean canAddUpgrade() {
        for (int i = 0; i < upgradeInventorySize(); i++) {
            if (availableUpgrades[arrayIndex(i).x][arrayIndex(i).y] == null) {
                return true;
            }
        }
        return false;
    }

    public void addInNextFreeSlot(Upgrade upgrade) {
        int slot = 0;
        for (int i = 0; i < upgradeInventorySize(); i++) {
            if (availableUpgrades[arrayIndex(i).x][arrayIndex(i).y] == null) {
                addUpgrade(upgrade, slot);
                return;
            }
            slot++;
        }
    }

    public Upgrade getUpgrade(int index) {
        return availableUpgrades[arrayIndex(index).x][arrayIndex(index).y];
    }

    public void addUpgrade(Upgrade u, int index) {
        availableUpgrades[arrayIndex(index).x][arrayIndex(index).y] = u;
    }

    public Point arrayIndex(int index) {
        int x = index >= 5 ? index - 5 : index;
        int y = index / 5;
        return new Point(x, y);
    }

    public int upgradeInventorySize() {
        return availableUpgrades.length * availableUpgrades[0].length;
    }

    public void removeUpgrade(int index) {
        availableUpgrades[arrayIndex(index).x][arrayIndex(index).y] = null;
    }
}
