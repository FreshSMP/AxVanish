package com.artillexstudios.axvanish;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.metrics.AxMetrics;
import com.artillexstudios.axapi.utils.featureflags.FeatureFlags;
import com.artillexstudios.axvanish.command.AxVanishCommand;
import com.artillexstudios.axvanish.config.Config;
import com.artillexstudios.axvanish.config.Groups;
import com.artillexstudios.axvanish.config.Language;
import com.artillexstudios.axvanish.database.DataHandler;
import com.artillexstudios.axvanish.listeners.PlayerListener;
import com.artillexstudios.axvanish.placeholders.PlaceholderRegistry;
import com.artillexstudios.axvanish.utils.VanishStateManager;
import com.artillexstudios.axvanish.utils.VanishStateManagerFactory;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AxVanishPlugin extends AxPlugin {
    private final JavaPlugin plugin;
    private static AxVanishPlugin instance;
    private AxMetrics metrics;
    private VanishStateManagerFactory stateManagerFactory;

    public AxVanishPlugin(JavaPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void updateFlags(FeatureFlags flags) {
        flags.PLACEHOLDER_API_HOOK.set(true);
        flags.PLACEHOLDER_API_IDENTIFIER.set("axvanish");
    }

    @Override
    public void load() {
        instance = this;
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this.plugin)
                .skipReloadDatapacks(true)
                .setNamespace("axvanish")
        );

        Config.reload();
        Language.reload();
        DataHandler.setup();

        this.metrics = new AxMetrics(40);
        this.metrics.start();
    }

    @Override
    public void enable() {
        this.stateManagerFactory = new VanishStateManagerFactory(this.plugin);
        new AxVanishCommand(this).register();
        Groups.reload();
        CommandAPI.onEnable();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this.plugin), this.plugin);
        PlaceholderRegistry.INSTANCE.register();
    }

    @Override
    public void disable() {
        this.metrics.cancel();

        CommandAPI.onDisable();
    }

    public VanishStateManagerFactory stateManagerFactory() {
        return this.stateManagerFactory;
    }

    public static AxVanishPlugin instance() {
        return instance;
    }
}
