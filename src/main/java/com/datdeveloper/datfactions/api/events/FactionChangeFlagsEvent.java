package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.EFactionFlags;
import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

/**
 * Events for when a faction changes its flags
 * @see FactionChangeFlagsEvent.PreAdd
 * @see FactionChangeFlagsEvent.PreRemove
 * @see FactionChangeFlagsEvent.PostAdd
 * @see FactionChangeFlagsEvent.PostRemove
 */
public abstract class FactionChangeFlagsEvent extends FactionEvent {
    /** The flags being added to/removed from the faction */
    Set<EFactionFlags> flags;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param flags The flags being added/removed from the faction
     */
    protected FactionChangeFlagsEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final Set<EFactionFlags> flags) {
        super(instigator, faction);
        this.flags = flags;
    }

    /**
     * Get the flags that are being added/removed
     * Modifications to this set will be reflected
     * @return the new Flags
     */
    public Set<EFactionFlags> getFlags() {
        return flags;
    }

    /**
     * Superclass for pre flag events
     * Supplies a setter for the flags
     */
    public static class Pre extends FactionChangeFlagsEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param flags      The flags that were added to/removed from the faction
         */
        public Pre(@Nullable final CommandSource instigator, @NotNull final Faction faction, final Set<EFactionFlags> flags) {
            super(instigator, faction, flags);
        }

        /**
         * Set the flags the faction will add/remove
         * @param flags the new flags
         */
        public void setFlags(final Set<EFactionFlags> flags) {
            this.flags = flags;
        }
    }

    /**
     * Fired before a faction adds some flags
     * <br>
     * The purpose of this event is to allow for modifying/checking the flags that players are adding.
     * This could be used to ensure a flag is always added at the same time as another one, or to deny a specific flag
     * for specific factions.
     * <br>
     * This event {@linkplain HasResult has a result}.<br>
     * To change the result of this event, use {@link #setResult}. Results are interpreted in the following manner:
     * <ul>
     * <li>Allow - The check will succeed, and the flags will be added to the faction</li>
     * <li>Default - The additional flags will be accepted if they are whitelisted and not admin flags</li>
     * <li>Deny - The check will fail, and the flags will not be added.</li>
     * </ul>
     */
    @HasResult
    public static class PreAdd extends Pre {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param flags   The flags being added to the faction
         */
        public PreAdd(@Nullable final CommandSource instigator, @NotNull final Faction faction, final Set<EFactionFlags> flags) {
            super(instigator, faction, flags);
        }
    }

    /**
     * Fired before a faction removes some flags
     * <br>
     * The purpose of this event is to allow for modifying/checking the flags that a faction is removing.
     * This could be used to ensure a flag is always removed at the same time as another one, ensure specific factions
     * cannot remove a flag
     * <br>
     * This event {@linkplain HasResult has a result}.<br>
     * To change the result of this event, use {@link #setResult}. Results are interpreted in the following manner:
     * <ul>
     * <li>Allow - The check will succeed, and the flags will be removed from the faction</li>
     * <li>Default - The flags will be removed if they are not admin flags</li>
     * <li>Deny - The check will fail, and the flags will not be removed.</li>
     * </ul>
     */
    @Cancelable
    public static class PreRemove extends FactionChangeFlagsEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param flags      The flags being removed from the faction
         */
        public PreRemove(@Nullable final CommandSource instigator, @NotNull final Faction faction, final Set<EFactionFlags> flags) {
            super(instigator, faction, flags);
        }
    }

    /**
     * Superclass for post flag events
     * Sets the flags set to unmodifiable
     */
    private static class Post extends FactionChangeFlagsEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param flags      The flags that were added to/removed from the faction
         */
        public Post(@Nullable final CommandSource instigator, @NotNull final Faction faction, final Set<EFactionFlags> flags) {
            super(instigator, faction, Collections.unmodifiableSet(flags));
        }
    }

    /**
     * Fired after a faction adds flags
     * <br>
     * The intention of this event is to allow observing flag additions to update other resources
     */
    public static class PostAdd extends Post {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param flags      The flags that were added to the faction
         */
        public PostAdd(@Nullable final CommandSource instigator, @NotNull final Faction faction, final Set<EFactionFlags> flags) {
            super(instigator, faction, flags);
        }
    }

    /**
     * Fired after a faction removes flags
     * <br>
     * The intention of this event is to allow observing flag removals to update other resources
     */
    public static class PostRemove extends Post {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param flags      The flags that were removed from the faction
         */
        public PostRemove(@Nullable final CommandSource instigator, @NotNull final Faction faction, final Set<EFactionFlags> flags) {
            super(instigator, faction, flags);
        }
    }
}
