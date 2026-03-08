package zzai.clearlogic;

import zzai.clearlogic.config.ClearConfigNode;
import net.minecraft.server.MinecraftServer;
import zzai.clearlogic.Static;
import zzai.clearlogic.ClearEngine;

public class TimerTasklet {

    private static int counter = -1;

    public static void beginClearCountDown() {
        counter = ClearConfigNode.INSTANCE.getCommon().getclearDiscount() * 20;
    }

    public static void abortClearCountDown() {
        counter = -1;
    }

    public static void onServerTickClear(MinecraftServer server) {
        if (counter >= 0) {
            if (counter == 0) {
                ClearEngine.INSTANCE.clear(server);
                counter = -1;
            } else {
                if (counter % 20 == 0) {
                    Static.sendMessageToAllPlayers(ClearConfigNode.INSTANCE.getCommon().getclearNotice(), counter / 20);
                }

                --counter;
            }
        }

    }

}
