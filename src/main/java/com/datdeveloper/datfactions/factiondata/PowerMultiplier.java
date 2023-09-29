package com.datdeveloper.datfactions.factiondata;

import com.datdeveloper.datmoddingapi.localisation.DatLocalisation;
import com.datdeveloper.datmoddingapi.util.DatMessageFormatter;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.ArrayUtils;

import java.util.function.BiFunction;

/**
 * A multiplier for a player power change
 * <br>
 * This class contains the actual multiplier, and some other metadata for identifying/displaying the multiplier
 */
public class PowerMultiplier {
    /** A consistent ID used to identify this PowerMultiplier */
    final String id;

    /** The title that is displayed for the multiplier */
    String title;

    /** The power multiplier */
    float multiplier;

    /**
     * A function that transforms the title into a chat component to be displayed
     * <br>
     * The function takes the title and the multiplier as it's arguments, and returns a chat component
     */
    BiFunction<String, Float, Component> titleTransformer;

    /**
     * @param id                A consistent ID used to identify this PowerMultiplier
     * @param title             The title that is displayed for the multiplier
     * @param multiplier        The multiplier
     * @param titleTransformer  A function to convert the title into a chat component
     *                          <br>
     *                          The function takes the title and the multiplier as it's arguments, and returns a chat
     *                          component
     * @see PowerMultiplier#withTranslatableTitle(String, String, float, Object...)
     * @see PowerMultiplier#withFormattableTitle(String, String, float, Object...)
     * @see PowerMultiplier#withPlainTitle(String, String, float)
     */
    public PowerMultiplier(final String id, final String title, final float multiplier, final BiFunction<String, Float, Component> titleTransformer) {
        this.id = id;
        this.title = title;
        this.multiplier = multiplier;
        this.titleTransformer = titleTransformer;
    }

    /**
     * Create a PowerMultiplier with a title that is used as a key for {@link DatLocalisation} then formatted with
     * {@link DatMessageFormatter}
     * <br>
     * Please note that the arguments are provided to the formatter in the order of: multiplier, then the provided args.
     * Therefore, the index of the multiplier is 0, and the index of the provided args starts from 1.
     * @param id            A consistent ID used to identify this PowerMultiplier
     * @param title         The title that is displayed for the multiplier
     * @param multiplier    The multiplier
     * @param args          Arguments that are passed to the formatter.
     * @return A new power multiplier
     */
    public static PowerMultiplier withTranslatableTitle(final String id, final String title, final float multiplier, final Object... args) {
        return new PowerMultiplier(id, title, multiplier, (t, m) -> DatMessageFormatter.formatChatString(DatLocalisation.getInstance().getLocalisation(t), m, args));
    }

    /**
     * Create a PowerMultiplier with a title that is formatted with {@link DatMessageFormatter}
     * <br>
     * Please note that the arguments are provided to the formatter in the order of: multiplier, then the provided args.
     * Therefore, the index of the multiplier is 0, and the index of the provided args starts from 1.
     *
     * @param id            A consistent ID used to identify this PowerMultiplier
     * @param title         The title that is displayed for the multiplier
     * @param multiplier    The multiplier
     * @param args          Arguments that are passed to the formatter.
     * @return A new power multiplier
     */
    public static PowerMultiplier withFormattableTitle(final String id, final String title, final float multiplier, final Object... args) {
        return new PowerMultiplier(id, title, multiplier, (f, m) -> DatMessageFormatter.formatChatString(f, ArrayUtils.addFirst(args, m)));
    }

    /**
     * Create a PowerMultiplier with a title that is formatted with {@link String#format(String, Object...)}
     * <br>
     * Please note that the arguments are provided to the formatter in the order of: multiplier, then the provided args.
     * Therefore, the index of the multiplier is 0, and the index of the provided args starts from 1.
     *
     * @param id            A consistent ID used to identify this PowerMultiplier
     * @param title         The title that is displayed for the multiplier
     * @param multiplier    The multiplier
     * @param args          Arguments that are passed to the formatter.
     * @return A new power multiplier
     */
    public static PowerMultiplier withStringFormatTitle(final String id, final String title, final float multiplier, final Object... args) {
        return new PowerMultiplier(id, title, multiplier, (f, m) -> Component.literal(String.format(f, ArrayUtils.addFirst(args, m))));
    }

    /**
     * Create a PowerMultiplier with a title that is unchanged when displayed
     * @param id            A consistent ID used to identify this PowerMultiplier
     * @param title         The title that is displayed for the multiplier
     * @param multiplier    The multiplier
     * @return A new power multiplier
     */
    public static PowerMultiplier withPlainTitle(final String id, final String title, final float multiplier) {
        return new PowerMultiplier(id, title, multiplier, (f, m) -> Component.literal(f));
    }

    public String getId() {
        return id;
    }

    public String getRawTitle() {
        return title;
    }

    /**
     * Get the title after being transformed with the {@link PowerMultiplier#titleTransformer}
     * @return The title transformed into a component
     */
    public Component getTransformedTitle() {
        return titleTransformer.apply(getRawTitle(), getMultiplier());
    }

    public float getMultiplier() {
        return multiplier;
    }

    public BiFunction<String, Float, Component> getTitleTransformer() {
        return titleTransformer;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setMultiplier(final float multiplier) {
        this.multiplier = multiplier;
    }

    public void setTitleTransformer(final BiFunction<String, Float, Component> titleTransformer) {
        this.titleTransformer = titleTransformer;
    }
}
