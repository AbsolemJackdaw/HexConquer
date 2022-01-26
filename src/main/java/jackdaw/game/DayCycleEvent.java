package jackdaw.game;

import jackdaw.game.player.Player;
import jackdaw.game.resources.MatStack;
import jackdaw.game.resources.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DayCycleEvent {
    Random rand = new Random();

    public void rollDay(Level level) {
        level.getCityNodes().stream().filter(citySpot -> citySpot.getOwner() != null && citySpot.isBought()).forEach(citySpot -> {
            //different event for each city owned
            Event event = rand.nextInt(20) == 0 ? Event.dawnEvent().get(rand.nextInt(Event.dawnEvent().size())) : Event.NONE;
            citySpot.setCurrentDayEvent(event);
            if (!citySpot.isResistant(event))
                for (Material mat : citySpot.getFields()) {
                    if (mat != Material.NONE) {
                        int cityBonus = citySpot.getBonusMultiplier(mat);
                        int eventBonus = event.getMultiplier();
                        citySpot.getOwner().collect(mat, cityBonus * eventBonus);
                    }
                }
        });
    }

    public void startingCapital(Level level) {
        level.getCityNodes().stream().filter(citySpot -> citySpot.getOwner() != null && citySpot.isBought()).forEach(citySpot -> {
            for (Material mat : citySpot.getFields()) {
                citySpot.getOwner().collect(mat, 1);
            }
        });
    }

    public void rollNoon(Level level) {
        level.getCityNodes().stream().filter(citySpot -> citySpot.getOwner() != null && citySpot.isBought()).forEach(citySpot -> {
            Event event = rand.nextInt(20) == 0 ? Event.noonEvent().get(rand.nextInt(Event.noonEvent().size())) : Event.NONE;
            citySpot.setCurrentDayEvent(event);
            if (!citySpot.isResistant(event) && event != Event.NONE) {
                citySpot.setCurrentDayEvent(event);
                Player player = citySpot.getOwner();
                for (Material field : citySpot.getFields()) {
                    if (!event.nab.isEmpty() && event.nab.contains(field)) //nabbers
                    {
                        player.substractWith(new MatStack(field, 5));
                    } else // commerce
                    {
                        Material mat = field;
                        while (citySpot.getFields().contains(mat)) {
                            mat = Material.values()[rand.nextInt(Material.values().length)];
                        }
                        player.addWith(new MatStack(mat, 3));
                    }
                }
            }
        });
    }

    public enum Event {
        NONE,
        PESTILENCE(0),//no resources this roll
        ROBBERS(Material.METAL, Material.GOLD, Material.CLAY),//nab metal, gold, clay
        COMMERCE, //couple random resources
        GOODYEAR(2), //sun on fields, double resources of roll
        RODENTS(Material.WOOD, Material.WOOL, Material.WHEAT);//nab wood, wheat, wood
        private int multiplier = 1;
        private ArrayList<Material> nab = new ArrayList<>();

        Event(int multiplier) {
            this.multiplier = multiplier;
        }

        Event() {
        }

        Event(Material... nabs) {
            nab.addAll(Arrays.asList(nabs));
        }

        public static List<Event> noonEvent() {
            return List.of(ROBBERS, COMMERCE, RODENTS);
        }

        public static List<Event> dawnEvent() {
            return List.of(GOODYEAR, PESTILENCE);
        }

        public int getMultiplier() {
            return multiplier;
        }
    }
}
