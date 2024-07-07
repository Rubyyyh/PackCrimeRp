package me.rubyyyh.crimepackrp;

import dev.respark.licensegate.LicenseGate;
import fr.minuskube.inv.InventoryManager;
import me.rubyyyh.crimepackrp.commands.BombeCommand;
import me.rubyyyh.crimepackrp.commands.ModelCommand;
import me.rubyyyh.crimepackrp.events.BombTickListener;
import me.rubyyyh.crimepackrp.events.BombaListener;
import me.rubyyyh.crimepackrp.events.DetonatorListener;
import me.rubyyyh.crimepackrp.events.PcListener;
import me.rubyyyh.crimepackrp.tasks.BombsTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;


public final class CrimePackRp extends JavaPlugin {
    public static CrimePackRp plugin;
    public static InventoryManager MANAGER;


    @Override
    public void onEnable() {
        String red = "\u001B[31m";
        String reset = "\u001B[0m";



        System.out.println(red + "   _____       _                _____           _    _____  _____  " + reset);
        System.out.println(red + "  / ____|     (_)              |  __ \\         | |  |  __ \\|  __ \\ " + reset);
        System.out.println(red + " | |     _ __ _ _ __ ___   ___| |__) |_ _  ___| | _| |__) | |__) |" + reset);
        System.out.println(red + " | |    | '__ | | '_ ` _ \\ / _ \\  ___/ _` |/ __| |/ /  _  /|  ___/ " + reset);
        System.out.println(red + " | |____| |   | | | | | | |  __/ |  | (_| | (__|   <| | \\ \\| |     " + reset);
        System.out.println(red + "  \\_____|_|  |_|_| |_| |_|\\___|_|   \\__,_|\\___|_|\\_\\_|  \\_\\_|     " + reset);
        System.out.println(red + "                                                                  " + reset);

        LicenseGate licenseGate = new LicenseGate("a1cf2");

        LicenseGate.ValidationType result = licenseGate.verify(this.getConfig().getString("licenseKey"), "CrimePackRp");

        if (result == LicenseGate.ValidationType.VALID) {
            System.out.println("License is valid");
        } else if (result == LicenseGate.ValidationType.EXPIRED) {
            System.out.println("license is expired");
            Bukkit.getPluginManager().disablePlugin(this);

        } else {
            System.out.println("license is invalid");
            Bukkit.getPluginManager().disablePlugin(this);
        }



        plugin = this;
        MANAGER = new InventoryManager(this);


        saveDefaultConfig();

        ConfigurationSection section = getConfig().getConfigurationSection("bombs");
        if (section != null) {
            for (String uuid : section.getKeys(false)) {
                Data.bombs.put(UUID.fromString(uuid), section.getLocation(uuid));
            }
        }

        ConfigurationSection sectionpc = getConfig().getConfigurationSection("pc_piazzati");
        if (sectionpc != null) {
            for (String uuid : sectionpc.getKeys(false)) {
                Data.pcs.put(UUID.fromString(uuid), sectionpc.getLocation(uuid + ".location"));
            }
        }


        new BombsTask();
        MANAGER.init();

        Bukkit.getPluginManager().registerEvents(new BombaListener(), this);
        Bukkit.getPluginManager().registerEvents(new DetonatorListener(), this);
        Bukkit.getPluginManager().registerEvents(new PcListener(), this);
        Bukkit.getPluginManager().registerEvents(new BombTickListener(), this);

        getCommand("model-list").setExecutor(new ModelCommand());
        getCommand("crimepack").setExecutor(new BombeCommand());
        getCommand("crimepack").setTabCompleter(new BombeCommand());



    }


    @Override
    public void onDisable() {
    }





}

