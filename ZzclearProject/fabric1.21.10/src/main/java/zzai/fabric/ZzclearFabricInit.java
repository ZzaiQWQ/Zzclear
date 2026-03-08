package zzai.fabric;

import zzai.clearlogic.CommandController;
import zzai.clearlogic.config.ClearConfigNode;
import zzai.clearlogic.TimerTasklet;
import zzai.clearlogic.Static;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import zzai.clearlogic.ClearEngine;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import java.nio.file.Path;

import static zzai.clearlogic.Static.Zzclear_FOLDER;
import static zzai.clearlogic.Static.CONFIG_FOLDER;

public class ZzclearFabricInit implements ModInitializer {

    @Override
    public void onInitialize() {
        Zzclear_FOLDER = FabricLoader.getInstance().getGameDir().resolve("Zzclear");
        Static.checkFolder(Zzclear_FOLDER);

        CONFIG_FOLDER = Zzclear_FOLDER.resolve("config");
        Static.checkFolder(CONFIG_FOLDER);
        Static.CONFIG_FILE = CONFIG_FOLDER.resolve("config.json");
        Static.isLuckPerms = FabricLoader.getInstance().isModLoaded("luckperms");

        // Load config
        ClearConfigNode.load();

        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> CommandController.register(dispatcher));

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
        ClearEngine.INSTANCE.startClear(server);
    }

    public void onServerStopping(MinecraftServer server) {

    }

    public void onServerStopped(MinecraftServer server) {
        ClearEngine.INSTANCE.stopClear();
    }

    public void onServerTickClear(MinecraftServer server) {
        ClearEngine.INSTANCE.tick(server);
        TimerTasklet.onServerTickClear(server);
    }

}
