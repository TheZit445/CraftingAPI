package ru.thezit445.craftingapi.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.thezit445.craftingapi.CraftingAPI;

/**
 * <i>Created on 07.12.2019.</i>
 * Simple resource loader for plugin.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class ResourceLoader {
	
	final private static String resourcesFolder = "resources/";
	
	public ResourceLoader() {
	}
	
	public void uploadFile(String fileName, String targetFilePath) {
		InputStream uploadedFile = CraftingAPI.getInstance().getResource(resourcesFolder+fileName);
		File file = new File(targetFilePath);
		if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
		if (!file.exists()) {
			try {
				Files.copy(uploadedFile, file.getAbsoluteFile().toPath());
			} catch (IOException e) {
				Logger.getLogger(CraftingAPI.class.getName()).log(Level.SEVERE, "Resource load error: ", e);
			}
		}
	}
	
}
