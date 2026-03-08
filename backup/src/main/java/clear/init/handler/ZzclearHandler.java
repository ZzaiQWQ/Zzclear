package clear.init.handler;

import clear.init.config.ModConfig;
import net.minecraft.server.MinecraftServer;
import clear.Static;
import clear.core.ZzclearCore;

public class ZzclearHandler {

    private static int counter = -1;

    public static void beginClearCountDown() {
        counter = ModConfig.INSTANCE.getCommon().getclearDiscount() * 20;
    }

    public static void abortClearCountDown() {
        counter = -1;
    }

    public static void onServerTickClear(MinecraftServer server) {
        if (counter >= 0) {
            if (counter == 0) {
                ZzclearCore.INSTANCE.clear(server);
                counter = -1;
            } else {
                if (counter % 20 == 0) {
                    Static.sendMessageToAllPlayers(ModConfig.INSTANCE.getCommon().getclearNotice(), counter / 20);
                }

                --counter;
            }
        }

    }

}
