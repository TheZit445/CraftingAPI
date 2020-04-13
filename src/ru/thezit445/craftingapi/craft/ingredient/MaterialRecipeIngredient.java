package ru.thezit445.craftingapi.craft.ingredient;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * <i>Created on 07.12.2019.</i>
 * Material ingredient.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class MaterialRecipeIngredient extends RecipeIngredient {

	private Material material;
	
	public MaterialRecipeIngredient(Material material) {
		super(Type.MATERIAL);
		this.material = material;
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(material);
	}

	@Override
	public boolean isSimilar(ItemStack item) {
		return (material == item.getType());
	}
	
}
