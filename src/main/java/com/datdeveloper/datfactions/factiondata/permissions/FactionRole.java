package com.datdeveloper.datfactions.factiondata.permissions;

import com.datdeveloper.datfactions.database.DatabaseEntity;
import com.datdeveloper.datfactions.factiondata.FactionCollection;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.google.common.collect.Sets;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A role inside the faction, representing the permissions a player has in the faction
 * <br>
 * Roles are a hierarchical tree. Where Roles can have multiple children, and the higher up the tree the more authority
 */
public class FactionRole extends DatabaseEntity {
    /** The role's ID */
    final UUID id;

    /** The parent of this role */
    @Nullable
    FactionRole parent;

    /** The name of the role */
    String name;

    /** If the role has total permissions on the faction */
    boolean administrator;

    /** The child roles of this role */
    final transient List<FactionRole> children;

    /** The permissions the role has */
    Set<ERolePermissions> permissions;

    public FactionRole(final String name, final @Nullable FactionRole parent) {
        this.id = UUID.randomUUID();

        if (parent != null) {
            this.parent = parent;
            parent.addChild(this);
        } else {
            this.parent = null;
        }

        this.name = name;
        this.administrator = false;
        children = new ArrayList<>();
        permissions = new HashSet<>();
    }

    /**
     * Copy Constructor
     * @param role      The role to copy
     * @param newParent The new parent this copied role is now a child of
     */
    public FactionRole(final FactionRole role, final @Nullable FactionRole newParent) {
        this.id = UUID.randomUUID();
        this.parent = newParent;

        if (parent != null) parent.addChild(this);

        this.name = role.name;
        this.administrator = role.administrator;

        // Children are added by the child when they're constructed
        this.children = new ArrayList<>();

        // Enums are immutable, so we can reuse the enum, just not the set
        permissions = new HashSet<>(role.permissions);
    }

    /**
     * Make a deep copy of this role, duplicating all children recursively
     * @param newParent The new parent of this role
     * @param oldToNewMap A map that stores the old keys against the copied role for mapping old keys to their duplicates
     * @return A deep copy of the role
     */
    public FactionRole deepCopy(final @Nullable FactionRole newParent, final @NotNull Map<UUID, FactionRole> oldToNewMap) {
        final FactionRole newRole = new FactionRole(this, newParent);
        oldToNewMap.put(id, newRole);

        for (final FactionRole child : children) {
            newRole.addChild(child.deepCopy(newRole, oldToNewMap));
        }

        return newRole;
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

    public Set<ERolePermissions> getPermissions() {
        return permissions;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    /* ========================================= */
    /* Setters
    /* ========================================= */

    public void setName(final String name) {
        this.name = name;
        markDirty();
    }

    public void setAdministrator(final boolean administrator) {
        this.administrator = administrator;
        markDirty();
    }

    /* ========================================= */
    /* Hierarchy
    /* ========================================= */

    /**
     * Check if this node is the root of the hierarchy
     * @return True if this node is the route of the hierarchy
     */
    public boolean isRoot() {
        return getParent() == null;
    }

    public @Nullable FactionRole getParent() {
        return parent;
    }

    public void setParent(final @Nullable FactionRole parent) {
        this.parent = parent;
        if (parent != null) parent.addChild(this);
    }

    public List<FactionRole> getChildren() {
        return children;
    }

    /**
     * Get the child object at the given index
     * @param index The index of the child in the children array
     * @return The child object
     */
    public FactionRole getChild(final int index) {
        return children.get(index);
    }

    /**
     * Add a new child to the role
     * <br>
     * This child must have this role set as its parent
     * @param role The child to add
     */
    public void addChild(final FactionRole role) {
        children.add(role);
    }

    /**
     * Remove a child from the role
     * @param child The child to remove
     */
    public void removeChild(final FactionRole child) {
        children.remove(child);
    }

    /* ========================================= */
    /* Permission Management
    /* ========================================= */

    /**
     * Check the role has a permission
     * @param permission The permission to test for
     * @return True if the role has the given permission
     */
    public boolean hasPermission(final ERolePermissions permission) {
        return administrator || permissions.contains(permission);
    }

    /**
     * Check the role has any of the given permissions
     * @param permissionTests The permissions to test for
     * @return True if the role has any of the given permissions
     */
    public boolean hasAnyPermissions(final ERolePermissions... permissionTests) {
        return administrator || Arrays.stream(permissionTests).anyMatch(permissions::contains);
    }

    /**
     * Check the role has all the given permissions
     * @param permissionTests The permissions to test for
     * @return True if the role has all the given permissions
     */
    public boolean hasAllPermissions(final ERolePermissions... permissionTests) {
        return administrator || permissions.containsAll(List.of(permissionTests));
    }

    /**
     * Add a permission to the role
     * Ignores if the role already has the permission
     * @param permission The permission to add
     */
    public void addPermission(final ERolePermissions permission) {
        this.permissions.add(permission);
        markDirty();
    }

    /**
     * Remove a permission to the role
     * Ignores if the role does not have the permission
     * @param permission The permission to remove
     */
    public void removePermission(final ERolePermissions permission) {
        this.permissions.remove(permission);
        markDirty();
    }

    /* ========================================= */
    /* Chat Summaries
    /* ========================================= */

    /**
     * Get a summary of the role for chat
     * @return a summary of the role for chat
     */
    public MutableComponent getChatSummary() {
        final MutableComponent message = Component.literal(DatChatFormatting.TextColour.HEADER + "____===")
                .append(Component.literal(getName())
                        .withStyle(Style.EMPTY
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.SUGGEST_COMMAND,
                                        "/f role info " + getName()
                                ))
                        )
                )
                .append(DatChatFormatting.TextColour.HEADER +"===____");

        if (isAdministrator()) {
            message.append("\n")
                    .append(
                            Component.literal("Admin")
                                    .withStyle(ChatFormatting.DARK_PURPLE)
                    );
        }

        if (parent != null) {
            message.append("\n")
                    .append(
                            Component.literal("Superior: ")
                                    .withStyle(DatChatFormatting.TextColour.INFO)
                    )
                    .append(ChatFormatting.WHITE.toString())
                    .append(parent.getNameWithDescription());
        }

        if (!children.isEmpty()) {
            final List<MutableComponent> subordinates = children.stream()
                    .map(FactionRole::getNameWithDescription)
                    .toList();

            message.append("\n")
                    .append(
                            Component.literal("Subordinates(s): ")
                                    .withStyle(DatChatFormatting.TextColour.INFO)
                    )
                    .append(ChatFormatting.WHITE.toString())
                    .append(ComponentUtils.formatList(subordinates, ComponentUtils.DEFAULT_SEPARATOR));
        }

        if (!permissions.isEmpty()) {
            final List<MutableComponent> permissions = getPermissions().stream()
                    .sorted()
                    .map(ERolePermissions::getChatComponent)
                    .toList();
            message.append("\n")
                    .append(
                            Component.literal("Permissions: ")
                                    .withStyle(DatChatFormatting.TextColour.INFO)
                    )
                    .append(ChatFormatting.WHITE.toString())
                    .append(ComponentUtils.formatList(permissions, ComponentUtils.DEFAULT_SEPARATOR));
        }

        return message;
    }

    /**
     * Get a chat component containing the role name and with a hover event showing a short description of the role
     * @return A chat component representing the role
     */
    public MutableComponent getNameWithDescription() {
        return Component.literal(getName())
                .withStyle(
                        Style.EMPTY
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.SUGGEST_COMMAND,
                                        "/f role info " + getName()
                                ))
                                .withHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        getShortDescription()
                                ))
                );
    }

    /**
     * Get a short version of the role description
     * @return A short description of the role
     */
    public MutableComponent getShortDescription() {
        final MutableComponent component = Component.empty();

        if (isAdministrator()) {
            component.append(
                    Component.literal("Faction Administrator")
                            .withStyle(ChatFormatting.DARK_PURPLE)
            );
        } else {
            final List<MutableComponent> permissions = getPermissions().stream()
                    .sorted()
                    .map(permission ->
                            Component.literal(permission.name().toLowerCase())
                    )
                    .toList();
            component.append(DatChatFormatting.TextColour.INFO + "Permissions: ").append("\n")
                    .append(ComponentUtils.formatList(permissions, Component.literal("\n")));
        }

        return component;
    }

    /* ========================================= */
    /* Default Roles
    /* ========================================= */

    /**
     * Get a list of the default roles
     * This should only be used to generate the template, to get the server's default roles you should look there
     * @see FactionCollection#getTemplate()
     * @return a list of the default roles, where the last element is the recruit
     */
    public static List<FactionRole> getDefaultRoles() {
        final FactionRole owner;
        {
            owner = new FactionRole("Owner", null);
            owner.administrator = true;
        }

        final FactionRole officer;
        {
            officer = new FactionRole("Officer", owner);
            officer.permissions = defaultOfficerPermissions();
        }

        final FactionRole member;
        {
            member = new FactionRole("Member", officer);
            member.permissions = defaultMemberRolePermissions();
        }

        final FactionRole recruit;
        {
            recruit = new FactionRole("Member", officer);
            recruit.permissions = defaultRecruitRolePermissions();
        }

        return List.of(owner, officer, member, recruit);
    }

    /**
     * Get the permissions of the default officer role
     * This should only be used to generate the template
     * @see FactionCollection#getTemplate()
     * @return the default Officer role's permissions
     */
    public static Set<ERolePermissions> defaultOfficerPermissions() {
        return Sets.newHashSet(
                // Player
                ERolePermissions.KICK,
                ERolePermissions.SETROLE,
                ERolePermissions.PROMOTE,
                ERolePermissions.DEMOTE,

                // Invites
                ERolePermissions.INVITELIST,
                ERolePermissions.INVITE,
                ERolePermissions.UNINVITE,

                // Roles
                ERolePermissions.ROLELIST,
                ERolePermissions.ROLEINFO,

                // Land
                ERolePermissions.CLAIMONE,
                ERolePermissions.UNCLAIMONE,
                ERolePermissions.AUTOCLAIM,

                // Land Access
                ERolePermissions.CONTAINERS,
                ERolePermissions.BUILD,
                ERolePermissions.INTERACT,

                // Faction Management
                ERolePermissions.SETMOTD,

                // Relations
                ERolePermissions.RELATIONLIST,
                ERolePermissions.RELATIONWISHES,
                ERolePermissions.RELATIONALLY,
                ERolePermissions.RELATIONTRUCE,
                ERolePermissions.RELATIONNEUTRAL,
                ERolePermissions.RELATIONENEMY,

                // Chat
                ERolePermissions.FACTIONCHAT,
                ERolePermissions.ALLYCHAT,

                // Flags
                ERolePermissions.FLAGLIST,
                ERolePermissions.FLAGADD,
                ERolePermissions.FLAGREMOVE,

                // Misc
                ERolePermissions.HOME,
                ERolePermissions.SETHOME
        );
    }

    /**
     * Get the permissions of the default member role
     * This should only be used to generate the template
     * @see FactionCollection#getTemplate()
     * @return the default Member role's permissions
     */
    public static Set<ERolePermissions> defaultMemberRolePermissions() {
        return Sets.newHashSet(
                // Land
                ERolePermissions.CLAIMONE,
                ERolePermissions.UNCLAIMONE,

                // Roles
                ERolePermissions.ROLELIST,
                ERolePermissions.ROLEINFO,

                // Land Access
                ERolePermissions.CONTAINERS,
                ERolePermissions.BUILD,
                ERolePermissions.INTERACT,

                // Relations
                ERolePermissions.RELATIONLIST,
                ERolePermissions.RELATIONWISHES,

                // Chat
                ERolePermissions.FACTIONCHAT,

                // Misc
                ERolePermissions.HOME
        );
    }
    /**
     * Get the default recruit role's permissions
     * This should only be used to generate the template
     * @see FactionCollection#getTemplate()
     * @return the default Recruit role's permissions
     */
    public static Set<ERolePermissions> defaultRecruitRolePermissions() {
        return Sets.newHashSet(
                ERolePermissions.FACTIONCHAT,
                ERolePermissions.HOME
        );
    }

    /* ========================================= */
    /* Database Stuff
    /* ========================================= */

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof final FactionRole role) && role.id.equals(this.id);
    }
}
