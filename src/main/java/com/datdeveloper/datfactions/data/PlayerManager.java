package com.datdeveloper.datfactions.data;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.api.events.PlayerDeregisterEvent;
import com.datdeveloper.datfactions.api.events.PlayerRegisterEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    Map<UUID, FactionPlayer> players = new ConcurrentHashMap<>();
    FactionPlayer template;

    FactionPlayer getPlayer(final UUID uuid) {
        return players.get(uuid);
    }

    FactionPlayer getPlayer(final ServerPlayer player) {
        return getPlayer(player.getUUID());
    }

    FactionPlayer getPlayer(final String username) {
        final ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(username);
        return player == null
                ? null
                : getPlayer(player);
    }

    /* ======================================== */
    /* Registration                             */
    /* ======================================== */

    FactionPlayer registerPlayer(final ServerPlayer player) {
        final FactionPlayer fPlayer = new FactionPlayer(player.getUUID(), template);
        players.put(player.getUUID(), fPlayer);

        // Store player

        NeoForge.EVENT_BUS.post(new PlayerRegisterEvent(fPlayer));
        Datfactions.LOGGER.info("Registered new player: {}}", fPlayer);

        return fPlayer;
    }

    void deregisterPlayer(final UUID playerId) {
        final FactionPlayer fPlayer = players.remove(playerId);

        // Remove from database

        NeoForge.EVENT_BUS.post(new PlayerDeregisterEvent(fPlayer));
        Datfactions.LOGGER.info("Deregistered player: {}", fPlayer);
    }
}
