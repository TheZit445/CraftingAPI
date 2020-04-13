package ru.thezit445.craftingapi.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

import ru.thezit445.craftingapi.craft.interfaces.IWorkbenchRecipe;
import ru.thezit445.craftingapi.craft.recipe.AbstractRecipe;

/**
 * <i>Created on 07.12.2019.</i>
 * Custom event for developing.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class PrepareCustomCraftEvent extends Event {
		
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	    
	private IWorkbenchRecipe recipe;
	private Inventory inventory;
	    
	public PrepareCustomCraftEvent(IWorkbenchRecipe recipe, Inventory inventory) {
		this.recipe = recipe;
		this.inventory = inventory;
	}
		
	public Inventory getInventory() {
		return inventory;
	}
		
	public IWorkbenchRecipe getRecipe() {
		return recipe;
	}
		
	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

}
