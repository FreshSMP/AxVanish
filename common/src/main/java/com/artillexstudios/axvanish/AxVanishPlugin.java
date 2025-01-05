package com.artillexstudios.axvanish;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.metrics.AxMetrics;
import com.artillexstudios.axapi.utils.featureflags.FeatureFlags;
import com.artillexstudios.axvanish.config.Config;
import com.artillexstudios.axvanish.config.Language;

public final class AxVanishPlugin extends AxPlugin {
    private static AxVanishPlugin instance;
    private AxMetrics metrics;

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
        super.enable();
    }

    @Override
    public void disable() {
        this.metrics.cancel();
    }

    public static AxVanishPlugin instance() {
        return instance;
    }
}
