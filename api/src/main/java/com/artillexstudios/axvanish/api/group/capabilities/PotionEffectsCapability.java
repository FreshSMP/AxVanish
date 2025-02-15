package com.artillexstudios.axvanish.api.group.capabilities;

import com.artillexstudios.axvanish.api.event.UserVanishStateChangeEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import revxrsal.commands.bukkit.Version;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PotionEffectsCapability extends VanishCapability implements Listener {
    private final List<PotionEffect> effects;

    public PotionEffectsCapability(Map<String, Object> config) {
        super(config);
        this.effects = new ArrayList<>();
        List<String> effects = (List<String>) config.get("effects");
        if (effects != null) {
            for (String effect : effects) {
                String[] split = effect.split(" ");
                this.effects.add(new PotionEffect(PotionEffectType.getByKey(NamespacedKey.minecraft(split[0])), Version.getServerVersion().isNewerThanOrEqualTo(Version.v1_19_3) ? -1 : Integer.MAX_VALUE, Integer.parseInt(split[1]), true, false));
            }
        }
    }

    public List<PotionEffect> effects() {
        return this.effects;
    }

    @EventHandler
    public void onUserVanishStateChangeEvent(UserVanishStateChangeEvent event) {
        Player player = event.user().onlinePlayer();
        if (player == null) {
            return;
        }

        PotionEffectsCapability capability = event.user().capability(VanishCapabilities.POTION_EFFECTS);
        if (capability == null) {
            return;
        }

        for (PotionEffect effect : capability.effects()) {
            if (event.newState()) {
                event.user().onlinePlayer().addPotionEffect(effect);
            } else {
                event.user().onlinePlayer().removePotionEffect(effect.getType());
            }
        }
    }
}
