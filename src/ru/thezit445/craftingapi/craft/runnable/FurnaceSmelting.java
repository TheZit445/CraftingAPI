package ru.thezit445.craftingapi.craft.runnable;

import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Furnace;
import org.bukkit.block.Smoker;
import org.bukkit.inventory.ItemStack;
import ru.thezit445.craftingapi.craft.ingredient.RecipeIngredient;
import ru.thezit445.craftingapi.craft.interfaces.IFurnaceRecipe;
import ru.thezit445.craftingapi.craft.interfaces.IRunnableRecipe;
import ru.thezit445.craftingapi.craft.recipe.*;
import ru.thezit445.craftingapi.utils.Fuel;

public class FurnaceSmelting extends SerializableRunnable {

    private Furnace furnace;
    private AbstractRecipe recipe;
    private int cookTime;
    private int currentCookTime;
    private RecipeIngredient ingredient;
    private ItemStack smelting;
    private ItemStack result;
    private ItemStack fuel;
    private int tick = 1;

    public FurnaceSmelting(Furnace furnace, IFurnaceRecipe recipe, int currentCookTime) {
        this(furnace, recipe);
        this.currentCookTime = currentCookTime;
    }

    public FurnaceSmelting(Furnace furnace, IFurnaceRecipe recipe) {
        super(furnace, recipe, Type.FURNACE_SMELTING);
        this.furnace = furnace;
        AbstractRecipe.Type type = ((AbstractRecipe) recipe).getType();
        switch (type) {
            case BLASTING: {
                this.recipe = (BlastingRecipe) recipe;
                cookTime = ((BlastingRecipe) recipe).getCookTime()-1;
                ingredient = ((BlastingRecipe) recipe).getIngredient();
                break;
            }
            case SMOKER: {
                this.recipe = (SmokerRecipe) recipe;
                cookTime = ((SmokerRecipe) recipe).getCookTime()-1;
                ingredient = ((SmokerRecipe) recipe).getIngredient();
                break;
            }
            default:{
                this.recipe = (FurnaceRecipe) recipe;
                cookTime = ((FurnaceRecipe) recipe).getCookTime()-1;
                ingredient = ((FurnaceRecipe) recipe).getIngredient();
                break;
            }
        }
        if (furnace instanceof BlastFurnace || furnace instanceof SmokerRecipe) {
            tick++;
        }
        currentCookTime = furnace.getCookTime();
        result = furnace.getSnapshotInventory().getResult();
    }

    @Override
    public void run() {

        if (isCancelled()) return;

        furnace = (Furnace) furnace.getBlock().getState();
        smelting = furnace.getSnapshotInventory().getSmelting();

        if (smelting == null) {
            RunnableManager.removeRunnable(furnace.getLocation());
            cancel();
            return;
        }

        if (!ingredient.isSimilar(smelting)) {
            RunnableManager.removeRunnable(furnace.getLocation());
            cancel();
            return;
        }

        result = furnace.getSnapshotInventory().getResult();

        if (furnace.getBurnTime() > 0) {
            if (result == null || (result.isSimilar(recipe.getResult()) && result.getAmount() < 64 )) {
                currentCookTime+=tick;
                if (currentCookTime >= cookTime) {
                    currentCookTime = 0;
                    if (result == null) {
                        furnace.getSnapshotInventory().setResult(recipe.getResult());
                    } else {
                        result.setAmount(result.getAmount() + 1);
                        furnace.getSnapshotInventory().setResult(result);
                    }
                    if (smelting.getAmount() > 1) {
                        smelting.setAmount(smelting.getAmount() - 1);
                    } else {
                        smelting = null;
                    }
                    furnace.getSnapshotInventory().setSmelting(smelting);
                }
                furnace.setCookTime((short) currentCookTime);
            }
        } else {
            if (result == null || result.getAmount() < 64) {
                fuel = furnace.getSnapshotInventory().getFuel();
                if (fuel != null && fuel.getType().isFuel()) {
                    Fuel fuelType = Fuel.getFuel(fuel.getType());
                    int duration = fuelType.getDuration();
                    if (furnace instanceof BlastFurnace || furnace instanceof Smoker) duration/=2;
                    furnace.setBurnTime((short) duration);
                    if (fuel.getAmount() > 1) {
                        fuel.setAmount(fuel.getAmount() - 1);
                    } else {
                        fuel = null;
                    }
                    furnace.getSnapshotInventory().setFuel(fuel);
                    currentCookTime+=tick;
                } else {
                    if (currentCookTime > 0) currentCookTime--;
                }
            }
        }
        furnace.update();
    }

    @Override
    public String serialize() {
        return "FurnaceSmelting=[" +
                "locCoords=(" + furnace.getLocation().getBlockX() + "," + furnace.getLocation().getBlockY() + "," + furnace.getLocation().getBlockZ() + ");" +
                "locWorld=" + furnace.getLocation().getWorld().getName() + ";" +
                "recKey=" + recipe.getKey().getKey() + ";" +
                "recSpace=" + recipe.getKey().getNamespace() + ";" +
                "during=" + currentCookTime +
                "]";
    }

}
