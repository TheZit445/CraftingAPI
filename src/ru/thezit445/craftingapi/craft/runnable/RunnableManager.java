package ru.thezit445.craftingapi.craft.runnable;

import org.bukkit.Location;
import ru.thezit445.craftingapi.CraftingAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RunnableManager {

    private static final String tempFileName = "runnables";

    private static final Map<Location, SerializableRunnable> runnables = new HashMap<>();

    public static void addRunnable(Location location, SerializableRunnable runnable) {
        runnables.put(location, runnable);
    }

    public static boolean isRunned(Location location) {
        return runnables.containsKey(location);
    }

    public static void removeRunnable(Location location) {
        runnables.remove(location);
    }

    public static SerializableRunnable getRunnable(Location location) {
        if (isRunned(location))
            return runnables.get(location);
        return null;
    }

    public static void createTempFiles() {
        String temps = "";
        for (SerializableRunnable serializableRunnable : runnables.values()) {
            temps += serializableRunnable.serialize() + "#";
        }
        if (temps.isEmpty()) return;
        temps = temps.substring(0, temps.length()-1);
        File file = CraftingAPI.getFileManager().createTempFile(tempFileName);
        CraftingAPI.getFileManager().writeInFile(file, temps);
    }

    public static void loadRunnables() {
        File file = CraftingAPI.getFileManager().getTempFile(tempFileName);
        if (!file.exists()) return;
        try {
            String line = CraftingAPI.getFileManager().getFileContents(file);
            String[] temps = line.split("#");
            for (String temp : temps) {
                SerializableRunnable runnable = SerializableRunnable.deserialize(temp);
                addRunnable(runnable.getLocation(), runnable);
                runnable.runTaskTimer(CraftingAPI.getInstance(), 1, 1);
            }
        } catch (FileNotFoundException e) {
            Logger.getLogger(CraftingAPI.class.getName()).log(Level.SEVERE, null, e);
        }
        CraftingAPI.getFileManager().deleteTempFile(file);
    }

}
