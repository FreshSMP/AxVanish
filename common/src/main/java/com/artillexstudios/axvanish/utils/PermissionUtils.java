package com.artillexstudios.axvanish.utils;

import com.artillexstudios.axvanish.config.Groups;
import com.artillexstudios.axvanish.group.Group;
import org.bukkit.entity.Player;

import java.util.Map;

public enum PermissionUtils {
    INSTANCE;

    public Group group(Player player) {
        Group largestGroup = null;
        for (Map.Entry<String, Group> entry : Groups.groups.entrySet()) {
            if (player.hasPermission("axvanish.group." + entry.getKey())) {
                if (largestGroup == null) {
                    largestGroup = entry.getValue();
                    continue;
                }

                if (entry.getValue().priority() > largestGroup.priority()) {
                    largestGroup = entry.getValue();
                }
            }
        }

        return largestGroup;
    }
}
