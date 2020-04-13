package ru.thezit445.craftingapi.craft.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import ru.thezit445.craftingapi.config.PluginPermission;
import ru.thezit445.nbtreflectionapi.nbt.NBTTagCompound;
import ru.thezit445.nbtreflectionapi.nms.NMSItem;
import ru.thezit445.nbtreflectionapi.util.NBTUtils;

/**
 * <i>Created on 07.12.2019.</i>
 * Abstract recipe.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public abstract class AbstractRecipe {
	
	protected Type type;
	protected String result;
	protected NamespacedKey key;
	protected boolean usePermission = false;
	
	public AbstractRecipe(ItemStack result, NamespacedKey key, Type type) {
		NMSItem nmsItem = new NMSItem(result);
		NBTTagCompound nbt = nmsItem.getNBT();
		String tag = nbt.asString();
		this.result = tag;
		this.key = key;
		this.type = type;
	}
	
	public void usePermission(boolean use) {
		this.usePermission = use;
	}
	
	public String getPermission() {
		return PluginPermission.CRAFTINGAPI_RECIPE_USE.getPermission(key).toString();
	}
	
	public boolean isUsedPermission() {
		return usePermission;
	}
	
	public void setResult(ItemStack result) {
		NMSItem nmsItem = new NMSItem(result);
		NBTTagCompound nbt = nmsItem.getNBT();
		String tag = nbt.asString();
		this.result = tag;
	}
	
	public ItemStack getResult() {
		NBTTagCompound nbt = NBTUtils.parse(result);
		NMSItem nmsItem = new NMSItem(nbt);
		ItemStack item = nmsItem.asBukkit();
		return item;
	}
	
	public void setKey(NamespacedKey key) {
		this.key = key;
	}
	
	public NamespacedKey getKey() {
		return key;
	}
	
	public Type getType() {
		return type;
	}
	
	public abstract String toJson();
	
	public abstract void fromJson(String json);
	
	public abstract void register();
	
	public enum Type {
		BLASTING("blasting"),
		BREWING("brewing"),
		SMOKER("smoker"),
		WORKBENCH_SHAPED("shaped"),
		WORKBENCH_SHAPELESS("shapeless"),
		FURNACE("furnace");
		
		private String name;
		
		Type(String name){
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
	}

	void a(){

    }

}
