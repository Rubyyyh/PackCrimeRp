package me.rubyyyh.crimepackrp.events;

import me.rubyyyh.crimepackrp.inventory.ProximityBombInventory;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DetonatorListener implements Listener {

    public static ItemStack createDetonator() {

        ItemStack detonator = new ItemStack(Material.STICK);
        ItemMeta meta = detonator.getItemMeta();
        meta.setDisplayName("Detonatore");
        meta.setCustomModelData(101);
        detonator.setItemMeta(meta);
        return detonator;
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
            if (item.getItemMeta().getCustomModelData() == 101) {
                ProximityBombInventory.INVENTORY.open(event.getPlayer());
            }
        }
    }
}
