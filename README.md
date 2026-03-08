# Zzclear 扫地大妈 - Minecraft 极致优化与实体清理模组

**中文版** | [English Version (英文版)](./README_en.md)

![Zzclear](https://img.shields.io/badge/Minecraft-1.21.10%20%7C%201.21.11-success?style=for-the-badge&logo=minecraft)
![Fabric](https://img.shields.io/badge/Fabric-0.16.5+-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-All_Rights_Reserved-red?style=for-the-badge)

**Zzclear** 是一款专为高版本 Minecraft (1.21.10 / 1.21.11 Fabric) 设计的轻量级、智能化的实体清理（扫地）模组。
它不仅能在后台静默运行并根据设定频率自动清理地图上的掉落物和冗余实体，还提供了强大的游戏内指令系统与支持热更新的精细化配置文件，帮助服主从源头上根治服务器卡顿问题！

---

## ✨ 核心特性

- 🕐 **自动化定时清理：** 自定义清理周期（支持按照分钟级别配置清扫时间），时间一到自动剿灭滞留的实体。
- 🛡️ **智能过滤与白名单：** 告别“一刀切”！无论是掉落物、怪物、还是特定的实体，支持在配置文件中通过“黑名单/白名单”进行精准放行或彻底根除。
- 🪓 **全类型实体管控：**
  - **常规清理：** 掉落物 (Item)、经验球 (Experience Orb)
  - **生物清理：** 敌对怪物 (Monsters)、中性/友好动物 (Animals)
  - **载具/装饰物管控：** 矿车 (Minecarts)、船 (Boats)、画 (Paintings)、物品展示框 (Item Frames)
  - **物理/投掷物管控：** 射出的箭矢、三叉戟、掉落的方块 (Falling Blocks)、点燃的 TNT 等动态实体。
- 🔄 **热重载指令：** 调整配置文件后无需重启服务器，一条指令直接生效。

---

## ⌨️ 游戏内指令用法

以下所有指令的主命令为 `/zzclear`，需要服务器 OP 或具备相应指令执行权限的管理员才能使用：

### 1. 配置文件重载
` /zzclear reload `
- **作用：** 重新读取 `config/config.json` 文件并立即应用新的清理规则和白名单数据。

### 2. 手动清理指令 (Manual Clear)
如果你不想等后台计时器，可以使用这些指令**立刻**清理对应的实体类别：
- `/zzclear items` —— 强制立即清理所有掉落物。
- `/zzclear monsters` —— 强制立即清除所有敌对/中立怪物。
- `/zzclear animals` —— 强制立即清除所有动物。
- `/zzclear xps` —— 强制立即清除悬浮的经验球。
- `/zzclear others` —— 强制立即清除杂项（如画、矿车、掉落的方块、飞行中的箭矢等）。

### 3. 掉落物白名单管理
当你手里拿着某件物品时，可以直接使用该系列指令将其加入或移出清理过滤名单（不会被扫地大妈清掉）：
- `/zzclear white item add`  —— 将**当前手持的主武器/物品**加入全局白名单。
- `/zzclear white item del`  —— 将**当前手持的物品**从白名单中彻底移除。

### 4. 指定实体 ID 名单管理
如果你希望精准地保护或排除某种特定的生物或实体 (如 `minecraft:zombie`)，可以使用该系列指令：
- `/zzclear white entity add <实体ID>` —— 添加指定实体 ID 进入白名单。
- `/zzclear white entity del <实体ID>` —— 从白名单中移除指定的实体 ID。
  > *注：指令支持游戏内 ID 自动补全补全 (Tab suggestions)*。

---

## ⚙️ 配置文件说明

运行一次游戏后，模组会在 `config/` 目录下生成 `config.json` 文件，你可以自由调整以下策略：

| 设定参数 (Config) | 默认行为 | 说明 |
| :--- | :--- | :--- |
| **周期 (Period)** | 定时分钟数 | 设置模组每隔多久执行一次全图扫地任务。 |
| **EnableAnimals** | `false` | 是否开启清理友好的被动动物（牛、羊、猪等）。 |
| **EnableMonsters** | `true` | 是否清理敌对和中立怪物（僵尸、小白等）。 |
| **EnableMinecart** | `false` | 是否清退地图上的矿车。 |
| **EnableBoat** | `false` | 是否清退地图上的船。 |
| **EnablePaintingClear** | `false` | 是否连同墙壁上的“画”一起清理（防止恶意实体卡服）。 |
| **ItemFrameClear** | `false` | 是否强制剥落“物品展示框”。 |

> **提示：** 修改好 json 数据之后，回到游戏里敲一下 `/zzclear reload` 瞬间搞定，绝不拖泥带水！

---

## 📦 安装与支持版本

将编译好的 `zzclear-1.2.0.jar` 拖入服务端的 `mods` 文件夹即可。
- **支持端：** 纯服务器端与单人客户端 (Fabric)
- **要求：** Fabric Loader `0.16.5+`，Java 21
- **分支版本兼容：** 当前提供了专用于 `1.21.10` 及 `1.21.11` 的双版本原生编译架构。
