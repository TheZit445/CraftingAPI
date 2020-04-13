package ru.thezit445.craftingapi.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ru.thezit445.craftingapi.craft.ingredient.RecipeIngredient;

/**
 * <i>Created on 07.12.2019.</i>
 * Json manager for plugin.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class JsonManager<T> {
	
	private Class<T> clazz;
	
	public JsonManager(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	public T getInstanceFromFile(File file) throws IOException {
		String json = new String(Files.readAllBytes(file.toPath()));
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		T item = gson.fromJson(json, clazz);
		return item;
	}
	
	public T getInstanceFromJson(String json) {
		Gson gson = new GsonBuilder().registerTypeAdapter(RecipeIngredient.class, new CustomJsonDeserializer<>()).setPrettyPrinting().create();
		T item = gson.fromJson(json, clazz);
		return item;
	}
	
	public String toJsonString(T t) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(t, clazz);
	}
	
}
