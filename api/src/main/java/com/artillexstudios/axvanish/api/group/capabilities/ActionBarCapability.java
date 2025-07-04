package com.artillexstudios.axvanish.api.group.capabilities;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.ActionBar;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.featureflags.FeatureFlags;
import com.artillexstudios.axvanish.api.AxVanishAPI;
import com.artillexstudios.axvanish.api.users.User;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.regex.Matcher;

public final class ActionBarCapability extends VanishCapability {

    public ActionBarCapability(long refreshInterval, String message) {
        super(null);
        ActionBar cached = FeatureFlags.PLACEHOLDER_PATTERNS.get()
                .stream()
                .map(pattern -> pattern.matcher(message))
                .anyMatch(Matcher::find) ? null : ActionBar.create(StringUtils.format(message));

        Scheduler.get().runAsyncTimer(() -> {
            for (User user : AxVanishAPI.instance().vanished()) {
                if (user.capability(VanishCapabilities.ACTION_BAR) != this) {
                    continue;
                }

                Player player = user.onlinePlayer();
                if (cached != null && player != null) {
                    cached.send(player);
                    continue;
                }

                String messageCopy = message;
                if (ClassUtils.INSTANCE.classExists("me.clip.placeholderapi.PlaceholderAPI")) {
                    messageCopy = PlaceholderAPI.setPlaceholders(player, messageCopy);
                }

                ActionBar actionBar = ActionBar.create(StringUtils.format(messageCopy));
                actionBar.send(player);
            }
        }, 0, refreshInterval);
    }

    public ActionBarCapability(Map<String, Object> config) {
        this((Integer) config.getOrDefault("refresh-interval", 20), (String) config.getOrDefault("message", "<white>You are vanished!"));
    }
}
