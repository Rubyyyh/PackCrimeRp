package me.rubyyyh.crimepackrp.inventory;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import me.rubyyyh.crimepackrp.CrimePackRp;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ModelInventory implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("ModelGUI")
            .provider(new ModelInventory())
            .size(6, 9)
            .title(ChatColor.BLUE + "Modelli")
            .manager(CrimePackRp.MANAGER)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        contents.fillRow(0, ClickableItem.empty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));
        contents.fillRow(5, ClickableItem.empty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));

        ItemStack itemInHand = player.getInventory().getItemInHand();
        ClickableItem[] items = new ClickableItem[50000];

        for (int i = 0; i < 50000; i++) {
            ItemStack modelItem = itemInHand.clone();
            ItemMeta modelMeta = modelItem.getItemMeta();
            modelMeta.setDisplayName("Modello #" + (i));
            modelMeta.setCustomModelData(i);
            modelItem.setItemMeta(modelMeta);

            items[i] = ClickableItem.of(modelItem, e -> {
                player.getInventory().addItem(modelItem);
            });
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
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
