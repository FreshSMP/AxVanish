package com.artillexstudios.axvanish.command;

import com.artillexstudios.axapi.utils.LogUtils;
import com.artillexstudios.axapi.utils.MessageUtils;
import com.artillexstudios.axvanish.AxVanishPlugin;
import com.artillexstudios.axvanish.api.AxVanishAPI;
import com.artillexstudios.axvanish.api.context.VanishContext;
import com.artillexstudios.axvanish.api.context.source.CommandVanishSource;
import com.artillexstudios.axvanish.api.users.User;
import com.artillexstudios.axvanish.config.Config;
import com.artillexstudios.axvanish.config.Groups;
import com.artillexstudios.axvanish.config.Language;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public final class AxVanishCommand {
    private final AxVanishPlugin plugin;

    public AxVanishCommand(AxVanishPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        new CommandTree("vanish")
                .withAliases("v", "axvanish")
                .withPermission("axvanish.vanish")
                .executesPlayer((sender, args) -> {
                    User user = AxVanishAPI.instance().getUserIfLoadedImmediately(sender);
                    if (user == null) {
                        MessageUtils.sendMessage(sender, Language.prefix, Language.error.userNotLoaded);
                        return;
                    }
                    if (Config.debug) {
                        LogUtils.debug("Debug is working!");
                    }

                    VanishContext context = new VanishContext.Builder()
                            .withSource(user)
                            .withSource(CommandVanishSource.INSTANCE)
                            .build();
                    boolean previous = user.vanished();
                    if (!user.update(!user.vanished(), context)) {
                        LogUtils.info("Failed to change state!");
                        // The user's visibility was not changed because an event was cancelled
                        return;
                    }

                    if (previous) {
                        MessageUtils.sendMessage(sender, Language.prefix, Language.unVanish.unVanish);
                    } else {
                        MessageUtils.sendMessage(sender, Language.prefix, Language.vanish.vanish);
                    }
                })
                .then(new LiteralArgument("toggle")
                        .withPermission("axvanish.command.toggle.other")
                        .then(new OfflinePlayerArgument("player")
                                .withPermission("axvanish.command.toggle.other")

                        )
                )
                .then(new LiteralArgument("set")
                        .withPermission("axvanish.command.set")
                        .then(new OfflinePlayerArgument("player")
                                .withPermission("axvanish.command.set.other")
                                .then(new BooleanArgument("enabled")
                                        .executes((sender, args) -> {

                                        })
                                )
                        )
                        .then(new BooleanArgument("enabled")
                                .executes((sender, args) -> {

                                })
                        )
                )
                .then(new LiteralArgument("admin")
                        .withPermission("axvanish.command.admin")
                        .then(new LiteralArgument("version")
                                .withPermission("axvanish.command.admin.version")
                                .executes((sender, args) -> {
                                    MessageUtils.sendMessage(sender, Language.prefix, "<green>You are running <white>AxVanish</white> version <white><version></white> on <white><implementation></white> version <white><implementation-version></white> (Implementing API version <white><api-version></white>)",
                                            Placeholder.unparsed("version", this.plugin.getDescription().getVersion()),
                                            Placeholder.unparsed("implementation", Bukkit.getName()),
                                            Placeholder.unparsed("implementation-version", Bukkit.getVersion()),
                                            Placeholder.unparsed("api-version", Bukkit.getBukkitVersion())
                                    );
                                })
                        )
                        .then(new LiteralArgument("reload")
                                .withPermission("axvanish.command.admin.reload")
                                .executes((sender, args) -> {
                                    long start = System.nanoTime();
                                    List<String> failed = new ArrayList<>();

                                    if (!Config.reload()) {
                                        failed.add("config.yml");
                                    }

                                    if (!Language.reload()) {
                                        failed.add("language/" + Language.lastLanguage + ".yml");
                                    }

                                    if (!Groups.reload()) {
                                        failed.add("groups.yml");
                                    }

                                    if (failed.isEmpty()) {
                                        MessageUtils.sendMessage(sender, Language.prefix, Language.reload.success, Placeholder.unparsed("time", Long.toString((System.nanoTime() - start) / 1_000_000)));
                                    } else {
                                        MessageUtils.sendMessage(sender, Language.prefix, Language.reload.fail, Placeholder.unparsed("time", Long.toString((System.nanoTime() - start) / 1_000_000)), Placeholder.unparsed("files", String.join(", ", failed)));
                                    }
                                })
                        )
                )
                .register();
    }
}
