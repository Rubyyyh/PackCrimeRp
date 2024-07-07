package me.rubyyyh.crimepackrp;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Data {


    public static Map<UUID, Location> bombs = new HashMap<>();


    public static Map<UUID, Location> pcs = new HashMap<>();

    public static Map<UUID, UUID> pc_occupato = new HashMap<>();

    public static String prefix = "[§4Bombs§f]§2 ";

    public static String defaultConfigPassword = CrimePackRp.plugin.getConfig().getString("Password-Default");


}
