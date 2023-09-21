package com.datdeveloper.datfactions.factiondata;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionPlayerChangeRoleEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.database.DatabaseEntity;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import com.datdeveloper.datfactions.factiondata.relations.EFactionRelation;
import com.datdeveloper.datfactions.factiondata.relations.FactionRelation;
import com.datdeveloper.datfactions.util.AgeUtil;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.collections.Pair;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Faction extends DatabaseEntity {
    /**
     * The faction's ID
     */
    final UUID id;

    /**
     * The faction's name
     */
    String name;

    /**
     * The faction's Description
     */
    String description;

    /**
     * The faction's MOTD
     */
    String motd;

    /**
     * The amount of bonus power that the faction has
     */
    int factionPower;

    /**
     * The timestamp the faction was created
     */
    long creationTime;

    /**
     * The block position of the faction's home
     */
    BlockPos homeLocation;

    /**
     * The level of the faction's home
     */
    ResourceKey<Level> homeLevel;

    /**
     * The invites to players the faction has
     */
    final Set<UUID> playerInvites;

    /**
     * The roles the faction has, in order from owner to recruit (owner at position 0, recruit in last place)
     */
    final List<FactionRole> roles;

    /**
     * A set of flags the faction has
     */
    final Set<EFactionFlags> flags;
    /**
     * The relations the faction has
     */
    final Map<UUID, FactionRelation> relations;

    /**
     * Default Constructor for Deserialization
     */
    protected Faction() {
        id = null;
        playerInvites = new HashSet<>();
        roles = new ArrayList<>();
        flags = new HashSet<>();
        relations = new HashMap<>();
    }

    public Faction(final UUID id, final String name) {
        this.id = id;
        this.name = name;
        this.description = "";
        this.motd = null;

        this.factionPower = 0;

        this.creationTime = System.currentTimeMillis();

        this.homeLocation = null;
        this.homeLevel = null;

        this.playerInvites = new HashSet<>();
        this.roles = FactionRole.getDefaultRoles();
        this.flags = new HashSet<>();
        this.relations = new HashMap<>();
    }

    public Faction(final UUID id, final String name, final Faction template) {
        this.id = id;
        this.name = name;
        this.description = "";
        this.motd = "";

        this.factionPower = template.factionPower;

        this.creationTime = System.currentTimeMillis();

        this.homeLocation = template.homeLocation;
        this.homeLevel = template.homeLevel;

        this.playerInvites = new HashSet<>();

        // Deep Copy roles
        this.roles = new ArrayList<>();
        for (final FactionRole role : template.roles) {
            this.roles.add(new FactionRole(role));
        }

        this.flags = new HashSet<>(template.flags);

        // Don't copy relations
        this.relations = new HashMap<>();
    }

    public Set<FactionPlayer> getPlayers() {
        return FactionIndex.getInstance().getFactionPlayers(this);
    }

    /* ========================================= */
    /* Getters
    /* ========================================= */

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMotd() {
        return motd;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public BlockPos getHomeLocation() {
        return homeLocation;
    }

    public ResourceKey<Level> getHomeLevel() {
        return homeLevel;
    }

    public void setName(final String newName) {
        if (newName.equals(name) || newName.isEmpty()) return;

        this.name = newName;
        markDirty();
    }

    public void setDescription(final String newDescription) {
        if (newDescription.equals(description)) return;
        
        this.description = newDescription;
        markDirty();
    }

    public void setMotd(final String newMotd) {
        if (newMotd.equals(motd)) return;

        this.motd = newMotd;

        sendFactionWideMessage(Component.literal(DatChatFormatting.TextColour.INFO + "Your faction's MOTD has changed\n" + ChatFormatting.WHITE + getMotd()));

        markDirty();
    }

    public void setFactionHome(final ResourceKey<Level> newHomeLevel, final BlockPos newHomeLocation) {
        this.homeLocation = newHomeLocation;
        this.homeLevel = newHomeLevel;
        markDirty();
    }



    /* ========================================= */
    /* Power
    /* ========================================= */

    public int getTotalPower() {
        if (hasFlag(EFactionFlags.INFINITEPOWER)) return Integer.MAX_VALUE;
        return Math.min(factionPower + getPlayers().stream().mapToInt(FactionPlayer::getPower).sum(), FactionsConfig.getFactionMaxPower());
    }

    public int getTotalMaxPower() {
        if (hasFlag(EFactionFlags.INFINITEPOWER)) return Integer.MAX_VALUE;
        return Math.min(factionPower + getPlayers().stream().mapToInt(FactionPlayer::getMaxPower).sum(), FactionsConfig.getFactionMaxPower());
    }

    public int getMaxLandWorth() {
        return (int) (getTotalPower() * FactionsConfig.getPowerLandMultiplier());
    }

    /* ========================================= */
    /* Owned Chunks
    /* ========================================= */

    /**
     * Check if the faction owns the given chunk in the given level
     * @param level The level that contains the chunk
     * @param chunkPos The position of the chunk
     * @return True if the faction owns the chunk
     */
    private boolean checkOwnsChunk(final ResourceKey<Level> level, final ChunkPos chunkPos) {
        return this.getId().equals(FLevelCollection.getInstance().getByKey(level).getChunkOwner(chunkPos));
    }

    /**
     * Get the count of the chunks the faction owns in the specified level
     * @param level The level to check
     * @return the count of chunks the faction owns in the specified level
     */
    public int getLandCountInlevel(final FactionLevel level) {
        return level.getClaimsCount(this.getId());
    }

    /**
     * Get the worth of the chunks the faction owns in the specified level
     * @param level The level to check
     * @return the total worth of the chunks the faction owns in the specified level
     */
    public int getLandWorthInLevel(final FactionLevel level) {
        return level.getClaimsWorth(this.getId());
    }

    /**
     * Get the count of all the chunks the faction owns
     * @return the count of all the chunks the faction owns
     */
    public int getTotalLandCount() {
        return FLevelCollection.getInstance().getAll().values().stream()
                .mapToInt(factionLevel -> factionLevel.getClaimsCount(this.getId()))
                .sum();
    }

    /**
     * Get the worth of all the chunks the faction owns
     * @return the total worth of all the faction's chunks
     */
    public int getTotalLandWorth() {
        return FLevelCollection.getInstance().getAll().values().stream()
                .mapToInt(factionLevel -> factionLevel.getClaimsWorth(this.getId()))
                .sum();
    }

    /* ========================================= */
    /* Roles
    /* ========================================= */

    public List<FactionRole> getRoles() {
        return roles;
    }

    /**
     * Get the role that represents the owner of the faction
     * @return the owner role of the faction
     */
    public FactionRole getOwnerRole() {
        return roles.get(0);
    }

    /**
     * Get the role that represents the recruits of the faction
     * @return The recruit role of the faction
     */
    public FactionRole getRecruitRole() {
        return roles.get(roles.size() - 1);
    }

    /**
     * Get a role by its ID
     * @param roleId the ID of the role
     * @return the role with the given ID, or null if it doesn't exist
     */
    public FactionRole getRole(final UUID roleId) {
        return roles.stream()
                .filter(role -> roleId.equals(role.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get a role by its name
     * @param roleName The name of the role
     * @return The role with the given name, or null if it doesn't exist
     */
    public FactionRole getRoleByName(final String roleName) {
        return roles.stream()
                .filter(role -> roleName.equals(role.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the index a role by its ID
     * @param roleId The ID of the role
     * @return The index of the role with the given ID, or -1 if it doesn't exist
     */
    public int getRoleIndex(final UUID roleId) {
        return IntStream.range(0, roles.size())
                .filter(i -> roleId.equals(roles.get(i).getId()))
                .findFirst()
                .orElse(-1);
    }

    /**
     * Get the index a role by its name
     * @param roleName The name of the role
     * @return The index of the role with the given name, or -1 if it doesn't exist
     */
    public int getRoleIndexByName(final String roleName) {
        return IntStream.range(0, roles.size())
                .filter(i -> roleName.equals(roles.get(i).getName()))
                .findFirst()
                .orElse(-1);
    }

    /**
     * Create a new role
     * @param roleName The name of the new role
     * @param roleParent The parent of the new role
     * @return the new role
     */
    public FactionRole createNewRole(final String roleName, final FactionRole roleParent) {
        if (getRoleByName(roleName) != null) {
            return null;
        }

        final int parentIndex = roles.indexOf(roleParent);
        if (parentIndex == -1) {
            return null;
        }

        final FactionRole newRole = new FactionRole(roleName);
        newRole.markDirty();
        this.roles.add(parentIndex + 1, newRole);

        return newRole;
    }

    /**
     * Change the parent of a role
     * @param roleId The id of the role to change
     * @param newParentId The new parent of the role
     */
    public void setRoleParent(final UUID roleId, final UUID newParentId) {
        final FactionRole role = getRole(roleId);
        if (role == null || roleId.equals(newParentId)) {
            return;
        }

        if (role.getId().equals(getOwnerRole().getId())
                || role.getId().equals(getRecruitRole().getId())) {
            return;
        }
        final FactionRole newParentRole = getRole(newParentId);
        if (newParentRole == null) {
            return;
        }

        if (newParentRole.getId().equals(getRecruitRole().getId())) {
            return;
        }

        roles.remove(role);
        final int newIndex = roles.indexOf(newParentRole) + 1;
        roles.add(newIndex, role);

        markDirty();
    }

    /**
     * Remove the roll with the given ID
     * @param role The role to remove
     */
    public void removeRole(final FactionRole role) {
        if (role == null) {
            return;
        }

        if (role.getId().equals(getOwnerRole().getId())) {
            return;
        }

        if (role.getId().equals(getRecruitRole().getId())) {
            return;
        }

        final UUID newRole = getRoles().get(getRoleIndex(role.getId()) - 1).getId();
        for (final FactionPlayer fPlayer : getPlayers()) {
            if (!fPlayer.getRoleId().equals(role.getId())) {
                continue;
            }

            final FactionPlayerChangeRoleEvent event = new FactionPlayerChangeRoleEvent(null, fPlayer, getRole(newRole), FactionPlayerChangeRoleEvent.EChangeRoleReason.REMOVED);
            MinecraftForge.EVENT_BUS.post(event);

            fPlayer.setRole(event.getNewRole().getId());
        }

        roles.remove(role);

        markDirty();
    }

    /* ========================================= */
    /* Invites
    /* ========================================= */

    public Set<UUID> getPlayerInvites() {
        return playerInvites;
    }

    public boolean hasInvitedPlayer(final UUID playerId) {
        return getPlayerInvites().contains(playerId);
    }

    public void addInvite(final UUID playerId) {
        playerInvites.add(playerId);
        markDirty();
    }

    public void removeInvite(final UUID playerId) {
        playerInvites.remove(playerId);
        markDirty();
    }

    /* ========================================= */
    /* Flags
    /* ========================================= */

    public Set<EFactionFlags> getFlags() {
        return flags;
    }

    /**
     * Check if the faction has a flag
     * @param flag The flag to check for
     * @return true if the faction has the flag
     */
    public boolean hasFlag(final EFactionFlags flag) {
        return flags.contains(flag);
    }

    /**
     * Add a flag to the faction
     * @param flag The flag to add
     */
    public void addFlag(final EFactionFlags flag) {
        if (flags.contains(flag)) return;

        flags.add(flag);
        updateAllPlayersCommands();

        markDirty();
    }

    /**
     * Remove a flag from the faction
     * @param flag The flag to remove
     */
    public void removeFlag(final EFactionFlags flag) {
        if (!flags.contains(flag)) return;

        flags.remove(flag);
        updateAllPlayersCommands();

        markDirty();
    }

    /* ========================================= */
    /* Relations
    /* ========================================= */

    public Map<UUID, FactionRelation> getRelations() {
        return relations;
    }

    public FactionRelation getRelation(final UUID otherFaction) {
        return relations.get(otherFaction);
    }

    public FactionRelation getRelation(final Faction otherFaction) {
        if (otherFaction == null) return null;
        return getRelation(otherFaction.getId());
    }

    /**
     * Change a faction relation
     * @param otherFaction The faction to create the relation with
     * @param newRelation The relation to have with the faction
     * @return the faction relation
     */
    public FactionRelation setRelation(@NotNull final Faction otherFaction, final EFactionRelation newRelation) {
        final FactionRelation relation = getRelation(otherFaction);
        if ((relation == null && newRelation == EFactionRelation.NEUTRAL)
                || (relation != null && relation.getRelation() == newRelation)) return relation;

        if (newRelation == EFactionRelation.NEUTRAL) {
            relations.remove(otherFaction.getId());
            return null;
        }

        final FactionRelation factionRelation = new FactionRelation(otherFaction.getId(), newRelation);
        relations.put(otherFaction.getId(), factionRelation);

        markDirty();

        return factionRelation;
    }

    /**
     * Inform a faction about a relation that's just been created
     * @param otherFaction The faction that created the relation
     * @param fromRelation The relation that's been created
     */
    public void informRelation(final Faction otherFaction, final EFactionRelation fromRelation) {
        final FactionRelation toRelation = getRelation(otherFaction);
        final EFactionRelation toRelationType = toRelation != null ? toRelation.getRelation() : EFactionRelation.NEUTRAL;
        final MutableComponent message = Component.empty();
        message
                .append(
                        otherFaction.getNameWithDescription(this)
                                .withStyle(RelationUtil.getRelation(this, otherFaction).formatting)
                )
                .append(DatChatFormatting.TextColour.INFO.toString());
        switch (fromRelation){
            case ALLY -> {
                message.append(EFactionRelation.ALLY.formatting + " has declared you an ally, ");
                switch (toRelationType) {
                    case ALLY:
                        message.append(EFactionRelation.ALLY.formatting + "you can now both speak privately in ally chat");
                        if (!(hasFlag(EFactionFlags.FRIENDLYFIRE) || otherFaction.hasFlag(EFactionFlags.FRIENDLYFIRE))) {
                            message.append(EFactionRelation.ALLY.formatting + " and are prevented from dealing pvp damage with each other");
                        }
                        break;
                    case TRUCE:
                        message.append(EFactionRelation.TRUCE.formatting + "you still have a truce with them and are prevented from dealing pvp damage with each other, ");
                    case NEUTRAL:
                        message.append(EFactionRelation.NEUTRAL.formatting + "you can add them as an ally with ")
                                .append(FactionCommandUtils.wrapCommand("/factions relations ally " + otherFaction.getName()));
                        break;
                    case ENEMY:
                        message.append(EFactionRelation.ENEMY.formatting + "but you still regard them as an enemy");
                        break;
                }
            }
            case TRUCE -> {
                message.append(EFactionRelation.TRUCE.formatting + " has declared a truce with you, ");
                switch (toRelationType) {
                    case ALLY -> message.append(EFactionRelation.ALLY.formatting + "you still regard them as allies");
                    case TRUCE ->
                            message.append(EFactionRelation.TRUCE.formatting + "you are now both at truce and are prevented from dealing pvp damage with each other, ");
                    case NEUTRAL -> message.append(EFactionRelation.NEUTRAL.formatting + "you can also declare a truce with them with ")
                            .append(FactionCommandUtils.wrapCommand("/factions relations truce " + otherFaction.getName()));
                    case ENEMY -> message.append(EFactionRelation.ENEMY.formatting + "but you still regard them as an enemy");
                }
            }
            case ENEMY -> {
                message.append(EFactionRelation.ENEMY.formatting + " has declared you an enemy, ");
                switch (toRelationType) {
                    case ALLY:
                        message.append(EFactionRelation.ALLY.formatting + "you still regard them as allies, but are not protected from pvp with them");
                        break;
                    case TRUCE:
                        message.append(EFactionRelation.TRUCE.formatting + "you are currently at truce with them, but are not protected from pvp with them ");
                    case NEUTRAL:
                        message.append(EFactionRelation.NEUTRAL.formatting + "you can also declare them as enemies with ")
                                .append(FactionCommandUtils.wrapCommand("/factions relations enemy " + otherFaction.getName()));
                        break;
                    case ENEMY:
                        message.append(EFactionRelation.ENEMY.formatting + "you are now hostile factions");
                        break;
                }
            }
            case NEUTRAL -> {
                message.append(EFactionRelation.NEUTRAL.formatting + " have removed their relation with you, ");
                switch (toRelationType) {
                    case ALLY ->
                            message.append(EFactionRelation.ALLY.formatting + "you still regard them as allies, but are not protected from pvp with them");
                    case TRUCE ->
                            message.append(EFactionRelation.TRUCE.formatting + "you are currently at truce with them, but are not protected from pvp with them");
                    case NEUTRAL -> message.append(EFactionRelation.NEUTRAL.formatting + "you already did not have a relation with them");
                    case ENEMY -> message.append(EFactionRelation.ENEMY.formatting + "you still regard them as an enemy");
                }
            }
        }

        sendFactionWideMessage(message);
    }

    /* ========================================= */
    /* Chat Summaries
    /* ========================================= */

    /**
     * Get a summary of the faction for chat
     * @return A chat component with the message
     */
    public Component getChatSummary(@Nullable final Faction from) {
        // Title
        final MutableComponent message = Component.literal(DatChatFormatting.TextColour.HEADER + "____===")
                .append(Component.literal(getName())
                        .withStyle(RelationUtil.getRelation(from, this).formatting)
                        .withStyle(Style.EMPTY
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.SUGGEST_COMMAND,
                                        "/factions info " + getName())
                                ))
                )
                .append(DatChatFormatting.TextColour.HEADER +"===____");

        // Description
        message.append("\n")
                .append(DatChatFormatting.TextColour.INFO + "Description: " + ChatFormatting.WHITE + description);



        // Last Online
        if (!isAnyoneOnline()) {
            final long factionOfflineExpiryTime = FactionsConfig.getFactionOfflineExpiryTime() * 1000L;
            final MutableComponent component = Component.literal(AgeUtil.calculateAgeString(getLastOnline()))
                    .withStyle(
                            factionOfflineExpiryTime > 0 && System.currentTimeMillis() - getLastOnline() > Math.min(0.9 * factionOfflineExpiryTime, 604800)
                                ? Style.EMPTY
                                    .withHoverEvent(new HoverEvent(
                                            HoverEvent.Action.SHOW_TEXT,
                                            Component.literal("This faction hasn't been online for a while and is at risk of being removed")
                                    ))
                                    .withColor(ChatFormatting.DARK_RED)
                                : Style.EMPTY.withColor(ChatFormatting.WHITE)
                    );

            message.append("\n")
                    .append(DatChatFormatting.TextColour.INFO + "Last Online: ").append(component);
        }

        // Age
        if (!hasFlag(EFactionFlags.DEFAULT)) {
            message.append("\n")
                    .append(DatChatFormatting.TextColour.INFO + "Age: " + ChatFormatting.WHITE + AgeUtil.calculateAgeString(creationTime));
        }

        // Power
        final int power = getTotalPower();
        {
            final boolean infinitePower = hasFlag(EFactionFlags.INFINITEPOWER);
            final int maxPower = getTotalMaxPower();
            final String powerStr = infinitePower ? "∞" : String.valueOf(power);
            final String maxPowerStr = infinitePower ? "∞" : String.valueOf(maxPower);
            message.append("\n")
                    .append(DatChatFormatting.TextColour.INFO + "Power/Max: ")
                    .append(
                            Component.literal(powerStr)
                                    .withStyle( power > 0.6666 * maxPower
                                            ? ChatFormatting.DARK_GREEN
                                            : (power > 0.3333 * maxPower
                                                    ? ChatFormatting.GOLD
                                                    : ChatFormatting.DARK_RED))
                    )
                    .append(ChatFormatting.WHITE + "/" + maxPowerStr);
        }
        // Land
        if (!hasFlag(EFactionFlags.UNCHARTED)) {
            final Pair<Integer, Integer> total = new Pair<>(0, 0);
            final Map<FactionLevel, Pair<Integer, Integer>> landCount = FLevelCollection.getInstance().getAll().values().stream()
                    .collect(Collectors.toMap(
                            level -> level,
                            level -> {
                                final int landAmount = level.getClaimsCount(getId());
                                final int landWorth = landAmount * level.getSettings().landWorth;
                                total.setLeftHand(total.getLeftHand() + landWorth);
                                total.setRightHand(total.getRightHand() + landAmount);
                                return new Pair<>(landWorth, landAmount);
                            }
                    ));
            message.append("\n");
            message.append(DatChatFormatting.TextColour.INFO + "Land worth/count: ")
                    .append(
                            Component.literal("%d/%d".formatted(total.getLeftHand(), total.getRightHand()))
                            .withStyle(total.getRightHand() > getMaxLandWorth() ? ChatFormatting.DARK_RED : ChatFormatting.WHITE)
                    );
            for (final FactionLevel level : landCount.keySet()) {
                final Pair<Integer, Integer> value = landCount.get(level);
                if (value.getLeftHand() == 0) continue;
                message.append("\n")
                        .append("    ")
                        .append(
                                level.getNameWithDescription(from)
                                        .withStyle(ChatFormatting.DARK_GREEN)
                        )
                        .append(": " + ChatFormatting.WHITE + "%d/%d".formatted(value.getLeftHand(), value.getRightHand()));
            }
        }

        // Members
        if (!hasFlag(EFactionFlags.ANONYMOUS)) {
            final Set<FactionPlayer> players = getPlayers();
            final List<MutableComponent> playersComponents = players.stream()
                    .limit(20)
                    .map(player -> player.getNameWithDescription(from).withStyle(player.isPlayerOnline() ? DatChatFormatting.PlayerColour.ONLINE : DatChatFormatting.PlayerColour.OFFLINE))
                    .toList();
            final Component playersComponent = ComponentUtils.formatList(playersComponents, ComponentUtils.DEFAULT_SEPARATOR);

            message.append("\n")
                    .append(DatChatFormatting.TextColour.INFO + "Players: ")
                    .append(playersComponent);

            if (players.size() > 20) message.append("...");
        }

        // Relations
        if (!hasFlag(EFactionFlags.UNRELATEABLE) && !relations.isEmpty()) {
            final Collection<FactionRelation> relationList = getRelations().values();
            final List<MutableComponent> relationsComponents = relationList.stream()
                    .sorted(Comparator.comparingInt(relation -> relation.getRelation().ordinal()))
                    .limit(20)
                    .map(relation -> {
                        final Faction otherFaction = relation.getFaction();
                        return otherFaction.getNameWithDescription(this).withStyle(relation.getRelation().formatting);
                    }).toList();
            final Component relationsComponent = ComponentUtils.formatList(relationsComponents, ComponentUtils.DEFAULT_SEPARATOR);

            message.append("\n")
                    .append(DatChatFormatting.TextColour.INFO + "Relations: ")
                    .append(relationsComponent);
            if (relationList.size() > 20) message.append("...");
        }

        // Flags
        if (!flags.isEmpty()) {
            final List<MutableComponent> flagComponents = flags.stream()
                    .map(EFactionFlags::getChatComponent)
                    .toList();
            final Component flagComponent = ComponentUtils.formatList(flagComponents, ComponentUtils.DEFAULT_SEPARATOR);
            message.append("\n")
                    .append(DatChatFormatting.TextColour.INFO + "Flags: ")
                    .append(flagComponent);
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
        component.withStyle(Style.EMPTY
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getShortDescription(from)))
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/factions info " + name))
        );

        return component;
    }

    public MutableComponent getShortDescription(@Nullable final Faction from) {
        final MutableComponent component = Component.literal("");

        // Relation
        final EFactionRelation relation = RelationUtil.getRelation(from, this);
        if (relation != EFactionRelation.SELF) {
            component.append(relation.formatting + relation.name())
                    .append("\n");
        }

        // Power
        final int power = getTotalPower();
        {
            final boolean infinitePower = hasFlag(EFactionFlags.INFINITEPOWER);
            final int maxPower = getTotalMaxPower();
            final String powerStr = infinitePower ? "∞" : String.valueOf(power);
            final String maxPowerStr = infinitePower ? "∞" : String.valueOf(maxPower);
            component.append(DatChatFormatting.TextColour.INFO + "Power/Max: ")
                    .append(
                            Component.literal(powerStr)
                                    .withStyle( power > 0.6666 * maxPower
                                            ? ChatFormatting.DARK_GREEN
                                            : (power > 0.3333 * maxPower
                                                    ? ChatFormatting.GOLD
                                                    : ChatFormatting.DARK_RED))
                    )
                    .append(ChatFormatting.WHITE + "/" + maxPowerStr);
        }

        // Land
        if (!hasFlag(EFactionFlags.UNCHARTED)) {
            int count = 0;
            int worth = 0;
            for (final FactionLevel level : FLevelCollection.getInstance().getAll().values()) {
                final int levelCount = level.getClaimsCount(getId());
                count += levelCount;
                worth += levelCount * level.getSettings().landWorth;
            }

            component.append("\n")
                    .append(DatChatFormatting.TextColour.INFO + "Land/Worth: ")
                    .append(
                            Component.literal("%s/%s".formatted(count, worth))
                                    .withStyle(worth > power ? ChatFormatting.DARK_RED : ChatFormatting.WHITE)
                    );
        }

        // Age
        if (!hasFlag(EFactionFlags.DEFAULT)) {
            component.append("\n")
                    .append(DatChatFormatting.TextColour.INFO + "Age: " + ChatFormatting.WHITE + AgeUtil.calculateAgeString(creationTime));
        }

        // Last Online
        if (!isAnyoneOnline()) {
            component.append("\n")
                    .append(DatChatFormatting.TextColour.INFO + "Last Online: " + ChatFormatting.WHITE + AgeUtil.calculateAgeString(getLastOnline()));
        }

        return component;
    }

    public long getLastOnline() {
        final Set<FactionPlayer> players = getPlayers();
        if (players.isEmpty() || isAnyoneOnline()) return System.currentTimeMillis();

        return players.stream()
                .max(Comparator.comparing(FactionPlayer::getLastActiveTime))
                .get()
                .getLastActiveTime();
    }

    /* ========================================= */
    /* Misc
    /* ========================================= */

    /**
     * Check the faction's home is still valid
     */
    public void validateHome() {
        if (homeLevel == null || homeLocation == null) return;
        final FactionLevel homeLevel = FLevelCollection.getInstance().getByKey(getHomeLevel());

        if (homeLevel.getChunkOwner(new ChunkPos(homeLocation)).equals(getId())) return;

        setFactionHome(null, null);
        sendFactionWideMessage(Component.literal(DatChatFormatting.TextColour.ERROR + "You no longer have a faction home"));
        markDirty();
    }

    /**
     * Send a message to every member in the faction
     * @param message The message to send
     */
    public void sendFactionWideMessage(final Component message) {
        sendFactionWideMessage(message, Collections.emptyList());
    }

    /**
     * Send a message to every member in the faction except the one's specified
     * @param message The message to send
     * @param exclusions The faction members to exclude sending the message to
     */
    public void sendFactionWideMessage(final Component message, final List<UUID> exclusions) {
        getPlayers().stream()
                .filter(player -> !exclusions.contains(player.getId()))
                .forEach(player -> {
                    final ServerPlayer serverPlayer = player.getServerPlayer();
                    if (serverPlayer != null) serverPlayer.sendSystemMessage(message);
                }
        );
    }

    /**
     * Get if any faction members are online
     * @return true if any faction members are online
     */
    public boolean isAnyoneOnline() {
        for (final FactionPlayer player : getPlayers()) {
            if (player.isPlayerOnline()) return true;
        }

        return false;
    }

    /**
     * Update the available commands for every player
     * <br>
     * Required when a role is updated or a flag is changed
     */
    public void updateAllPlayersCommands() {
        getPlayers().forEach(FactionPlayer::updateCommands);
    }

    /* ========================================= */
    /* Database Stuff
    /* ========================================= */

    @Override
    public void markClean() {
        super.markClean();

        // We need to make the roles as clean as well
        roles.forEach(DatabaseEntity::markClean);
    }

    @Override
    public boolean isDirty() {
        // Account for dirty roles
        return super.isDirty() || roles.stream()
                .map(DatabaseEntity::isDirty)
                .reduce(Boolean.FALSE, Boolean::logicalOr);
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof final Faction faction) && this.getId().equals(faction.getId());
    }
}
