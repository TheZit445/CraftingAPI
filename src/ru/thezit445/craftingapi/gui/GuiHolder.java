package ru.thezit445.craftingapi.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * <i>Created on 07.12.2019.</i>
 * Inventory holder for GUI.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class GuiHolder implements InventoryHolder {
	
	public static final String keySelectionName = "key";
	public static final String playerSelectionName = "player";
	
	private Inventory inventory;
	private GuiType type;
	
	private int currentPage = 0;
	
	private Map<String, Object> parameters = new HashMap<>();
	
	public GuiHolder(GuiType type) {
		this.type = type;
	}
	
	public void setParameters(Map<String, Object> params) {
		this.parameters = params;
	}
	
	public Map<String, Object> getParameters(){
		return parameters;
	}
	
	public void setParameter(String section, Object parameter) {
		parameters.put(section, parameter);
	}
	
	public void open(Player player) {
		player.openInventory(inventory);
	}
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
	public GuiType getGuiType() {
		return type;
	}
	
	public void setPage(int page) {
		currentPage = page;
	}
	
	public int getPage() {
		return currentPage;
	}
	
	public void createGui() {
		switch (type) {	
			case SHAPELESS_RECIPE_CREATOR:
			case SHAPED_RECIPE_CREATOR: {
				inventory = GuiManager.createWorkbenchRecipeCreator(this);
				break;
			}
			case BLASTING_RECIPE_CREATOR:
			case SMOKER_RECIPE_CREATOR:
			case FURNACE_RECIPE_CREATOR: {
				inventory = GuiManager.createFurnaceRecipeCreator(this);
				break;
			}
			case BREWING_RECIPE_CREATOR: {
				inventory = GuiManager.createBrewingRecipeCreator(this);
				break;
			}
			case RECIPES_BROWSER: {
				inventory = GuiManager.createRecipesViewer(this, 0);
				break;
			}
		}
	}
	
	public enum GuiType {
		
		SHAPED_RECIPE_CREATOR,
		SHAPELESS_RECIPE_CREATOR,
		FURNACE_RECIPE_CREATOR,
		BLASTING_RECIPE_CREATOR,
		SMOKER_RECIPE_CREATOR,
		BREWING_RECIPE_CREATOR,
		RECIPES_BROWSER;
		
	}
	
}
