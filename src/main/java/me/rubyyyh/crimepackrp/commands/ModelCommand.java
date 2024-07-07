package me.rubyyyh.crimepackrp.commands;

import me.rubyyyh.crimepackrp.inventory.ModelInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ModelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Solo i giocatori possono usare questo comando.");
            return true;
        }

        if (args.length > 0) {
            player.sendMessage("Usage: /model-list");
            return true;
        }

        if (player.getInventory().getItemInMainHand().getType().isAir()) {
            player.sendMessage("Devi avere un item in mano per fare il comando pise");
            return true;
        }

        if (!player.hasPermission("model.command")) {
            player.sendMessage("Non hai i permessi per usare questo comando.");
            return true;
        }

        ModelInventory.INVENTORY.open(player);
        return true;
    }



}
