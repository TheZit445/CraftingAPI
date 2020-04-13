package ru.thezit445.craftingapi.craft.ingredient;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import ru.thezit445.nbtreflectionapi.nbt.NBTTagCompound;
import ru.thezit445.nbtreflectionapi.nms.NMSItem;
import ru.thezit445.nbtreflectionapi.util.NBTUtils;

/**
 * <i>Created on 07.12.2019.</i>
 * Exact ingredient.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class ExactRecipeIngredient extends RecipeIngredient {
	
	private String nbtItemFormat;
	
	public ExactRecipeIngredient(ItemStack item) {
		super(Type.EXACT);
		NMSItem nmsItem = new NMSItem(item);
		NBTTagCompound nbtTag = nmsItem.getNBT();
		nbtItemFormat = nbtTag.asString();
	}
	
	public ExactRecipeIngredient(String nbtItemFormat) {
		super(Type.EXACT);
		this.nbtItemFormat = nbtItemFormat;
	}

	@Override
	public ItemStack getItem() {
		NBTTagCompound nbtTag = NBTUtils.parse(nbtItemFormat);
		NMSItem nmsItem = new NMSItem(nbtTag);
		ItemStack item = nmsItem.asBukkit();
		return item;
	}

	@Override
	public boolean isSimilar(ItemStack item) {
		NMSItem nmsItem = new NMSItem(item);
		NBTTagCompound itemNbtTag = nmsItem.getNBT();
		String tagSection = "tag";
		NBTTagCompound itemSubNbtTag = itemNbtTag.get(tagSection);
		if (itemSubNbtTag.asNMS() == null) return false;
		NBTTagCompound ingredientNbtTag = NBTUtils.parse(nbtItemFormat);
		ItemStack ingredient = new NMSItem(ingredientNbtTag).asBukkit();
		NBTTagCompound ingredientSubNbtTag = ingredientNbtTag.get(tagSection);
		boolean tagsAreEqual = itemSubNbtTag.asNMS().equals(ingredientSubNbtTag.asNMS());
		boolean materialsAreEqual = item.getType() == ingredient.getType();
		return tagsAreEqual && materialsAreEqual;
	}
	
}
