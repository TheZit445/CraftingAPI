package ru.thezit445.craftingapi.eventhandlers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.thezit445.craftingapi.CraftingAPI;
import ru.thezit445.craftingapi.config.Message;
import ru.thezit445.craftingapi.craft.recipe.AbstractRecipe;
import ru.thezit445.craftingapi.craft.recipe.BrewingRecipe;
import ru.thezit445.craftingapi.craft.recipe.RecipeManager;
import ru.thezit445.craftingapi.gui.GuiHolder;
import ru.thezit445.craftingapi.gui.GuiManager;
import ru.thezit445.craftingapi.gui.GuiHolder.GuiType;

/**
 * <i>Created on 07.12.2019.</i>
 * Listener for click in inventory events.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class ClickGuiListener implements Listener {
	
	public static final List<Integer> workbenchMatrix = Arrays.asList(10,11,12,19,20,21,28,29,30);
	public static final List<Integer> buttonCells = Arrays.asList(48,49,50);
	public static final int workbenckResultSlot = 24;
	
	@EventHandler
	public void onCloseGui(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() instanceof GuiHolder) {
			if (CraftingAPI.playersWithOpenedGui.contains(event.getPlayer().getUniqueId().toString())) {
				CraftingAPI.playersWithOpenedGui.remove(event.getPlayer().getUniqueId().toString());
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getInventory().getHolder() instanceof GuiHolder) {
			if (event.getClick().isShiftClick()) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void workbechRecipeCreatorHandle(InventoryClickEvent event) {
		
		Inventory inv = event.getClickedInventory();
		if (inv == null) return;
		if (!(inv.getHolder() instanceof GuiHolder)) return;
		GuiHolder holder = (GuiHolder) inv.getHolder();
		if (holder.getGuiType()!=GuiType.SHAPED_RECIPE_CREATOR && holder.getGuiType()!=GuiType.SHAPELESS_RECIPE_CREATOR) return;
		GuiType type = holder.getGuiType();
		Map<String, Object> params = holder.getParameters();

		int slot = event.getSlot();
		if (!workbenchMatrix.contains(slot) && slot!=workbenckResultSlot) event.setCancelled(true);
		
		ItemStack result = inv.getItem(workbenckResultSlot);
		ItemStack[] matrix = new ItemStack[9];
		int i = 0;
		for (int j : workbenchMatrix) {
			matrix[i] = inv.getItem(j);
			i++;
		}
		
		NamespacedKey key = (NamespacedKey) params.get(GuiHolder.keySelectionName);
		Player player = (Player) event.getWhoClicked();
		
		if (buttonCells.contains(slot)) {
			switch (type) {
				case SHAPED_RECIPE_CREATOR: RecipeManager.createShapedRecipe(matrix, result, key, player); break;
				case SHAPELESS_RECIPE_CREATOR: RecipeManager.createShapelessRecipe(matrix, result, key, player); break;
				default: break;
			}
		}
	}
	
	public static final int furnaceResultSlot = 24;
	public static final int furnaceIngredientSlot = 11;
	
	@EventHandler
	public void furnaceRecipeCreatorHandle(InventoryClickEvent event) {
		
		Inventory inv = event.getClickedInventory();
		if (inv == null) return;
		if (!(inv.getHolder() instanceof GuiHolder)) return;
		GuiHolder holder = (GuiHolder) inv.getHolder();
		if (holder.getGuiType()!=GuiType.FURNACE_RECIPE_CREATOR
			&& holder.getGuiType()!=GuiType.SMOKER_RECIPE_CREATOR
			&& holder.getGuiType()!=GuiType.BLASTING_RECIPE_CREATOR) return;
		Map<String, Object> params = holder.getParameters();

		int slot = event.getSlot();
		if (slot!=furnaceResultSlot && slot!=furnaceIngredientSlot) event.setCancelled(true);
		
		ItemStack result = inv.getItem(furnaceResultSlot);
		ItemStack ingredient = inv.getItem(furnaceIngredientSlot);
		
		NamespacedKey key = (NamespacedKey) params.get(GuiHolder.keySelectionName);
		Player player = (Player) event.getWhoClicked();

		AbstractRecipe.Type type;
		switch (holder.getGuiType()) {
			case BLASTING_RECIPE_CREATOR: type = AbstractRecipe.Type.BLASTING; break;
			case SMOKER_RECIPE_CREATOR: type = AbstractRecipe.Type.SMOKER; break;
			default: type = AbstractRecipe.Type.FURNACE;
		}

		if (buttonCells.contains(slot)) RecipeManager.createFurnaceRecipe(ingredient, result, key, player, type);
	}

	public static final int brewingResultSlot = 32;
	public static final int brewingPotionSlot = 30;
	public static final int brewingIngredientSlot = 13;

	@EventHandler
	public void brewingRecipeCreatorHandle(InventoryClickEvent event) {

		Inventory inv = event.getClickedInventory();
		if (inv == null) return;
		if (!(inv.getHolder() instanceof GuiHolder)) return;
		GuiHolder holder = (GuiHolder) inv.getHolder();
		if (holder.getGuiType()!=GuiType.BREWING_RECIPE_CREATOR) return;
		Map<String, Object> params = holder.getParameters();

		int slot = event.getSlot();
		if (slot!=brewingResultSlot && slot!=brewingIngredientSlot && slot!=brewingPotionSlot) event.setCancelled(true);

		if (slot==brewingPotionSlot || slot==brewingResultSlot) {
			if ((event.getCursor()!=null && event.getCursor().getType()!= Material.AIR)) {
				if (!event.getCursor().getType().name().toLowerCase().endsWith("potion")) {
					System.out.println(event.getCursor().getType().name());
					event.setCancelled(true);
					event.getWhoClicked().sendMessage(Message.GUI_RECIPE_CREATE_BREWING_NOT_A_POTION.getMessage());
					return;
				}
			}
		}

		ItemStack result = inv.getItem(brewingResultSlot);
		ItemStack ingredient = inv.getItem(brewingIngredientSlot);
		ItemStack potion = inv.getItem(brewingPotionSlot);

		NamespacedKey key = (NamespacedKey) params.get(GuiHolder.keySelectionName);
		Player player = (Player) event.getWhoClicked();

		if (buttonCells.contains(slot)) RecipeManager.createBrewingRecipe(ingredient, potion, result, key, player);
	}
	
	private final int buttonNextSlot = 53;
	private final int buttonPreviousSlot = 45;
	
	private final int lastRecipeInLineOne = 16;
	private final int firstRecipeInLineOne = 10;
	private final int lastRecipeInLineTwo = 25;
	private final int firstRecipeInLineTwo = 19;
	private final int lastRecipeInLineThree = 34;
	private final int firstRecipeInLineThree = 28;
	private final int lastRecipeInLineFour = 43;
	private final int firstRecipeInLineFour = 37;
	
	private final int constTechVar = 2;
	
	@EventHandler
	public void recipeViewerHandler(InventoryClickEvent event) {
		
		Inventory inv = event.getClickedInventory();
		if (inv == null) return;
		if (!(inv.getHolder() instanceof GuiHolder)) return;
		GuiHolder holder = (GuiHolder) inv.getHolder();
		if (holder.getGuiType()!=GuiType.RECIPES_BROWSER) return;
		
		event.setCancelled(true);
		
		int page = holder.getPage();
		int slot = event.getSlot();
		ItemStack clickedStack = inv.getItem(slot);
		Player player = (Player) event.getWhoClicked();
		
		if (clickedStack == null) return;
		
		if (slot == buttonNextSlot) {
			page++;
			Inventory nextInv = GuiManager.createRecipesViewer(holder, page);
			player.openInventory(nextInv);
			if (!CraftingAPI.playersWithOpenedGui.contains(player.getUniqueId().toString()))
				CraftingAPI.playersWithOpenedGui.add(player.getUniqueId().toString());
		}
		
		if (slot == buttonPreviousSlot) {
			page--;
			Inventory nextInv = GuiManager.createRecipesViewer(holder, page);
			player.openInventory(nextInv);
			if (!CraftingAPI.playersWithOpenedGui.contains(player.getUniqueId().toString()))
				CraftingAPI.playersWithOpenedGui.add(player.getUniqueId().toString());
		}
		
		int line = 3;
		if (slot<=lastRecipeInLineFour) {
			if (slot<=lastRecipeInLineThree) {
				line = 2;
				if (slot<=lastRecipeInLineTwo) {
					line = 1;
					if (slot<=lastRecipeInLineOne) {
						line = 0;
					}
				}
			}
		} else {
			return;
		}
		
		switch (line) {
			case 0: {
				if (slot<firstRecipeInLineOne) return;
				break;
			}
			case 1: {
				if (slot<firstRecipeInLineTwo) return;
				break;
			}
			case 2: {
				if (slot<firstRecipeInLineThree) return;
				break;
			}
			case 3: {
				if (slot<firstRecipeInLineFour) return;
				break;
			}
			default: break;
		}
		int id = slot-firstRecipeInLineOne-line*constTechVar;
		AbstractRecipe recipe = RecipeManager.getRecipe(id+page*GuiManager.recipesOnPage);
		RecipeManager.openRecipeEditor(player, recipe.getKey().getKey());
	}
	
}
