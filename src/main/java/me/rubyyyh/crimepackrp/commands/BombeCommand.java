package me.rubyyyh.crimepackrp.commands;

import me.rubyyyh.crimepackrp.CrimePackRp;
import me.rubyyyh.crimepackrp.Data;
import me.rubyyyh.crimepackrp.events.DetonatorListener;
import me.rubyyyh.crimepackrp.inventory.BombaInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class BombeCommand implements CommandExecutor, TabCompleter {

    public static ItemStack createPc() {

        ItemStack SkullPC = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta myAwesomeSkullMeta = (SkullMeta) SkullPC.getItemMeta();
        myAwesomeSkullMeta.setOwner(CrimePackRp.plugin.getConfig().getString("debug.nickPC"));
        myAwesomeSkullMeta.setDisplayName("§4Monitor Bombe");
        SkullPC.setItemMeta(myAwesomeSkullMeta);


        return SkullPC;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Questo comando può essere eseguito solo da un giocatore.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Data.prefix + "Usage: /CrimePack <commands> ");
            return true;
        }
        if (args[0].equalsIgnoreCase("view") && player.hasPermission("bomb.commands.view")) {
            BombaInventory.INVENTORY.open(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("get") && player.hasPermission("bomb.commands.get")) {

            if (args[1].equalsIgnoreCase("bomba")) {

                ItemStack bomba = new ItemStack(Material.BEDROCK);
                ItemMeta bomba_meta = bomba.getItemMeta();
                bomba_meta.setDisplayName("§4Bomba");
                bomba.setItemMeta(bomba_meta);

                player.getInventory().addItem(bomba);
                return true;
            }

            if (args[1].equalsIgnoreCase("bombaTicks")) {

                ItemStack bomba = new ItemStack(Material.IRON_BLOCK);
                ItemMeta bomba_meta = bomba.getItemMeta();
                bomba_meta.setDisplayName("§4Bomba");
                bomba.setItemMeta(bomba_meta);

                player.getInventory().addItem(bomba);
                return true;
            }

            if (args[1].equalsIgnoreCase("pc")) {
                player.getInventory().addItem(createPc());
                player.sendMessage(Data.prefix + ChatColor.GREEN + "Hai ottenuto un PC");
            }
        }

        if (args[1].equalsIgnoreCase("Detonatore")) {
            ItemStack Detonatore = DetonatorListener.createDetonator();
            player.getInventory().addItem(Detonatore);
            player.sendMessage(Data.prefix + ChatColor.GREEN + "Hai ottenuto un detonatore");
        }


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("view");
            completions.add("get");
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("get")) {
                completions.add("bomba");
                completions.add("pc");
                completions.add("detonatore");
                completions.add("bombaTicks");
            }
        }

        return completions;
    }

}

