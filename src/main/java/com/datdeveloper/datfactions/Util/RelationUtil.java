package com.datdeveloper.datfactions.Util;

import com.datdeveloper.datfactions.factionData.EFactionRelation;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.FactionRelation;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class RelationUtil {
    public static EFactionRelation getRelation(FactionPlayer from, FactionPlayer to) {
        if (from.hasFaction() && to.hasFaction()) return getRelation(from.getFaction(), to.getFaction());
        return EFactionRelation.NEUTRAL;
    }

    public static EFactionRelation getRelation(FactionPlayer from, Faction to) {
        if (from.hasFaction()) return getRelation(from.getFaction(), to);
        return EFactionRelation.NEUTRAL;
    }

    public static EFactionRelation getRelation(Faction from, Faction to) {
        if (from.getId().equals(to.getId())) return EFactionRelation.SELF;

        FactionRelation relation = from.getRelation(to);
        if (relation != null) return relation.getRelation();

        return EFactionRelation.NEUTRAL;
    }

    public static Component wrapFactionName(Faction from, Faction to) {
        return MutableComponent.create(Component.literal(to.getName()).getContents())
                .withStyle(getRelation(from, to).getFormatting())
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/faction info " + to.getName())));
    }

    public static Component wrapFactionName(FactionPlayer from, Faction to) {
        return MutableComponent.create(Component.literal(to.getName()).getContents())
                .withStyle(getRelation(from, to).getFormatting())
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/faction info " + to.getName())));
    }

    public static Component wrapPlayerName(FactionPlayer from, FactionPlayer to) {

        return MutableComponent.create(Component.literal(to.getLastName()).getContents())
                .withStyle(getRelation(from, to).getFormatting())
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/faction pinfo " + to.getLastName())));
    }
}
