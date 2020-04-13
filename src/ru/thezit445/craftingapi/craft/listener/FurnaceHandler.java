package ru.thezit445.craftingapi.craft.listener;

import org.bukkit.Bukkit;
import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Furnace;
import org.bukkit.block.Smoker;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.thezit445.craftingapi.CraftingAPI;
import ru.thezit445.craftingapi.craft.ingredient.RecipeIngredient;
import ru.thezit445.craftingapi.craft.interfaces.IFurnaceRecipe;
import ru.thezit445.craftingapi.craft.recipe.*;
import ru.thezit445.craftingapi.craft.runnable.FurnaceSmelting;
import ru.thezit445.craftingapi.craft.runnable.RunnableManager;

import java.util.ArrayList;
import java.util.List;

public class FurnaceHandler implements Listener {

    @EventHandler
    public void onItemSet(InventoryMoveItemEvent event) {
        Inventory destination = event.getDestination();
        InventoryHolder destinationHolder = destination.getHolder();
        if (!(destinationHolder instanceof Furnace)) return;
        Furnace furnace = (Furnace) destinationHolder;
        Bukkit.getScheduler().runTaskLater(CraftingAPI.getInstance(), () -> {
            ItemStack smelting = furnace.getInventory().getSmelting();
            if (smelting == null) return;
            for (IFurnaceRecipe recipe : RecipeManager.getFurnaceRecipes()) {
                if ((furnace instanceof BlastFurnace) && ((AbstractRecipe) recipe).getType() != AbstractRecipe.Type.BLASTING) continue;
                if ((furnace instanceof Smoker) && ((AbstractRecipe) recipe).getType() != AbstractRecipe.Type.SMOKER) continue;
                RecipeIngredient ingredient;
                switch (((AbstractRecipe) recipe).getType()) {
                    case SMOKER: ingredient = ((SmokerRecipe) recipe).getIngredient(); break;
                    case BLASTING: ingredient = ((BlastingRecipe) recipe).getIngredient(); break;
                    default: ingredient = ((FurnaceRecipe) recipe).getIngredient(); break;
                }
                if (ingredient.isSimilar(smelting)) {
                    if (RunnableManager.isRunned(furnace.getLocation())) return;
                    FurnaceSmelting furnaceSmelting = new FurnaceSmelting(furnace, recipe);
                    RunnableManager.addRunnable(furnace.getLocation(), furnaceSmelting);
                    furnaceSmelting.runTaskTimer(CraftingAPI.getInstance(), 1, 1);
                }
            }
        }, 1);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory openedInventory = event.getInventory();
        if (!(clickedInventory instanceof FurnaceInventory)) {
            if (!(openedInventory instanceof FurnaceInventory)) {
                return;
            }
        }
        InventoryAction action = event.getAction();
        Furnace furnace;
        if (clickedInventory instanceof Furnace) {
            furnace = ((FurnaceInventory) clickedInventory).getHolder();
        } else {
            furnace = ((FurnaceInventory) openedInventory).getHolder();
        }
        boolean isIngredient = false;
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            if (!(event.getClickedInventory() instanceof FurnaceInventory)) {
                ItemStack stack = event.getCurrentItem();
                for (IFurnaceRecipe recipe : RecipeManager.getFurnaceRecipes()) {
                    if ((furnace instanceof BlastFurnace) && ((AbstractRecipe) recipe).getType() != AbstractRecipe.Type.BLASTING) continue;
                    if ((furnace instanceof Smoker) && ((AbstractRecipe) recipe).getType() != AbstractRecipe.Type.SMOKER) continue;
                    RecipeIngredient ingredient;
                    switch (((AbstractRecipe) recipe).getType()) {
                        case SMOKER: ingredient = ((SmokerRecipe) recipe).getIngredient(); break;
                        case BLASTING: ingredient = ((BlastingRecipe) recipe).getIngredient(); break;
                        default: ingredient = ((FurnaceRecipe) recipe).getIngredient(); break;
                    }
                    if (ingredient.isSimilar(stack)) {
                        ItemStack smelting = furnace.getInventory().getSmelting();
                        if (smelting == null || (ingredient.isSimilar(smelting) && smelting.getAmount() < smelting.getMaxStackSize())) {
                            if (smelting == null) {
                                furnace.getInventory().setSmelting(stack);
                                stack.setAmount(0);
                            } else {
                                int moveCount = smelting.getMaxStackSize() - smelting.getAmount();
                                if (stack.getAmount() < moveCount) {
                                    smelting.setAmount(smelting.getAmount() + stack.getAmount());
                                    stack.setAmount(0);
                                } else {
                                    smelting.setAmount(smelting.getMaxStackSize());
                                    stack.setAmount(stack.getAmount() - moveCount);
                                }
                            }
                            isIngredient = true;
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
                ItemStack smelting = furnace.getInventory().getSmelting();
                if (smelting == null) return;
                for (IFurnaceRecipe recipe : RecipeManager.getFurnaceRecipes()) {
                    if ((furnace instanceof BlastFurnace) && ((AbstractRecipe) recipe).getType() != AbstractRecipe.Type.BLASTING) continue;
                    if ((furnace instanceof Smoker) && ((AbstractRecipe) recipe).getType() != AbstractRecipe.Type.SMOKER) continue;
                    RecipeIngredient ingredient;
                    switch (((AbstractRecipe) recipe).getType()) {
                        case SMOKER: ingredient = ((SmokerRecipe) recipe).getIngredient(); break;
                        case BLASTING: ingredient = ((BlastingRecipe) recipe).getIngredient(); break;
                        default: ingredient = ((FurnaceRecipe) recipe).getIngredient(); break;
                    }
                    if (ingredient.isSimilar(smelting)) {
                        if (RunnableManager.isRunned(furnace.getLocation())) return;
                        FurnaceSmelting furnaceSmelting = new FurnaceSmelting(furnace, recipe);
                        RunnableManager.addRunnable(furnace.getLocation(), furnaceSmelting);
                        furnaceSmelting.runTaskTimer(CraftingAPI.getInstance(), 1, 1);
                    }
                }
            }, 1);

        }

    }

}
