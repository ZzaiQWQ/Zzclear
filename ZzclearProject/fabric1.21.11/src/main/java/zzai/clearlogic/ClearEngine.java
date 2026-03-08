package zzai.clearlogic;

import zzai.clearlogic.Static;
import zzai.clearlogic.config.ClearConfigNode;
import zzai.clearlogic.TimerTasklet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.painting.Painting;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class ClearEngine {
    public static final ClearEngine INSTANCE = new ClearEngine();

    private long _targetClearTickTime = -1;
    private boolean _hasNotified = false;
    private boolean _hasDiscounted = false;

    public void tick(MinecraftServer server) {
        if (_targetClearTickTime == -1) {
            resetTimer(server);
        }

        long current = server.getTickCount();
        long next = _targetClearTickTime;

        int notifyT = ClearConfigNode.INSTANCE.getCommon().getWarningTimeSeconds() * 20;
        int discountT = ClearConfigNode.INSTANCE.getCommon().getFinalCountdownSeconds() * 20;

        if (!_hasNotified && current >= next - notifyT && current < next) {
            Static.sendMessageToAllPlayers(ClearConfigNode.INSTANCE.getCommon().getCountdownMessage(),
                    ClearConfigNode.INSTANCE.getCommon().getWarningTimeSeconds());
            _hasNotified = true;
        }

        if (!_hasDiscounted && current >= next - discountT && current < next) {
            TimerTasklet.startFinalCountdown();
            _hasDiscounted = true;
        }

        if (current >= next) {
            int cycleTicks = ClearConfigNode.INSTANCE.getCommon().getSweepIntervalMinutes() * 1200;
            _targetClearTickTime = current + cycleTicks;
            _hasNotified = false;
            _hasDiscounted = false;
        }
    }

    public void startClear(MinecraftServer server) {
        resetTimer(server);
    }

    public void startClearTick() {
        TimerTasklet.startFinalCountdown();
    }

    public void stopClear() {
        _targetClearTickTime = -1;
        _hasNotified = false;
        _hasDiscounted = false;
    }

    public void resetTimer(MinecraftServer server) {
        if (server != null) {
            _targetClearTickTime = server.getTickCount()
                    + (long) ClearConfigNode.INSTANCE.getCommon().getSweepIntervalMinutes() * 1200L;
            _hasNotified = false;
            _hasDiscounted = false;
            TimerTasklet.abortClearCountDown();
            System.out.println("[Zzclear Debug] Timer Reset! New Target: " + _targetClearTickTime + ". Period is: "
                    + ClearConfigNode.INSTANCE.getCommon().getSweepIntervalMinutes());
        }
    }

    public void clear(MinecraftServer server) {
        int rItems = 0;
        int rMobs = 0;
        int rXp = 0;
        int rMisc = 0;

        for (ServerLevel level : server.getAllLevels()) {
            boolean itemEn = ClearConfigNode.INSTANCE.getItem().isSweepItems();
            boolean mobEn = ClearConfigNode.INSTANCE.getMob().isSweepLivingEntities();
            boolean xpEn = ClearConfigNode.INSTANCE.getOther().isSweepExperienceOrbs();

            List<Entity> entitiesToClear = new ArrayList<>();
            level.getAllEntities().forEach(entitiesToClear::add);

            for (Entity ent : entitiesToClear) {
                if (ent == null || ent.isRemoved())
                    continue;

                if (ClearConfigNode.INSTANCE.getCommon().ignoreNamedEntities && ent.hasCustomName()) {
                    continue;
                }

                if (itemEn && ent instanceof ItemEntity itemEntity) {
                    if (shouldRemoveItem(itemEntity)) {
                        rItems += itemEntity.getItem().getCount();
                        itemEntity.discard();
                    }
                    continue;
                }

                if (mobEn && ent instanceof Mob mob) {
                    if (shouldRemoveMob(mob)) {
                        rMobs++;
                        if (ClearConfigNode.INSTANCE.getMob().isDropExp()) {
                            mob.kill(level);
                        } else {
                            mob.discard();
                        }
                    }
                    continue;
                }

                if (xpEn && ent instanceof ExperienceOrb) {
                    rXp++;
                    ent.discard();
                    continue;
                }

                if (shouldRemoveMisc(ent)) {
                    rMisc++;
                    ent.discard();
                }
            }
        }

        Static.sendMessageToAllPlayers(server, ClearConfigNode.INSTANCE.getCommon().getSweepCompleteMessage(), rItems,
                rMobs, rXp, rMisc);
    }

    public int clearItems(MinecraftServer server) {
        int rItems = 0;
        if (!ClearConfigNode.INSTANCE.getItem().isSweepItems())
            return rItems;
        for (ServerLevel level : server.getAllLevels()) {
            List<Entity> entitiesToClear = new ArrayList<>();
            level.getAllEntities().forEach(entitiesToClear::add);
            for (Entity ent : entitiesToClear) {
                if (ent == null || ent.isRemoved())
                    continue;
                if (ClearConfigNode.INSTANCE.getCommon().ignoreNamedEntities && ent.hasCustomName())
                    continue;

                if (ent instanceof ItemEntity itemEntity) {
                    if (shouldRemoveItem(itemEntity)) {
                        rItems += itemEntity.getItem().getCount();
                        itemEntity.discard();
                    }
                }
            }
        }
        return rItems;
    }

    public int clearMonsters(MinecraftServer server) {
        int rMobs = 0;
        for (ServerLevel level : server.getAllLevels()) {
            List<Entity> entitiesToClear = new ArrayList<>();
            level.getAllEntities().forEach(entitiesToClear::add);
            for (Entity ent : entitiesToClear) {
                if (ent == null || ent.isRemoved())
                    continue;
                if (ClearConfigNode.INSTANCE.getCommon().ignoreNamedEntities && ent.hasCustomName())
                    continue;

                if (ent instanceof Monster mob) {
                    if (shouldRemoveMob(mob)) {
                        rMobs++;
                        if (ClearConfigNode.INSTANCE.getMob().isDropExp()) {
                            mob.kill(level);
                        } else {
                            mob.discard();
                        }
                    }
                }
            }
        }
        return rMobs;
    }

    public int clearAnimals(MinecraftServer server) {
        int rMobs = 0;
        System.out.println("Executing clearAnimals...");
        for (ServerLevel level : server.getAllLevels()) {
            int totalEnts = 0;
            List<Entity> entitiesToClear = new ArrayList<>();
            level.getAllEntities().forEach(entitiesToClear::add);
            for (Entity ent : entitiesToClear) {
                totalEnts++;
                if (ent == null || ent.isRemoved())
                    continue;
                if (ClearConfigNode.INSTANCE.getCommon().ignoreNamedEntities && ent.hasCustomName())
                    continue;

                if (ent instanceof Mob mob && !(ent instanceof Monster)) {
                    if (shouldRemoveMob(mob)) {
                        rMobs++;
                        if (ClearConfigNode.INSTANCE.getMob().isDropExp()) {
                            mob.kill(level);
                        } else {
                            mob.discard();
                        }
                    }
                }
            }
            System.out.println("Level " + level.dimension().toString() + " has " + totalEnts + " entities.");
        }
        System.out.println("Finished clearAnimals. Cleared: " + rMobs);
        return rMobs;
    }

    public int clearXPs(MinecraftServer server) {
        int rXp = 0;
        if (!ClearConfigNode.INSTANCE.getOther().isSweepExperienceOrbs())
            return rXp;
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity ent : level.getAllEntities()) {
                if (ent == null || ent.isRemoved())
                    continue;
                if (ClearConfigNode.INSTANCE.getCommon().ignoreNamedEntities && ent.hasCustomName())
                    continue;

                if (ent instanceof ExperienceOrb) {
                    rXp++;
                    ent.discard();
                }
            }
        }
        return rXp;
    }

    public int clearMisc(MinecraftServer server) {
        int rMisc = 0;
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity ent : level.getAllEntities()) {
                if (ent == null || ent.isRemoved())
                    continue;
                if (ClearConfigNode.INSTANCE.getCommon().ignoreNamedEntities && ent.hasCustomName())
                    continue;

                if (shouldRemoveMisc(ent)) {
                    rMisc++;
                    ent.discard();
                }
            }
        }
        return rMisc;
    }

    private boolean shouldRemoveItem(ItemEntity item) {
        Identifier rl = BuiltInRegistries.ITEM.getKey(item.getItem().getItem());
        List<String> white = ClearConfigNode.INSTANCE.getItem().getItemWhitelist();
        List<String> black = ClearConfigNode.INSTANCE.getItem().getItemBlacklist();

        if (ClearConfigNode.INSTANCE.getItem().isUseItemWhitelist()) {
            return !isMatchList(rl, white);
        }
        if (ClearConfigNode.INSTANCE.getItem().isUseItemBlacklist()) {
            return isMatchList(rl, black);
        }
        return true;
    }

    private boolean shouldRemoveMob(Mob mob) {
        boolean isMonster = mob instanceof Monster;
        boolean enableMonsters = ClearConfigNode.INSTANCE.getMob().isSweepHostileMonsters();
        boolean enableAnimals = ClearConfigNode.INSTANCE.getMob().isSweepPassiveAnimals();

        if (isMonster && !enableMonsters)
            return false;
        if (!isMonster && !enableAnimals)
            return false;

        Identifier rl = EntityType.getKey(mob.getType());
        List<String> white = ClearConfigNode.INSTANCE.getMob().getMobWhitelist();
        List<String> black = ClearConfigNode.INSTANCE.getMob().getMobBlacklist();

        if (ClearConfigNode.INSTANCE.getMob().isUseMobWhitelist()) {
            return !isMatchList(rl, white);
        }
        if (ClearConfigNode.INSTANCE.getMob().isUseMobBlacklist()) {
            return isMatchList(rl, black);
        }
        return true;
    }

    private boolean shouldRemoveMisc(Entity entity) {
        ClearConfigNode.Misc cfg = ClearConfigNode.INSTANCE.getOther();
        if (entity instanceof FallingBlockEntity && cfg.isSweepFallingBlocks())
            return true;
        if (entity instanceof AbstractArrow && !(entity instanceof ThrownTrident) && cfg.isSweepArrows())
            return true;
        if (entity instanceof ThrownTrident && cfg.isSweepTridents())
            return true;
        if (entity instanceof AbstractHurtingProjectile && cfg.isSweepProjectiles())
            return true;
        if (entity instanceof ShulkerBullet && cfg.isSweepShulkerBullets())
            return true;
        if (entity instanceof FireworkRocketEntity && cfg.isSweepFireworks())
            return true;
        if (entity instanceof ItemFrame && cfg.isSweepItemFrames())
            return true;
        if (entity instanceof Painting && cfg.isSweepPaintings())
            return true;
        if (entity instanceof Boat && cfg.isSweepBoats())
            return true;
        if (entity instanceof PrimedTnt && cfg.isSweepPrimedTNT())
            return true;
        return false;
    }

    private boolean isMatchList(Identifier rl, List<String> list) {
        if (rl == null)
            return false;
        String fullPath = rl.toString();
        String pathOnly = rl.getPath();
        for (String entry : list) {
            if (entry.equals(fullPath))
                return true;
            if (entry.endsWith("*")) {
                String prefix = entry.substring(0, entry.length() - 2); // Exclude "/*"
                if (rl.getNamespace().equals(prefix))
                    return true;
            }
        }
        return false;
    }
}

