package ru.thezit445.craftingapi.craft.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.thezit445.craftingapi.CraftingAPI;
import ru.thezit445.craftingapi.craft.ingredient.RecipeIngredient;
import ru.thezit445.craftingapi.craft.recipe.BrewingRecipe;
import ru.thezit445.craftingapi.craft.recipe.RecipeManager;
import ru.thezit445.craftingapi.craft.runnable.Brewing;
import ru.thezit445.craftingapi.craft.runnable.RunnableManager;

import java.util.ArrayList;
import java.util.List;

public class BrewingHandler implements Listener {

    @EventHandler
    public void onItemSet(InventoryMoveItemEvent event) {
        Inventory destination = event.getDestination();
        InventoryHolder destinationHolder = destination.getHolder();
        if (!(destinationHolder instanceof BrewingStand)) return;
        BrewingStand stand = (BrewingStand) destinationHolder;
        Bukkit.getScheduler().runTaskLater(CraftingAPI.getInstance(), () -> {

            ItemStack ingredient = stand.getSnapshotInventory().getIngredient();
            ItemStack postions[] = new ItemStack[3];
            for (int i = 0; i<3; i++) {
                postions[i] = stand.getSnapshotInventory().getItem(i);
            }
            if (ingredient == null) return;
            for (BrewingRecipe recipe : RecipeManager.getBrewingRecipes()) {
                RecipeIngredient recipeIngredient = recipe.getIngredient();
                if (recipeIngredient.isSimilar(ingredient)) {
                    if (RunnableManager.isRunned(stand.getLocation())) return;
                    Brewing brewing = new Brewing(stand);
                    RunnableManager.addRunnable(brewing.getLocation(), brewing);
                    brewing.runTaskTimer(CraftingAPI.getInstance(), 1, 1);
                }
            }
        }, 1);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory openedInventory = event.getInventory();
        if (!(clickedInventory instanceof BrewerInventory)) {
            if (!(openedInventory instanceof BrewerInventory)) {
                return;
            }
        }
        InventoryAction action = event.getAction();
        BrewingStand stand;
        if (clickedInventory instanceof BrewerInventory) {
            stand = ((BrewerInventory) clickedInventory).getHolder();
        } else {
            stand = ((BrewerInventory) openedInventory).getHolder();
        }
        boolean isIngredient = false;
        if (event.getClick().isShiftClick()) {
            if (!(event.getClickedInventory() instanceof BrewerInventory)) {
                ItemStack stack = event.getCurrentItem();
                for (BrewingRecipe recipe : RecipeManager.getBrewingRecipes()) {
                    RecipeIngredient ingredient = recipe.getIngredient();
                    if (ingredient.isSimilar(stack)) {
                        ItemStack brewing = stand.getInventory().getIngredient();
                        if (brewing == null || (ingredient.isSimilar(brewing) && brewing.getAmount() < brewing.getMaxStackSize())) {
                            if (brewing == null) {
                                stand.getInventory().setIngredient(stack);
                                stack.setAmount(0);
                            } else {
                                int moveCount = brewing.getMaxStackSize() - brewing.getAmount();
                                if (stack.getAmount() < moveCount) {
                                    brewing.setAmount(brewing.getAmount() + stack.getAmount());
                                    stack.setAmount(0);
                                } else {
                                    brewing.setAmount(brewing.getMaxStackSize());
                                    stack.setAmount(stack.getAmount() - moveCount);
                                }
                            }
                            isIngredient = true;
                        }
                    }
                }
            }
        } else {
            if (event.getClickedInventory() instanceof BrewerInventory) {
                int slot = event.getSlot();
                if (slot == 3) {
                    for (BrewingRecipe recipe : RecipeManager.getBrewingRecipes()) {
                        if (recipe.getIngredient().isSimilar(event.getCursor())) {
                            event.setCancelled(true);
                            ItemStack inSlot = event.getClickedInventory().getItem(slot);
                            ItemStack inCursor = event.getCursor();
                            if (inSlot != null && inSlot.getType() != Material.AIR) {
                                int moveCount = inSlot.getMaxStackSize() - inSlot.getAmount();
                                if (inCursor.getAmount() < moveCount) {
                                    inSlot.setAmount(inSlot.getAmount() + inCursor.getAmount());
                                    inCursor.setAmount(0);
                                } else {
                                    inSlot.setAmount(inSlot.getMaxStackSize());
                                    inCursor.setAmount(inCursor.getAmount() - moveCount);
                                }
                            } else {
                                event.getView().setCursor(new ItemStack(Material.AIR));
                                event.getClickedInventory().setItem(slot, inCursor);
                            }
                            isIngredient = true;
                            break;
                        }
                    }
                }
            }
        }
        if (isIngredient || action == InventoryAction.MOVE_TO_OTHER_INVENTORY
                || action == InventoryAction.PLACE_ALL
                || action == InventoryAction.PLACE_ONE
                || action == InventoryAction.PLACE_SOME
                || action == InventoryAction.HOTBAR_MOVE_AND_READD
                || action == InventoryAction.HOTBAR_SWAP
                || action == InventoryAction.SWAP_WITH_CURSOR
        ) {
            boolean finalIsIngredient = isIngredient;
            List<HumanEntity> viewers = new ArrayList<>(event.getViewers());
            Bukkit.getScheduler().runTaskLater(CraftingAPI.getInstance(), () -> {
                if (finalIsIngredient) viewers.forEach(p -> ((Player) p).updateInventory());
                ItemStack standIngredient = stand.getInventory().getIngredient();
                if (standIngredient == null) return;
                for (BrewingRecipe recipe : RecipeManager.getBrewingRecipes()) {
                    RecipeIngredient ingredient = recipe.getIngredient();
                    if (ingredient.isSimilar(standIngredient)) {
                        if (RunnableManager.isRunned(stand.getLocation())) return;
                        Brewing brewingRunnable = new Brewing(stand);
                        RunnableManager.addRunnable(stand.getLocation(), brewingRunnable);
                        brewingRunnable.runTaskTimer(CraftingAPI.getInstance(), 1, 1);
                    }
                }
            }, 1);

        }

    }

}
