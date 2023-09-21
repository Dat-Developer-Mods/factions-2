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
 */
public class FactionChangeFlagsEvent extends FactionEvent {
    /**
     * The flags being added to/removed from the faction
     */
    Set<EFactionFlags> flags;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param flags The flags being added/removed from the faction
     */
    public FactionChangeFlagsEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final Set<EFactionFlags> flags) {
        super(instigator, faction);
        this.flags = flags;
    }

    /**
     * Get the flags that the faction is changing to
     * Modifications to this will be reflected
     * @return the new Flags
     */
    public Set<EFactionFlags> getFlags() {
        return flags;
    }

    /**
     * Set the new flags the faction will change to
     * @param flags the new flags
     */
    public void setFlags(final Set<EFactionFlags> flags) {
        this.flags = flags;
    }

    /**
     * Fired before a faction adds flags
     */
    @Cancelable
    @SkipChecks
    public static class PreAdd extends FactionChangeFlagsEvent {
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
     * Fired before a faction removes flags
     */
    @Cancelable
    @SkipChecks
    public static class Remove extends FactionChangeFlagsEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param flags   The flags being removed from the faction
         */
        public Remove(@Nullable final CommandSource instigator, @NotNull final Faction faction, final Set<EFactionFlags> flags) {
            super(instigator, faction, flags);
        }
    }

    private static class Post extends FactionChangeFlagsEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param flags      The flags that were added to/removed from the faction
         */
        public Post(@Nullable final CommandSource instigator, @NotNull final Faction faction, final Set<EFactionFlags> flags) {
            super(instigator, faction, Collections.unmodifiableSet(flags));
        }

        @Override
        public void setFlags(final Set<EFactionFlags> flags) {
            throw new UnsupportedOperationException("You cannot change the flags in the post event");
        }
    }

    /**
     * Fired after a faction adds flags
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
