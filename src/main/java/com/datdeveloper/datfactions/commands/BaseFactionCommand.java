package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

public class BaseFactionCommand {

    /**
     * Get the player or the player template if the player hasn't been registered yet
     * @param player the serverplayer to get
     * @return the player or the player template
     */
    protected static FactionPlayer getPlayerOrTemplate(final ServerPlayer player) {
        FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
        return fPlayer != null ? fPlayer : FPlayerCollection.getInstance().getTemplate();
    }
    protected static Component wrapCommand(final String display, final String actualCommand) {
        return MutableComponent.create(Component.literal(display).getContents())
                .withStyle(DatChatFormatting.TextColour.COMMAND)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, actualCommand)));
    }
}
