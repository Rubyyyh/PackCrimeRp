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

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();
        Block block = event.getBlock();

        UUID uniqueID = UUID.randomUUID();
        Location loc = block.getLocation();

        if (!(player.getInventory().getItemInHand().getType().equals(Material.PLAYER_HEAD)
                && player.getInventory().getItemInHand().getItemMeta().getDisplayName().equals("§4Monitor Bombe"))) {
            return;
        }

        if (!event.canBuild() || event.isCancelled()) {
            return;
        }

        Data.pcs.put(uniqueID, loc);

        String defaultPassword = Data.defaultConfigPassword;
        String password = new String("password123");

        CrimePackRp.plugin.getConfig().set("pc_piazzati." + uniqueID + "." + "location", loc);
        CrimePackRp.plugin.getConfig().set("pc_piazzati." + uniqueID + "." + "password", password);
        CrimePackRp.plugin.saveConfig();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        Location blockLocation = block.getLocation();

        for (Map.Entry<UUID, Location> entry : Data.pcs.entrySet()) {
            UUID uuid = entry.getKey();
            Location storedLocation = entry.getValue();

            if (storedLocation.equals(blockLocation)) {
                event.setDropItems(false);

                ItemStack SkullPC = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta myAwesomeSkullMeta = (SkullMeta) SkullPC.getItemMeta();
                myAwesomeSkullMeta.setOwner(CrimePackRp.plugin.getConfig().getString("debug.nickPC"));
                myAwesomeSkullMeta.setDisplayName("§4Monitor Bombe");
                SkullPC.setItemMeta(myAwesomeSkullMeta);

                block.getWorld().dropItemNaturally(blockLocation, SkullPC);

                Data.pcs.remove(uuid);
                CrimePackRp.plugin.getConfig().set("pc_piazzati." + uuid.toString(), null);
                CrimePackRp.plugin.saveConfig();
                break;
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Location clickedLocation = clickedBlock.getLocation();

        if (event.getHand() != EquipmentSlot.HAND) return;

        for (Map.Entry<UUID, Location> entry : Data.pcs.entrySet()) {
            Location storedLocation = entry.getValue();
            UUID storedUUID = entry.getKey();

            if (storedLocation.getWorld().equals(clickedLocation.getWorld()) &&
                    storedLocation.getBlockX() == clickedLocation.getBlockX() &&
                    storedLocation.getBlockY() == clickedLocation.getBlockY() &&
                    storedLocation.getBlockZ() == clickedLocation.getBlockZ()) {

                ItemStack anvilVetro = new ItemStack(Material.GLASS_PANE);
                ItemMeta meta = anvilVetro.getItemMeta();
                meta.setCustomModelData(76);
                meta.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.GREEN + "Inserisci la password per accedere al PC!"));
                anvilVetro.setItemMeta(meta);

                ItemStack anvilVetroFinale = new ItemStack(Material.GLASS_PANE);
                ItemMeta meta1 = anvilVetroFinale.getItemMeta();
                meta1.setCustomModelData(76);
                meta1.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.GREEN + "✔ Conferma"));
                anvilVetroFinale.setItemMeta(meta1);

                Data.pc_occupato.put(player.getUniqueId(), storedUUID);

                new AnvilGUI.Builder()
                        .onClick((slot, stateSnapshot) -> {
                            Player playerPC = stateSnapshot.getPlayer();

                            if (slot != AnvilGUI.Slot.OUTPUT) {
                                return Collections.emptyList();
                            }

                            String password = CrimePackRp.plugin.getConfig().getString("pc_piazzati." + storedUUID + ".password");

                            if (stateSnapshot.getText().equals(password)) {
                                if (password.equals("password123")) {
                                    playerPC.sendMessage(Data.prefix + "§cPassword di default! Cambiala subito!");

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
                                    playerPC.sendMessage(Data.prefix + "§aPassword corretta!");
                                    BombaInventory.INVENTORY.open(playerPC);
                                }

                                return Arrays.asList(AnvilGUI.ResponseAction.close());
                            } else {
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




