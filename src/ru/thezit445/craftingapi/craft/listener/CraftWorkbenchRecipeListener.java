package ru.thezit445.craftingapi.craft.listener;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import ru.thezit445.craftingapi.config.Message;
import ru.thezit445.craftingapi.config.PluginPermission;
import ru.thezit445.craftingapi.craft.interfaces.IWorkbenchRecipe;
import ru.thezit445.craftingapi.craft.recipe.AbstractRecipe;
import ru.thezit445.craftingapi.craft.recipe.RecipeManager;
import ru.thezit445.craftingapi.events.CustomCraftEvent;
import ru.thezit445.craftingapi.events.PrepareCustomCraftEvent;

/**
 * <i>Created on 07.12.2019.</i>
 * Listener of craft and prepaire craft events.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class CraftWorkbenchRecipeListener implements Listener {
	
	@EventHandler
	public void prepareCraftEventHandle(PrepareItemCraftEvent event) {
		CraftingInventory inventory = event.getInventory(); 
		ItemStack[] matrix = inventory.getMatrix();
		Recipe vanilaRecipe = event.getRecipe();
		if (vanilaRecipe==null) return;
		NamespacedKey key = ((Keyed) vanilaRecipe).getKey();
		if (RecipeManager.exist(key)) {
			AbstractRecipe recipe = RecipeManager.getRecipe(key);
			if (((IWorkbenchRecipe) recipe).isComplete(matrix)) {
				inventory.setResult(recipe.getResult());
				PrepareCustomCraftEvent newEvent = new PrepareCustomCraftEvent(((IWorkbenchRecipe) recipe), inventory);
				Bukkit.getPluginManager().callEvent(newEvent);
			} else {
				inventory.setResult(null);
			}
			return;
		}
		
	}
	
	@EventHandler
	public void craftEventHandle(CraftItemEvent event) {
		
		CraftingInventory inventory = event.getInventory(); 
		ItemStack[] matrix = inventory.getMatrix();
		Recipe vanilaRecipe = event.getRecipe();
		NamespacedKey key = ((Keyed) vanilaRecipe).getKey();
		AbstractRecipe recipe = RecipeManager.getRecipe(key);
		
		Player player = (Player) event.getWhoClicked();
		
		if (RecipeManager.exist(key)) {
			if (recipe.isUsedPermission()) {
				if (!player.hasPermission(PluginPermission.CRAFTINGAPI_RECIPE_USE.getPermission(key))) {
					event.setCancelled(true);
					player.sendMessage(Message.PERMISSION_DENIED.getMessage());
					return;
				}
			}
			
			if (((IWorkbenchRecipe) recipe).isComplete(matrix)) {
				CustomCraftEvent newEvent = new CustomCraftEvent(((IWorkbenchRecipe) recipe), inventory);
				Bukkit.getPluginManager().callEvent(newEvent);
				if (newEvent.isCancelled()) event.setCancelled(true);
				return;
			}
		}
	}


	
}
