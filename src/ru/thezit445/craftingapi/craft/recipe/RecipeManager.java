package ru.thezit445.craftingapi.craft.recipe;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import ru.thezit445.craftingapi.CraftingAPI;
import ru.thezit445.craftingapi.config.Message;
import ru.thezit445.craftingapi.craft.ingredient.RecipeIngredient;
import ru.thezit445.craftingapi.craft.interfaces.IFurnaceRecipe;
import ru.thezit445.craftingapi.craft.interfaces.IRunnableRecipe;
import ru.thezit445.craftingapi.craft.recipe.AbstractRecipe.Type;
import ru.thezit445.craftingapi.craft.runnable.FurnaceSmelting;
import ru.thezit445.craftingapi.eventhandlers.ClickGuiListener;
import ru.thezit445.craftingapi.gui.GuiHolder;
import ru.thezit445.craftingapi.gui.GuiHolder.GuiType;
import ru.thezit445.craftingapi.utils.FileManager;
import ru.thezit445.craftingapi.utils.FileManager.Folder;

/**
*
* @author thezit445<thezit445@yandex.ru>
*/

public class RecipeManager {
	
	private RecipeManager() {}
	
	private static final Map<NamespacedKey, AbstractRecipe> recipes = new HashMap<>();
	private static final List<AbstractRecipe> recipesList = new ArrayList<>();
	private static final List<IFurnaceRecipe> furnaceRecipes = new ArrayList<>();
	private static final List<BrewingRecipe> brewingRecipes = new ArrayList<>();

	public static Collection<AbstractRecipe> getRecipes(){
		return recipes.values();
	}
	
	public static boolean exist(NamespacedKey key) {
		return recipes.containsKey(key);
	}
	
	public static AbstractRecipe getRecipe(int id) {
		return recipesList.get(id);
	}
	
	public static AbstractRecipe getRecipe(NamespacedKey key) {
		return recipes.get(key);
	}

	public static void addRecipe(AbstractRecipe recipe) {
		recipes.put(recipe.getKey(), recipe);
		recipesList.add(recipe);
	}

	public static List<BrewingRecipe> getBrewingRecipes(){
		return new ArrayList<>(brewingRecipes);
	}

	public static void addBrewingRecipe(BrewingRecipe recipe){
		brewingRecipes.add(recipe);
	}

	public static List<IFurnaceRecipe> getFurnaceRecipes(){
		return new ArrayList<>(furnaceRecipes);
	}

	public static void addFurnaceRecipe(IFurnaceRecipe recipe){
		furnaceRecipes.add(recipe);
	}

	public static boolean matrixIsEmpty(ItemStack[] workbenchMatrix) {
		for (ItemStack item : workbenchMatrix) {
			if (item!=null) return false;
		}
		return true;
	}
	
	public static void openRecipeCreator(Player player, Type type, String name) {
		GuiHolder.GuiType guiType = null;
		switch (type) {
			case WORKBENCH_SHAPED: guiType = GuiType.SHAPED_RECIPE_CREATOR; break;
			case WORKBENCH_SHAPELESS: guiType = GuiType.SHAPELESS_RECIPE_CREATOR; break;
			case FURNACE: guiType = GuiType.FURNACE_RECIPE_CREATOR; break;
			case BLASTING: guiType = GuiType.BLASTING_RECIPE_CREATOR; break;
			case SMOKER: guiType = GuiType.SMOKER_RECIPE_CREATOR; break;
			case BREWING: guiType = GuiType.BREWING_RECIPE_CREATOR; break;
		}
		GuiHolder holder = new GuiHolder(guiType);
		NamespacedKey key = new NamespacedKey(CraftingAPI.getInstance(), name);
		holder.setParameter(GuiHolder.keySelectionName, key);
		holder.setParameter(GuiHolder.playerSelectionName, player);
		holder.createGui();
		holder.open(player);
		CraftingAPI.playersWithOpenedGui.add(player.getUniqueId().toString());
	}
	
	public static void openRecipeEditor(Player player, String nameString) {
		NamespacedKey key = new NamespacedKey(CraftingAPI.getInstance(), nameString);
		if (!RecipeManager.exist(key)) {
			player.sendMessage(Message.CMD_EDIT_FAIL.getMessage(Message.keyTag, key.toString()));
			return;
		}
		AbstractRecipe recipe = RecipeManager.getRecipe(key);
		GuiHolder.GuiType guiType = null;
		switch (recipe.getType()) {
			case WORKBENCH_SHAPED: guiType = GuiType.SHAPED_RECIPE_CREATOR; break;
			case WORKBENCH_SHAPELESS: guiType = GuiType.SHAPELESS_RECIPE_CREATOR; break;
			case FURNACE: guiType = GuiType.FURNACE_RECIPE_CREATOR; break;
			case BLASTING: guiType = GuiType.BLASTING_RECIPE_CREATOR; break;
			case SMOKER: guiType = GuiType.SMOKER_RECIPE_CREATOR; break;
			case BREWING: guiType = GuiType.BREWING_RECIPE_CREATOR; break;
		}
		GuiHolder holder = new GuiHolder(guiType);
		holder.setParameter(GuiHolder.keySelectionName, key);
		holder.setParameter(GuiHolder.playerSelectionName, player);
		holder.createGui();
		holder.open(player);
		
		Inventory inv = holder.getInventory();
		if (recipe instanceof ShapedRecipe) {
			inv.setItem(ClickGuiListener.workbenckResultSlot, recipe.getResult());
			ShapedRecipe exactRecipe = (ShapedRecipe) recipe;
			for (int i = 0; i<exactRecipe.getShapeRows(); i++) {
				for (int j = 0; j<exactRecipe.getShapeColumms(); j++) {
					char character = exactRecipe.getShape()[i].charAt(j);
					RecipeIngredient ingredient = exactRecipe.getItems().get(character);
					if (ingredient!=null) {
						ItemStack item = exactRecipe.getItems().get(character).getItem();
						inv.setItem(ClickGuiListener.workbenchMatrix.get(j+i*3), item);
					}
				}
			}
		} else if (recipe instanceof ShapelessRecipe) {
			inv.setItem(ClickGuiListener.workbenckResultSlot, recipe.getResult());
			ShapelessRecipe exactRecipe = (ShapelessRecipe) recipe;
			for (int i = 0; i<exactRecipe.getItems().size(); i++) {
				ItemStack item = exactRecipe.getItems().get(i).getItem();
				inv.setItem(ClickGuiListener.workbenchMatrix.get(i), item);
			}
		} else if (recipe instanceof IFurnaceRecipe) {
			inv.setItem(ClickGuiListener.furnaceResultSlot, recipe.getResult());
			switch (recipe.getType()) {
				case SMOKER: inv.setItem(ClickGuiListener.furnaceIngredientSlot, ((SmokerRecipe) recipe).getIngredient().getItem()); break;
				case BLASTING: inv.setItem(ClickGuiListener.furnaceIngredientSlot, ((BlastingRecipe) recipe).getIngredient().getItem()); break;
				default: inv.setItem(ClickGuiListener.furnaceIngredientSlot, ((FurnaceRecipe) recipe).getIngredient().getItem()); break;
			}
		} else if (recipe instanceof BrewingRecipe) {
			inv.setItem(ClickGuiListener.brewingIngredientSlot, ((BrewingRecipe) recipe).getIngredient().getItem());
			inv.setItem(ClickGuiListener.brewingPotionSlot, ((BrewingRecipe) recipe).getPotion().getItem());
			inv.setItem(ClickGuiListener.brewingResultSlot, ((BrewingRecipe) recipe).getResult());
		}
		
		CraftingAPI.playersWithOpenedGui.add(player.getUniqueId().toString());
	}
	
	public static void removeRecipe(Player player, String nameString) {
		NamespacedKey key = new NamespacedKey(CraftingAPI.getInstance(), nameString);
		if (!RecipeManager.exist(key)) {
			player.sendMessage(Message.CMD_REMOVE_FAIL.getMessage(Message.keyTag, key.toString()));
			return;
		}
		AbstractRecipe recipe = RecipeManager.getRecipe(key);
		String localPath = key.getNamespace()+File.separator+recipe.getType().getName()+File.separator;
		File file = CraftingAPI.getFileManager().getFile(Folder.RECIPES, localPath, nameString, FileManager.jsonFileType);
		if (file.exists()) {
			file.delete();
			recipes.remove(key);
			recipesList.remove(recipe);
			Bukkit.resetRecipes();
			loadRecipes(CraftingAPI.getInstance());
			player.sendMessage(Message.CMD_REMOVE_SUCCESS.getMessage(Message.keyTag, key.toString()));
			return;
		}
	}
	
	public static void setUseOfPermission(Player player, String nameString, boolean use) {
		NamespacedKey key = new NamespacedKey(CraftingAPI.getInstance(), nameString);
		if (!RecipeManager.exist(key)) {
			player.sendMessage(Message.CMD_SET_PERMISSION_FAIL.getMessage(Message.keyTag, key.toString()));
			return;
		}
		AbstractRecipe recipe = RecipeManager.getRecipe(key);
		if (recipe instanceof FurnaceRecipe) {
			player.sendMessage(Message.CMD_USE_SET_PERMISSION_ILLEGAL_TYPE.getMessage(Message.typeTag,recipe.getType().getName()));
			return;
		}
		String localPath = key.getNamespace()+File.separator+recipe.getType().getName()+File.separator;
		File file = CraftingAPI.getFileManager().getFile(Folder.RECIPES, localPath, nameString, FileManager.jsonFileType);
		if (file.exists()) {
			file.delete();
			recipes.remove(key);
			Bukkit.reloadData();
			loadRecipes(CraftingAPI.getInstance());
			recipe.usePermission(use);
			recipe.register();
			String json = recipe.toJson();
			CraftingAPI.getFileManager().writeInFile(file, json);
			Map<String,String> replacements = new HashMap<>();
			replacements.put(Message.keyTag, key.toString());
			replacements.put(Message.useTag, String.valueOf(use));
			player.sendMessage(Message.CMD_SET_PERMISSION_SUCCESS.getMessage(replacements));
			return;
		}
	}
	
	public static void createShapedRecipe(ItemStack[] matrix, ItemStack result, NamespacedKey key, Player player) {
		
		String localPath = key.getNamespace()+File.separator+AbstractRecipe.Type.WORKBENCH_SHAPED.getName();
		String fileName = key.getKey();
		boolean exist = true;
		File file = CraftingAPI.getFileManager().getFile(Folder.RECIPES, localPath, fileName, FileManager.jsonFileType);
		if (!file.exists()) {
			exist = false;
			file = CraftingAPI.getFileManager().createFile(Folder.RECIPES, localPath, fileName, FileManager.jsonFileType);
		}
		
		if (exist) {
			file.delete();
			recipes.remove(key);
			Bukkit.reloadData();
			loadRecipes(CraftingAPI.getInstance());
		}
		
		if (result==null) return;
		if (matrixIsEmpty(matrix)) return;
		
		int matrixSize = 3;
		int baseStartX = 2, baseStartY = 2;
		int width = 1, height = 1;
		for (int y = 0; y<matrixSize; y++) {
			for (int x = 0; x<matrixSize; x++) {
				if (matrix[x+y*matrixSize]==null) continue;
				if (x<baseStartX) baseStartX = x;
				if (y<baseStartY) baseStartY = y;
			}
		}
		
		for (int y = baseStartY, i = 1; y<matrixSize; y++, i++) {
			for (int x = baseStartX, j = 1; x<matrixSize; x++, j++) {
				if (matrix[x+y*matrixSize]!=null) {
					if (j>width) width = j;
					if (i>height) height = i;
				}
			}
		}
		
		ShapedRecipe recipe = new ShapedRecipe(result, key);
		
		ItemStack[] items = new ItemStack[width*height];
		String[] shape = new String[height];
		Arrays.fill(shape, "");
		ShapedRecipe.KeyCharacter[] chars = ShapedRecipe.KeyCharacter.values();
		for (int y = baseStartY, i = 0, j=0; y<baseStartY+height; y++, i++) {
			for (int x = baseStartX; x<baseStartX+width; x++, j++) {
				items[j] = matrix[x+y*matrixSize];
				if (matrix[x+y*matrixSize]==null) {
					shape[i]+=" ";
				} else {
					shape[i]+=chars[j].getKey();
					RecipeIngredient ingredient = RecipeIngredient.create(items[j]);
					recipe.addItem(chars[j], ingredient);
				}
			}
		}
		
		recipe.setShape(shape);
		recipe.register();
		String json = recipe.toJson();
		CraftingAPI.getFileManager().writeInFile(file, json);
		
		if (exist) {
			player.sendMessage(Message.GUI_RECIPE_UPDATE.getMessage(Message.keyTag, key.toString()));
		} else {
			player.sendMessage(Message.GUI_RECIPE_CREATE_SUCCSEFULLY.getMessage(Message.keyTag, key.toString()));
		}
		player.closeInventory();
		player.closeInventory();
	}
	
	public static void createShapelessRecipe(ItemStack[] matrix, ItemStack result, NamespacedKey key, Player player) {
		String localPath = key.getNamespace()+File.separator+AbstractRecipe.Type.WORKBENCH_SHAPELESS.getName();
		String fileName = key.getKey();
		File file = CraftingAPI.getFileManager().getFile(Folder.RECIPES, localPath, fileName, FileManager.jsonFileType);
		boolean exist = true;
		if (!file.exists()) {
			exist = false;
			file = CraftingAPI.getFileManager().createFile(Folder.RECIPES, localPath, fileName, FileManager.jsonFileType);
		}
		
		if (exist) {
			file.delete();
			recipes.remove(key);
			Bukkit.reloadData();
			loadRecipes(CraftingAPI.getInstance());
		}
		
		if (result==null) return;
		if (matrixIsEmpty(matrix)) return;
		
		ShapelessRecipe recipe = new ShapelessRecipe(result, key);
		for (ItemStack stack : matrix) {
			if (stack==null) continue;
			RecipeIngredient ingredient = RecipeIngredient.create(stack);
			recipe.addItem(ingredient);
		}
		
		recipe.register();
		String json = recipe.toJson();
		CraftingAPI.getFileManager().writeInFile(file, json);
		
		if (exist) {
			player.sendMessage(Message.GUI_RECIPE_UPDATE.getMessage(Message.keyTag, key.toString()));
		} else {
			player.sendMessage(Message.GUI_RECIPE_CREATE_SUCCSEFULLY.getMessage(Message.keyTag, key.toString()));
		}
		player.closeInventory();
	}

	public static void createFurnaceRecipe(ItemStack ingredient, ItemStack result, NamespacedKey key, Player player, Type type) {
		
		String localPath = key.getNamespace()+File.separator+type.getName();
		String fileName = key.getKey();
		boolean exist = true;
		File file = CraftingAPI.getFileManager().getFile(Folder.RECIPES, localPath, fileName, FileManager.jsonFileType);
		if (!file.exists()) {
			exist = false;
			file = CraftingAPI.getFileManager().createFile(Folder.RECIPES, localPath, fileName, FileManager.jsonFileType);
		}
		
		if (result==null) return;
		if (ingredient==null) return;
		
		IFurnaceRecipe recipe;
		switch (type) {
			case SMOKER: recipe = new SmokerRecipe(result, key); break;
			case BLASTING: recipe = new BlastingRecipe(result, key); break;
			default: recipe = new FurnaceRecipe(result, key); break;
		}
		((IRunnableRecipe) recipe).setIngredient(RecipeIngredient.create(ingredient));
		((AbstractRecipe) recipe).register();
		
		String json = ((AbstractRecipe) recipe).toJson();
		CraftingAPI.getFileManager().writeInFile(file, json);
		
		if (exist) {
			player.sendMessage(Message.GUI_RECIPE_UPDATE.getMessage(Message.keyTag, key.toString()));
		} else {
			player.sendMessage(Message.GUI_RECIPE_CREATE_SUCCSEFULLY.getMessage(Message.keyTag, key.toString()));
		}
		player.closeInventory();
		
	}

	public static void createBrewingRecipe(ItemStack ingredient, ItemStack potion, ItemStack result, NamespacedKey key, Player player) {

		String localPath = key.getNamespace()+File.separator+Type.BREWING.getName();
		String fileName = key.getKey();
		boolean exist = true;
		File file = CraftingAPI.getFileManager().getFile(Folder.RECIPES, localPath, fileName, FileManager.jsonFileType);
		if (!file.exists()) {
			exist = false;
			file = CraftingAPI.getFileManager().createFile(Folder.RECIPES, localPath, fileName, FileManager.jsonFileType);
		}

		if (result==null) return;
		if (ingredient==null) return;

		BrewingRecipe recipe = new BrewingRecipe(result, key);
		recipe.setIngredient(RecipeIngredient.create(ingredient));
		recipe.setPotion(RecipeIngredient.create(potion));
		recipe.register();

		String json = ((AbstractRecipe) recipe).toJson();
		CraftingAPI.getFileManager().writeInFile(file, json);

		if (exist) {
			player.sendMessage(Message.GUI_RECIPE_UPDATE.getMessage(Message.keyTag, key.toString()));
		} else {
			player.sendMessage(Message.GUI_RECIPE_CREATE_SUCCSEFULLY.getMessage(Message.keyTag, key.toString()));
		}
		player.closeInventory();
	}
	
	public static void loadRecipes(JavaPlugin plugin) {
		
		NamespacedKey key = new NamespacedKey(plugin, "sample");
		
		File[] shapedRecipes = CraftingAPI.getFileManager().getFiles(Folder.RECIPES, key.getNamespace()+File.separator+Type.WORKBENCH_SHAPED.getName());
		File[] shapelessRecipes = CraftingAPI.getFileManager().getFiles(Folder.RECIPES, key.getNamespace()+File.separator+Type.WORKBENCH_SHAPELESS.getName());
		File[] furnaceRecipes = CraftingAPI.getFileManager().getFiles(Folder.RECIPES, key.getNamespace()+File.separator+Type.FURNACE.getName());
		File[] blastingRecipes = CraftingAPI.getFileManager().getFiles(Folder.RECIPES, key.getNamespace()+File.separator+Type.BLASTING.getName());
		File[] smokerRecipes = CraftingAPI.getFileManager().getFiles(Folder.RECIPES, key.getNamespace()+File.separator+Type.SMOKER.getName());
		File[] brewingRecipes = CraftingAPI.getFileManager().getFiles(Folder.RECIPES, key.getNamespace()+File.separator+Type.BREWING.getName());

		File[][] allFiles = {shapedRecipes, shapelessRecipes, furnaceRecipes, blastingRecipes, smokerRecipes, brewingRecipes};
		
		for (File[] files : allFiles) {
			if (files==null || files.length==0) continue;
			for (File file : files) {
				String json = null;
				try {
					json = CraftingAPI.getFileManager().getFileContents(file);
				} catch (FileNotFoundException ex) {
					Logger.getLogger(CraftingAPI.class.getName()).log(Level.SEVERE, "File not found error: ", ex);
				}
				AbstractRecipe recipe = null;
				String path = file.getPath().toLowerCase();
				if (path.contains(AbstractRecipe.Type.WORKBENCH_SHAPED.getName())) {
					recipe = new ShapedRecipe(json);
				} else if (path.contains(AbstractRecipe.Type.WORKBENCH_SHAPELESS.getName())) {
					recipe = new ShapelessRecipe(json);
				} else if (path.contains(AbstractRecipe.Type.FURNACE.getName())) {
					recipe = new FurnaceRecipe(json);
				} else if (path.contains(Type.BLASTING.getName())) {
					recipe = new BlastingRecipe(json);
				} else if (path.contains(Type.SMOKER.getName())) {
					recipe = new SmokerRecipe(json);
				} else if (path.contains(Type.BREWING.getName())) {
					recipe = new BrewingRecipe(json);
				}
				recipe.register();
			}
		}
		
	}
	
	public static void openRecipesBrowser(Player player) {
		GuiHolder holder = new GuiHolder(GuiHolder.GuiType.RECIPES_BROWSER);
		holder.createGui();
		holder.open(player);
		CraftingAPI.playersWithOpenedGui.add(player.getUniqueId().toString());
	}
	
}
