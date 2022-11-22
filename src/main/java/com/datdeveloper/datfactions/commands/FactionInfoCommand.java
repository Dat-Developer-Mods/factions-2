package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionChangeNameEvent;
import com.datdeveloper.datfactions.commands.arguments.FactionArgument;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONINFO;
import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONSETNAME;

public class FactionInfoCommand extends BaseFactionCommand {
    static void register(LiteralArgumentBuilder<CommandSourceStack> command) {
        LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("info")
                .requires(FactionPermissions.hasPermission(FACTIONINFO))
                .then(Commands.argument("targetFaction", new FactionArgument())
                        .executes(c -> {
                            Faction target = c.getArgument("targetFaction", Faction.class);

                            c.getSource().sendSystemMessage(target.getChatSummary());

                            return 0;
                        })).build();

        command.then(subCommand);
        command.then(Commands.literal("finfo").redirect(subCommand));
        command.then(Commands.literal("faction").redirect(subCommand));
        command.then(Commands.literal("show").redirect(subCommand));
    }
}
