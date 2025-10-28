package com.artillexstudios.axvanish;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.dependencies.DependencyManagerWrapper;
import com.artillexstudios.axapi.metrics.AxMetrics;
import com.artillexstudios.axapi.utils.AsyncUtils;
import com.artillexstudios.axapi.utils.featureflags.FeatureFlags;
import com.artillexstudios.axvanish.command.AxVanishCommand;
import com.artillexstudios.axvanish.config.Config;
import com.artillexstudios.axvanish.config.Groups;
import com.artillexstudios.axvanish.config.Language;
import com.artillexstudios.axvanish.database.DataHandler;
import com.artillexstudios.axvanish.listeners.PlayerListener;
import com.artillexstudios.axvanish.placeholders.PlaceholderRegistry;
import com.artillexstudios.axvanish.utils.VanishStateManagerFactory;
import org.bukkit.Bukkit;
import revxrsal.zapper.repository.Repository;

public final class AxVanishPlugin extends AxPlugin {
    private static AxVanishPlugin instance;
    private AxMetrics metrics;
    private VanishStateManagerFactory stateManagerFactory;
    private AxVanishCommand command;

    @Override
    public void dependencies(DependencyManagerWrapper manager) {
        manager.repository(Repository.mavenCentral());
        manager.repository(Repository.jitpack());
        manager.repository("https://repo.codemc.org/repository/maven-public/");
        manager.repository("https://hub.spigotmc.org/nexus/content/repositories/snapshots/");
        manager.dependency("dev{}jorel:commandapi-paper-shade:11.0.0");
        manager.dependency("com{}h2database:h2:2.4.240");
        manager.dependency("com{}zaxxer:HikariCP:7.0.2");
        manager.dependency("org{}jooq:jooq:3.20.8");
        manager.relocate("dev{}jorel{}commandapi", "com.artillexstudios.axvanish.libs.commandapi");
        manager.relocate("com{}zaxxer", "com.artillexstudios.axvanish.libs.hikaricp");
        manager.relocate("org{}jooq", "com.artillexstudios.axvanish.libs.jooq");
        manager.relocate("org{}h2", "com.artillexstudios.axvanish.libs.h2");
    }

    @Override
    public void updateFlags() {
        FeatureFlags.PLACEHOLDER_API_HOOK.set(true);
        FeatureFlags.PLACEHOLDER_API_IDENTIFIER.set("axvanish");
        FeatureFlags.ASYNC_UTILS_POOL_SIZE.set(Config.asyncUtilsThreadCount);
        FeatureFlags.ENABLE_PACKET_LISTENERS.set(false);
    }

    @Override
    public void load() {
        instance = this;
        this.command = new AxVanishCommand(this);
        this.command.load();

        Config.reload();
        Language.reload();
        DataHandler.setup();

        this.metrics = new AxMetrics(this, 40);
        this.metrics.start();
    }

    @Override
    public void enable() {
        this.stateManagerFactory = new VanishStateManagerFactory(this);
        this.command.enable();
        this.command.register();
        Groups.reload();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        PlaceholderRegistry.INSTANCE.register();
    }

    @Override
    public void disable() {
        this.metrics.cancel();
        this.command.disable();
        AsyncUtils.stop();
    }

    public VanishStateManagerFactory stateManagerFactory() {
        return this.stateManagerFactory;
    }

    public static AxVanishPlugin instance() {
        return instance;
    }
}
