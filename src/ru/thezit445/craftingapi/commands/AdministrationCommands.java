package ru.thezit445.craftingapi.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import ru.thezit445.craftingapi.CraftingAPI;
import ru.thezit445.craftingapi.config.Message;
import ru.thezit445.craftingapi.config.PluginPermission;
import ru.thezit445.craftingapi.craft.recipe.AbstractRecipe;
import ru.thezit445.craftingapi.craft.recipe.RecipeManager;

/**
 * <i>Created on 07.12.2019.</i>
 * Command executor class for plugin.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 1.1.0
 */
public class AdministrationCommands implements TabCompleter, CommandExecutor {
	
	public static final String cmd = "craftingapi";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(Message.CMD_ONLY_PLAYER.getMessage());
			return true;
		}
		
		Player player = (Player) sender;
		
		if (args.length>0) {
			for (CmdAction action : CmdAction.values()) {
				if (action.subLabel.equalsIgnoreCase(args[0])) {
					action.invoke(player, args);
					return true;
				}
			}
			player.sendMessage(Message.CMD_ILLEGAL_ARGUMENT.getMessage(Message.argumentTag, args[0]));
			return true;
		}
		
		CmdAction.HELP.invoke(player, args);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		
		String subLabel = args[0];
		
		if (args.length == 1) {
			if (!subLabel.equals("")) {	
				if (subLabel.length()<=CmdAction.CREATE_RECIPE.subLabel.length() && CmdAction.CREATE_RECIPE.subLabel.startsWith(subLabel.toLowerCase())) {
					list.add(CmdAction.CREATE_RECIPE.subLabel);
				}
				if (subLabel.length()<=CmdAction.EDIT_RECIPE.subLabel.length() && CmdAction.EDIT_RECIPE.subLabel.startsWith(subLabel.toLowerCase())) {
					list.add(CmdAction.EDIT_RECIPE.subLabel);
				}
				if (subLabel.length()<=CmdAction.HELP.subLabel.length() && CmdAction.HELP.subLabel.startsWith(subLabel.toLowerCase())) {
					list.add(CmdAction.HELP.subLabel);
				}
				if (subLabel.length()<=CmdAction.REMOVE.subLabel.length() && CmdAction.REMOVE.subLabel.startsWith(subLabel.toLowerCase())) {
					list.add(CmdAction.REMOVE.subLabel);
				}
				if (subLabel.length()<=CmdAction.SET_PERMISSION.subLabel.length() && CmdAction.SET_PERMISSION.subLabel.startsWith(subLabel.toLowerCase())) {
					list.add(CmdAction.SET_PERMISSION.subLabel);
				}
				if (subLabel.length()<=CmdAction.VIEW_RECIPES.subLabel.length() && CmdAction.VIEW_RECIPES.subLabel.startsWith(subLabel.toLowerCase())) {
					list.add(CmdAction.VIEW_RECIPES.subLabel);
				}
			} else {
				list.add(CmdAction.CREATE_RECIPE.subLabel);
				list.add(CmdAction.EDIT_RECIPE.subLabel);
				list.add(CmdAction.HELP.subLabel);
				list.add(CmdAction.REMOVE.subLabel);
				list.add(CmdAction.SET_PERMISSION.subLabel);
				list.add(CmdAction.VIEW_RECIPES.subLabel);
			}
		} else if (args.length == 2) {
			if (subLabel.equalsIgnoreCase(CmdAction.CREATE_RECIPE.subLabel)) {
				String type = args[1];
				if (!type.equals("")) {
					if (type.length()<=AbstractRecipe.Type.WORKBENCH_SHAPED.getName().length() && AbstractRecipe.Type.WORKBENCH_SHAPED.getName().startsWith(type.toLowerCase())) {
						list.add(AbstractRecipe.Type.WORKBENCH_SHAPED.getName());
					}
					if (type.length()<=AbstractRecipe.Type.WORKBENCH_SHAPELESS.getName().length() && AbstractRecipe.Type.WORKBENCH_SHAPELESS.getName().startsWith(type.toLowerCase())) {
						list.add(AbstractRecipe.Type.WORKBENCH_SHAPELESS.getName());
					}
					if (type.length()<=AbstractRecipe.Type.FURNACE.getName().length() && AbstractRecipe.Type.FURNACE.getName().startsWith(type.toLowerCase())) {
						list.add(AbstractRecipe.Type.FURNACE.getName());
					}
					if (type.length()<=AbstractRecipe.Type.SMOKER.getName().length() && AbstractRecipe.Type.SMOKER.getName().startsWith(type.toLowerCase())){
						list.add(AbstractRecipe.Type.SMOKER.getName());
					}
					if (type.length()<=AbstractRecipe.Type.BLASTING.getName().length() && AbstractRecipe.Type.BLASTING.getName().startsWith(type.toLowerCase())){
						list.add(AbstractRecipe.Type.BLASTING.getName());
					}
					if (type.length()<=AbstractRecipe.Type.BREWING.getName().length() && AbstractRecipe.Type.BREWING.getName().startsWith(type.toLowerCase())){
						list.add(AbstractRecipe.Type.BREWING.getName());
					}
				} else {
					list.add(AbstractRecipe.Type.WORKBENCH_SHAPED.getName());
					list.add(AbstractRecipe.Type.WORKBENCH_SHAPELESS.getName());
					list.add(AbstractRecipe.Type.FURNACE.getName());
					list.add(AbstractRecipe.Type.SMOKER.getName());
					list.add(AbstractRecipe.Type.BLASTING.getName());
					list.add(AbstractRecipe.Type.BREWING.getName());
				}
			}
		} else if (args.length == 3) {
			if (subLabel.equalsIgnoreCase(CmdAction.SET_PERMISSION.subLabel)) {
				String use = args[2];
				if (!use.equals("")) {
					if (use.length()<=String.valueOf(true).length() && String.valueOf(true).startsWith(use.toLowerCase())) {
						list.add(String.valueOf(true));
					}
					if (use.length()<=String.valueOf(false).length() && String.valueOf(false).startsWith(use.toLowerCase())) {
						list.add(String.valueOf(false));
					}
				} else {
					list.add(String.valueOf(true));
					list.add(String.valueOf(false));
				}
			}
		}
		
		return list;
	}
	
	enum CmdAction {
		HELP("help") {
			@Override
			public void invoke(Player player, String[] args){
				int argsLenght = args.length - 1;
				if (argsLenght>1) {
					player.sendMessage(Message.CMD_USE_HELP.getMessage());
					return;
				}
				String msg = "";
				String messageName = "CMD_HELP_LINE_";
				for (int i = 1; i<10; i++) {
					String msgLine = Message.valueOf(messageName+i).getMessage();
					if (i!=9) {
						msg+=msgLine+"\n";
					} else {
						msg+=msgLine;
					}
				}
				player.sendMessage(msg);
			}
		},
		CREATE_RECIPE("create") {
			@Override
			public void invoke(Player player, String[] args){
				int argsLenght = args.length - 1;
				if (!player.hasPermission(PluginPermission.CRAFTINGAPI_ADMIN_CREATE.getPermission())) {
					player.sendMessage(Message.PERMISSION_DENIED.getMessage());
					return;
				}
				if (argsLenght!=2) {
					player.sendMessage(Message.CMD_USE_CREATE.getMessage());
					return;
				}
				
				String typeString = args[1];
				String nameString = args[2];
				
				NamespacedKey key = new NamespacedKey(CraftingAPI.getInstance(), nameString);
				if (RecipeManager.exist(key)) {
					player.sendMessage(Message.GUI_RECIPE_CREATE_FAILLY.getMessage(Message.keyTag, key.toString()));
					return;
				}
				
				AbstractRecipe.Type recipeType = null;
				for (AbstractRecipe.Type type : AbstractRecipe.Type.values()) {
					if (typeString.equalsIgnoreCase(type.getName())) { 
						recipeType = type;
						break;
					}
				}
				if (recipeType==null) {
					Message.CMD_CREATE_ILLEGAL_RECIPE_TYPE.getMessage(Message.typeTag, typeString);
					return;
				}
				RecipeManager.openRecipeCreator(player, recipeType, nameString);
			}
		},
		VIEW_RECIPES("browse") {
			@Override
			public void invoke(Player player, String[] args) {
				int argsLenght = args.length - 1;
				if (!player.hasPermission(PluginPermission.CRAFTINGAPI_ADMIN_VIEW.getPermission())) {
					player.sendMessage(Message.PERMISSION_DENIED.getMessage());
					return;
				}
				if (argsLenght!=0) {
					player.sendMessage(Message.CMD_USE_VIEW.getMessage());
					return;
				}
				if (RecipeManager.getRecipes().size()==0) {
					player.sendMessage(Message.CMD_NO_RECIPES.getMessage());
					return;
				}
				RecipeManager.openRecipesBrowser(player);
			}
		},
		EDIT_RECIPE("edit") {
			@Override
			public void invoke(Player player, String[] args) {
				int argsLenght = args.length - 1;
				if (!player.hasPermission(PluginPermission.CRAFTINGAPI_ADMIN_EDIT.getPermission())) {
					player.sendMessage(Message.PERMISSION_DENIED.getMessage());
					return;
				}
				if (argsLenght!=1) {
					player.sendMessage(Message.CMD_USE_EDIT.getMessage());
					return;
				}
				String nameString = args[1];
				RecipeManager.openRecipeEditor(player, nameString);
			}
		},
		REMOVE("remove") {
			@Override
			public void invoke(Player player, String[] args) {
				int argsLenght = args.length - 1;
				if (!player.hasPermission(PluginPermission.CRAFTINGAPI_ADMIN_REMOVE.getPermission())) {
					player.sendMessage(Message.PERMISSION_DENIED.getMessage());
					return;
				}
				if (argsLenght!=1) {
					player.sendMessage(Message.CMD_USE_REMOVE.getMessage());
					return;
				}
				String nameString = args[1];
				RecipeManager.removeRecipe(player, nameString);
			}
		},
		SET_PERMISSION("permission") {
			@Override
			public void invoke(Player player, String[] args) {
				int argsLenght = args.length - 1;
				if (!player.hasPermission(PluginPermission.CRAFTINGAPI_ADMIN_SET_PERMISSION.getPermission())) {
					player.sendMessage(Message.PERMISSION_DENIED.getMessage());
					return;
				}
				if (argsLenght!=2) {
					player.sendMessage(Message.CMD_USE_SET_PERMISSION.getMessage());
					return;
				}
				String nameString = args[1];
				String useString = args[2];
				if (!useString.equalsIgnoreCase("true") && !useString.equalsIgnoreCase("false")) {
					player.sendMessage(Message.CMD_SET_PERMISSION_ILLEGAL_BOOLEAN.getMessage(Message.typeTag, useString));
					return;
				}
				boolean use = Boolean.valueOf(useString.toLowerCase());
				RecipeManager.setUseOfPermission(player, nameString, use);
			}
		};
		
		private String subLabel;
		
		CmdAction(String subLabel){
			this.subLabel = subLabel;
		}
		
		public abstract void invoke(Player player, String[] args);
	}

}
