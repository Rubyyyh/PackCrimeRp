package me.rubyyyh.crimepackrp.events;

import me.rubyyyh.crimepackrp.CrimePackRp;
import me.rubyyyh.crimepackrp.Data;
import me.rubyyyh.crimepackrp.inventory.BombaInventory;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class PcListener implements Listener {

    // Event handler for block placement
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        // Get the player and block involved in the event
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Generate a unique ID and get the location of the block
        UUID uniqueID = UUID.randomUUID();
        Location loc = block.getLocation();

        // Check if the item in the player's hand is a player head with a specific display name
        if (!(player.getInventory().getItemInHand().getType().equals(Material.PLAYER_HEAD)
                && player.getInventory().getItemInHand().getItemMeta().getDisplayName().equals("§4Monitor Bombe"))) {
            return;
        }

        // Check if the event is cancelled or the block can't be built
        if (!event.canBuild() || event.isCancelled()) {
            return;
        }

        // Store the unique ID and location in the Data.pcs map
        Data.pcs.put(uniqueID, loc);

        // Set a default password
        String defaultPassword = Data.defaultConfigPassword;
        String password = new String("password123");

        // Store the location and password in the plugin's config
        CrimePackRp.plugin.getConfig().set("pc_piazzati." + uniqueID + "." + "location", loc);
        CrimePackRp.plugin.getConfig().set("pc_piazzati." + uniqueID + "." + "password", password);
        CrimePackRp.plugin.saveConfig();
    }

    // Event handler for block break
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Get the player and block involved in the event
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Get the location of the block
        Location blockLocation = block.getLocation();

        // Iterate over the entries in the Data.pcs map
        for (Map.Entry<UUID, Location> entry : Data.pcs.entrySet()) {
            UUID uuid = entry.getKey();
            Location storedLocation = entry.getValue();

            // Check if the stored location matches the block location
            if (storedLocation.equals(blockLocation)) {
                // Prevent the block from dropping items
                event.setDropItems(false);

                // Create a new player head item stack with a specific owner and display name
                ItemStack SkullPC = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta myAwesomeSkullMeta = (SkullMeta) SkullPC.getItemMeta();
                myAwesomeSkullMeta.setOwner(CrimePackRp.plugin.getConfig().getString("debug.nickPC"));
                myAwesomeSkullMeta.setDisplayName("§4Monitor Bombe");
                SkullPC.setItemMeta(myAwesomeSkullMeta);

                // Drop the item stack at the block location
                block.getWorld().dropItemNaturally(blockLocation, SkullPC);

                // Remove the entry from the Data.pcs map and the plugin's config
                Data.pcs.remove(uuid);
                CrimePackRp.plugin.getConfig().set("pc_piazzati." + uuid.toString(), null);
                CrimePackRp.plugin.saveConfig();
                break;
            }
        }
    }

    // Event handler for player interaction
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        // Get the player and clicked block involved in the event
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        // Check if the clicked block is null or the action is not a right click
        if (clickedBlock == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Get the location of the clicked block
        Location clickedLocation = clickedBlock.getLocation();

        // Check if the event hand is not the main hand
        if (event.getHand() != EquipmentSlot.HAND) return;

        // Iterate over the entries in the Data.pcs map
        for (Map.Entry<UUID, Location> entry : Data.pcs.entrySet()) {
            Location storedLocation = entry.getValue();
            UUID storedUUID = entry.getKey();

            // Check if the stored location matches the clicked location
            if (storedLocation.getWorld().equals(clickedLocation.getWorld()) &&
                    storedLocation.getBlockX() == clickedLocation.getBlockX() &&
                    storedLocation.getBlockY() == clickedLocation.getBlockY() &&
                    storedLocation.getBlockZ() == clickedLocation.getBlockZ()) {

                // Create a new glass pane item stack with a custom model data and lore
                ItemStack anvilVetro = new ItemStack(Material.GLASS_PANE);
                ItemMeta meta = anvilVetro.getItemMeta();
                meta.setCustomModelData(76);
                meta.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.GREEN + "Inserisci la password per accedere al PC!"));
                anvilVetro.setItemMeta(meta);

                // Create a new glass pane item stack with a custom model data and lore
                ItemStack anvilVetroFinale = new ItemStack(Material.GLASS_PANE);
                ItemMeta meta1 = anvilVetroFinale.getItemMeta();
                meta1.setCustomModelData(76);
                meta1.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.GREEN + "✔ Conferma"));
                anvilVetroFinale.setItemMeta(meta1);

                // Store the player's UUID and the stored UUID in the Data.pc_occupato map
                Data.pc_occupato.put(player.getUniqueId(), storedUUID);

                // Create a new AnvilGUI builder
                new AnvilGUI.Builder()
                        .onClick((slot, stateSnapshot) -> {
                            // Get the player involved in the state snapshot
                            Player playerPC = stateSnapshot.getPlayer();

                            // Check if the slot is not the output slot
                            if (slot != AnvilGUI.Slot.OUTPUT) {
                                return Collections.emptyList();
                            }

                            // Get the password from the plugin's config
                            String password = CrimePackRp.plugin.getConfig().getString("pc_piazzati." + storedUUID + ".password");

                            // Check if the input text matches the password
                            if (stateSnapshot.getText().equals(password)) {
                                // Check if the password is the default password
                                if (password.equals("password123")) {
                                    // Send a message to the player
                                    playerPC.sendMessage(Data.prefix + "§cPassword di default! Cambiala subito!");

                                    // Create a new AnvilGUI builder
                                    new AnvilGUI.Builder()
                                            .onClick((slot1, stateSnapshot1) -> {
                                                // Set the new password in the plugin's config
                                                CrimePackRp.plugin.getConfig().set("pc_piazzati." + storedUUID + ".password", stateSnapshot1.getText());
                                                // Save and reload the plugin's config
                                                CrimePackRp.plugin.saveConfig();
                                                CrimePackRp.plugin.reloadConfig();
                                                return AnvilGUI.Response.close();

                                            })
                                            .text("Inserisci..")
                                            .title("Inserisci una nuova password")
                                            .plugin(CrimePackRp.plugin)
                                            .itemLeft(anvilVetro)
                                            .itemOutput(anvilVetroFinale)
                                            .onClose(stateSnapshot2 -> {
                                                // Remove the entry from the Data.pc_occupato map
                                                Data.pc_occupato.remove(player.getUniqueId());
                                            })
                                            .open(player);

                                } else {
                                    // Send a message to the player
                                    playerPC.sendMessage(Data.prefix + "§aPassword corretta!");
                                    // Open the BombaInventory for the player
                                    BombaInventory.INVENTORY.open(playerPC);
                                }

                                return Arrays.asList(AnvilGUI.ResponseAction.close());
                            } else {
                                // Close the player's inventory and send a message to the player
                                playerPC.closeInventory();
                                playerPC.sendMessage(Data.prefix + "§cPassword errata!");
                                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Password errata!"));

                            }


                        })
                        .text("Inserisci..")
                        .title("Inserisci la password")
                        .plugin(CrimePackRp.plugin)
                        .itemLeft(anvilVetro)
                        .itemOutput(anvilVetroFinale)
                        .open(player);
                break;
            }
        }
    }
}




