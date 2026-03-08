package zzai.clearlogic;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import zzai.clearlogic.Static;
import zzai.clearlogic.ClearEngine;
import zzai.clearlogic.config.ClearConfigNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

public class CommandController {
        public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
                dispatcher.register(
                                Commands.literal(Static.MOD_ID)
                                                .then(Commands.literal("items")
                                                                .executes(CommandController::itemsExe)
                                                                .requires(ctx -> Static.cmdPermission(ctx,
                                                                                "zzclear.command.items", true)))
                                                .then(Commands.literal("monsters")
                                                                .executes(CommandController::monstersExe)
                                                                .requires(ctx -> Static.cmdPermission(ctx,
                                                                                "zzclear.command.monsters", true)))
                                                .then(Commands.literal("animals")
                                                                .executes(CommandController::animalsExe)
                                                                .requires(ctx -> Static.cmdPermission(ctx,
                                                                                "zzclear.command.animals", true)))
                                                .then(Commands.literal("others")
                                                                .executes(CommandController::othersExe)
                                                                .requires(ctx -> Static.cmdPermission(ctx,
                                                                                "zzclear.command.others", true)))
                                                .then(Commands.literal("xps")
                                                                .executes(CommandController::xpsExe)
                                                                .requires(ctx -> Static.cmdPermission(ctx,
                                                                                "zzclear.command.xps", true)))
                                                .then(Commands.literal("white")
                                                                .then(Commands.literal("item")
                                                                                .then(Commands.literal("add").executes(
                                                                                                CommandController::addItemWhite))
                                                                                .then(Commands.literal("del").executes(
                                                                                                CommandController::delItemWhite)))
                                                                .then(Commands.literal("entity")
                                                                                .then(Commands.literal("add")
                                                                                                .then(Commands.argument(
                                                                                                                "id",
                                                                                                                ResourceLocationArgument
                                                                                                                                .id())
                                                                                                                .executes(CommandController::addEntityWhite)))
                                                                                .then(Commands.literal("del")
                                                                                                .then(Commands.argument(
                                                                                                                "id",
                                                                                                                ResourceLocationArgument
                                                                                                                                .id())
                                                                                                                .executes(CommandController::delEntityWhite))))
                                                                .requires(ctx -> Static.cmdPermission(ctx,
                                                                                "zzclear.command.admin", true)))
                                                .then(Commands.literal("reload")
                                                                .executes(CommandController::reloadConfig)
                                                                .requires(ctx -> Static.cmdPermission(ctx,
                                                                                "zzclear.command.admin", true))));
        }

        private static int reloadConfig(CommandContext<CommandSourceStack> context) {
                ClearConfigNode.load();
                ClearEngine.INSTANCE.resetTimer(context.getSource().getServer());
                if (context.getSource().getEntity() instanceof Player player) {
                        Static.sendMessage(player, "§a[崽崽] 配置文件已重新加载！");
                } else {
                        context.getSource().sendSuccess(() -> Component.literal("[崽崽] 配置文件已重新加载！"), true);
                }
                return 1;
        }

        private static int addItemWhite(CommandContext<CommandSourceStack> context) {
                try {
                        Player player = context.getSource().getPlayerOrException();
                        ItemStack item = player.getMainHandItem();
                        ResourceLocation rl = BuiltInRegistries.ITEM.getKey(item.getItem());
                        if (rl != null) {
                                ClearConfigNode.INSTANCE.getItem().getItemWhitelist().add(rl.toString());
                                ClearConfigNode.save();
                                Static.sendMessage(player, "zzclear.chat.whitelist.item.added");
                        } else {
                                Static.sendMessage(player, "zzclear.chat.whitelist.item.exists");
                        }
                } catch (Exception e) {
                        // Error handling
                }
                return 1;
        }

        private static int delItemWhite(CommandContext<CommandSourceStack> context) {
                try {
                        Player player = context.getSource().getPlayerOrException();
                        ItemStack item = player.getMainHandItem();
                        ResourceLocation rl = BuiltInRegistries.ITEM.getKey(item.getItem());
                        if (rl != null) {
                                ClearConfigNode.INSTANCE.getItem().getItemWhitelist().remove(rl.toString());
                                ClearConfigNode.save();
                                Static.sendMessage(player, "zzclear.chat.whitelist.item.removed");
                        }
                } catch (Exception e) {
                        // Error handling
                }
                return 1;
        }

        private static int addEntityWhite(CommandContext<CommandSourceStack> context) {
                try {
                        Player player = context.getSource().getPlayerOrException();
                        ResourceLocation rl = context.getArgument("id", ResourceLocation.class);
                        if (BuiltInRegistries.ENTITY_TYPE.get(rl).isPresent()) {
                                ClearConfigNode.INSTANCE.getMob().getMobWhitelist().add(rl.toString());
                                ClearConfigNode.save();
                                Static.sendMessage(player, "zzclear.chat.whitelist.entity.added");
                        } else {
                                Static.sendMessage(player, "zzclear.chat.whitelist.entity.exists");
                        }
                } catch (Exception e) {
                        // Error handling
                }
                return 1;
        }

        private static int delEntityWhite(CommandContext<CommandSourceStack> context) {
                try {
                        Player player = context.getSource().getPlayerOrException();
                        ResourceLocation rl = context.getArgument("id", ResourceLocation.class);
                        if (BuiltInRegistries.ENTITY_TYPE.get(rl).isPresent()) {
                                ClearConfigNode.INSTANCE.getMob().getMobWhitelist().remove(rl.toString());
                                ClearConfigNode.save();
                                Static.sendMessage(player, "zzclear.chat.whitelist.entity.removed");
                        }
                } catch (Exception e) {
                        // Error handling
                }
                return 1;
        }

        private static int itemsExe(CommandContext<CommandSourceStack> context) {
                int r = ClearEngine.INSTANCE.clearItems(context.getSource().getServer());
                Static.sendMessageToAllPlayers(context.getSource().getServer(),
                                ClearConfigNode.INSTANCE.getCommon().getSweepCompleteMessage(), r, 0, 0, 0);
                return 1;
        }

        private static int monstersExe(CommandContext<CommandSourceStack> context) {
                int r = ClearEngine.INSTANCE.clearMonsters(context.getSource().getServer());
                Static.sendMessageToAllPlayers(context.getSource().getServer(),
                                ClearConfigNode.INSTANCE.getCommon().getSweepCompleteMessage(), 0, r, 0, 0);
                return 1;
        }

        private static int animalsExe(CommandContext<CommandSourceStack> context) {
                int r = ClearEngine.INSTANCE.clearAnimals(context.getSource().getServer());
                Static.sendMessageToAllPlayers(context.getSource().getServer(),
                                ClearConfigNode.INSTANCE.getCommon().getSweepCompleteMessage(), 0, r, 0, 0);
                return 1;
        }

        private static int xpsExe(CommandContext<CommandSourceStack> context) {
                int r = ClearEngine.INSTANCE.clearXPs(context.getSource().getServer());
                Static.sendMessageToAllPlayers(context.getSource().getServer(),
                                ClearConfigNode.INSTANCE.getCommon().getSweepCompleteMessage(), 0, 0, r, 0);
                return 1;
        }

        private static int othersExe(CommandContext<CommandSourceStack> context) {
                int r = ClearEngine.INSTANCE.clearMisc(context.getSource().getServer());
                Static.sendMessageToAllPlayers(context.getSource().getServer(),
                                ClearConfigNode.INSTANCE.getCommon().getSweepCompleteMessage(), 0, 0, 0, r);
                return 1;
        }
}
