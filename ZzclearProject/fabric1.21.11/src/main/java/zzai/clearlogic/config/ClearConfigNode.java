package zzai.clearlogic.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import zzai.clearlogic.Static;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClearConfigNode {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .setLenient()
            .create();
    public static ClearConfigNode INSTANCE = new ClearConfigNode();
    @SerializedName("common")
    public Settings settings = new Settings();
    @SerializedName("item")
    public Items items = new Items();
    @SerializedName("mob")
    public Mobs mobs = new Mobs();
    @SerializedName("other")
    public Misc misc = new Misc();

    public static void load() {
        File f = Static.CONFIG_FILE.toFile();
        if (f.exists()) {
            System.out.println("[Zzclear Debug] Loading config from " + f.getAbsolutePath());
            try (FileReader r = new FileReader(f, java.nio.charset.StandardCharsets.UTF_8)) {
                INSTANCE = GSON.fromJson(r, ClearConfigNode.class);
                System.out.println(
                        "[Zzclear Debug] Successfully loaded config! Period: "
                                + INSTANCE.settings.getSweepIntervalMinutes());
            } catch (Exception e) {
                Static.LOGGER.error("Load config error", e);
                INSTANCE = new ClearConfigNode();
            }
        } else {
            System.out.println("[Zzclear Debug] Config not found, creating default.");
            INSTANCE = new ClearConfigNode();
            save();
        }
        if (INSTANCE.settings == null)
            INSTANCE.settings = new Settings();
        if (INSTANCE.items == null)
            INSTANCE.items = new Items();
        if (INSTANCE.mobs == null)
            INSTANCE.mobs = new Mobs();
        if (INSTANCE.misc == null)
            INSTANCE.misc = new Misc();
    }

    public static void save() {
        if (INSTANCE == null)
            return;
        File f = Static.CONFIG_FILE.toFile();
        f.getParentFile().mkdirs();
        try (FileWriter w = new FileWriter(f, java.nio.charset.StandardCharsets.UTF_8)) {
            String json = GSON.toJson(INSTANCE);
            json = json.replace("\"sweepIntervalMinutes\":", "/* 清理大周期 (分钟) */\n    \"sweepIntervalMinutes\":");
            json = json.replace("\"warningTimeSeconds\":", "/* 提前多少秒通报警告 */\n    \"warningTimeSeconds\":");
            json = json.replace("\"finalCountdownSeconds\":", "/* 最后多少秒进入扫地读秒阶段 */\n    \"finalCountdownSeconds\":");
            json = json.replace("\"countdownMessage\":", "/* 倒计时警告文本内容 */\n    \"countdownMessage\":");
            json = json.replace("\"sweepCompleteMessage\":", "/* 清理完成后的总结通报文本 */\n    \"sweepCompleteMessage\":");
            json = json.replace("\"ignoreNamedEntities\":", "/* 是否忽略带有自定义名称的实体 */\n    \"ignoreNamedEntities\":");

            json = json.replace("\"sweepItems\":", "/* 是否开启掉落物清理 */\n    \"sweepItems\":");
            json = json.replace("\"useItemWhitelist\":", "/* 是否开启掉落物白名单模式 (仅排除白名单) */\n    \"useItemWhitelist\":");
            json = json.replace("\"useItemBlacklist\":", "/* 是否开启掉落物黑名单模式 (仅清理黑名单) */\n    \"useItemBlacklist\":");
            json = json.replace("\"itemWhitelist\":", "/* 掉落物白名单列表 */\n    \"itemWhitelist\":");
            json = json.replace("\"itemBlacklist\":", "/* 掉落物黑名单列表 */\n    \"itemBlacklist\":");

            json = json.replace("\"sweepLivingEntities\":", "/* 是否开启生物清理 */\n    \"sweepLivingEntities\":");
            json = json.replace("\"dropExp\":", "/* 清理生物时是否掉落经验 */\n    \"dropExp\":");
            json = json.replace("\"sweepPassiveAnimals\":", "/* 是否清理动物 (非怪物) */\n    \"sweepPassiveAnimals\":");
            json = json.replace("\"sweepHostileMonsters\":", "/* 是否清理怪物 */\n    \"sweepHostileMonsters\":");
            json = json.replace("\"useMobWhitelist\":", "/* 是否开启生物白名单模式 */\n    \"useMobWhitelist\":");
            json = json.replace("\"useMobBlacklist\":", "/* 是否开启生物黑名单模式 */\n    \"useMobBlacklist\":");
            json = json.replace("\"mobWhitelist\":", "/* 生物白名单列表 */\n    \"mobWhitelist\":");
            json = json.replace("\"mobBlacklist\":", "/* 生物黑名单列表 */\n    \"mobBlacklist\":");

            json = json.replace("\"sweepExperienceOrbs\":", "/* 是否清理掉落的经验球 */\n    \"sweepExperienceOrbs\":");
            json = json.replace("\"sweepFallingBlocks\":", "/* 是否清理下落的方块 (如沙子) */\n    \"sweepFallingBlocks\":");
            json = json.replace("\"sweepArrows\":", "/* 是否清理射出的箭 */\n    \"sweepArrows\":");
            json = json.replace("\"sweepTridents\":", "/* 是否清理掷出的三叉戟 */\n    \"sweepTridents\":");
            json = json.replace("\"sweepProjectiles\":", "/* 是否清理各类投射物 */\n    \"sweepProjectiles\":");
            json = json.replace("\"sweepShulkerBullets\":", "/* 是否清理潜影贝导弹 */\n    \"sweepShulkerBullets\":");
            json = json.replace("\"sweepFireworks\":", "/* 是否清理烟花火箭 */\n    \"sweepFireworks\":");
            json = json.replace("\"sweepItemFrames\":", "/* 是否清理物品展示框 */\n    \"sweepItemFrames\":");
            json = json.replace("\"sweepPaintings\":", "/* 是否清理画 */\n    \"sweepPaintings\":");
            json = json.replace("\"sweepBoats\":", "/* 是否清理船 */\n    \"sweepBoats\":");
            json = json.replace("\"sweepPrimedTNT\":", "/* 是否清理点燃的TNT */\n    \"sweepPrimedTNT\":");
            w.write(json);
        } catch (Exception e) {
            Static.LOGGER.error("Save config error", e);
        }
    }

    public static class Settings {
        public int sweepIntervalMinutes = 4;
        public int warningTimeSeconds = 20;
        public int finalCountdownSeconds = 5;
        public String countdownMessage = "§6[§b崽崽§6]§c 注意：还有 §e{0} §c秒就要吃掉地上的垃圾了~";
        public String sweepCompleteMessage = "§6[§b崽崽§6]§a 这次一共吃掉了 §e{0}§a 个掉落物，§e{1}§a 个生物，§e{2}§a 个经验球和 §e{3}§a 个其他实体~";
        public boolean ignoreNamedEntities = true;

        public String getCountdownMessage() {
            return countdownMessage;
        }

        public String getSweepCompleteMessage() {
            return sweepCompleteMessage;
        }

        public int getWarningTimeSeconds() {
            return warningTimeSeconds;
        }

        public int getSweepIntervalMinutes() {
            return sweepIntervalMinutes;
        }

        public int getFinalCountdownSeconds() {
            return finalCountdownSeconds;
        }
    }

    public static class Items {
        public boolean sweepItems = true;
        public boolean useItemWhitelist = true;
        public boolean useItemBlacklist = false;
        public List<String> itemWhitelist = new ArrayList<>();
        public List<String> itemBlacklist = new ArrayList<>(Arrays.asList("minecraft:diamond"));

        public boolean isSweepItems() {
            return sweepItems;
        }

        public boolean isUseItemWhitelist() {
            return useItemWhitelist;
        }

        public boolean isUseItemBlacklist() {
            return useItemBlacklist;
        }

        public List<String> getItemWhitelist() {
            return itemWhitelist;
        }

        public List<String> getItemBlacklist() {
            return itemBlacklist;
        }
    }

    public static class Mobs {
        public boolean sweepLivingEntities = true;
        public boolean dropExp = false;
        public boolean sweepPassiveAnimals = true;
        public boolean sweepHostileMonsters = true;
        public boolean useMobWhitelist = true;
        public boolean useMobBlacklist = false;
        public List<String> mobWhitelist = new ArrayList<>(Arrays.asList("minecraft:villager", "minecraft:iron_golem",
                "minecraft:wandering_trader", "minecraft:snow_golem"));
        public List<String> mobBlacklist = new ArrayList<>(Arrays.asList("minecraft:villager", "minecraft:iron_golem"));

        public boolean isSweepLivingEntities() {
            return sweepLivingEntities;
        }

        public boolean isDropExp() {
            return dropExp;
        }

        public boolean isSweepPassiveAnimals() {
            return sweepPassiveAnimals;
        }

        public boolean isSweepHostileMonsters() {
            return sweepHostileMonsters;
        }

        public boolean isUseMobWhitelist() {
            return useMobWhitelist;
        }

        public boolean isUseMobBlacklist() {
            return useMobBlacklist;
        }

        public List<String> getMobWhitelist() {
            return mobWhitelist;
        }

        public List<String> getMobBlacklist() {
            return mobBlacklist;
        }
    }

    public static class Misc {
        public boolean sweepExperienceOrbs = true;
        public boolean sweepFallingBlocks = true;
        public boolean sweepArrows = true;
        public boolean sweepTridents = true;
        public boolean sweepProjectiles = true;
        public boolean sweepShulkerBullets = true;
        public boolean sweepFireworks = true;
        public boolean sweepItemFrames = true;
        public boolean sweepPaintings = true;
        public boolean sweepBoats = true;
        public boolean sweepPrimedTNT = true;

        public boolean isSweepExperienceOrbs() {
            return sweepExperienceOrbs;
        }

        public boolean isSweepFallingBlocks() {
            return sweepFallingBlocks;
        }

        public boolean isSweepArrows() {
            return sweepArrows;
        }

        public boolean isSweepTridents() {
            return sweepTridents;
        }

        public boolean isSweepProjectiles() {
            return sweepProjectiles;
        }

        public boolean isSweepShulkerBullets() {
            return sweepShulkerBullets;
        }

        public boolean isSweepFireworks() {
            return sweepFireworks;
        }

        public boolean isSweepItemFrames() {
            return sweepItemFrames;
        }

        public boolean isSweepPaintings() {
            return sweepPaintings;
        }

        public boolean isSweepBoats() {
            return sweepBoats;
        }

        public boolean isSweepPrimedTNT() {
            return sweepPrimedTNT;
        }
    }

    public Settings getCommon() {
        return settings;
    }

    public Items getItem() {
        return items;
    }

    public Mobs getMob() {
        return mobs;
    }

    public Misc getOther() {
        return misc;
    }
}