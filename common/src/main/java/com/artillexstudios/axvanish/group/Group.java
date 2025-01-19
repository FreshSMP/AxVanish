package com.artillexstudios.axvanish.group;

import com.artillexstudios.axvanish.api.group.capabilities.VanishCapabilities;
import com.artillexstudios.axvanish.api.group.capabilities.VanishCapability;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

import java.util.List;
import java.util.Map;

public final class Group implements com.artillexstudios.axvanish.api.group.Group {
    private final String name;
    private final int priority;
    private final ClassToInstanceMap<VanishCapability> capabilities;

    public Group(String name, int priority, List<Map<String, Object>> capabilities) {
        this.name = name;
        this.priority = priority;
        ClassToInstanceMap<VanishCapability> map = MutableClassToInstanceMap.create();
        for (Map<String, Object> capability : capabilities) {
            String type = (String) capability.get("type");
            if (type == null || type.isBlank()) {
                continue;
            }

            VanishCapability vanishCapability = VanishCapabilities.create(type, capability);
            map.put(vanishCapability.getClass(), vanishCapability);
        }
        this.capabilities = ImmutableClassToInstanceMap.copyOf(map);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public int priority() {
        return this.priority;
    }

    @Override
    public boolean hasCapability(Class<? extends VanishCapability> capability) {
        return this.capabilities.containsKey(capability);
    }

    @Override
    public <T extends VanishCapability> T capability(Class<T> capability) {
        return capability.cast(this.capabilities.get(capability));
    }
}
