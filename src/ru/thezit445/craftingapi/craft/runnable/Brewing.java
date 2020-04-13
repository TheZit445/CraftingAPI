package ru.thezit445.craftingapi.craft.runnable;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.ItemStack;
import ru.thezit445.craftingapi.craft.ingredient.RecipeIngredient;
import ru.thezit445.craftingapi.craft.recipe.*;

public class Brewing extends SerializableRunnable {

    private BrewingStand stand;
    private int brewTime = 400;
    private int currentBrewTime = brewTime;
    private RecipeIngredient[] potions = new RecipeIngredient[3];
    private ItemStack ingredient;
    private ItemStack[] brewings = new ItemStack[3];
    private ItemStack fuel;
    private BrewingRecipe[] recipes = new BrewingRecipe[3];
    private boolean startNewBrew = true;

    public Brewing(BrewingStand stand, int currentBrewTime) {
        this(stand);
        this.currentBrewTime = currentBrewTime;
    }

    public Brewing(BrewingStand stand) {
        super(stand, Type.BREWING);
        this.stand = stand;
        for (int i = 0; i<3; i++) {
            brewings[i] = stand.getSnapshotInventory().getItem(i);
        }
    }

    @Override
    public void run() {
        if (isCancelled()) return;

        stand = (BrewingStand) stand.getBlock().getState();
        for (int i = 0; i<3; i++){
            brewings[i] = stand.getSnapshotInventory().getItem(i);
        }

        ingredient = stand.getSnapshotInventory().getIngredient();

        if (ingredient == null || (brewings[0] == null && brewings[1] == null && brewings[2] == null)) {
            RunnableManager.removeRunnable(stand.getLocation());
            cancel();
            return;
        }

        a: for (int i = 0; i<3; i++) {
            for (BrewingRecipe recipe : RecipeManager.getBrewingRecipes()) {
                if (brewings[i] == null) break;
                if (!recipe.getIngredient().isSimilar(ingredient)) continue;
                if (recipe.getPotion().isSimilar(brewings[i])) {
                    recipes[i] = recipe;
                    potions[i] = recipe.getPotion();
                    continue a;
                }
            }
            recipes[i] = null;
            potions[i] = null;
        }

        if (recipes[0] == null && recipes[1] == null && recipes[2] == null) {
            RunnableManager.removeRunnable(stand.getLocation());
            cancel();
            return;
        }

        if (stand.getFuelLevel() > 0) {
            if (startNewBrew) {
                stand.setFuelLevel(stand.getFuelLevel() - 1);
                startNewBrew = false;
            }
            currentBrewTime--;
            stand.setBrewingTime(currentBrewTime);
            if (currentBrewTime <= 1) {
                currentBrewTime = brewTime;
                ingredient.setAmount(ingredient.getAmount() - 1);
                stand.getSnapshotInventory().setIngredient(ingredient);
                startNewBrew = true;
                stand.getWorld().playSound(stand.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1, 1);
                for (int i = 0; i<3; i++) {
                    if (potions[i] != null && potions[i].isSimilar(brewings[i])) {
                        stand.getSnapshotInventory().setItem(i, recipes[i].getResult());
                    }
                }
            }
        }
        stand.update();
    }

    @Override
    public String serialize() {
        return "Brewing=[" +
                "locCoords=(" +
                    stand.getLocation().getBlockX() + "," +
                    stand.getLocation().getBlockY() + "," +
                    stand.getLocation().getBlockZ() +
                ");" +
                "locWorld=" + stand.getLocation().getWorld().getName() + ";" +
                "during=" + currentBrewTime +
                "]";
    }


}
