package ru.thezit445.craftingapi.craft.runnable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.thezit445.craftingapi.craft.interfaces.IFurnaceRecipe;
import ru.thezit445.craftingapi.craft.recipe.RecipeManager;

public abstract class SerializableRunnable extends BukkitRunnable {

    protected BlockState state;
    protected IFurnaceRecipe recipe;
    protected Type type;

    public SerializableRunnable(BrewingStand state, Type type) {
        this.state = state;
        this.type = type;
    }

    public SerializableRunnable(Furnace state, IFurnaceRecipe recipe, Type type) {
        this.state = state;
        this.recipe = recipe;
        this.type = type;
    }

    abstract public String serialize();

    public Location getLocation() {
        return state.getLocation();
    }

    public static SerializableRunnable deserialize(String str) {
        String[] nameAndParams = str.split("=", 2);
        SerializableRunnable.Type type = SerializableRunnable.Type.getType(nameAndParams[0]);
        String[] params = nameAndParams[1].replaceAll("[\\[\\]]", "").split(";");
        String[] locCoords = params[0].split("=")[1].replaceAll("[\\(\\)]","").split(",");
        World locWorld = Bukkit.getWorld(params[1].split("=")[1]);
        Location loc = new Location(locWorld, Integer.parseInt(locCoords[0]),Integer.parseInt(locCoords[1]),Integer.parseInt(locCoords[2]));
        int during;
        String recKey = null;
        String recSpace = null;
        Plugin plugin = null;
        if (params.length == 5) {
            recKey = params[2].split("=")[1];
            recSpace = params[3].split("=")[1];
            during = Integer.parseInt(params[4].split("=")[1]);
            for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
                if (pl.getName().equalsIgnoreCase(recSpace)) {
                    plugin = pl;
                    break;
                }
            }
            if (plugin == null) return null;
            NamespacedKey key = new NamespacedKey(plugin, recKey);
            IFurnaceRecipe recipe = (IFurnaceRecipe) RecipeManager.getRecipe(key);
            FurnaceSmelting smelting = new FurnaceSmelting((Furnace) loc.getBlock().getState(), recipe, during);
            return smelting;
        } else {
            during = Integer.parseInt(params[2].split("=")[1]);
            Brewing brewing = new Brewing((BrewingStand) loc.getBlock().getState(), during);
            return brewing;
        }
    }

    public enum Type {

        FURNACE_SMELTING("FurnaceSmelting"),
        BREWING("Brewing"),
        NO_A_RECIPE_HANDLER_RUNNABLE("unknown");

        private String type;

        Type(String type) {
            this.type = type;
        }

        public static Type getType(String strType){
            for (Type type : Type.values()) {
                if (type.type.equalsIgnoreCase(strType)) return type;
            }
            return NO_A_RECIPE_HANDLER_RUNNABLE;
        }

    }

}
