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
                        "[Zzclear Debug] Successfully loaded config! Period: " + INSTANCE.settings.getclearPeriod());
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
            json = json.replace("\"clearPeriod\":", "/* 清理大周期 (分钟) */\n    \"clearPeriod\":");
            json = json.replace("\"clearNotify\":", "/* 提前多少秒通报警告 */\n    \"clearNotify\":");
            json = json.replace("\"clearDiscount\":", "/* 最后多少秒进入扫地读秒阶段 */\n    \"clearDiscount\":");
            json = json.replace("\"clearNotice\":", "/* 倒计时警告文本内容 */\n    \"clearNotice\":");
            json = json.replace("\"clearNoticeComplete\":", "/* 清理完成后的总结通报文本 */\n    \"clearNoticeComplete\":");
            json = json.replace("\"ignoreNamedEntities\":", "/* 是否忽略带有自定义名称的实体 */\n    \"ignoreNamedEntities\":");
            json = json.replace("\"enableItemclear\":", "/* 是否开启掉落物清理 */\n    \"enableItemclear\":");
            json = json.replace("\"itemWhiteMode\":", "/* 是否开启掉落物白名单模式 (仅排除白名单) */\n    \"itemWhiteMode\":");
            json = json.replace("\"itemBlackMode\":", "/* 是否开启掉落物黑名单模式 (仅清理黑名单) */\n    \"itemBlackMode\":");
            json = json.replace("\"itemEntitiesWhitelist\":", "/* 掉落物白名单列表 */\n    \"itemEntitiesWhitelist\":");
            json = json.replace("\"itemEntitiesBlacklist\":", "/* 掉落物黑名单列表 */\n    \"itemEntitiesBlacklist\":");
            json = json.replace("\"enableMobclear\":", "/* 是否开启生物清理 */\n    \"enableMobclear\":");
            json = json.replace("\"dropExp\":", "/* 清理生物时是否掉落经验 */\n    \"dropExp\":");
            json = json.replace("\"clearAnimals\":", "/* 是否清理动物 (非怪物) */\n    \"clearAnimals\":");
            json = json.replace("\"clearMonsters\":", "/* 是否清理怪物 */\n    \"clearMonsters\":");
            json = json.replace("\"mobWhiteMode\":", "/* 是否开启生物白名单模式 */\n    \"mobWhiteMode\":");
            json = json.replace("\"mobBlackMode\":", "/* 是否开启生物黑名单模式 */\n    \"mobBlackMode\":");
            json = json.replace("\"mobEntitiesWhitelist\":", "/* 生物白名单列表 */\n    \"mobEntitiesWhitelist\":");
            json = json.replace("\"mobEntitiesBlacklist\":", "/* 生物黑名单列表 */\n    \"mobEntitiesBlacklist\":");
            json = json.replace("\"enableExpclear\":", "/* 是否清理掉落的经验球 */\n    \"enableExpclear\":");
            json = json.replace("\"enableFallingBlockclear\":",
                    "/* 是否清理下落的方块 (如沙子) */\n    \"enableFallingBlockclear\":");
            json = json.replace("\"enableArrowclear\":", "/* 是否清理射出的箭 */\n    \"enableArrowclear\":");
            json = json.replace("\"enableTridentclear\":", "/* 是否清理掷出的三叉戟 */\n    \"enableTridentclear\":");
            json = json.replace("\"enableProjectileclear\":", "/* 是否清理各类投射物 */\n    \"enableProjectileclear\":");
            json = json.replace("\"enableBulletclear\":", "/* 是否清理潜影贝导弹 */\n    \"enableBulletclear\":");
            json = json.replace("\"enableFireworkclear\":", "/* 是否清理烟花火箭 */\n    \"enableFireworkclear\":");
            json = json.replace("\"enableItemFrameclear\":", "/* 是否清理物品展示框 */\n    \"enableItemFrameclear\":");
            json = json.replace("\"enablePaintingclear\":", "/* 是否清理画 */\n    \"enablePaintingclear\":");
            json = json.replace("\"enableBoatclear\":", "/* 是否清理船 */\n    \"enableBoatclear\":");
            json = json.replace("\"enableTNTclear\":", "/* 是否清理点燃的TNT */\n    \"enableTNTclear\":");
            w.write(json);
        } catch (Exception e) {
            Static.LOGGER.error("Save config error", e);
        }
    }

    public static class Settings {
        public int clearPeriod = 4;
        public int clearNotify = 20;
        public int clearDiscount = 5;
        public String clearNotice = "§6[§b崽崽§6]§c 注意：还有 §e{0} §c秒就要吃掉地上的垃圾了~";
        public String clearNoticeComplete = "§6[§b崽崽§6]§a 这次一共吃掉了 §e{0}§a 个掉落物，§e{1}§a 个生物，§e{2}§a 个经验球和 §e{3}§a 个其他实体~";
        public boolean ignoreNamedEntities = true;

        public String getclearNotice() {
            return clearNotice;
        }

        public String getclearNoticeComplete() {
            return clearNoticeComplete;
        }

        public int getclearNotify() {
            return clearNotify;
        }

        public int getclearPeriod() {
            return clearPeriod;
        }

        public int getclearDiscount() {
            return clearDiscount;
        }
    }

    public static class Items {
        public boolean enableItemclear = true;
        public boolean itemWhiteMode = true;
        public boolean itemBlackMode = false;
        public List<String> itemEntitiesWhitelist = new ArrayList<>();
        public List<String> itemEntitiesBlacklist = new ArrayList<>(Arrays.asList("minecraft:diamond"));

        public boolean isEnableItemclear() {
            return enableItemclear;
        }

        public boolean isItemWhiteMode() {
            return itemWhiteMode;
        }

        public boolean isItemBlackMode() {
            return itemBlackMode;
        }

        public List<String> getItemEntitiesWhitelist() {
            return itemEntitiesWhitelist;
        }

        public List<String> getItemEntitiesBlacklist() {
            return itemEntitiesBlacklist;
        }
    }

    public static class Mobs {
        public boolean enableMobclear = true;
        public boolean dropExp = false;
        public boolean clearAnimals = true;
        public boolean clearMonsters = true;
        public boolean mobWhiteMode = true;
        public boolean mobBlackMode = false;
        public List<String> mobEntitiesWhitelist = new ArrayList<>(Arrays.asList("minecraft:villager",
                "minecraft:iron_golem", "minecraft:wandering_trader", "minecraft:snow_golem"));
        public List<String> mobEntitiesBlacklist = new ArrayList<>(
                Arrays.asList("minecraft:villager", "minecraft:iron_golem"));

        public boolean isEnableMobclear() {
            return enableMobclear;
        }

        public boolean isDropExp() {
            return dropExp;
        }

        public boolean isclearAnimals() {
            return clearAnimals;
        }

        public boolean isclearMonsters() {
            return clearMonsters;
        }

        public boolean isMobWhiteMode() {
            return mobWhiteMode;
        }

        public boolean isMobBlackMode() {
            return mobBlackMode;
        }

        public List<String> getMobEntitiesWhitelist() {
            return mobEntitiesWhitelist;
        }

        public List<String> getMobEntitiesBlacklist() {
            return mobEntitiesBlacklist;
        }
    }

    public static class Misc {
        public boolean enableExpclear = true;
        public boolean enableFallingBlockclear = true;
        public boolean enableArrowclear = true;
        public boolean enableTridentclear = true;
        public boolean enableProjectileclear = true;
        public boolean enableBulletclear = true;
        public boolean enableFireworkclear = true;
        public boolean enableItemFrameclear = true;
        public boolean enablePaintingclear = true;
        public boolean enableBoatclear = true;
        public boolean enableTNTclear = true;

        public boolean isEnableExpclear() {
            return enableExpclear;
        }

        public boolean isEnableFallingBlockclear() {
            return enableFallingBlockclear;
        }

        public boolean isEnableArrowclear() {
            return enableArrowclear;
        }

        public boolean isEnableTridentclear() {
            return enableTridentclear;
        }

        public boolean isEnableProjectileclear() {
            return enableProjectileclear;
        }

        public boolean isEnableBulletclear() {
            return enableBulletclear;
        }

        public boolean isEnableFireworkclear() {
            return enableFireworkclear;
        }

        public boolean isEnableItemFrameclear() {
            return enableItemFrameclear;
        }

        public boolean isEnablePaintingclear() {
            return enablePaintingclear;
        }

        public boolean isEnableBoatclear() {
            return enableBoatclear;
        }

        public boolean isEnableTNTclear() {
            return enableTNTclear;
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