package clear.common.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import clear.Static;
import clear.core.ZzclearCore;
import clear.init.config.ModConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

public class ZzclearCommand {
        public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
                dispatcher.register(
                                Commands.literal(Static.MOD_ID)
                                                .then(Commands.literal("items")
                                                                .executes(ZzclearCommand::itemsExe)
                                                                .requires(ctx -> Static.cmdPermission(ctx,
                                                                                "zzclear.command.items", true)))
                                                .then(Commands.literal("monsters")
                                                                .executes(ZzclearCommand::monstersExe)
                                                                .requires(ctx -> Static.cmdPermission(ctx,
                                                                                "zzclear.command.monsters", true)))
                                                .then(Commands.literal("animals")
                                                                .executes(ZzclearCommand::animalsExe)
                                                                .requires(ctx -> Static.cmdPermission(ctx,
                                                                                "zzclear.command.animals", true)))
                                                .then(Commands.literal("others")
                                                                .executes(ZzclearCommand::othersExe)
                                                                .requires(ctx -> Static.cmdPermission(ctx,
                                                                                "zzclear.command.others", true)))
                                                .then(Commands.literal("xps")
                                                                .executes(ZzclearCommand::xpsExe)
                                                                .requires(ctx -> Static.cmdPermission(ctx,
                                                                                "zzclear.command.xps", true)))
                                                .then(Commands.literal("white")
                                                                .then(Commands.literal("item")
                                                                                .then(Commands.literal("add").executes(
                                                                                                ZzclearCommand::addItemWhite))
                                                                                .then(Commands.literal("del").executes(
                                                                                                ZzclearCommand::delItemWhite)))
                                                                .then(Commands.literal("entity")
                                                                                .then(Commands.literal("add")
                                                                                                .then(Commands.argument(
                                                                                                                "id",
                                                                                                                ResourceLocationArgument
                                                                                                                                .id())
                                                                                                                .executes(ZzclearCommand::addEntityWhite)))
                                                                                .then(Commands.literal("del")
                                                                                                .then(Commands.argument(
                                                                                                                "id",
                                                                                                                ResourceLocationArgument
                                                                                                                                .id())
                                                                                                                .executes(ZzclearCommand::delEntityWhite))))
                                                                .requires(ctx -> Static.cmdPermission(ctx,
                                                                                "zzclear.command.admin", true)))
                                                .then(Commands.literal("reload")
                                                                .executes(ZzclearCommand::reloadConfig)
                                                                .requires(ctx -> Static.cmdPermission(ctx,
                                                                                "zzclear.command.admin", true))));
        }

        private static int reloadConfig(CommandContext<CommandSourceStack> context) {
                ModConfig.load();
                ZzclearCore.INSTANCE.resetTimer(context.getSource().getServer());
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
                                ModConfig.INSTANCE.getItem().getItemEntitiesWhitelist().add(rl.toString());
                                ModConfig.save();
                                Static.sendMessage(player, "message.cmd.item_white.add.success");
                        } else {
                                Static.sendMessage(player, "message.cmd.item_white.add.fail");
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
                                ModConfig.INSTANCE.getItem().getItemEntitiesWhitelist().remove(rl.toString());
                                ModConfig.save();
                                Static.sendMessage(player, "message.cmd.item_white.del.success");
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
                                ModConfig.INSTANCE.getMob().getMobEntitiesWhitelist().add(rl.toString());
                                ModConfig.save();
                                Static.sendMessage(player, "message.cmd.entity_white.add.success");
                        } else {
                                Static.sendMessage(player, "message.cmd.entity_white.add.fail");
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
                                ModConfig.INSTANCE.getMob().getMobEntitiesWhitelist().remove(rl.toString());
                                ModConfig.save();
                                Static.sendMessage(player, "message.cmd.entity_white.del.success");
                        }
                } catch (Exception e) {
                        // Error handling
                }
                return 1;
        }

        private static int itemsExe(CommandContext<CommandSourceStack> context) {
                int r = ZzclearCore.INSTANCE.clearItems(context.getSource().getServer());
                Static.sendMessageToAllPlayers(context.getSource().getServer(),
                                ModConfig.INSTANCE.getCommon().getclearNoticeComplete(), r, 0, 0, 0);
                return 1;
        }

        private static int monstersExe(CommandContext<CommandSourceStack> context) {
                int r = ZzclearCore.INSTANCE.clearMonsters(context.getSource().getServer());
                Static.sendMessageToAllPlayers(context.getSource().getServer(),
                                ModConfig.INSTANCE.getCommon().getclearNoticeComplete(), 0, r, 0, 0);
                return 1;
        }

        private static int animalsExe(CommandContext<CommandSourceStack> context) {
                int r = ZzclearCore.INSTANCE.clearAnimals(context.getSource().getServer());
                Static.sendMessageToAllPlayers(context.getSource().getServer(),
                                ModConfig.INSTANCE.getCommon().getclearNoticeComplete(), 0, r, 0, 0);
                return 1;
        }

        private static int xpsExe(CommandContext<CommandSourceStack> context) {
                int r = ZzclearCore.INSTANCE.clearXPs(context.getSource().getServer());
                Static.sendMessageToAllPlayers(context.getSource().getServer(),
                                ModConfig.INSTANCE.getCommon().getclearNoticeComplete(), 0, 0, r, 0);
                return 1;
        }

        private static int othersExe(CommandContext<CommandSourceStack> context) {
                int r = ZzclearCore.INSTANCE.clearMisc(context.getSource().getServer());
                Static.sendMessageToAllPlayers(context.getSource().getServer(),
                                ModConfig.INSTANCE.getCommon().getclearNoticeComplete(), 0, 0, 0, r);
                return 1;
        }
}
