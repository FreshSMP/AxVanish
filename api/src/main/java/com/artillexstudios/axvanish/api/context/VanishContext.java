package com.artillexstudios.axvanish.api.context;

import java.util.HashMap;
import java.util.Map;

public final class VanishContext {
    private final Map<Class<? extends VanishSource>, VanishSource> sources;

    VanishContext(Map<Class<? extends VanishSource>, VanishSource> sources) {
        this.sources = sources;
    }

    public VanishSource getSource(Class<? extends VanishSource> sourceClass) {
        return this.sources.get(sourceClass);
    }

    public static class Builder {
        private final Map<Class<? extends VanishSource>, VanishSource> sources = new HashMap<>();

        public Builder withSource(VanishSource source) {
            this.sources.put(source.getClass(), source);
            return this;
        }

        public VanishContext build() {
            return new VanishContext(this.sources);
        }
    }
}
