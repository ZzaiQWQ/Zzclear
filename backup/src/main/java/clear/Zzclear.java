package clear;

import clear.common.cmd.ZzclearCommand;
import clear.init.config.ModConfig;
import clear.init.handler.ZzclearHandler;
import clear.util.FileUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import clear.core.ZzclearCore;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import java.nio.file.Path;

import static clear.Static.ZZCLEAR_FOLDER;
import static clear.Static.CONFIG_FOLDER;

public class Zzclear implements ModInitializer {

    @Override
    public void onInitialize() {
        ZZCLEAR_FOLDER = FabricLoader.getInstance().getGameDir().resolve("zzclear");
        FileUtils.checkFolder(ZZCLEAR_FOLDER);

        CONFIG_FOLDER = ZZCLEAR_FOLDER.resolve("config");
        FileUtils.checkFolder(CONFIG_FOLDER);
        Static.CONFIG_FILE = CONFIG_FOLDER.resolve("config.json");
        Static.isLuckPerms = FabricLoader.getInstance().isModLoaded("luckperms");

        // Load config
        ModConfig.load();

        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> ZzclearCommand.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStopped);
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTickClear);

    }

    public void onServerStarting(MinecraftServer server) {
        Static.SERVER = server;
    }

    public void onServerStarted(MinecraftServer server) {
        ZzclearCore.INSTANCE.startClear(server);
    }

    public void onServerStopping(MinecraftServer server) {

    }

    public void onServerStopped(MinecraftServer server) {
        ZzclearCore.INSTANCE.stopClear();
    }

    public void onServerTickClear(MinecraftServer server) {
        ZzclearCore.INSTANCE.tick(server);
        ZzclearHandler.onServerTickClear(server);
    }

}
