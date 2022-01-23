package jackdaw.game.map;

import jackdaw.game.resources.MatStack;

public interface Buyable {
    MatStack[] cost();

    void buy(String owner);

    boolean isBought();

    boolean canBuy();
}
