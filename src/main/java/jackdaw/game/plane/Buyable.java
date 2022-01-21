package jackdaw.game.plane;

import jackdaw.game.player.MatStack;
import jackdaw.game.resources.Material;

public interface Buyable {
    MatStack[] cost();

    void buy(String owner);
}
