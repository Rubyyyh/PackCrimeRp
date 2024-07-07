package me.rubyyyh.crimepackrp.events;

import me.rubyyyh.crimepackrp.CrimePackRp;
import me.rubyyyh.crimepackrp.Data;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class BombTickListener implements Listener {


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();
        Block block = event.getBlock();

        UUID uniqueID = UUID.randomUUID();
        Location loc = block.getLocation();


        if (!(player.getInventory().getItemInHand().getType().equals(Material.IRON_BLOCK)
                && player.getInventory().getItemInHand().getItemMeta().getDisplayName().equals("§4Bomba"))) {
            return;
        }


        if (!event.canBuild() || event.isCancelled()) {
            return;
        }


        Data.bombs.put(uniqueID, loc);

        String prefix = "[§4Bombs§f]§2 ";


        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("bomb.alert")) {
                // onlinePlayer.sendMessage(prefix + "E' stata piazzata una bomba a coord: " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " Da §c" + player.getName());

                String messageTemplate = CrimePackRp.plugin.getConfig().getString("messages.bombPlaced");
                if (messageTemplate != null) {
                    String message = messageTemplate
                            .replace("%x%", String.valueOf(loc.getX()))
                            .replace("%y%", String.valueOf(loc.getY()))
                            .replace("%z%", String.valueOf(loc.getZ()))
                            .replace("%player%", player.getName());


                    onlinePlayer.sendMessage(prefix + message);
                    onlinePlayer.sendMessage(prefix + message);
                }
            }
        }

        CrimePackRp.plugin.getConfig().set("bombs." + uniqueID, loc);
        CrimePackRp.plugin.saveConfig();

        Location holoLoc = loc.clone().add(0.5, 1.5, 0.5);

        ArmorStand armorStand = block.getWorld().spawn(holoLoc, ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setCustomName("§4§lBOMBA");
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setMarker(true);

        ItemStack vetro = new ItemStack(Material.GLASS_PANE);
        ItemMeta vetro_meta = vetro.getItemMeta();
        vetro_meta.setCustomModelData(18);
        vetro.setItemMeta(vetro_meta);

        new AnvilGUI.Builder()
                .onClick((integer, stateSnapshot) -> {
                    AtomicInteger timer = new AtomicInteger(Integer.parseInt(stateSnapshot.getText()));

                    ArmorStand timerStand = block.getWorld().spawn(holoLoc.clone().add(0, 0.5, 0), ArmorStand.class);
                    timerStand.setCustomNameVisible(true);
                    timerStand.setCustomName("§6§lSecondi rimanenti: " + timer);
                    timerStand.setMarker(true);
                    timerStand.setGravity(false);
                    timerStand.setVisible(false);

                    Bukkit.getScheduler().runTaskTimer(CrimePackRp.plugin, () -> {
                        if (!Data.bombs.containsKey(uniqueID)) {
                            timerStand.remove();
                            return;
                        }
                        timerStand.setCustomName("§6§lSecondi rimanenti: " + timer);
                        timer.getAndDecrement();
                    }, 0L, 20L);

                    Bukkit.getScheduler().runTaskLater(CrimePackRp.plugin, () -> {
                        float myFloatNumber = Float.parseFloat(CrimePackRp.plugin.getConfig().getString("debug.powerExplosion"));
                        if (!Data.bombs.containsKey(uniqueID)) {
                            return;
                        }
                        block.setType(Material.AIR);
                        block.getWorld().createExplosion(loc, myFloatNumber, true, true);

                        for (Entity entity : block.getWorld().getNearbyEntities(holoLoc, 1, 1, 1)) {
                            if (entity instanceof ArmorStand armorStand1 && "§4§lBOMBA".equals(armorStand1.getCustomName())) {
                                armorStand1.remove();
                            }
                        }
                        Data.bombs.remove(uniqueID);
                        CrimePackRp.plugin.getConfig().set("bombs." + uniqueID, null);

                        timerStand.remove();

                        String prefix1 = "[§4Bombs§f]§2 ";
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if (onlinePlayer.hasPermission("bomb.alert")) {
                                String messageTemplate = CrimePackRp.plugin.getConfig().getString("messages.bombExploded");
                                if (messageTemplate != null) {
                                    String messageWithPlaceholder = messageTemplate
                                            .replace("%x%", String.valueOf(loc.getBlockX()))
                                            .replace("%y%", String.valueOf(loc.getBlockY()))
                                            .replace("%z%", String.valueOf(loc.getBlockZ()))
                                            .replace("%player%", player.getName());
                                    onlinePlayer.sendMessage(prefix1 + messageWithPlaceholder);
                                }
                            }
                        }
                    }, timer.get() * 20L);


                    return AnvilGUI.Response.close();

                })
                .itemLeft(vetro)
                .itemOutput(vetro)
                .plugin(CrimePackRp.plugin)
                .text("Inserisci i secondi")
                .title("Timer")
                .preventClose()
                .open(player);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location blockLoc = event.getBlock().getLocation();

        for (UUID uuid : Data.bombs.keySet()) {
            if (Data.bombs.get(uuid).equals(blockLoc)) {
                if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
                    event.setCancelled(true);
                    return;
                }

                    Data.bombs.remove(uuid);
                    CrimePackRp.plugin.getConfig().set("bombs." + uuid, null);
                    CrimePackRp.plugin.saveConfig();
                    for (Entity entity : blockLoc.getWorld().getNearbyEntities(blockLoc.clone().add(0.5, 1.5, 0.5), 1, 1, 1)) {
                        if (entity instanceof ArmorStand armorStand && "§4§lBOMBA".equals(armorStand.getCustomName())) {
                            armorStand.remove();

                        }
                        for (Entity entity1 : blockLoc.getWorld().getNearbyEntities(blockLoc.clone().add(0.5, 1.5, 0.5), 1, 1, 1)) {
                            if (entity1 instanceof ArmorStand armorStand1 && "§6§lSecondi rimanenti: ".equals(armorStand1.getCustomName())) {
                                armorStand1.remove();
                            }
                        }


                    }
            }


        }
    }


}
