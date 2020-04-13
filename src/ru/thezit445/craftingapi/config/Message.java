package ru.thezit445.craftingapi.config;

import java.util.Map;

import org.bukkit.ChatColor;

/**
 * <i>Created on 07.12.2019.</i>
 * Enum with plugin messages.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public enum Message {
	
	ITEM__BUTTON_CREATE_NAME("item.button-create-name"),
	ITEM__BUTTON_CREATE_LORE_1("item.button-create-lore-1"),
	ITEM__BUTTON_CREATE_LORE_2("item.button-create-lore-2"),
	ITEM__BUTTON_ALLOW_NAME("item.button-allow-name"),
	ITEM__BUTTON_DENY_NAME("item.button-deny-name"),
	ITEM__CELL_FUEL_NAME("item.cell-fuel-name"),
	ITEM__BUTTON_NEXT_NAME("item.button-next-name"),
	ITEM__BUTTON_PREVIOUS_NAME("item.button-previous-name"),
	GUI_TITLE_RECIPE_CREATOR("gui-title-recipe-creator"),
	GUI_TITLE_RECIPES_VIEWER("gui-title-recipes-viewer"),
	PERMISSION_DENIED("cmd-permission-denied"),
	CMD_ONLY_PLAYER("cmd-only-player"),
	CMD_ILLEGAL_ARGUMENT("cmd-illegal-argument"),
	CMD_NO_RECIPES("cmd-no-recipes"),
	CMD_USE_HELP("cmd-use-help"),
	CMD_HELP_LINE_1("cmd-help-line-1"),
	CMD_HELP_LINE_2("cmd-help-line-2"),
	CMD_HELP_LINE_3("cmd-help-line-3"),
	CMD_HELP_LINE_4("cmd-help-line-4"),
	CMD_HELP_LINE_5("cmd-help-line-5"),
	CMD_HELP_LINE_6("cmd-help-line-6"),
	CMD_HELP_LINE_7("cmd-help-line-7"),
	CMD_HELP_LINE_8("cmd-help-line-8"),
	CMD_HELP_LINE_9("cmd-help-line-9"),
	CMD_USE_CREATE("cmd-use-create"),
	CMD_CREATE_ILLEGAL_RECIPE_TYPE("cmd-create-illegal-recipe-type"),
	CMD_USE_VIEW("cmd-use-view"),
	CMD_USE_EDIT("cmd-use-edit"),
	CMD_USE_REMOVE("cmd-use-remove"),
	CMD_USE_SET_PERMISSION("cmd-use-setpermission"),
	CMD_USE_SET_PERMISSION_ILLEGAL_TYPE("cmd-setpermission-illegal-type"),
	CMD_SET_PERMISSION_ILLEGAL_BOOLEAN("cmd-setpermission-illegal-boolean"),
	CMD_REMOVE_SUCCESS("cmd-remove-success"),
	CMD_REMOVE_FAIL("cmd-remove-fail"),
	CMD_SET_PERMISSION_SUCCESS("cmd-setpermission-success"),
	CMD_SET_PERMISSION_FAIL("cmd-setpermission-fail"),
	CMD_EDIT_FAIL("cmd-edit-fail"),
	GUI_RECIPE_CREATE_SUCCSEFULLY("gui-recipe-create-succefully"),
	GUI_RECIPE_CREATE_FAILLY("gui-recipe-create-failly"),
	GUI_RECIPE_UPDATE("gui-recipe-update"),
	GUI_RECIPE_CREATE_BREWING_NOT_A_POTION("gui-recipe-create-brewing-not-a-potion");
	
	public static final String argumentTag = "%arg%";
	public static final String typeTag = "%type%";
	public static final String keyTag = "%key%";
	public static final String useTag = "%use%";
	
	private String section;
	private String message = ChatColor.DARK_RED+"Message error.";
	
	Message(String section) {
		this.section = section;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getMessage(Map<String, String> replacements) {
		String msg = message;
		for (String tag : replacements.keySet()) {
			if (msg.contains(tag)) {
				msg = msg.replace(tag, replacements.get(tag));
			}
		}
		return msg;
	}
	
	public String getMessage(String tag, String replacement) {
		String msg = message;
		if (message.contains(tag)) {
			msg = message.replace(tag, replacement);
		}
		return msg;
	}
	
	public static void initialize(PluginConfiguration config) {
		for (Message msg : Message.values()) {
			String text = config.getString(msg.section);
			msg.message = convertToColorText(text);
		}
	}
	
	public static String convertToColorText(String text) {
		if (text.contains("&")) {
			text = ChatColor.translateAlternateColorCodes('&', text);
		}
		return text;
	}
	
}
