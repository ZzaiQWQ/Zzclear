# Zzclear - The Ultimate Optimization & Auto-Clearing Mod

[中文版 (Chinese Version)](./README.md) | **English**

![Zzclear](https://img.shields.io/badge/Minecraft-1.21.10%20%7C%201.21.11-success?style=for-the-badge&logo=minecraft)
![Fabric](https://img.shields.io/badge/Fabric-0.16.5+-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-All_Rights_Reserved-red?style=for-the-badge)

**Zzclear** is a lightweight, highly intelligent entity clearing and server optimization mod built specifically for modern Minecraft (1.21.10 / 1.21.11 Fabric).
It silently operates in the background to automatically sweep up dropped items, abandoned entities, and lag-inducing objects based on a highly configurable frequency timer. With a powerful in-game command system and hot-reloadable configuration logic, it eliminates server lag from its absolute source!

---

## ✨ Core Features

- 🕐 **Automated Scheduled Clearing:** Customize the sweeping cycles (down to the exact minute); when the timer strikes, lingering entities are flawlessly eradicated.
- 🛡️ **Smart Whitelists & Filters:** Say goodbye to "one-size-fits-all" wipes! Whether it's dropped items, specific monsters, or individual entity types, Zzclear supports precise bypassing and explicit targetting via its blacklist/whitelist arrays.
- 🪓 **Total Entity Management:**
  - **Standard Clearing:** Dropped Items, Experience Orbs
  - **Mob Clearing:** Hostile/Neutral Monsters, Passive Animals
  - **Vehicles/Decorations:** Minecarts, Boats, Paintings, Item Frames
  - **Physics/Projectiles:** Fired Arrows, Thrown Tridents, Falling Blocks, Primed TNT, and dynamic entities.
- 🔄 **Hot-Reload Commands:** Changing the configuration data file? No need to restart the server! A single built-in command applies the new rules instantly.

---

## ⌨️ In-Game Commands

The primary root command is `/zzclear`. You must be an OP or possess the necessary administrative execution permissions to utilize these commands:

### 1. Configuration Reload
` /zzclear reload `
- **Function:** Re-reads the `config/config.json` file from your disk and immediately applies all new clearing filters and timer periods.

### 2. Manual Immediate Sweeping
If you do not want to wait for the background timer intervals, use these commands to instantly wipe specific entity categories:
- `/zzclear items` —— Instantly clears all dropped items on the ground.
- `/zzclear monsters` —— Instantly clears all hostile and neutral monsters.
- `/zzclear animals` —— Instantly clears all friendly, passive animals.
- `/zzclear xps` —— Instantly clears all floating experience orbs.
- `/zzclear others` —— Instantly clears miscellaneous entities (e.g., Paintings, Minecarts, Projectiles, Falling Blocks).

### 3. Item Whitelists
Hold an item in your main hand and instantly register it into the exact whitelist filters to prevent the sweeper from ever consuming it:
- `/zzclear white item add`  —— Adds the **currently held item** into the global whitelist.
- `/zzclear white item del`  —— Removes the **currently held item** from the global whitelist.

### 4. Entity ID Whitelists
If you need to meticulously protect or exclude a specific living organism or object (e.g., `minecraft:zombie`), utilize these endpoints:
- `/zzclear white entity add <Entity ID>` —— Adds a specific entity registry ID into the whitelist.
- `/zzclear white entity del <Entity ID>` —— Un-registers a specific entity registry ID from the whitelist.
  > *Note: These commands fully support in-game Tab autocompletion suggestions!*

---

## ⚙️ Configuration Setup

After launching the game once, the mod automatically generates a `config.json` file inside the `config/` directory. You are free to heavily modify the following matrices:

| Parameter (Config) | Default | Description |
| :--- | :--- | :--- |
| **Period** | Timer Minutes | Sets the interval delay in minutes between full-map sweeps. |
| **EnableAnimals** | `false` | Whether to purge friendly/passive animals (Cows, Sheep, Pigs, etc.). |
| **EnableMonsters** | `true` | Whether to dispatch hostile/neutral monsters (Zombies, Skeletons, etc.). |
| **EnableMinecart** | `false` | Whether to sweep abandoned Minecarts. |
| **EnableBoat** | `false` | Whether to sweep abandoned Boats. |
| **EnablePaintingClear** | `false` | Specifies whether to peel "Paintings" off the walls (prevents entity spam exploits). |
| **ItemFrameClear** | `false` | Forcibly pops off "Item Frames" alongside their items. |

> **Tip:** After tweaking the JSON arrays, jump right back into the game and fire `/zzclear reload`. Done instantly, with absolutely zero delays!

---

## 📦 Installation & Version Support

Simply drag-and-drop the compiled `zzclear-1.2.0.jar` into your server or client `mods/` folder.
- **Environments:** Dedicated Server & Singleplayer Client (Fabric)
- **Requirements:** Fabric Loader `0.16.5+`, Java 21
- **Cross-Version Architecture:** We proudly provide dual native architectures compiled exclusively and stably for both `1.21.10` and `1.21.11` builds.

