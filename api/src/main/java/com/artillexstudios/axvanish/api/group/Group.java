package com.artillexstudios.axvanish.api.group;

import com.artillexstudios.axvanish.api.group.capabilities.VanishCapability;

public interface Group {

    String name();

    int priority();

    boolean hasCapability(Class<? extends VanishCapability> capability);

    <T extends VanishCapability> T capability(Class<T> capability);
}
