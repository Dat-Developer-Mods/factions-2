package com.datdeveloper.datfactions.commands.suggestions;

public class DatSuggestionProviders {
    public static FactionSuggestionProvider factionProvider = new FactionSuggestionProvider();
    public static FPlayerSuggestionProvider fPlayerProvider = new FPlayerSuggestionProvider(false);
    public static FactionPermissionSuggestionProvider permissionProvider = new FactionPermissionSuggestionProvider();
}
