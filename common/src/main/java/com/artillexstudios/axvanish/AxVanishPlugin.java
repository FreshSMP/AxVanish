package com.artillexstudios.axvanish;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.metrics.AxMetrics;
import com.artillexstudios.axapi.utils.featureflags.FeatureFlags;
import com.artillexstudios.axvanish.config.Config;
import com.artillexstudios.axvanish.config.Language;
import com.artillexstudios.axvanish.utils.VanishStateManager;

public final class AxVanishPlugin extends AxPlugin {
    private static AxVanishPlugin instance;
    private AxMetrics metrics;
    private VanishStateManager stateManager;

    @Override
    public void updateFlags(FeatureFlags flags) {
        flags.PLACEHOLDER_API_HOOK.set(true);
        flags.PLACEHOLDER_API_IDENTIFIER.set("axvanish");
    }

    @Override
    public void load() {
        instance = this;

        Config.reload();
        Language.reload();

        this.metrics = new AxMetrics(40);
        this.metrics.start();
    }

    @Override
    public void enable() {
        this.stateManager = new VanishStateManager(this);
    }

    @Override
    public void disable() {
        this.metrics.cancel();
    }

    public VanishStateManager stateManager() {
        return this.stateManager;
    }

    public static AxVanishPlugin instance() {
        return instance;
    }
}
