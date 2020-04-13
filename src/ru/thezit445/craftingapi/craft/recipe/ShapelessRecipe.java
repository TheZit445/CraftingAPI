package ru.thezit445.craftingapi.craft.recipe;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import ru.thezit445.craftingapi.config.PluginPermission;
import ru.thezit445.craftingapi.craft.ingredient.RecipeIngredient;
import ru.thezit445.craftingapi.craft.interfaces.IWorkbenchRecipe;
import ru.thezit445.craftingapi.utils.JsonManager;

/**
 * <i>Created on 07.12.2019.</i>
 * Shapeless recipe.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class ShapelessRecipe extends AbstractRecipe implements IWorkbenchRecipe {
	
	private List<RecipeIngredient> items = new ArrayList<>();
	
	public ShapelessRecipe(ItemStack result, NamespacedKey key) {
		super(result, key, Type.WORKBENCH_SHAPELESS);
	}
	
	public ShapelessRecipe(String json) {
		super(null, null, Type.WORKBENCH_SHAPELESS);
		fromJson(json);
	}
	
	public void addItem(RecipeIngredient ingredient) {
		items.add(ingredient);
	}
	
	public List<RecipeIngredient> getItems(){
		return items;
	}
	
	@Override
	public boolean isComplete(ItemStack[] workbenchMatrix) {
		if (RecipeManager.matrixIsEmpty(workbenchMatrix)) return false;
		List<RecipeIngredient> temp = new ArrayList<>(items);
		if (workbenchMatrix.length<temp.size()) return false;
		int nullItemsCount = workbenchMatrix.length-temp.size();
		int nullItemCounter = 0;
		for (ItemStack item : workbenchMatrix) {
			if (item==null) {
				nullItemCounter++;
				continue;
			}
			for (RecipeIngredient ingredient : new ArrayList<>(temp)) {
				if (ingredient.isSimilar(item)) {
					temp.remove(ingredient);
					break;
				}
			}
		}
		return temp.isEmpty() && nullItemCounter==nullItemsCount;
	}
	
	@Override
	public void register() {
		RecipeManager.addRecipe(this);
		org.bukkit.inventory.ShapelessRecipe recipe = new org.bukkit.inventory.ShapelessRecipe(key, getResult());
		recipe.setGroup(key.getKey());
		for (RecipeIngredient ingredient : items) {
			recipe.addIngredient(ingredient.getItem().getType());
		}
		Bukkit.addRecipe(recipe);
		PluginPermission.addNewUsePermission(key);
	}
	
	@Override
	public String toJson() {
		JsonManager<ShapelessRecipe> jsonManager = new JsonManager<>(ShapelessRecipe.class);
		String json = jsonManager.toJsonString(this);
		return json;
	}

	@Override
	public void fromJson(String json) {
		JsonManager<ShapelessRecipe> jsonManager = new JsonManager<>(ShapelessRecipe.class);
		ShapelessRecipe temp = jsonManager.getInstanceFromJson(json);
		this.key = temp.key;
		this.result = temp.result;
		this.items = temp.items;
		this.usePermission = temp.usePermission;
	}
	
}
