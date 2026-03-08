package clear.core;

import clear.Static;
import clear.init.config.ModConfig;
import clear.init.handler.ZzclearHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class ZzclearCore {
    public static final ZzclearCore INSTANCE = new ZzclearCore();

    private long _targetClearTickTime = -1;
    private boolean _hasNotified = false;
    private boolean _hasDiscounted = false;

    public void tick(MinecraftServer server) {
        if (_targetClearTickTime == -1) {
            resetTimer(server);
        }

        long current = server.getTickCount();
        long next = _targetClearTickTime;

        int notifyT = ModConfig.INSTANCE.getCommon().getclearNotify() * 20;
        int discountT = ModConfig.INSTANCE.getCommon().getclearDiscount() * 20;

        if (!_hasNotified && current >= next - notifyT && current < next) {
            Static.sendMessageToAllPlayers(ModConfig.INSTANCE.getCommon().getclearNotice(),
                    ModConfig.INSTANCE.getCommon().getclearNotify());
            _hasNotified = true;
        }

        if (!_hasDiscounted && current >= next - discountT && current < next) {
            ZzclearHandler.beginClearCountDown();
            _hasDiscounted = true;
        }

        if (current >= next) {
            int cycleTicks = ModConfig.INSTANCE.getCommon().getclearPeriod() * 1200;
            _targetClearTickTime = current + cycleTicks;
            _hasNotified = false;
            _hasDiscounted = false;
        }
    }

    public void startClear(MinecraftServer server) {
        resetTimer(server);
    }

    public void startClearTick() {
        ZzclearHandler.beginClearCountDown();
    }

    public void stopClear() {
        _targetClearTickTime = -1;
        _hasNotified = false;
        _hasDiscounted = false;
    }

    public void resetTimer(MinecraftServer server) {
        if (server != null) {
            _targetClearTickTime = server.getTickCount()
                    + (long) ModConfig.INSTANCE.getCommon().getclearPeriod() * 1200L;
            _hasNotified = false;
            _hasDiscounted = false;
            ZzclearHandler.abortClearCountDown();
            System.out.println("[Zzclear Debug] Timer Reset! New Target: " + _targetClearTickTime + ". Period is: "
                    + ModConfig.INSTANCE.getCommon().getclearPeriod());
        }
    }

    public void clear(MinecraftServer server) {
        int rItems = 0;
        int rMobs = 0;
        int rXp = 0;
        int rMisc = 0;

        for (ServerLevel level : server.getAllLevels()) {
            boolean itemEn = ModConfig.INSTANCE.getItem().isEnableItemclear();
            boolean mobEn = ModConfig.INSTANCE.getMob().isEnableMobclear();
            boolean xpEn = ModConfig.INSTANCE.getOther().isEnableExpclear();

            List<Entity> entitiesToClear = new ArrayList<>();
            level.getAllEntities().forEach(entitiesToClear::add);

            for (Entity ent : entitiesToClear) {
                if (ent == null || ent.isRemoved())
                    continue;

                if (ModConfig.INSTANCE.getCommon().ignoreNamedEntities && ent.hasCustomName()) {
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
                        if (ModConfig.INSTANCE.getMob().isDropExp()) {
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

        Static.sendMessageToAllPlayers(server, ModConfig.INSTANCE.getCommon().getclearNoticeComplete(), rItems,
                rMobs, rXp, rMisc);
    }

    public int clearItems(MinecraftServer server) {
        int rItems = 0;
        if (!ModConfig.INSTANCE.getItem().isEnableItemclear())
            return rItems;
        for (ServerLevel level : server.getAllLevels()) {
            List<Entity> entitiesToClear = new ArrayList<>();
            level.getAllEntities().forEach(entitiesToClear::add);
            for (Entity ent : entitiesToClear) {
                if (ent == null || ent.isRemoved())
                    continue;
                if (ModConfig.INSTANCE.getCommon().ignoreNamedEntities && ent.hasCustomName())
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
                if (ModConfig.INSTANCE.getCommon().ignoreNamedEntities && ent.hasCustomName())
                    continue;

                if (ent instanceof Monster mob) {
                    if (shouldRemoveMob(mob)) {
                        rMobs++;
                        if (ModConfig.INSTANCE.getMob().isDropExp()) {
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
                if (ModConfig.INSTANCE.getCommon().ignoreNamedEntities && ent.hasCustomName())
                    continue;

                if (ent instanceof Mob mob && !(ent instanceof Monster)) {
                    if (shouldRemoveMob(mob)) {
                        rMobs++;
                        if (ModConfig.INSTANCE.getMob().isDropExp()) {
                            mob.kill(level);
                        } else {
                            mob.discard();
                        }
                    }
                }
            }
            System.out.println("Level " + level.dimension().location() + " has " + totalEnts + " entities.");
        }
        System.out.println("Finished clearAnimals. Cleared: " + rMobs);
        return rMobs;
    }

    public int clearXPs(MinecraftServer server) {
        int rXp = 0;
        if (!ModConfig.INSTANCE.getOther().isEnableExpclear())
            return rXp;
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity ent : level.getAllEntities()) {
                if (ent == null || ent.isRemoved())
                    continue;
                if (ModConfig.INSTANCE.getCommon().ignoreNamedEntities && ent.hasCustomName())
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
                if (ModConfig.INSTANCE.getCommon().ignoreNamedEntities && ent.hasCustomName())
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
        ResourceLocation rl = BuiltInRegistries.ITEM.getKey(item.getItem().getItem());
        List<String> white = ModConfig.INSTANCE.getItem().getItemEntitiesWhitelist();
        List<String> black = ModConfig.INSTANCE.getItem().getItemEntitiesBlacklist();

        if (ModConfig.INSTANCE.getItem().isItemWhiteMode()) {
            return !isMatchList(rl, white);
        }
        if (ModConfig.INSTANCE.getItem().isItemBlackMode()) {
            return isMatchList(rl, black);
        }
        return true;
    }

    private boolean shouldRemoveMob(Mob mob) {
        boolean isMonster = mob instanceof Monster;
        boolean enableMonsters = ModConfig.INSTANCE.getMob().isclearMonsters();
        boolean enableAnimals = ModConfig.INSTANCE.getMob().isclearAnimals();

        if (isMonster && !enableMonsters)
            return false;
        if (!isMonster && !enableAnimals)
            return false;

        ResourceLocation rl = EntityType.getKey(mob.getType());
        List<String> white = ModConfig.INSTANCE.getMob().getMobEntitiesWhitelist();
        List<String> black = ModConfig.INSTANCE.getMob().getMobEntitiesBlacklist();

        if (ModConfig.INSTANCE.getMob().isMobWhiteMode()) {
            return !isMatchList(rl, white);
        }
        if (ModConfig.INSTANCE.getMob().isMobBlackMode()) {
            return isMatchList(rl, black);
        }
        return true;
    }

    private boolean shouldRemoveMisc(Entity entity) {
        ModConfig.Misc cfg = ModConfig.INSTANCE.getOther();
        if (entity instanceof FallingBlockEntity && cfg.isEnableFallingBlockclear())
            return true;
        if (entity instanceof AbstractArrow && !(entity instanceof ThrownTrident) && cfg.isEnableArrowclear())
            return true;
        if (entity instanceof ThrownTrident && cfg.isEnableTridentclear())
            return true;
        if (entity instanceof AbstractHurtingProjectile && cfg.isEnableProjectileclear())
            return true;
        if (entity instanceof ShulkerBullet && cfg.isEnableBulletclear())
            return true;
        if (entity instanceof FireworkRocketEntity && cfg.isEnableFireworkclear())
            return true;
        if (entity instanceof ItemFrame && cfg.isEnableItemFrameclear())
            return true;
        if (entity instanceof Painting && cfg.isEnablePaintingclear())
            return true;
        if (entity instanceof Boat && cfg.isEnableBoatclear())
            return true;
        if (entity instanceof PrimedTnt && cfg.isEnableTNTclear())
            return true;
        return false;
    }

    private boolean isMatchList(ResourceLocation rl, List<String> list) {
        String id = rl.toString();
        for (String entry : list) {
            if (entry.equals(id))
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
