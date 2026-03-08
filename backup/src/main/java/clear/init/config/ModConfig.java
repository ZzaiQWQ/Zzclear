package clear.init.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import clear.Static;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static ModConfig INSTANCE;

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
                INSTANCE = GSON.fromJson(r, ModConfig.class);
                System.out.println(
                        "[Zzclear Debug] Successfully loaded config! Period: " + INSTANCE.settings.getclearPeriod());
            } catch (Exception e) {
                Static.LOGGER.error("Load config error", e);
                INSTANCE = new ModConfig();
            }
        } else {
            System.out.println("[Zzclear Debug] Config not found, creating default.");
            INSTANCE = new ModConfig();
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
            GSON.toJson(INSTANCE, w);
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
        public List<String> mobEntitiesWhitelist = new ArrayList<>(Arrays.asList(
                "minecraft:villager", "minecraft:iron_golem",
                "minecraft:wandering_trader", "minecraft:snow_golem"));
        public List<String> mobEntitiesBlacklist = new ArrayList<>(Arrays.asList(
                "minecraft:villager", "minecraft:iron_golem"));

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
