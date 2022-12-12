package com.datdeveloper.datfactions.factionData;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionPlayerChangeMembershipEvent;
import com.datdeveloper.datfactions.database.DatabaseEntity;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import com.datdeveloper.datfactions.factionData.relations.EFactionRelation;
import com.datdeveloper.datfactions.util.AgeUtil;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * The representation of a player
 */
public class FactionPlayer extends DatabaseEntity {
    /**
     * The id of the player
     */
    final UUID id;

    /**
     * The name of the player the last time they logged in
     */
    String lastName;

    /**
     * The last time the player was active on the server
     */
    long lastActiveTime;

    /**
     * The amount of power the player has
     */
    int power;

    /**
     * The maximum amount of power the player can have
     */
    int maxPower;

    /**
     * The ID of the faction the player belongs to
     */
    UUID factionId;

    /**
     * The ID of the role of the player in its faction
     */
    UUID role;

    /**
     * Whether the player is currently autoclaiming
     */
    transient boolean autoClaim = false;

    /**
     * The chat mode the player is using
     */
    transient EFPlayerChatMode chatMode = EFPlayerChatMode.PUBLIC;

    /**
     * The method used to alert the player that it's moved into territory
     */
    EFPlayerChunkAlertMode chunkAlertMode;

    public FactionPlayer(final UUID id, final String lastName) {
        this.id = id;
        this.lastName = lastName;
        this.lastActiveTime = System.currentTimeMillis();

        this.maxPower = 20;
        this.power = this.maxPower;
        this.factionId = null;
        this.role = null;
        this.chunkAlertMode = EFPlayerChunkAlertMode.TITLE;
    }

    public FactionPlayer(final ServerPlayer player, final FactionPlayer template) {
        this.id = player.getUUID();
        this.lastName = player.getDisplayName().getString();
        this.lastActiveTime = System.currentTimeMillis();
        this.power = template.power;
        this.maxPower = template.maxPower;
        this.factionId = template.factionId;
        this.role = template.role;
        this.chunkAlertMode = template.chunkAlertMode;
    }

    /* ========================================= */
    /* Getters
    /* ========================================= */

    public UUID getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public boolean hasFaction() {
        return factionId != null;
    }

    public UUID getFactionId() {
        return factionId;
    }

    public Faction getFaction() {
        if (!hasFaction()) return null;
        return FactionCollection.getInstance().getByKey(getFactionId());
    }

    public String getName() {
        final ServerPlayer player = getServerPlayer();
        return player != null ? player.getName().getString() : getLastName();
    }

    public UUID getRoleId() {
        return role;
    }

    public FactionRole getRole() {
        final UUID roleId = getRoleId();
        return roleId != null ? getFaction().getRole(roleId) : null;
    }

    public boolean isAutoClaim() {
        return autoClaim;
    }

    public EFPlayerChunkAlertMode getChunkAlertMode() {
        return chunkAlertMode;
    }

    public EFPlayerChatMode getChatMode() {
        return chatMode;
    }

    /* ========================================= */
    /* Setters
    /* ========================================= */

    public void setLastName(final String lastName) {
        this.lastName = lastName;
        this.markDirty();
    }

    public void setLastActiveTime(final long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
        this.markDirty();
    }

    public void setAutoClaim(final boolean autoClaim) {
        this.autoClaim = autoClaim;
    }

    public void setChatMode(final EFPlayerChatMode chatMode) {
        this.chatMode = chatMode;
    }

    public void setChunkAlertMode(final EFPlayerChunkAlertMode chunkAlertMode) {
        this.chunkAlertMode = chunkAlertMode;
        markDirty();
    }

    /**
     * Sets the players faction and role
     * Updates the faction index, the player's commands, and informs the faction members of the change
     *
     * @param factionId The ID of the new faction
     * @param roleId The new Role ID for the player
     * @param reason The reason the player left
     */
    public void setFaction(final UUID factionId, final UUID roleId, final FactionPlayerChangeMembershipEvent.EChangeFactionReason reason) {
        if (Objects.equals(this.getFactionId(), factionId)) return;

        final Faction oldFaction = getFaction();
        final Faction newFaction = FactionCollection.getInstance().getByKey(factionId);

        this.factionId = factionId;
        this.role = roleId;

        FactionIndex.getInstance().updatePlayerFaction(this);

        // Update Factions
        if (List.of(FactionPlayerChangeMembershipEvent.EChangeFactionReason.JOIN, FactionPlayerChangeMembershipEvent.EChangeFactionReason.LEAVE, FactionPlayerChangeMembershipEvent.EChangeFactionReason.KICK).contains(reason)) {
            if (oldFaction != null) {
                oldFaction.sendFactionWideMessage(getNameWithDescription(oldFaction)
                                .withStyle(RelationUtil.getRelation(oldFaction, this).formatting)
                                .append(DatChatFormatting.TextColour.INFO + " has left the faction"),
                        List.of(getId())
                );
            }
            if (newFaction != null) {
                newFaction.sendFactionWideMessage(getNameWithDescription(newFaction)
                                .withStyle(RelationUtil.getRelation(newFaction, this).formatting)
                                .append(DatChatFormatting.TextColour.INFO + " has joined the faction"),
                        List.of(getId())
                );
            }
        }

        updateCommands();

        this.markDirty();
    }

    public void setRole(final UUID role) {
        this.role = role;
        updateCommands();
        markDirty();
    }

    /* ========================================= */
    /* Power
    /* ========================================= */

    public int getPower() {
        return power;
    }

    public int getMaxPower() {
        return maxPower;
    }

    public void addPower(final int power) {
        this.setPower(this.power + power);
    }

    public void addMaxPower(final int power) {
        this.setMaxPower(this.maxPower + power);
    }

    public void setPower(final int power) {
        this.power = Math.min(this.maxPower, (Math.max(power, FactionsConfig.getPlayerMinPower())));

        this.markDirty();
    }

    public void setMaxPower(final int maxPower) {
        this.maxPower = Math.min(FactionsConfig.getPlayerMaxPower(), (Math.max(maxPower, FactionsConfig.getPlayerMinPower())));

        this.markDirty();
    }

    /* ========================================= */
    /* Chat Summaries
    /* ========================================= */

    /**
     * Get a description of the player for chat
     * @param from The faction querying the relation
     * @return a description of the player for chat
     */
    public Component getChatSummary(@Nullable final Faction from) {
        // Title

        final MutableComponent message = Component.literal(DatChatFormatting.TextColour.HEADER + "____===[")
                .append(Component.literal(getLastName())
                        .withStyle(RelationUtil.getRelation(from, this).formatting)
                        .withStyle(Style.EMPTY
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.SUGGEST_COMMAND,
                                        "/factions pinfo " + getLastName()
                                ))
                        )
                )
                .append(DatChatFormatting.TextColour.HEADER +"]===____");


        if (hasFaction()) {
            final Faction faction = getFaction();
            if (!faction.hasFlag(EFactionFlags.ANONYMOUS)) {
                message.append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "Faction: ").append(faction.getNameWithDescription(from)).append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "Role: " + ChatFormatting.WHITE + getRole().getName());
            }
        }

        message.append("\n")
                .append(DatChatFormatting.TextColour.INFO + "Power/Max: " + ChatFormatting.WHITE + "%d/%d".formatted(getPower(), getMaxPower()));

        // Last Online
        if (!isPlayerOnline()) {
            message.append("\n")
                    .append(DatChatFormatting.TextColour.INFO + "Last Online: " + ChatFormatting.WHITE + AgeUtil.calculateAgeString(getLastActiveTime()) + " ago");
        }

        return message;
    }

    /**
     * Get a component containing the player's name with a hover event for showing player info and a click event for getting more player info
     * @param from the faction asking for the description
     * @return the player's name, ready for chat
     */
    public MutableComponent getNameWithDescription(@Nullable final Faction from) {
        final String name = getName();
        final MutableComponent component = Component.literal(name);
        component.withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getShortDescription(from))).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/factions pinfo " + name)));

        return component;
    }

    /**
     * Get a short version of the players' description for hover events
     * @param from The faction asking for the description
     * @return A short version of the player's description for hover
     */
    public MutableComponent getShortDescription(@Nullable final Faction from) {
        final MutableComponent component = Component.literal("");

        final EFactionRelation relation = RelationUtil.getRelation(from, this);
        if (relation != EFactionRelation.SELF) {
            component.append(relation.formatting + relation.name())
                    .append("\n");
        }

        component.append(DatChatFormatting.TextColour.INFO + "Power/Max: " + ChatFormatting.WHITE + "%d/%d".formatted(getPower(), getMaxPower()));


        if (hasFaction()) {
            final Faction faction = getFaction();
            if (!faction.hasFlag(EFactionFlags.ANONYMOUS)) {
                component.append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "Faction: " + RelationUtil.getRelation(from, faction).formatting + faction.getName()).append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "Role: " + ChatFormatting.WHITE + getRole().getName());
            }
        }

        // Last Online
        if (!isPlayerOnline()) {
            component.append("\n")
                    .append(DatChatFormatting.TextColour.INFO + "Last Online: " + ChatFormatting.WHITE + AgeUtil.calculateAgeString(getLastActiveTime()) + " ago");
        }

        return component;
    }

    /* ========================================= */
    /* Misc
    /* ========================================= */

    public @Nullable ServerPlayer getServerPlayer() {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(this.id);
    }

    public boolean isPlayerOnline() {
        return getServerPlayer() != null;
    }

    /**
     * Update the commands that the player has available to them
     */
    public void updateCommands() {
        final ServerPlayer serverPlayer = getServerPlayer();
        if (serverPlayer == null) return;

        serverPlayer.getServer().getCommands().sendCommands(serverPlayer);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof FactionPlayer fPlayer) && this.getId().equals(fPlayer.getId());
    }
}
