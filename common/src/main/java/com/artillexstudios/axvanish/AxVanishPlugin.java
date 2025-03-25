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
import com.artillexstudios.axvanish.utils.VanishStateManagerFactory;
import org.bukkit.Bukkit;
import revxrsal.zapper.Dependency;
import revxrsal.zapper.DependencyManager;
import revxrsal.zapper.relocation.Relocation;
import revxrsal.zapper.repository.Repository;

public final class AxVanishPlugin extends AxPlugin {
    private static AxVanishPlugin instance;
    private AxMetrics metrics;
    private VanishStateManagerFactory stateManagerFactory;
    private AxVanishCommand command;

    @Override
    public void dependencies(DependencyManager manager) {
        manager.repository(Repository.mavenCentral());
        manager.repository(Repository.jitpack());
        manager.repository(Repository.maven("https://repo.codemc.org/repository/maven-public/"));
        manager.repository(Repository.maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"));
        manager.dependency(new Dependency("dev.jorel", "commandapi-bukkit-shade", "9.7.0", null, true));
        manager.dependency("com{}h2database:h2:2.3.232".replace("{}", "."));
        manager.dependency("com{}zaxxer:HikariCP:5.1.0".replace("{}", "."));
        manager.dependency("org{}jooq:jooq:3.19.10".replace("{}", "."));
        manager.relocate(new Relocation("dev{}jorel{}commandapi".replace("{}", "."), "com.artillexstudios.axvanish.libs.commandapi"));
        manager.relocate(new Relocation("com{}zaxxer".replace("{}", "."), "com.artillexstudios.axvanish.libs.hikaricp"));
        manager.relocate(new Relocation("org{}jooq".replace("{}", "."), "com.artillexstudios.axvanish.libs.jooq"));
        manager.relocate(new Relocation("org{}h2".replace("{}", "."), "com.artillexstudios.axvanish.libs.h2"));
    }

    @Override
    public void updateFlags(FeatureFlags flags) {
        flags.PLACEHOLDER_API_HOOK.set(true);
        flags.PLACEHOLDER_API_IDENTIFIER.set("axvanish");
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
        this.command.register();
        Groups.reload();
        this.command.enable();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        PlaceholderRegistry.INSTANCE.register();
    }

    @Override
    public void disable() {
        this.metrics.cancel();

        this.command.disable();
    }

    public VanishStateManagerFactory stateManagerFactory() {
        return this.stateManagerFactory;
    }

    public static AxVanishPlugin instance() {
        return instance;
    }
}
