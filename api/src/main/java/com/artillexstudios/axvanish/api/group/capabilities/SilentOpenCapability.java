package com.artillexstudios.axvanish.api.group.capabilities;

import com.artillexstudios.axapi.utils.LogUtils;
import com.artillexstudios.axvanish.api.AxVanishAPI;
import com.artillexstudios.axvanish.api.users.User;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SilentOpenCapability extends VanishCapability implements Listener {
    private static final Map<UUID, Inventory> inventories = new HashMap<>();

    public SilentOpenCapability(Map<String, Object> config) {
        super(config);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = AxVanishAPI.instance().userOrThrow(player);
        if (!user.hasCapability(VanishCapabilities.SILENT_OPEN)) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        if (!(block.getState() instanceof Container container)) {
            return;
        }

        event.setCancelled(true);
        Inventory inventory = Bukkit.createInventory(null, container.getInventory().getSize(), container.getCustomName() == null ? container.getInventory().getType().getDefaultTitle() : container.getCustomName());
        inventory.setContents(container.getInventory().getContents());
        inventories.put(event.getPlayer().getUniqueId(), container.getInventory());
        event.getPlayer().openInventory(inventory);
    }


    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        Inventory other = inventories.remove(event.getPlayer().getUniqueId());
        if (other == null) {
            return;
        }

        other.setContents(event.getView().getTopInventory().getContents());
    }
}
