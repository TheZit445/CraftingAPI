package ru.thezit445.craftingapi.utils;

import java.lang.reflect.Type;
import org.bukkit.Material;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ru.thezit445.craftingapi.craft.ingredient.ExactRecipeIngredient;
import ru.thezit445.craftingapi.craft.ingredient.MaterialRecipeIngredient;
import ru.thezit445.craftingapi.craft.ingredient.RecipeIngredient;

/**
 * <i>Created on 07.12.2019.</i>
 * Custom deserializer for json.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class CustomJsonDeserializer<T> implements JsonDeserializer<RecipeIngredient> {
	
	private static String materialSection = "material";
	private static String itemSection = "nbtItemFormat";
	private static String typeSection = "type";
	
	@Override
	public RecipeIngredient deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = json.getAsJsonObject();
        String ingredientTypeString = jsonObject.get(typeSection).getAsString();
        
        RecipeIngredient.Type ingredientType = RecipeIngredient.Type.valueOf(ingredientTypeString);
        RecipeIngredient ingredient = null;
        
        switch (ingredientType) {
        	case MATERIAL: {
        		String materialString = jsonObject.get(materialSection).getAsString();
        		Material material = Material.getMaterial(materialString);
        		ingredient = new MaterialRecipeIngredient(material);
        		break;
        	}
        	case EXACT: {
        		String itemNbtFormat = jsonObject.get(itemSection).getAsString();
        		ingredient = new ExactRecipeIngredient(itemNbtFormat);
        		break;
        	}
        }
        return ingredient;
	}
	
	

}
