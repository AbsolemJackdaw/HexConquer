package jackdaw.game;

import jackdaw.game.map.level.PlainHex;
import jackdaw.game.resources.MatStack;
import jackdaw.game.resources.Material;

import java.util.Collection;
import java.util.Random;

public class DayCycleEvent {
    Random rand = new Random();

    public void rollDay(Level level, Collection<PlainHex> plains) {
        DAWNEVENTS event = rand.nextInt(10) == 0 ? rand.nextBoolean() ? DAWNEVENTS.GOODYEAR : DAWNEVENTS.BLACKDEATH : DAWNEVENTS.NONE;
        plains.forEach(plainHex -> plainHex.getTrackingCities().forEach(city -> {
            if (city.getOwner() != null && plainHex.getMaterial() != Material.NONE) {
                level.getPlayerByName(city.getOwner()).ifPresent(p -> p.collect(plainHex.getMaterial(), city.getResourceMultiplier(plainHex.getMaterial()) * event.multiplier));
                city.setCurrentDayEvent(event);
            }
        }));
    }

    public void startGame(Level level, Collection<PlainHex> plains) {
        plains.forEach(plainHex -> plainHex.getTrackingCities().forEach(city -> {
            if (city.getOwner() != null) {
                level.getPlayerByName(city.getOwner()).ifPresent(p ->
                        p.collect(plainHex.getMaterial(), 1));
            }
        }));
    }

    public void rollNoon(Level level) {
        level.getPlayerByName("player").ifPresent(player -> {
            NOONEVENTS event = rand.nextInt(10) == 0 ? NOONEVENTS.values()[rand.nextInt(3)] : NOONEVENTS.NONE;
            player.setCurrentNoonEvent(event);
            switch (event) {
                case RODENTS -> {
                    player.substractWith(new MatStack(Material.WOOL, 2));
                    player.substractWith(new MatStack(Material.WHEAT, 2));
                    player.substractWith(new MatStack(Material.WOOD, 2));
                }
                case ROBBERS -> {
                    player.substractWith(new MatStack(Material.GOLD, 2));
                    player.substractWith(new MatStack(Material.METAL, 2));
                    player.substractWith(new MatStack(Material.CLAY, 2));
                }
                case COMMERCE -> {
                    for (int i = 0; i < 5; i++) {
                        player.addWith(new MatStack(Material.values()[rand.nextInt(Material.values().length)], 1));
                    }
                }
            }
        });
    }

    public void rollSix() {

    }

    public enum DAWNEVENTS {
        GOODYEAR(2),  //sun on fields, double resources of roll
        BLACKDEATH(0),//no resources this roll
        NONE(1);
        int multiplier;

        DAWNEVENTS(int multiplier) {
            this.multiplier = multiplier;
        }
    }

    public enum NOONEVENTS {
        ROBBERS,   //nab metal, gold, clay
        COMMERCE,  //couple random resources
        RODENTS,    //nab wood, wheat, wood
        NONE;
    }
}
