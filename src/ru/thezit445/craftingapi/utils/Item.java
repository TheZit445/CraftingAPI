package ru.thezit445.craftingapi.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import ru.thezit445.craftingapi.config.Config;
import ru.thezit445.craftingapi.config.Message;
import ru.thezit445.craftingapi.config.PluginConfiguration;

/**
 * <i>Created on 07.12.2019.</i>
 * Item enum for plugins.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public enum Item {
	
	CELL_BACKGROUND("cell-background"),
	BUTTON_CREATE("button-create"),
	BUTTON_ALLOW("button-allow"),
	BUTTON_DENY("button-deny"),
	CELL_FUEL("cell-fuel"),
	BUTTON_NEXT("button-next"),
	BUTTON_PREVIOUS("button-previous");
	
	private static final String langRegexString = "@lang=\\[.*\\]";
	private static final Pattern langRegex = Pattern.compile(langRegexString);
	private static final int startRegexIndex = 7;
	
	private static final String materialSection = "material";
    private static final String durabilitySection = "durability";
    private static final String nameSection = "name";
    private static final String loreSection = "lore";
	
	private String section;
    private String name = null;
    private Material material = Material.AIR;
    private int durability = 0;
    private List<String> lore = new ArrayList<>();
	
	Item(String section) {
		this.section = section;
	}
	private String getSection(){
        return section;
    }

    private void setDisplayName(String name){
        this.name = name;
    }

    private void setMaterial(Material material){
        this.material = material;
    }

    private void setDurability(int durability){
        this.durability = durability;
    }

    private void setLore(List<String> lore){
        this.lore = lore;
    }
    
    public ItemStack getItemWithReplacement(Map<String, String> tags) {
    	String name = this.name;
	    List<String> lore = new ArrayList<>(this.lore);
	   	for (String tag : tags.keySet()) {
	    	if (name!=null && !name.isEmpty()) {
	    		if (name.contains(tag)) name = name.replace(tag, tags.get(tag));
	    	}
	    	if (lore!=null && !lore.isEmpty()) {
	    		for (int i = 0; i<lore.size(); i++) {
	    			String line = lore.get(i);
	    			if (line.contains(tag)) {
	    				String tempLine = line.replace(tag, tags.get(tag));
	    				lore.set(i, tempLine);
	    			}
	    		}
	    	}
	    }
    	ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (name!=null && !name.isEmpty()) {
            meta.setDisplayName(name);
        }
        if (meta instanceof Damageable){
            ((Damageable) meta).setDamage(durability);
        }
        if (lore!=null && !lore.isEmpty()){
            meta.setLore(lore);
        }
        stack.setItemMeta(meta);
        return stack;
    }
    
    public ItemStack getItemWithReplacement(String tag, String text) {
    	ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (name!=null && !name.isEmpty()) {
        	String name = this.name;
        	if (name.contains(tag)) name = name.replace(tag, text);
            meta.setDisplayName(name);
        }
        if (meta instanceof Damageable){
            ((Damageable) meta).setDamage(durability);
        }
        if (lore!=null && !lore.isEmpty()){
        	List<String> lore = new ArrayList<>();
        	this.lore.forEach(line -> {
        		if (line.contains(tag)) line = line.replace(tag, text);
        		lore.add(line);
        	});
            meta.setLore(lore);
        }
        stack.setItemMeta(meta);
        return stack;
    }
    
    public ItemStack getItem(){
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (name!=null && !name.isEmpty()) {
            meta.setDisplayName(name);
        }
        if (meta instanceof Damageable){
            ((Damageable) meta).setDamage(durability);
        }
        if (lore!=null && !lore.isEmpty()){
            meta.setLore(lore);
        }
        stack.setItemMeta(meta);
        return stack;
    }

    public static void initialize(){
    	PluginConfiguration config = Config.GENERAL_ITEMS.getConfig();
        for (Item builder : Item.values()){
            ConfigurationSection section = config.getConfigurationSection(builder.getSection());
            String materialName = section.getString(materialSection).toUpperCase().trim();
            Material material = Material.getMaterial(materialName);
            builder.setMaterial(material);
            if (section.contains(nameSection)) {
            	String name = section.getString(nameSection);
            	Matcher matcher = langRegex.matcher(name);
            	if (matcher.find()) {
            		String langPath = name.substring(matcher.start(), matcher.end());
            		langPath = langPath.substring(startRegexIndex,langPath.length()-1);
            		name = matcher.replaceFirst(Message.valueOf(langPath).getMessage());
            	}
                builder.setDisplayName(Message.convertToColorText(name));
            }
            if (section.contains(loreSection)) {
                List<String> lore = new ArrayList<>();
                for (String line : section.getStringList(loreSection)) {
                	Matcher matcher = langRegex.matcher(line);
                	if (matcher.find()) {
                		String langPath = line.substring(matcher.start(), matcher.end());
                		langPath = langPath.substring(startRegexIndex,langPath.length()-1);
                		line = matcher.replaceFirst(Message.valueOf(langPath).getMessage());
                	}
                    lore.add(Message.convertToColorText(line));
                }
                builder.setLore(lore);
            }
            if (section.contains(durabilitySection)){
                builder.setDurability(config.getInt(durabilitySection));
            }
        }
    }
	
}
