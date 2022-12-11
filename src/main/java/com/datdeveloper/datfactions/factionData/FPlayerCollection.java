package com.datdeveloper.datfactions.factionData;

import com.datdeveloper.datfactions.api.events.FactionPlayerChangeMembershipEvent;
import com.datdeveloper.datfactions.database.Database;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;

public class FPlayerCollection extends BaseCollection<UUID, FactionPlayer> {
    final Logger logger = LogUtils.getLogger();

    FactionPlayer template;

    // Singleton
    static final FPlayerCollection instance = new FPlayerCollection();
    public static FPlayerCollection getInstance() {
        return instance;
    }

    FPlayerCollection() {
        template = new FactionPlayer(UUID.randomUUID(), "null");
    }

    /* ========================================= */
    /* Getters
    /* ========================================= */

    public FactionPlayer getTemplate() {
        return template;
    }

    /**
     * Get a player by it's {@link ServerPlayer}
     * @param player the ServerPlayer to get the equivilent FactionPlayer of
     * @return The FactionPlayer represented by the server player
     */
    public FactionPlayer getPlayer(@Nullable final ServerPlayer player) {
        if (player == null) return null;
        return this.getByKey(player.getUUID());
    }

    /**
     * Get a faction player by its name
     * @param name The name of the player
     * @return the FactionPlayer
     */
    public FactionPlayer getByName(final String name) {
        return map.values().stream()
                .filter(player -> name.equals(player.getName()))
                .findFirst()
                .orElse(null);
    }

    /* ========================================= */
    /* Registration
    /* ========================================= */

    public boolean isPlayerRegistered(final UUID id) {
        return map.containsKey(id);
    }

    public boolean isPlayerRegistered(final ServerPlayer player) {
        return isPlayerRegistered(player.getUUID());
    }

    /**
     * Register a new player to the factions system
     * @param player the player to register
     */
    public void registerNewPlayer(final ServerPlayer player) {
        final FactionPlayer newPlayer = new FactionPlayer(player, template);
        map.put(player.getUUID(), newPlayer);
        Database.instance.storePlayer(newPlayer);
        FactionIndex.getInstance().updatePlayerFaction(newPlayer);
        logger.info("Registered new factions player: " + player.getName().getString());
    }

    /**
     * Delete a player from the faction's system
     * @param id The id of the player to deregister
     */
    public void deregisterPlayer(final UUID id) {
        final FactionPlayer player = map.remove(id);
        player.setFaction(null, null, FactionPlayerChangeMembershipEvent.EChangeFactionReason.DELETE);
        Database.instance.deletePlayer(player);
    }

    /* ========================================= */
    /* Setup and teardown
    /* ========================================= */

    @Override
    public void initialise() {
        final List<UUID> storedPlayers = Database.instance.getAllStoredPlayers();
        for (final UUID playerId : storedPlayers) {
            final FactionPlayer player = Database.instance.loadPlayer(playerId);
            if (player == null) continue;

            if (player.hasFaction() && FactionCollection.getInstance().getByKey(player.getFactionId()) == null) {
                logger.warn("Player " + player.lastName + " (" + player.id + ") belongs to a faction that no longer exists, they will be corrected to no faction");
                player.setFaction(null, null, FactionPlayerChangeMembershipEvent.EChangeFactionReason.CREATE);
            }

            map.put(playerId, player);
        }

        // Load Template player
        template = Database.instance.loadPlayerTemplate();
        if (template == null) {
            template = new FactionPlayer((UUID) null, null);
            Database.instance.storePlayerTemplate(template);
        }

        // TODO: Check for pending invites
    }
}
