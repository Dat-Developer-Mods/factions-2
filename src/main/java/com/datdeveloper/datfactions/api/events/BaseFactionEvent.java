package com.datdeveloper.datfactions.api.events;

import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.LockHelper;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.IdentityHashMap;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public abstract class BaseFactionEvent extends Event {
    /**
     * The player that caused the event to be fired, can be null
     */
    @Nullable
    final CommandSource instigator;

    boolean skipDefaultChecks = false;

    BaseFactionEvent(@Nullable final CommandSource instigator) {
        this.instigator = instigator;
    }

    public @Nullable CommandSource getInstigator() {
        return instigator;
    }

    /**
     * Determine if this event is set to skip the default checks
     * @return The current skipDefaultChecks state
     */
    public boolean isSkipDefaultChecks() {
        return skipDefaultChecks;
    }

    /**
     * Determine if this function can skip the default checks at all.
     * @return If access to setSkipDefaultChecks should be allowed
     * <p>
     * Note:
     * Events with the SkipChecks annotation will have this method automatically added to return true.
     */
    public boolean canSkipDefaultChecks() {
        return hasCanSkipDefaultCheck(this.getClass());
    }

    /**
     * Sets the skipDefaultChecks state of this event.
     * This skips checks that follow the event (For example, the check that the chunk is owned by the faction when
     * setting the faction home)
     * <br>
     * Note, not all events are cancelable, and any attempt to
     * invoke this method on an event that does not support it (as determined by {@link #canSkipDefaultChecks})
     * will result in an {@link UnsupportedOperationException}.
     * <br>
     * The functionality of setting the skipDefaultChecks state is defined on a per-event basis.
     *
     * @param skipDefaultChecks The new skipDefaultChecks value
     */
    public void setSkipDefaultChecks(final boolean skipDefaultChecks) {
        if (!canSkipDefaultChecks()) {
            throw new UnsupportedOperationException(
                    "Attempted to call BaseFactionEvent#setSkipDefaultChecks() on an event that doesn't support it of type: "
                            + this.getClass().getCanonicalName()
            );
        }
        this.skipDefaultChecks = skipDefaultChecks;
    }

    /* ========================================= */
    /* Event Meta Stuff
    /* ========================================= */

    // Implementation borrowed from EventListenerHelper

    private static final LockHelper<Class<?>, Boolean> canSkipChecksLists = new LockHelper<>(new IdentityHashMap<>());

    private boolean hasCanSkipDefaultCheck(final Class<?> eventClass) {
        return hasAnnotation(eventClass, SkipChecks.class, canSkipChecksLists);
    }

    private static boolean hasAnnotation(final Class<?> eventClass, final Class<? extends Annotation> annotation, final LockHelper<Class<?>, Boolean> lock) {
        if (eventClass == BaseFactionEvent.class)
            return false;

        return lock.computeIfAbsent(eventClass, () -> {
            final var parent = eventClass.getSuperclass();
            return eventClass.isAnnotationPresent(annotation) || (parent != null && hasAnnotation(parent, annotation, lock));
        });
    }

    /**
     * Marker annotation indicating the checks after the event can be skipped using {@link BaseFactionEvent#setSkipDefaultChecks(boolean)}
     */
    @Retention(value = RUNTIME)
    @Target(value = TYPE)
    public @interface SkipChecks{}
}
