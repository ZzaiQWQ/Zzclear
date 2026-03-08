package zzai.clearlogic;

import zzai.clearlogic.config.ClearConfigNode;
import net.minecraft.server.MinecraftServer;
import zzai.clearlogic.Static;
import zzai.clearlogic.ClearEngine;

public class TimerTasklet {

    private static int sweepTicksRemaining = -1;

    public static void startFinalCountdown() {
        sweepTicksRemaining = ClearConfigNode.INSTANCE.getCommon().getFinalCountdownSeconds() * 20;
    }

    public static void abortClearCountDown() {
        sweepTicksRemaining = -1;
    }

    public static void onServerTickClear(MinecraftServer server) {
        if (sweepTicksRemaining >= 0) {
            if (sweepTicksRemaining == 0) {
                ClearEngine.INSTANCE.clear(server);
                sweepTicksRemaining = -1;
            } else {
                if (sweepTicksRemaining % 20 == 0) {
                    Static.sendMessageToAllPlayers(ClearConfigNode.INSTANCE.getCommon().getCountdownMessage(),
                            sweepTicksRemaining / 20);
                }
                --sweepTicksRemaining;
            }
        }
    }
}
