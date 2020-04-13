package ru.thezit445.craftingapi.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

import ru.thezit445.craftingapi.CraftingAPI;
import ru.thezit445.craftingapi.utils.FileManager;

/**
 * <i>Created on 07.12.2019.</i>
 * Custom configuration class.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class PluginConfiguration extends YamlConfiguration {
	
	private String dirPath;
	private String fileName;
	private File file;
	
	public PluginConfiguration(String dirPath, String fileName) {
		this.dirPath = dirPath;
		this.fileName = fileName;
		String path = dirPath+File.separator+fileName+FileManager.dot+FileManager.ymlFileType;
		file = new File(path);
		if (file.exists()) {
			try {
				this.load(file);
			} catch (Exception e) {
				Logger.getLogger(CraftingAPI.class.getName()).log(Level.SEVERE, "Error: ", e);
			}
		} else {
			if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				Logger.getLogger(CraftingAPI.class.getName()).log(Level.SEVERE, "Error: ", e);
			}
		}
	}
	
	public PluginConfiguration(File file) {
		this.dirPath = file.getParentFile().getPath();
		this.fileName = file.getName();
		if (file.exists()) {
			try {
				this.load(file);
			} catch (Exception e) {
				Logger.getLogger(CraftingAPI.class.getName()).log(Level.SEVERE, "Error: ", e);
			}
		} else {
			if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				Logger.getLogger(CraftingAPI.class.getName()).log(Level.SEVERE, "Error: ", e);
			}
		}
	}
	
	public void save() {
		try {
			this.save(file);
		} catch (IOException e) {
			Logger.getLogger(CraftingAPI.class.getName()).log(Level.SEVERE, "Error ", e);
		}
	}
	
	public String getDirPath() {
		return dirPath;
	}
	
	public String getFileName() {
		return fileName;
	}
	
}
