package com.datdeveloper.datfactions.factionData;

import com.datdeveloper.datfactions.Util.RelationUtil;
import com.datdeveloper.datfactions.database.DatabaseEntity;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;

public class Faction extends DatabaseEntity {
    /**
     * The faction's ID
     */
    UUID id;

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
    List<UUID> playerInvites;

    /**
     * The roles the faction has, in order from owner to recruit (owner at position 0, recruit in last place)
     */
    List<FactionRole> roles;

    /**
     * A set of flags the faction has
     */
    Set<EFactionFlags> flags;
    /**
     * The relations the faction has
     */
    Map<UUID, FactionRelation> relations;

    public Faction(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.description = "";
        this.motd = null;

        this.factionPower = 0;

        this.creationTime = System.currentTimeMillis();

        this.homeLocation = null;
        this.homeLevel = null;

        this.playerInvites = new ArrayList<>();
        this.roles = FactionRole.getDefaultRoles();
        this.flags = new HashSet<>();
        this.relations = new HashMap<>();
    }

    public Faction(UUID id, String name, Faction template) {
        this.id = id;
        this.name = name;
        this.description = "";
        this.motd = "";

        this.factionPower = template.factionPower;

        this.creationTime = System.currentTimeMillis();

        this.homeLocation = template.homeLocation;
        this.homeLevel = template.homeLevel;

        this.playerInvites = new ArrayList<>();

        // Deep Copy roles
        this.roles = new ArrayList<>();
        for (FactionRole role : template.roles) {
            this.roles.add(new FactionRole(role));
        }

        this.flags = new HashSet<>(template.flags);

        // Don't copy relations
        this.relations = new HashMap<>();
    }

    public Set<FactionPlayer> getPlayers() {
        return FactionIndex.getInstance().getFactionPlayers(this.getId());
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

    /* ========================================= */
    /* Setters
    /* ========================================= */

    public void setName(String newName) {
        if (newName.equals(name) || newName.isEmpty()) return;

        this.name = newName;
        markDirty();
    }

    public void setDescription(String newDescription) {
        if (newDescription.equals(description)) return;
        
        this.description = newDescription;
        markDirty();
    }

    public void setMotd(String newMotd) {
        if (newMotd.equals(motd)) return;

        this.description = newMotd;
        markDirty();
    }

    public void setFactionHome(ResourceKey<Level> newHomeLevel, BlockPos newHomeLocation) {
        this.homeLocation = newHomeLocation;
        this.homeLevel = newHomeLevel;
        markDirty();
    }

    /* ========================================= */
    /* Power
    /* ========================================= */

    public int getTotalPower() {
        return factionPower + getPlayers().stream().mapToInt(FactionPlayer::getPower).sum();
    }

    public int getTotalMaxPower() {
        return factionPower + getPlayers().stream().mapToInt(FactionPlayer::getMaxPower).sum();
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
    private boolean checkOwnsChunk(ResourceKey<Level> level, ChunkPos chunkPos) {
        return this.getId().equals(FLevelCollection.getInstance().getByKey(level).getChunkOwner(chunkPos));
    }

    /**
     * Get the worth of the chunks the factions owns in the specified level
     * @param level The level to check
     * @return the total worth of the chunks the faction owns in the specified level
     */
    public int getLandWorthInLevel(FactionLevel level) {
        return level.getClaimsWorth(this.getId());
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
    public FactionRole getRole(UUID roleId) {
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
    public FactionRole getRoleByName(String roleName) {
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
    public int getRoleIndex(UUID roleId) {
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
    public int getRoleIndexByName(String roleName) {
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
    public FactionRole createNewRole(String roleName, String roleParent) {
        if (getRoleByName(roleName) != null) {
            return null;
        }

        int parentIndex = getRoleIndexByName(roleParent);
        if (parentIndex == -1) {
            return null;
        }

        FactionRole newRole = new FactionRole(roleName);
        this.roles.add(parentIndex, newRole);

        markDirty();
        return newRole;
    }

    /**
     * Change the parent of a role
     * @param roleId The id of the role to change
     * @param newParentId The new parent of the role
     */
    public void setRoleParent(UUID roleId, UUID newParentId) {
        FactionRole role = getRole(roleId);
        if (role == null || roleId.equals(newParentId)) {
            return;
        }

        if (role.getId().equals(getOwnerRole().getId())
                || role.getId().equals(getRecruitRole().getId())) {
            return;
        }
        FactionRole newParentRole = getRole(newParentId);
        if (newParentRole == null) {
            return;
        }

        if (newParentRole.getId().equals(getRecruitRole().getId())) {
            return;
        }

        roles.remove(role);
        int newIndex = roles.indexOf(newParentRole);
        roles.add(newIndex, role);

        markDirty();
    }

    /**
     * Remove the roll with the given ID
     * @param roleId the ID of the role to remove
     */
    public void removeRole(UUID roleId) {
        FactionRole role = getRole(roleId);
        if (role == null) {
            return;
        }

        if (role.getId().equals(getOwnerRole().getId())) {
            return;
        }

        if (role.getId().equals(getRecruitRole().getId())) {
            return;
        }

        roles.remove(role);
        markDirty();
    }

    /* ========================================= */
    /* Flags
    /* ========================================= */

    public Set<EFactionFlags> getFlags() {
        return flags;
    }

    public boolean hasFlag(EFactionFlags flag) {
        return flags.contains(flag);
    }

    /**
     * Add a flag to the faction
     * @param flag The flag to add
     */
    public void addFlag(EFactionFlags flag) {
        if (flags.contains(flag)) return;

        flags.add(flag);
        markDirty();
    }

    /**
     * Remove a flag from the faction
     * @param flag The flag to remove
     */
    public void removeFlag(EFactionFlags flag) {
        if (!flags.contains(flag)) return;

        flags.remove(flag);

        markDirty();
    }

    /* ========================================= */
    /* Relations
    /* ========================================= */

    public Map<UUID, FactionRelation> getRelations() {
        return relations;
    }

    public FactionRelation getRelation(UUID otherFaction) {
        return relations.get(otherFaction);
    }

    public FactionRelation getRelation(Faction otherFaction) {
        return getRelation(otherFaction.id);
    }

    /**
     * Change a faction relation
     * @param otherFaction The faction to create the relation with
     * @param newRelation The relation to have with the faction
     * @return the faction relation
     */
    public FactionRelation setRelation(@NotNull Faction otherFaction, EFactionRelation newRelation) {
        FactionRelation relation = getRelation(otherFaction);
        if ((relation == null && newRelation == EFactionRelation.NEUTRAL)
                || (relation != null && relation.relation == newRelation)) return relation;

        if (newRelation == EFactionRelation.NEUTRAL) {
            relations.remove(otherFaction.getId());
            return null;
        }

        FactionRelation factionRelation = new FactionRelation(newRelation);
        relations.put(otherFaction.getId(), factionRelation);

        return factionRelation;
    }

    /* ========================================= */
    /* Misc
    /* ========================================= */

    /**
     * Inform a faction about a relation that's just been created
     * @param otherFaction The faction that created the relation
     * @param fromRelation The relation that's been created
     */
    public void informRelation(Faction otherFaction, EFactionRelation fromRelation) {
        FactionRelation toRelation = getRelation(otherFaction);
        EFactionRelation toRelationType = toRelation != null ? toRelation.relation : EFactionRelation.NEUTRAL;
        MutableComponent message = MutableComponent.create(RelationUtil.wrapFactionName(this, otherFaction).getContents());
        message.append(DatChatFormatting.TextColour.INFO.toString());
        switch (fromRelation){
            case ALLY -> {
                message.append(" has declared you an ally, ");
                switch (toRelationType) {
                    case ALLY:
                        message.append("you can now both speak privately in ally chat and are prevented from dealing pvp damage with each other");
                        break;
                    case TRUCE:
                        message.append("you still have a truce with them and are prevented from dealing pvp damage with each other, ");
                    case NEUTRAL:
                        message.append("you can add them as an ally with")
                                .append(DatChatFormatting.TextColour.COMMAND.toString())
                                .append("/faction ally ")
                                .append(RelationUtil.wrapFactionName(this, otherFaction));
                        break;
                    case ENEMY:
                        message.append("but you still regard them as an enemy");
                        break;
                }
            }
            case TRUCE -> {
                message.append(" has declared a truce with you, ");
                switch (toRelationType) {
                    case ALLY:
                        message.append("you still regard them as allies");
                        break;
                    case TRUCE:
                        message.append("you are now both at truce and are prevented from dealing pvp damage with each other, ");
                        break;
                    case NEUTRAL:
                        message.append("you can also declare a truce with them with ")
                                .append(DatChatFormatting.TextColour.COMMAND.toString())
                                .append("/faction truce ")
                                .append(RelationUtil.wrapFactionName(this, otherFaction));
                        break;
                    case ENEMY:
                        message.append("but you still regard them as an enemy");
                        break;
                }
            }
            case ENEMY -> {
                message.append(" has declared you an enemy");
                switch (toRelationType) {
                    case ALLY:
                        message.append("you still regard them as allies, but are not protected from pvp with them");
                        break;
                    case TRUCE:
                        message.append("you are currently at truce with them, but are not protected from pvp with them ");
                    case NEUTRAL:
                        message.append("you can also declare them as enemies with them with ")
                                .append(DatChatFormatting.TextColour.COMMAND.toString())
                                .append("/faction enemy ")
                                .append(RelationUtil.wrapFactionName(this, otherFaction));
                        break;
                    case ENEMY:
                        message.append("you are now hostile factions");
                        break;
                }
            }
            case NEUTRAL -> {
                message.append(" have removed their relation with you, ");
                switch (toRelationType) {
                    case ALLY ->
                            message.append("you still regard them as allies, but are not protected from pvp with them");
                    case TRUCE ->
                            message.append("you are currently at truce with them, but are not protected from pvp with them");
                    case NEUTRAL -> message.append("you already did not have a relation with them");
                    case ENEMY -> message.append("you still regard them as an enemy");
                }
            }
        }

        sendFactionWideMessage(message);
    }

    /**
     * Send a message to every member in the faction
     * @param message The message to send
     */
    public void sendFactionWideMessage(Component message) {
        getPlayers().forEach(player -> {
            ServerPlayer serverPlayer = player.getServerPlayer();
            if (serverPlayer != null) serverPlayer.sendSystemMessage(message);
        });
    }

    /**
     * Get a summary of the faction for chat
     * @return A chat component with the message
     */
    public Component getChatSummary() {
        StringBuilder message = new StringBuilder();

        return Component.literal("");
    }

    /* ========================================= */
    /* Database Stuff
    /* ========================================= */

    @Override
    public void markClean() {
        super.markClean();
        roles.forEach(DatabaseEntity::markClean);
    }

    @Override
    public boolean isDirty() {
        // Account for dirty roles
        return super.isDirty() || roles.stream()
                .map(DatabaseEntity::isDirty)
                .reduce(Boolean.FALSE, Boolean::logicalOr);
    }
}
