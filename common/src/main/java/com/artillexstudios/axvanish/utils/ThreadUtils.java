package com.artillexstudios.axvanish.utils;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.logging.LogUtils;
import org.bukkit.Location;

public final class ThreadUtils {

    public static void ensureMain(Location location, String message) {
        if (!Scheduler.get().isOwnedByCurrentRegion(location)) {
            LogUtils.error("Thread {} failed main thread check: {}", Thread.currentThread().getName(), message, new Throwable());
            throw new IllegalStateException(message);
        }
    }

    public static void ensureMain(String message) {
        if (!Scheduler.get().isGlobalTickThread()) {
            LogUtils.error("Thread {} failed main thread check: {}", Thread.currentThread().getName(), message, new Throwable());
            throw new IllegalStateException(message);
        }
    }

    public static void ensureMain(Location location) {
        ensureMain(location, "");
    }
}
