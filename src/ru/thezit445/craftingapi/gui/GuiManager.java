package ru.thezit445.craftingapi.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.thezit445.craftingapi.config.Message;
import ru.thezit445.craftingapi.craft.recipe.AbstractRecipe;
import ru.thezit445.craftingapi.craft.recipe.RecipeManager;
import ru.thezit445.craftingapi.utils.Item;

/**
 * <i>Created on 07.12.2019.</i>
 * Simple GUIs manager.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class GuiManager {
	
	public static final int inventorySize = 54;
	private static final int[] workbenchRecipeCreatorGuiCells = {1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,0,0,0,1,1,0,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,1,1,1};
	private static final int[] furnaceRecipeCreatorGuiCells = {1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,1,1,1};
	private static final int[] brewingRecipeCreatorGuiCells = {1,1,1,1,1,1,1,1,1,1,3,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,1,1,1};
	private static final int[] recipeViewerGuiCells = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5,0,0,0,0,0,0,0,4};
	public static final int recipesOnPage = 28;
	
	private static final Map<Integer, CellItem> items = new HashMap<>();
	
	private GuiManager() {
		
	}
	
	public static Inventory createWorkbenchRecipeCreator(GuiHolder holder) {
		Inventory inventory = Bukkit.createInventory(holder, inventorySize, Message.GUI_TITLE_RECIPE_CREATOR.getMessage());
		Map<String,String> tags = new HashMap<>();
		tags.put(Message.keyTag, holder.getParameters().get(GuiHolder.keySelectionName).toString());
		switch (holder.getGuiType()) {
			case SHAPED_RECIPE_CREATOR: tags.put(Message.typeTag, AbstractRecipe.Type.WORKBENCH_SHAPED.getName()); break;
			case SHAPELESS_RECIPE_CREATOR: tags.put(Message.typeTag, AbstractRecipe.Type.WORKBENCH_SHAPELESS.getName()); break;
			default: break;
		}
		for (int i = 0; i<inventorySize; i++) {
			if (workbenchRecipeCreatorGuiCells[i]==CellItem.EMPTY.id) continue;
			ItemStack item = items.get(workbenchRecipeCreatorGuiCells[i]).builder.getItemWithReplacement(tags);
			inventory.setItem(i, item);
		}
		return inventory;
	}
	
	public static Inventory createFurnaceRecipeCreator(GuiHolder holder) {
		Inventory inventory = Bukkit.createInventory(holder, inventorySize, Message.GUI_TITLE_RECIPE_CREATOR.getMessage());
		Map<String,String> tags = new HashMap<>();
		tags.put(Message.keyTag, holder.getParameters().get(GuiHolder.keySelectionName).toString());
		switch (holder.getGuiType()) {
			case FURNACE_RECIPE_CREATOR: tags.put(Message.typeTag, AbstractRecipe.Type.FURNACE.getName()); break;
			case BLASTING_RECIPE_CREATOR: tags.put(Message.typeTag, AbstractRecipe.Type.BLASTING.getName()); break;
			case SMOKER_RECIPE_CREATOR: tags.put(Message.typeTag, AbstractRecipe.Type.SMOKER.getName()); break;
			default: break;
		}
		for (int i = 0; i<inventorySize; i++) {
			if (furnaceRecipeCreatorGuiCells[i]==CellItem.EMPTY.id) continue;
			ItemStack item = items.get(furnaceRecipeCreatorGuiCells[i]).builder.getItemWithReplacement(tags);
			inventory.setItem(i, item);
		}
		return inventory;
	}

	public static Inventory createBrewingRecipeCreator(GuiHolder holder) {
		Inventory inventory = Bukkit.createInventory(holder, inventorySize, Message.GUI_TITLE_RECIPE_CREATOR.getMessage());
		Map<String,String> tags = new HashMap<>();
		tags.put(Message.keyTag, holder.getParameters().get(GuiHolder.keySelectionName).toString());
		tags.put(Message.typeTag, AbstractRecipe.Type.BREWING.getName());
		for (int i = 0; i<inventorySize; i++) {
			if (brewingRecipeCreatorGuiCells[i]==CellItem.EMPTY.id) continue;
			ItemStack item = items.get(brewingRecipeCreatorGuiCells[i]).builder.getItemWithReplacement(tags);
			inventory.setItem(i, item);
		}
		return inventory;
	}
	
	public static Inventory createRecipesViewer(GuiHolder holder, int page) {
		Inventory inventory = Bukkit.createInventory(holder, inventorySize, Message.GUI_TITLE_RECIPES_VIEWER.getMessage());
		holder.setPage(page);
		for (int i = 0; i<inventorySize; i++) {
			switch (recipeViewerGuiCells[i]) {
				case 0: {
					continue;
				}
				case 4: {
					int lastPage = (RecipeManager.getRecipes().size()-1)/recipesOnPage;
					if (page==lastPage) {
						continue;
					}
					break;
				}
				case 5: {
					if (page==0) {
						continue;
					}
					break;
				}
				default: {
					break;
				}
			}
			ItemStack item = items.get(recipeViewerGuiCells[i]).builder.getItem();
			inventory.setItem(i, item);
		}
		
		main: for (int a = page*recipesOnPage, c = 0, i = 10; c<4; c++, i+=9) {
			for (int j = 0; j<7; j++, a++) {
				if (a<RecipeManager.getRecipes().size()) {
					ItemStack result = RecipeManager.getRecipe(a).getResult();
					inventory.setItem(i+j, result);
				} else {
					break main;
				}
			}
		}
		
		return inventory;
	}
	
	public static void initialize() {
		CellItem.initialize();
	}
	
	public enum CellItem {
		
		EMPTY(0),
		CELL_BACKGROUND(1),
		BUTTON_CREATE(2),
		CELL_FUEL(3),
		BUTTON_NEXT(4),
		BUTTON_PREVIOUS(5);
		
		private Item builder;
		private int id;
		
		CellItem(int id){
			this.id = id;
		}
		
		public ItemStack getItem() {
			return builder.getItem();
		}
		
		public ItemStack getItem(Map<String,String> replacements) {
			return builder.getItemWithReplacement(replacements);
		}
		
		private static void initialize() {
			for (CellItem cell : CellItem.values()) {
				if (cell != EMPTY) {
					cell.builder = Item.valueOf(cell.name());
				}
				items.put(cell.id, cell);
			}
		}
		
	}
	
}
