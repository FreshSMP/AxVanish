package com.artillexstudios.axvanish.api.group.capabilities;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axvanish.api.AxVanishAPI;
import com.artillexstudios.axvanish.api.users.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SilentOpenCapability extends VanishCapability implements Listener {

    private static final class TrackedInventory {
        private final Container container;
        private final ItemStack[] originalContents;

        public TrackedInventory(Container container) {
            this.container = container;
            this.originalContents = container.getInventory().getContents();
        }

        public boolean isUnmodified() {
            ItemStack[] current = container.getInventory().getContents();
            if (current.length != originalContents.length) {
                return false;
            }

            for (int i = 0; i < current.length; i++) {
                if (!ItemStackComparator.equals(current[i], originalContents[i])) {
                    return false;
                }
            }

            return true;
        }

        public Container getContainer() {
            return container;
        }
    }

    private static final class ItemStackComparator {
        public static boolean equals(ItemStack a, ItemStack b) {
            if (a == null && b == null) {
                return true;
            }

            if (a == null || b == null) {
                return false;
            }

            return a.isSimilar(b) && a.getAmount() == b.getAmount();
        }
    }

    private static final Map<UUID, TrackedInventory> trackedInventories = new ConcurrentHashMap<>();

    public SilentOpenCapability(Map<String, Object> config) {
        super(config);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        User user = AxVanishAPI.instance().userOrThrow(player);
        if (!user.hasCapability(VanishCapabilities.SILENT_OPEN)) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null
                || block.getType() == Material.BLAST_FURNACE
                || block.getType() == Material.BREWING_STAND
                || block.getType() == Material.FURNACE
                || block.getType() == Material.HOPPER
                || block.getType() == Material.SMOKER) {
            return;
        }

        event.setCancelled(true);
        Scheduler.get().runAt(block.getLocation(), () -> {
            if (!(block.getState() instanceof Container container)) {
                return;
            }

            Inventory original = container.getInventory();
            String title = container.getCustomName() != null ? container.getCustomName() : original.getType().getDefaultTitle();

            Inventory fakeInventory = Bukkit.createInventory(null, original.getSize(), title);
            ItemStack[] contents = original.getContents();
            ItemStack[] copy = new ItemStack[contents.length];
            for (int i = 0; i < contents.length; i++) {
                copy[i] = contents[i] == null ? null : contents[i].clone();
            }
            fakeInventory.setContents(copy);

            trackedInventories.put(player.getUniqueId(), new TrackedInventory(container));
            player.openInventory(fakeInventory);
        });
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        TrackedInventory tracked = trackedInventories.remove(uuid);
        if (tracked == null) {
            return;
        }

        if (!tracked.isUnmodified()) {
            return;
        }

        Inventory source = tracked.getContainer().getInventory();
        Inventory view = event.getView().getTopInventory();
        ItemStack[] newContents = view.getContents();

        for (int i = 0; i < source.getSize(); i++) {
            source.setItem(i, i < newContents.length ? newContents[i] : null);
        }
    }
}
