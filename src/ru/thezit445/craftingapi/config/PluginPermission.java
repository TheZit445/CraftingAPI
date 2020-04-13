package ru.thezit445.craftingapi.config;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import ru.thezit445.craftingapi.utils.FileManager;

/**
 * <i>Created on 07.12.2019.</i>
 * Permissions enum for plugin.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public enum PluginPermission {
	
	CRAFTINGAPI("craftingapi.*"),
	CRAFTINGAPI_ADMIN("craftingapi.admin.*"){
		@Override
		public void init() {
			permission = new Permission(permissionString, PermissionDefault.OP);
			permission.addParent(CRAFTINGAPI.getPermission(), true);
			PluginManager manager = Bukkit.getPluginManager();
			manager.addPermission(permission);
		}
	},
	CRAFTINGAPI_RECIPE("craftingapi.recipe.*"){
		@Override
		public void init() {
			permission = new Permission(permissionString, PermissionDefault.OP);
			permission.addParent(CRAFTINGAPI.getPermission(), true);
			PluginManager manager = Bukkit.getPluginManager();
			manager.addPermission(permission);
		}
	},
	CRAFTINGAPI_ADMIN_CREATE("craftingapi.admin.create") {
		@Override
		public void init() {
			permission = new Permission(permissionString, PermissionDefault.OP);
			permission.addParent(CRAFTINGAPI_ADMIN.getPermission(), true);
			PluginManager manager = Bukkit.getPluginManager();
			manager.addPermission(permission);
		}
	},
	CRAFTINGAPI_ADMIN_VIEW("craftingapi.admin.view") {
		@Override
		public void init() {
			permission = new Permission(permissionString, PermissionDefault.OP);
			permission.addParent(CRAFTINGAPI_ADMIN.getPermission(), true);
			PluginManager manager = Bukkit.getPluginManager();
			manager.addPermission(permission);
		}
	},
	CRAFTINGAPI_ADMIN_REMOVE("craftingapi.admin.remove") {
		@Override
		public void init() {
			permission = new Permission(permissionString, PermissionDefault.OP);
			permission.addParent(CRAFTINGAPI_ADMIN.getPermission(), true);
			PluginManager manager = Bukkit.getPluginManager();
			manager.addPermission(permission);
		}
	},
	CRAFTINGAPI_ADMIN_EDIT("craftingapi.admin.edit") {
		@Override
		public void init() {
			permission = new Permission(permissionString, PermissionDefault.OP);
			permission.addParent(CRAFTINGAPI_ADMIN.getPermission(), true);
			PluginManager manager = Bukkit.getPluginManager();
			manager.addPermission(permission);
		}
	},
	CRAFTINGAPI_ADMIN_SET_PERMISSION("craftingapi.admin.setpermission") {
		@Override
		public void init() {
			permission = new Permission(permissionString, PermissionDefault.OP);
			permission.addParent(CRAFTINGAPI_ADMIN.getPermission(), true);
			PluginManager manager = Bukkit.getPluginManager();
			manager.addPermission(permission);
		}
	},
	CRAFTINGAPI_RECIPE_USE("craftingapi.recipe.%namespacedkey%") {
		@Override
		public void init() {}
	};
	
	private static final String replacementTag = "%namespacedkey%";
	
	protected String permissionString;
	protected Permission permission;
	
	PluginPermission(String permissionString) {
		this.permissionString = permissionString;
	}
	
	public Permission getPermission() {
		return permission;
	}
	
	public void init() {
		permission = new Permission(permissionString, PermissionDefault.OP);
		PluginManager manager = Bukkit.getPluginManager();
		manager.addPermission(permission);
	}
	
	public Permission getPermission(NamespacedKey key) {
		String replacement = key.getNamespace() + FileManager.dot + key.getKey();
		String permissionString = this.permissionString.replace(replacementTag, replacement);
		Permission permission = Bukkit.getPluginManager().getPermission(permissionString);
		return permission;
	}
	
	public static void addNewUsePermission(NamespacedKey namespacedKey) {
		String key = namespacedKey.getNamespace()+FileManager.dot+namespacedKey.getKey();
		String recipePermission = PluginPermission.CRAFTINGAPI_RECIPE_USE.permissionString.replace(replacementTag, key);
		PluginManager manager = Bukkit.getPluginManager();
		if (manager.getPermission(recipePermission)!=null) return;
		Permission bukkitPermission = new Permission(recipePermission);
		bukkitPermission.addParent(PluginPermission.CRAFTINGAPI_RECIPE.getPermission(), true);
		manager.addPermission(bukkitPermission);
	}
	public static void initialize() {
		for (PluginPermission pluginPermission : PluginPermission.values()) {
			if (pluginPermission == CRAFTINGAPI_RECIPE_USE) continue;
			pluginPermission.init();
		}
	}
	
}
