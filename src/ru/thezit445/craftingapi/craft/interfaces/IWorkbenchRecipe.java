package ru.thezit445.craftingapi.craft.interfaces;

import org.bukkit.inventory.ItemStack;

public interface IWorkbenchRecipe {

    public abstract boolean isComplete(ItemStack[] workbenchMatrix);

}
