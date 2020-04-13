package ru.thezit445.craftingapi.craft.interfaces;

import ru.thezit445.craftingapi.craft.ingredient.RecipeIngredient;

public interface IRunnableRecipe {

    RecipeIngredient getIngredient();

    void setIngredient(RecipeIngredient ingredient);

}
