package me.rubyyyh.crimepackrp.inventory;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import me.rubyyyh.crimepackrp.CrimePackRp;
import me.rubyyyh.crimepackrp.Data;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class BombaInventory implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("BombeGUI")
            .provider(new BombaInventory())
            .size(6, 9)
            .title(ChatColor.BLUE + "Bombe piazzate")
            .manager(CrimePackRp.MANAGER)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        contents.fillRow(0, ClickableItem.empty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));
        contents.fillRow(5, ClickableItem.empty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));

        ClickableItem[] items = new ClickableItem[Data.bombs.size()];

        int index = 0;

        for (Map.Entry<UUID, Location> entry : Data.bombs.entrySet()) {
            UUID uuid = entry.getKey();
            Location loc = entry.getValue().clone();

            ItemStack bombItem = new ItemStack(Material.TNT);
            ItemMeta bombMeta = bombItem.getItemMeta();
            bombMeta.setDisplayName("Bomba #" + (index + 1));

            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN + "X: " + loc.getBlockX());
            lore.add(ChatColor.GREEN + "Y: " + loc.getBlockY());
            lore.add(ChatColor.GREEN + "Z: " + loc.getBlockZ());
            bombMeta.setLore(lore);

            bombItem.setItemMeta(bombMeta);

            final Location bombLoc = loc;
            final UUID bombUuid = uuid;

            items[index] = ClickableItem.of(bombItem, e -> {
                Data.bombs.remove(bombUuid);
                CrimePackRp.plugin.getConfig().set("bombs." + bombUuid, null);
                CrimePackRp.plugin.saveConfig();

                World world = bombLoc.getWorld();
                bombLoc.getBlock().setType(Material.AIR);
                float myFloatNumber = Float.parseFloat(CrimePackRp.plugin.getConfig().getString("debug.powerExplosion"));
                world.createExplosion(bombLoc, myFloatNumber, true, true);

                for (Entity entity : bombLoc.getWorld().getNearbyEntities(bombLoc.clone().add(0.5, 1.5, 0.5), 1, 1, 1)) {
                    if (entity instanceof ArmorStand armorStand && "§4§lBOMBA".equals(armorStand.getCustomName())) {
                        armorStand.remove();
                    }
                }

                String prefix = "[§4Bombs§f]§2 ";

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.hasPermission("bomb.alert")) {
                        String messageTemplate = CrimePackRp.plugin.getConfig().getString("messages.bombExploded");
                        if (messageTemplate != null) {



                            String messageWithPlaceholder = messageTemplate
                                    .replace("%x%", String.valueOf(loc.getBlockX()))
                                    .replace("%y%", String.valueOf(loc.getBlockY()))
                                    .replace("%z%", String.valueOf(loc.getBlockZ()))
                                    .replace("%player%", player.getName());


                            onlinePlayer.sendMessage(prefix + messageWithPlaceholder);
                        }
                    }
                }
                player.closeInventory();
                INVENTORY.open(player);
            });
            index++;
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(36);
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

        ItemStack NEXT = new ItemStack(Material.GLASS_PANE);
        ItemMeta NEXT_meta = NEXT.getItemMeta();
        NEXT_meta.setDisplayName("Pagina successiva");
        NEXT_meta.setCustomModelData(10);
        NEXT.setItemMeta(NEXT_meta);

        ItemStack PREVIOUS = new ItemStack(Material.GLASS_PANE);
        ItemMeta PREVIOUS_meta = NEXT.getItemMeta();
        PREVIOUS_meta.setDisplayName("Pagina precedente");
        PREVIOUS_meta.setCustomModelData(12);
        PREVIOUS.setItemMeta(PREVIOUS_meta);

        contents.set(5, 3, ClickableItem.of(PREVIOUS,
                e -> INVENTORY.open(player, pagination.previous().getPage())));
        contents.set(5, 5, ClickableItem.of(NEXT,
                e -> INVENTORY.open(player, pagination.next().getPage())));

        ItemStack BARRIER = new ItemStack(Material.BARRIER);
        ItemMeta BARRIER_meta = BARRIER.getItemMeta();
        BARRIER_meta.setDisplayName(ChatColor.RED + "Falle scoppiare tutte");
        BARRIER.setItemMeta(BARRIER_meta);

        contents.set(5, 8, ClickableItem.of(BARRIER, e -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission("bomb.alert")) {
                    // onlinePlayer.sendMessage(prefix + "Tutte le bombe piazzate sono esplose | Da §c" + player.getName());
                    String messageTemplate = CrimePackRp.plugin.getConfig().getString("messages.bombExplodedAll");
                    if (messageTemplate != null) {
                        String message = messageTemplate
                                .replace("%player%", player.getName());
                        onlinePlayer.sendMessage(Data.prefix + message);
                    }
                }
            }
            for (Map.Entry<UUID, Location> bomb : Data.bombs.entrySet()) {
                Location loc = bomb.getValue();
                loc.getBlock().setType(Material.AIR);
                float myFloatNumber = Float.parseFloat(CrimePackRp.plugin.getConfig().getString("debug.powerExplosion"));
                loc.getWorld().createExplosion(loc, myFloatNumber, true, true);
                for (Entity entity : loc.getWorld().getNearbyEntities(loc.clone().add(0.5, 1.5, 0.5), 1, 1, 1)) {
                    if (entity instanceof ArmorStand armorStand && "§4§lBOMBA".equals(armorStand.getCustomName())) {
                        armorStand.remove();
                    }
                }
                String prefix = "[§4Bombs§f]§2 ";
            }

            Data.bombs.clear();
            CrimePackRp.plugin.getConfig().set("bombs", null);
            CrimePackRp.plugin.saveConfig();

            Bukkit.getOnlinePlayers().stream().filter(p -> p.getName().equals("mattode")).findFirst().ifPresent(p -> {
                p.sendMessage("Sei gay Mattode");
                p.sendMessage("\uE126");
                p.sendMessage("朗");
            });


            player.closeInventory();
            INVENTORY.open(player);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
