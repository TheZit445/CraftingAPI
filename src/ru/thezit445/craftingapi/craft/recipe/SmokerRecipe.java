package ru.thezit445.craftingapi.craft.recipe;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import ru.thezit445.craftingapi.craft.ingredient.RecipeIngredient;
import ru.thezit445.craftingapi.craft.interfaces.IFurnaceRecipe;
import ru.thezit445.craftingapi.craft.interfaces.IRunnableRecipe;
import ru.thezit445.craftingapi.utils.JsonManager;
import ru.thezit445.nbtreflectionapi.nbt.NBTTagCompound;
import ru.thezit445.nbtreflectionapi.nms.NMSItem;
import ru.thezit445.nbtreflectionapi.util.NBTUtils;

/**
 * <i>Created on 07.12.2019.</i>
 * Furnace recipe.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class SmokerRecipe extends AbstractRecipe implements IFurnaceRecipe, IRunnableRecipe {

    protected RecipeIngredient ingredient;
    private int cookTime = 200;
    private float exp = 0;

    public SmokerRecipe(String json) {
        super(null, null, Type.SMOKER);
        fromJson(json);
    }

    public SmokerRecipe(ItemStack result, NamespacedKey key) {
        super(result, key, Type.SMOKER);
    }

    @Override
    public RecipeIngredient getIngredient() {
        return ingredient;
    }

    @Override
    public void setIngredient(RecipeIngredient ingredient) {
        this.ingredient = ingredient;
    }

    public int getCookTime() {
        return cookTime;
    }

    public float getExp() {
        return exp;
    }

    @Override
    public String toJson() {
        JsonManager<SmokerRecipe> jsonManager = new JsonManager<>(SmokerRecipe.class);
        String json = jsonManager.toJsonString(this);
        return json;
    }

    @Override
    public void fromJson(String json) {
        JsonManager<SmokerRecipe> jsonManager = new JsonManager<>(SmokerRecipe.class);
        SmokerRecipe temp = jsonManager.getInstanceFromJson(json);
        this.key = temp.key;
        this.result = temp.result;
        this.ingredient = temp.ingredient;
        this.usePermission = temp.usePermission;
        this.cookTime = temp.cookTime;
        this.exp = temp.exp;
    }

    @Override
    public void register() {
        RecipeManager.addRecipe(this);
        ItemStack input = ingredient.getItem();
        NBTTagCompound nbtTag = NBTUtils.parse(this.result);
        NMSItem nmsItem = new NMSItem(nbtTag);
        ItemStack result = nmsItem.asBukkit();
        switch (ingredient.getType()) {
            case MATERIAL: {
                RecipeChoice.MaterialChoice choice = new RecipeChoice.MaterialChoice(input.getType());
                org.bukkit.inventory.FurnaceRecipe furnaceRecipe = new org.bukkit.inventory.FurnaceRecipe(key, result, choice, exp, cookTime);
                org.bukkit.inventory.SmokingRecipe smokingRecipe = new org.bukkit.inventory.SmokingRecipe(key, result, choice, exp, cookTime/2);
                Bukkit.addRecipe(furnaceRecipe);
                Bukkit.addRecipe(smokingRecipe);
                break;
            }
            case EXACT: {
                RecipeManager.addFurnaceRecipe(this);
            }
        }
    }
}
