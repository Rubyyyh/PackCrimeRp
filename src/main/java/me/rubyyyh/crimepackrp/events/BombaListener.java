package me.rubyyyh.crimepackrp.events;

import dev.respark.licensegate.LicenseGate;
import me.rubyyyh.crimepackrp.CrimePackRp;
import me.rubyyyh.crimepackrp.Data;
import net.kyori.adventure.text.Component;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.AbstractMap;
import java.util.UUID;

public class BombaListener implements Listener {

    public ItemStack bomba() {

        ItemStack bomba = new ItemStack(Material.BEDROCK);
        ItemMeta bomba_meta = bomba.getItemMeta();
        bomba_meta.setDisplayName("Bomba");
        bomba.setItemMeta(bomba_meta);

        return bomba;
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();
        Block block = event.getBlock();

        UUID uniqueID = UUID.randomUUID();
        Location loc = block.getLocation();


        if (!(player.getInventory().getItemInHand().getType().equals(Material.BEDROCK)
                && player.getInventory().getItemInHand().getItemMeta().getDisplayName().equals("§4Bomba"))) {
            return;
        }


        if (!event.canBuild() || event.isCancelled()) {
            return;
        }

        if (!(loc.getBlock().getType().equals(Material.BEDROCK))) {
            return;
        }

        Block blockPlaced = event.getBlockPlaced();
        Bukkit.getScheduler().runTaskLater(CrimePackRp.plugin, () -> {
            if (blockPlaced.getType() == Material.AIR) {
                ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
                if (itemInHand.getAmount() > 1) {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                } else {
                    event.getPlayer().getInventory().setItemInMainHand(null);
                }

                blockPlaced.setType(event.getBlock().getType());
            }
        }, 1L);





        Data.bombs.put(uniqueID, loc);

        String prefix = "[§4Bombs§f]§2 ";


        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("bomb.alert")) {

                String messageTemplate = CrimePackRp.plugin.getConfig().getString("messages.bombPlaced");
                if (messageTemplate != null) {
                    String message = messageTemplate
                            .replace("%x%", String.valueOf(loc.getX()))
                            .replace("%y%", String.valueOf(loc.getY()))
                            .replace("%z%", String.valueOf(loc.getZ()))
                            .replace("%player%", player.getName());


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
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location blockLoc = event.getBlock().getLocation();

        for (UUID uuid : Data.bombs.keySet()) {
            if (Data.bombs.get(uuid).equals(blockLoc)) {
                if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("MEGRO");
                    return;
                }
                Data.bombs.remove(uuid);
                CrimePackRp.plugin.getConfig().set("bombs." + uuid, null);
                CrimePackRp.plugin.saveConfig();
                for (Entity entity : blockLoc.getWorld().getNearbyEntities(blockLoc.clone().add(0.5, 1.5, 0.5), 1, 1, 1)) {
                    if (entity instanceof ArmorStand armorStand && "§4§lBOMBA".equals(armorStand.getCustomName())) {
                        armorStand.remove();



                    }
                }
            }


        }
    }
}
