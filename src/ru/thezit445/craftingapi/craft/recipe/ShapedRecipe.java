package ru.thezit445.craftingapi.craft.recipe;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import ru.thezit445.craftingapi.config.PluginPermission;
import ru.thezit445.craftingapi.craft.ingredient.ExactRecipeIngredient;
import ru.thezit445.craftingapi.craft.ingredient.RecipeIngredient;
import ru.thezit445.craftingapi.craft.interfaces.IWorkbenchRecipe;
import ru.thezit445.craftingapi.utils.JsonManager;
import ru.thezit445.nbtreflectionapi.nms.NMSItem;

/**
 * <i>Created on 07.12.2019.</i>
 * Shaped recipe.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class ShapedRecipe extends AbstractRecipe implements IWorkbenchRecipe {
	
	private static final char emptyKey = 'z';
	
	private String[] shape;
	private Map<Character, RecipeIngredient> items = new HashMap<>();
	
	public ShapedRecipe(String json) {
		super(null, null, Type.WORKBENCH_SHAPED);
		fromJson(json);
	}
	
	public ShapedRecipe(ItemStack result, NamespacedKey key) {
		super(result, key, Type.WORKBENCH_SHAPED);
	}
	
	public void setShape(String[] shape) {
		this.shape = shape;
		for (int i = 0; i<shape.length; i++) {
			this.shape[i] = this.shape[i].replace(" ", "z");
		}
		if (!items.containsKey(emptyKey)) {
			items.put(emptyKey, null);
		}
	}
	
	public String[] getShape() {
		return shape;
	}
	
	public int getShapeColumms() {
		int columms = 1;
		for (String line : shape) {
			if (line.length()>columms) columms = line.length();
		}
		return columms;
	}
	
	public int getShapeRows() {
		return shape.length;
	}
	
	public void addItem(KeyCharacter key, RecipeIngredient ingredient) {
		items.put(key.key, ingredient);
	}
	
	public Map<Character, RecipeIngredient> getItems(){
		return items;
	}
	
	public boolean isComplete(ItemStack[] workbenchMatrix) {
		if (RecipeManager.matrixIsEmpty(workbenchMatrix)) return false;
		int matrixSize = (int) Math.sqrt(workbenchMatrix.length);
		if (matrixSize<getShapeRows() || matrixSize<getShapeColumms()) return false;
		int baseX = 2, baseY = 2;
		for (int y = 0; y<matrixSize; y++) {
			for (int x = 0; x<matrixSize; x++) {
				if (workbenchMatrix[x+y*matrixSize]==null) continue;
				if (x<baseX) baseX = x;
				if (y<baseY) baseY = y;
			}
		}
		
		int deltaX = getShapeColumms()+baseX;
		int deltaY = getShapeRows()+baseY;
		for (int y = baseY, shapeY = 0; y<deltaY; y++, shapeY++) {
			for (int x = baseX, shapeX = 0; x<deltaX; x++, shapeX++) {
				if (x>=matrixSize || y>=matrixSize) return false;
				ItemStack item = workbenchMatrix[x+y*matrixSize];
				char currentlyKey = shape[shapeY].charAt(shapeX);
				RecipeIngredient ingredient = items.get(currentlyKey);

				if (item==null) {
					if (ingredient==null) {
						continue;
					} else {
						return false;
					}
				} else {
					if (ingredient==null) return false;
					if (ingredient.isSimilar(item)) {
						continue;
					} else {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	@Override
	public void register() {
		RecipeManager.addRecipe(this);
		org.bukkit.inventory.ShapedRecipe recipe = new org.bukkit.inventory.ShapedRecipe(key, getResult());
		recipe.setGroup(key.getKey());
		String[] vanilaShape = new String[getShapeRows()];
		for (int i = 0; i<vanilaShape.length; i++) {
			if(i<shape.length) {
				vanilaShape[i] = shape[i];
				continue;
			}
			vanilaShape[i] = "";
			for (int j = 0; j<getShapeColumms(); j++) {
				vanilaShape[i]+=" ";
			}
		}
		recipe.shape(vanilaShape);
		for (char key : items.keySet()) {
			if (key!='z') recipe.setIngredient(key, items.get(key).getItem().getType());
		}
		Bukkit.addRecipe(recipe);
		PluginPermission.addNewUsePermission(key);
	}
	
	@Override
	public String toJson() {
		JsonManager<ShapedRecipe> jsonManager = new JsonManager<>(ShapedRecipe.class);
		String json = jsonManager.toJsonString(this);
		return json;
	}

	@Override
	public void fromJson(String json) {
		JsonManager<ShapedRecipe> jsonManager = new JsonManager<>(ShapedRecipe.class);
		ShapedRecipe temp = jsonManager.getInstanceFromJson(json);
		this.key = temp.key;
		this.result = temp.result;
		this.shape = temp.shape;
		this.items = temp.items;
		this.usePermission = temp.usePermission;
	}
	
	public enum KeyCharacter {
		A('a'),
		B('b'),
		C('c'),
		D('d'),
		E('e'),
		F('f'),
		G('g'),
		H('h'),
		I('i');
		
		private char key;
		
		KeyCharacter(char key) {
			this.key = key;
		}
		
		public char getKey() {
			return key;
		}
		
	}
	
}
