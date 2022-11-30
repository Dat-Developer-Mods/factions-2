package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Fired when a player changes faction
 * <br>
 * Changes to newFaction and newRole will only be reflected, and the event can only be cancelled, if the reason isn't CREATE, DISBAND, NEWPLAYER, or ADMIN
 */
public class FactionChangeMembershipEvent extends FactionPlayerEvent {

    /**
     * The faction that the player is joining
     */
    @Nullable
    Faction newFaction;

    /**
     * The new role in the faction that the player is joining
     */
    @Nullable
    FactionRole newRole;

    /**
     * The reason the player changed factions
     */
    final EChangeFactionReason reason;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param player The player changing faction
     * @param newFaction The faction the player is changing to
     * @param newRole The role in the faction the player will get
     * @param reason The reason the player changed faction
     */
    public FactionChangeMembershipEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player, @Nullable final Faction newFaction, @Nullable final FactionRole newRole, final EChangeFactionReason reason) {
        super(instigator, player);
        this.newFaction = newFaction;
        this.newRole = newRole;
        this.reason = reason;
    }

    /**
     * Get the faction the player is joining
     * @return the faction the player is joining
     */
    public @Nullable Faction getNewFaction() {
        return newFaction;
    }

    /**
     * Set the faction the player will join
     * Only works if the reason isn't CREATE, DISBAND, NEWPLAYER, or ADMIN
     * @param newFaction the faction the player will join
     */
    public void setNewFaction(@Nullable final Faction newFaction) {
        this.newFaction = newFaction;
    }

    /**
     * Get the new role the player will take
     * @return the new role for the player
     */
    public @Nullable FactionRole getNewRole() {
        return newRole;
    }

    /**
     * Set the role the player will take
     * Only works if the reason isn't CREATE, DISBAND, NEWPLAYER, or ADMIN
     * @param newRole The role the player will take
     */
    public void setNewRole(@Nullable final FactionRole newRole) {
        if (Arrays.asList(EChangeFactionReason.CREATE, EChangeFactionReason.DISBAND, EChangeFactionReason.NEWPLAYER, EChangeFactionReason.ADMIN).contains(getReason())) {
            throw new UnsupportedOperationException("You cannot set the new role of the player if reason is CREATE, DISBAND, NEWPLAYER, or ADMIN");
        }
        this.newRole = newRole;
    }

    /**
     * Get the reason for the membership change
     * @return the reason
     */
    public EChangeFactionReason getReason() {
        return reason;
    }

    @Override
    public boolean isCancelable() {
        return Stream.of(EChangeFactionReason.CREATE, EChangeFactionReason.DISBAND, EChangeFactionReason.ADMIN, EChangeFactionReason.NEWPLAYER)
                .allMatch(eChangeFactionReason -> reason != eChangeFactionReason);
    }

    /**
     * The reason the player left the faction
     */
    public enum EChangeFactionReason {
        /** Joined because they created the faction */
        CREATE,
        /** Joined because they left a faction */
        DISBAND,
        /** Joined because they left their previous faction */
        LEAVE,
        /** Joined because they were kicked from their previous faction */
        KICK,
        /** Joined because they joined a faction */
        JOIN,
        /** Joined because they are a new player joining the default faction */
        NEWPLAYER,
        /** Joined because they were deleted */
        DELETE,
        /** Joined because an admin forced their faction */
        ADMIN
    }
}
