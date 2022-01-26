package jackdaw.game.level.map;

import jackdaw.game.player.Player;
import jackdaw.game.resources.MatStack;

public interface Buyable {
    MatStack[] cost();

    void buy(Player owner);

    boolean isBought();

    boolean canBuy();
}
