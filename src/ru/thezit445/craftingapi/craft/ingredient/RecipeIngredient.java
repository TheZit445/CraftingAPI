package ru.thezit445.craftingapi.craft.ingredient;

import org.bukkit.inventory.ItemStack;

import ru.thezit445.nbtreflectionapi.nbt.NBTTagCompound;
import ru.thezit445.nbtreflectionapi.nms.NMSItem;


/**
 * <i>Created on 07.12.2019.</i>
 * Abstract recipe ingredient.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public abstract class RecipeIngredient {
	
	protected Type type;
	
	public static RecipeIngredient create(ItemStack item) {
		if (item==null) return null;
		NMSItem nmsItem = new NMSItem(item);
		NBTTagCompound nbt = nmsItem.getNBT();
		String tagSection = "tag";
		NBTTagCompound nbtSubTag = nbt.get(tagSection);
		if (nbtSubTag.asNMS() != null) {
			return new ExactRecipeIngredient(item);
		} else {
			return new MaterialRecipeIngredient(item.getType());
		}
	}
	
	public RecipeIngredient(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public abstract ItemStack getItem();
	
	public abstract boolean isSimilar(ItemStack item);
	
	public enum Type {
		EXACT,
		MATERIAL;
	}
	
}
