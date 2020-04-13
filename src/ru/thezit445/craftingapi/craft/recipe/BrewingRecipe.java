package ru.thezit445.craftingapi.craft.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import ru.thezit445.craftingapi.craft.ingredient.RecipeIngredient;
import ru.thezit445.craftingapi.craft.interfaces.IRunnableRecipe;
import ru.thezit445.craftingapi.utils.JsonManager;

public class BrewingRecipe extends AbstractRecipe implements IRunnableRecipe {

    protected RecipeIngredient ingredient;
    protected RecipeIngredient potion;
    private int brewTime = 200;

    public BrewingRecipe(String json) {
        super(null, null, Type.BREWING);
        fromJson(json);
    }

    public BrewingRecipe(ItemStack result, NamespacedKey key) {
        super(result, key, Type.BREWING);
    }

    @Override
    public String toJson() {
        JsonManager<BrewingRecipe> jsonManager = new JsonManager<>(BrewingRecipe.class);
        String json = jsonManager.toJsonString(this);
        return json;
    }

    @Override
    public void fromJson(String json) {
        JsonManager<BrewingRecipe> jsonManager = new JsonManager<>(BrewingRecipe.class);
        BrewingRecipe temp = jsonManager.getInstanceFromJson(json);
        this.key = temp.key;
        this.result = temp.result;
        this.ingredient = temp.ingredient;
        this.potion = temp.potion;
        this.usePermission = temp.usePermission;
        this.brewTime = temp.brewTime;
    }

    @Override
    public void register() {
        RecipeManager.addRecipe(this);
        RecipeManager.addBrewingRecipe(this);
    }

    @Override
    public RecipeIngredient getIngredient() {
        return ingredient;
    }

    public RecipeIngredient getPotion() {
        return potion;
    }

    @Override
    public void setIngredient(RecipeIngredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setPotion(RecipeIngredient potion) {
        this.potion = potion;
    }
}
