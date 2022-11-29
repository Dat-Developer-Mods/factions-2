package com.datdeveloper.datfactions.util;

import com.datdeveloper.datfactions.factionData.*;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUtil {
    final static CompassDirection[][] COMPASS = {
            {CompassDirection.NW, CompassDirection.N, CompassDirection.NE},
            {CompassDirection.W, CompassDirection.NONE, CompassDirection.E},
            {CompassDirection.SW, CompassDirection.S, CompassDirection.SE}
    };

    final static Component DEFAULTCHAR = Component.literal("-").withStyle(ChatFormatting.DARK_GRAY);;

    final static char[] SYMBOLS = "/\\#?!%$&*£[]{}abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    final static int HALFWIDTH = 20;
    final static int HALFHEIGHT = 5;
    private final ChunkPos centre;
    private final CompassDirection rotation;
    private final FactionLevel level;
    private final FactionPlayer from;

    Map<Faction, MutableComponent> symbolMap = new HashMap<>();
    int currentSymbol;

    public MapUtil(final ChunkPos centre, final float rotation, final ResourceKey<Level> level, final FactionPlayer from) {
        this.centre = centre;
        this.rotation = CompassDirection.fromYaw(rotation);
        this.level = FLevelCollection.getInstance().getByKey(level);
        this.from = from;
    }

    private Component getCompassCharacter(final int x, final int y) {
        final CompassDirection character = COMPASS[y][x];
        return Component.literal(character.displayText)
                .withStyle(character == rotation ? ChatFormatting.DARK_PURPLE : DatChatFormatting.TextColour.INFO);
    }

    MutableComponent buildHeader() {
        String x = String.valueOf(centre.x);
        String z = String.valueOf(centre.z);
        final int maxSize = Math.max(x.length(), z.length());

        if (x.length() < z.length()) x = " ".repeat(maxSize - x.length()) + x;
        else z = " ".repeat(maxSize - z.length()) + z;

        final String title = x + "," + z;
        final String padding = "=".repeat(HALFWIDTH - ((title.length() - 2) / 2));
        return Component.literal(padding + "[")
                .withStyle(DatChatFormatting.TextColour.INFO)
                .append(
                        Component.literal(title)
                                .withStyle(DatChatFormatting.TextColour.HEADER)
                )
                .append(
                        Component.literal("]" + padding)
                                .withStyle(DatChatFormatting.TextColour.INFO)
                );
    }

    MutableComponent buildMap() {
        final MutableComponent map = Component.empty();
        for (int row = centre.z - HALFHEIGHT, rowIndex = 0; row <= centre.z + HALFHEIGHT; ++row, ++rowIndex) {
            final MutableComponent rowComponent = Component.literal("\n");

            for (int column = centre.x - HALFWIDTH, columnIndex = 0; column <= centre.x + HALFWIDTH; ++column, ++columnIndex) {
                if (columnIndex < 3 && rowIndex < 3) {
                    rowComponent.append(getCompassCharacter(columnIndex, rowIndex));
                } else {
                    final ChunkPos pos = new ChunkPos(column, row);
                    if (pos.equals(centre)) {
                        rowComponent.append(
                                Component.literal("+")
                                .withStyle(ChatFormatting.WHITE)
                        );
                    } else {
                        final Faction owner = level.getChunkOwningFaction(pos);
                        if (owner.hasFlag(EFactionFlags.UNCHARTED) || owner.getId().equals(level.getSettings().getDefaultOwner())) {
                            rowComponent.append(DEFAULTCHAR);
                        } else {
                            rowComponent.append(symbolMap.computeIfAbsent(owner, this::getNextSymbol));
                        }
                    }
                }
            }
            map.append(rowComponent);
        }
        return map;
    }

    private MutableComponent getNextSymbol(final Faction faction) {
        final int nextSymbol = currentSymbol++;

        final char symbol;
        if (nextSymbol >= SYMBOLS.length) {
            symbol = '-';
        } else {
            symbol = SYMBOLS[nextSymbol];
        }
        final ChatFormatting formatting = RelationUtil.getRelation(from, faction).formatting;
        return Component.literal(String.valueOf(symbol))
                .withStyle(Style.EMPTY
                        .applyFormats(formatting)
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.literal(faction.getName())
                                        .withStyle(formatting)
                                        .append("\n")
                                        .append(faction.getShortDescription(from.getFaction()))
                        ))
                );
    }

    private Component buildFooter() {
        final MutableComponent component = Component.literal("Factions: ")
                .withStyle(DatChatFormatting.TextColour.INFO);
        final List<MutableComponent> components = symbolMap.keySet().stream()
                .map(faction ->
                        Component.literal(ChatFormatting.WHITE + symbolMap.get(faction).getString() + ": ")
                                .append(
                                        faction.getNameWithDescription(from.getFaction())
                                                .withStyle(RelationUtil.getRelation(from, faction).formatting)
                                ))
                .toList();
        component.append(ComponentUtils.formatList(components, ComponentUtils.DEFAULT_SEPARATOR));
        return component;
    }

    public MutableComponent build() {
        final MutableComponent component = buildHeader()
                .append(buildMap()).append("\n");

        if (!symbolMap.isEmpty()) component.append(buildFooter());
        return component;
    }

    private static enum CompassDirection {
        S("S"),
        SW("/"),
        W("W"),
        NW("\\"),
        N("N"),
        NE("/"),
        E("E"),
        SE("\\"),
        NONE("+");
        public final String displayText;

        CompassDirection(final String displayText) {
            this.displayText = displayText;
        }

        public static CompassDirection fromYaw(final float yaw) {
            final CompassDirection[] values = CompassDirection.values();
            return values[Mth.abs((Mth.floor(yaw / 45.0D + 0.5D) & 7) % values.length)];
        }
    }
}
