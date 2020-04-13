package ru.thezit445.craftingapi.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.thezit445.craftingapi.CraftingAPI;

/**
 * <i>Created on 07.12.2019.</i>
 * Simple file manager for plugin.
 * @author Titov Kirill <thezit445@yandex.ru>
 * @version 2.1.0
 */
public class FileManager {
	
	public static final String mainFolder = CraftingAPI.getInstance().getDataFolder().getPath();
	
	public static final String dot = ".";
	public static final String ymlFileType = "yml";
	public static final String jsonFileType = "json";
	public static final String tempFileType = "temp";
	
	public File[] getFiles(Folder folder) {
		File dir = new File(folder.path);
		File[] files = dir.listFiles();
		return files;
	}
	
	public File[] getFiles(Folder folder, String localPath) {
		File dir = new File(folder.path+File.separator+localPath);
		File[] files = dir.listFiles();
		return files;
	}
	
	public File[] getFiles(String path) {
		File dir = new File(path);
		File[] files = dir.listFiles();
		return files;
	}
	
	public File getFile(Folder folder, String fileName, String fileType) {
		String filePath = folder.path+File.separator+fileName+dot+fileType;
		File file = new File(filePath);
		return file;
	}
	
	public File getFile(Folder folder, String localPath, String fileName, String fileType) {
		String filePath = folder.path+File.separator+localPath+File.separator+fileName+dot+fileType;
		File file = new File(filePath);
		return file;
	}
	
	public File createFile(Folder folder, String fileName, String fileType) {
		String filePath = folder.path+File.separator+fileName+dot+fileType;
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException ex) {
				Logger.getLogger(CraftingAPI.class.getName()).log(Level.SEVERE, "Create file error: ", ex);
			}
		}
		return file;
	}
	
	public File createFile(Folder folder, String localPath, String fileName, String fileType) {
		String filePath = folder.path+File.separator+localPath+File.separator+fileName+dot+fileType;
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException ex) {
				Logger.getLogger(CraftingAPI.class.getName()).log(Level.SEVERE, "Create file error: ", ex);
			}
		}
		return file;
	}
	
	public String getFileContents(File file) throws FileNotFoundException {
		Reader reader = new FileReader(file);
		BufferedReader bufferReader = new BufferedReader(reader);
		String content = "";
		try {
			while (bufferReader.ready()) {
				content+=bufferReader.readLine();
			}
			bufferReader.close();
		} catch (IOException ex) {
			Logger.getLogger(CraftingAPI.class.getName()).log(Level.SEVERE, "File read error: ", ex);
		}
		return content;
	}

	public File createTempFile(String fileName) {
		String filePath = Folder.MAIN.path+File.separator+fileName+dot+tempFileType;
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException ex) {
				Logger.getLogger(CraftingAPI.class.getName()).log(Level.SEVERE, "Create file error: ", ex);
			}
		}
		return file;
	}

	public File getTempFile(String fileName) {
		String filePath = Folder.MAIN.path+File.separator+fileName+dot+tempFileType;
		File file = new File(filePath);
		return file;
	}

	public void writeInFile(File file, String content) {
		Writer writer = null;
		try {
			writer = new FileWriter(file);
			BufferedWriter bufferWriter = new BufferedWriter(writer);
			bufferWriter.write(content);
			bufferWriter.close();
		} catch (IOException ex) {
			Logger.getLogger(CraftingAPI.class.getName()).log(Level.SEVERE, "File write error: ", ex);
		}
	}

	public void deleteTempFile(File file) {
		if (file.exists()) {
			file.delete();
		}
	}
	
	public enum Folder {
		
		MAIN(mainFolder),
		CONFIGS(mainFolder+File.separator+"configs"),
		RECIPES(mainFolder+File.separator+"recipes");
		
		private String path;
		
		Folder(String path){
			this.path = path;
		}
		
		public String getPath() {
			return path;
		}
		
	}
	
}
