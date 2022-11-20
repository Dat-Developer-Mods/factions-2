package com.datdeveloper.datfactions.factionData;

import com.datdeveloper.datfactions.api.events.ChangeFactionMembershipEvent;
import com.datdeveloper.datfactions.database.Database;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FactionCollection extends BaseCollection<UUID, Faction>{

    Faction template;
    Faction WILDERNESS;
    Faction SAFEZONE;

    static FactionCollection instance = new FactionCollection();
    public static FactionCollection getInstance() {
        return instance;
    }

    public Faction createFaction(String name) {
        UUID factionId = UUID.randomUUID();
        Faction newFaction = new Faction(factionId, name);

        FactionIndex.getInstance().addFaction(newFaction);

        return newFaction;
    }

    public void disbandFaction(UUID factionId) {
        Faction faction = map.remove(factionId);
        Set<FactionPlayer> players = faction.getPlayers();
        FactionIndex.getInstance().deleteFaction(factionId);

        for (FactionPlayer player : players) {
            ChangeFactionMembershipEvent event = new ChangeFactionMembershipEvent(null, player, null, null, ChangeFactionMembershipEvent.EChangeFactionReason.DISBAND);
            MinecraftForge.EVENT_BUS.post(event);

            Faction newFaction = event.getNewFaction();
            FactionRole newRole = event.getNewRole();
            player.setFaction(newFaction != null ? newFaction.getId() : null, newRole != null ? newRole.getId() : null);
        }
    }

    @Override
    public void initialise() {
        List<UUID> storedFactions = Database.instance.getAllStoredFactions();
        for (UUID factionId : storedFactions) {
            Faction faction = Database.instance.loadFaction(factionId);
            if (faction != null) {
                map.put(factionId, faction);
            }
        }
    }
}
