package zzai.clearlogic;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.util.Tristate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.network.chat.Component;

public class Static {
    public static final String MOD_ID = "zzclear";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static Path CONFIG_FILE;
    public static Path Zzclear_FOLDER;
    public static Path CONFIG_FOLDER;
    public static MinecraftServer SERVER = null;
    public static boolean isLuckPerms = false;

    public static void checkFolder(Path path) {
        if (!java.nio.file.Files.exists(path)) {
            try {
                java.nio.file.Files.createDirectories(path);
            } catch (java.io.IOException e) {
                LOGGER.error("Failed to create folder: {}", path, e);
            }
        }
    }

    public static void sendMessage(Player player, String message) {
        player.displayClientMessage(Component.translatable(message), false);
    }

    public static void sendMessageToAllPlayers(Component message, boolean actionBar) {
        new Thread(() -> Optional.ofNullable(SERVER).ifPresent(server -> server.getPlayerList().getPlayers()
                .forEach(player -> player.displayClientMessage(message, actionBar)))).start();
    }

    public static void sendMessageToAllPlayers(MinecraftServer server1, String message, Object... args) {

        new Thread(() -> Optional.ofNullable(server1).ifPresent(server -> server.getPlayerList()
                .broadcastSystemMessage(Component.literal(MessageFormat.format(message, args)), false)))
                .start();

    }

    public static void sendMessageToAllPlayers(String message, Object... args) {
        new Thread(() -> Optional.ofNullable(SERVER).ifPresent(server -> server.getPlayerList()
                .broadcastSystemMessage(Component.literal(MessageFormat.format(message, args)), false)))
                .start();
    }

    public static Boolean hasPermission(ServerPlayer playerEntity, String permission) throws CommandSyntaxException {
        AtomicReference<Boolean> exist = new AtomicReference<>(false);

        LuckPermsProvider.get().getUserManager().loadUser(playerEntity.getUUID())
                .thenApplyAsync(user -> {
                    CachedPermissionData permissionData = user.getCachedData()
                            .getPermissionData(user.getQueryOptions());
                    Tristate tristate = permissionData.checkPermission(permission);
                    if (tristate.equals(Tristate.UNDEFINED)) {
                        return false;
                    }

                    return tristate.asBoolean();
                }).thenAcceptAsync(aBoolean -> {
                    if (aBoolean)
                        exist.set(true);
                });

        return exist.get();
    }

    public static Boolean cmdPermission(CommandSourceStack source, String permission, boolean admin) {
        if (!Static.isLuckPerms) {
            if (admin) {
                // Check if entity is a server player with op status, or if it's the server
                // console
                if (source.getEntity() instanceof ServerPlayer serverPlayer) {
                    return source.getServer().getPlayerList().isOp(serverPlayer.nameAndId());
                }
                return true; // Console/command block = always admin
            } else {
                return true;
            }
        } else if (!(source.getEntity() instanceof ServerPlayer)) {
            return true; // Non-player sources (console) are always admin
        } else {
            try {
                return Static.hasPermission(source.getPlayerOrException(), permission);
            } catch (CommandSyntaxException e) {
                return false;
            }
        }
    }

}
