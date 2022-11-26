package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import net.minecraft.server.level.ServerPlayer;

public class BaseFactionCommand {

    /**
     * Get the player or the player template if the player hasn't been registered yet
     * @param player the serverplayer to get
     * @return the player or the player template
     */
    protected static FactionPlayer getPlayerOrTemplate(final ServerPlayer player) {
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
        return fPlayer != null ? fPlayer : FPlayerCollection.getInstance().getTemplate();
    }
}
