package com.datdeveloper.datfactions.factionData.permissions;

import com.datdeveloper.datfactions.database.DatabaseEntity;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;

import java.util.*;

/**
 * A role inside the faction, representing the permissions a player has in the faction
 */
public class FactionRole extends DatabaseEntity {
    /**
     * The role's ID
     */
    final UUID id;

    /**
     * The name of the role
     */
    String name;

    /**
     * If the role has total permissions on the faction
     */
    boolean administrator;

    /**
     * The permissions the role has
     */
    Set<ERolePermissions> permissions;

    public FactionRole(final String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.administrator = false;
        permissions = new HashSet<>();
    }

    /**
     * Copy Constructor
     * @param role The role to copy
     */
    public FactionRole(final FactionRole role) {
        this.id = UUID.randomUUID();
        this.name = role.name;
        this.administrator = role.administrator;
        // Enums are immutable, so we can reuse the enum, just not the set
        permissions = new HashSet<>(role.permissions);
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

        if (!permissions.isEmpty()) {
            final List<MutableComponent> permissions = getPermissions().stream()
                    .sorted()
                    .map(ERolePermissions::getChatComponent
                    )
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
     * @return a list of the default roles
     */
    public static List<FactionRole> getDefaultRoles() {
        final List<FactionRole> roles = new ArrayList<>();
        roles.add(defaultOwnerRole());
        roles.add(defaultOfficerRole());
        roles.add(defaultMemberRole());
        roles.add(defaultRecruitRole());

        return roles;
    }

    /**
     * Get the default owner role
     * This should only be used to generate the template, to get the server's default owner role you should look there
     * @see FactionCollection#getTemplate()
     * @see Faction#getOwnerRole()
     * @return the default Owner role
     */
    public static FactionRole defaultOwnerRole() {
        final FactionRole owner = new FactionRole("Owner");

        owner.administrator = true;

        return owner;
    }

    /**
     * Get the default officer role
     * This should only be used to generate the template and is not guaranteed to be one of the default roles
     * @see FactionCollection#getTemplate()
     * @return the default Officer role
     */
    public static FactionRole defaultOfficerRole() {
        final FactionRole officer = new FactionRole("Officer");

        officer.permissions = new HashSet<>(Arrays.asList(
                // Member Management
                ERolePermissions.INVITE,
                ERolePermissions.UNINVITE,
                ERolePermissions.KICK,
                ERolePermissions.SETROLE,
                ERolePermissions.PROMOTE,
                ERolePermissions.DEMOTE,

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

                // Misc
                ERolePermissions.HOME,
                ERolePermissions.SETHOME
        ));

        return officer;
    }

    /**
     * Get the default member role
     * This should only be used to generate the template and is not guaranteed to be one of the default roles
     * @see FactionCollection#getTemplate()
     * @return the default Member role
     */
    public static FactionRole defaultMemberRole() {
        final FactionRole member = new FactionRole("Member");

        member.permissions = new HashSet<>(Arrays.asList(
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

                // Chat
                ERolePermissions.FACTIONCHAT,

                // Misc
                ERolePermissions.HOME
        ));

        return member;
    }
    /**
     * Get the default recruit role
     * This should only be used to generate the template, to get the server's default recruit role you should look there
     * @see FactionCollection#getTemplate()
     * @see Faction#getRecruitRole()
     * @return the default Recruit role
     */
    public static FactionRole defaultRecruitRole() {
        final FactionRole recruit = new FactionRole("Recruit");

        recruit.permissions = new HashSet<>(Arrays.asList(
                ERolePermissions.FACTIONCHAT,
                ERolePermissions.HOME
        ));

        return recruit;
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
        return (obj instanceof FactionRole role) && role.id.equals(this.id);
    }
}
