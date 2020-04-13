package ru.thezit445.craftingapi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.thezit445.craftingapi.commands.AdministrationCommands;
import ru.thezit445.craftingapi.config.Config;
import ru.thezit445.craftingapi.config.Message;
import ru.thezit445.craftingapi.config.PluginPermission;
import ru.thezit445.craftingapi.craft.listener.BrewingHandler;
import ru.thezit445.craftingapi.craft.listener.FurnaceHandler;
import ru.thezit445.craftingapi.craft.recipe.RecipeManager;
import ru.thezit445.craftingapi.craft.runnable.RunnableManager;
import ru.thezit445.craftingapi.eventhandlers.ClickGuiListener;
import ru.thezit445.craftingapi.craft.listener.CraftWorkbenchRecipeListener;
import ru.thezit445.craftingapi.gui.GuiManager;
import ru.thezit445.craftingapi.utils.FileManager;
import ru.thezit445.craftingapi.utils.Item;
import ru.thezit445.craftingapi.utils.ResourceLoader;

/**
 * <i>Created on 07.12.2019.</i>
 * Main plugin class.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class CraftingAPI extends JavaPlugin {
	
	public static final List<String> playersWithOpenedGui = new ArrayList<>();
	
	private static boolean isInitialized = false;
	
	private static JavaPlugin instance;
	private static ResourceLoader resourceLoader;
	private static FileManager fileManager;
	
	public CraftingAPI() {
	}
	
	@Override
	public void onEnable() {
		instance = this;
		load();
	}
	
	@Override
	public void onLoad() {
	}
	
	@Override
	public void onDisable() {
		unload();
	}
	
	public static boolean isInitialized() {
		return isInitialized;
	}
	
	public static JavaPlugin getInstance() {
		return instance;
	}
	
	public static ResourceLoader getResourceLoader() {
		return resourceLoader;
	}
	
	public static FileManager getFileManager() {
		return fileManager;
	}
	
	public static void load() {
		initialize();
		registerEvents();
		registerCommands();
		loadRecipes();
		isInitialized = true;
	}
	
	public static void unload() {
		RunnableManager.createTempFiles();
		for (String uuid : playersWithOpenedGui) {
			Player player = Bukkit.getPlayer(UUID.fromString(uuid));
			player.closeInventory();
		}
		playersWithOpenedGui.clear();
	}
	
	private static void registerCommands() {
		
		CraftingAPI.getInstance().getCommand(AdministrationCommands.cmd).setExecutor(new AdministrationCommands());
		
	}
	
	private static void initialize() {
		
		fileManager = new FileManager();
		resourceLoader = new ResourceLoader();
		
		Config.initialize();
		Message.initialize(Config.MESSAGE_FILE.getConfig());
		Item.initialize();
		GuiManager.initialize();
		
	}
	
	private static void registerEvents() {
		
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new CraftWorkbenchRecipeListener(), instance);
		manager.registerEvents(new ClickGuiListener(), instance);
		manager.registerEvents(new FurnaceHandler(), instance);
		manager.registerEvents(new BrewingHandler(), instance);
		
	}
	
	private static void loadRecipes() {
		
		PluginPermission.initialize();
		RecipeManager.loadRecipes(instance);
		RunnableManager.loadRunnables();
		
	}
	
}
