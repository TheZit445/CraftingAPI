package ru.thezit445.craftingapi.utils;

import org.bukkit.Material;

public enum Fuel {

    LAVA_BUCKET(20000),
    COAL_BLOCK(16000),
    DRIED_KELP_BLOCK(4000),
    BLAZE_ROD(2400),
    COAL(1600),
    CHARCOAL(1600),
    PLANKS(300),
    SLAB(150),
    STICK(100),
    SAPLING(100),
    FENCE(300),
    STAIRS(300),
    TRAPDOOR(300),
    WORKBENCH(300),
    BOOKSHELF(300),
    LOG(300),
    WOOD(300),
    CHEST(300),
    JUKEBOX(300),
    NOTE_BLOCK(300),
    MUSHROOM_BLOCK(300),
    BANNER(300),
    WOOL(100),
    CARPET(67),
    LADDER(300),
    BUTTON(100),
    BOW(300),
    FISHING_ROD(300),
    SIGN(200),
    BOWL(100),
    DOOR(200),
    BOAT(200),
    BAMBOO(50),
    PICKAXE(200),
    AXE(200),
    HOE(200),
    SHOVEL(200),
    SWORD(200),

    NOT_A_FUEL(-1);


    private final int duration;

    Fuel(int duration) {
        this.duration = duration;
    }

    public int getDuration(){
        return duration;
    }

    public static Fuel getFuel(Material material) {
        for (Fuel fuel : Fuel.values()) {
            if (material.name().endsWith(fuel.name())) return fuel;
        }
        return NOT_A_FUEL;
    }

}
