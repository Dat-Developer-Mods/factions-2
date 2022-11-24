package com.datdeveloper.datfactions.commands.arguments;

import com.datdeveloper.datfactions.Datfactions;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * A simple class for registering custom argument types
 * @see Datfactions#Datfactions()
 */
public class DatFactionArguments {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registry.COMMAND_ARGUMENT_TYPE_REGISTRY, Datfactions.MODID);

    /* ========================================= */
    /* Argument Registration
    /* ========================================= */

    public static final RegistryObject<SingletonArgumentInfo<FactionArgument>> FACTION_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("faction", () ->
            ArgumentTypeInfos.registerByClass(FactionArgument.class, SingletonArgumentInfo.contextFree(FactionArgument::new)));

    public static final RegistryObject<SingletonArgumentInfo<NewFactionNameArgument>> FACTION_NAME_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("factionname", () ->
            ArgumentTypeInfos.registerByClass(NewFactionNameArgument.class, SingletonArgumentInfo.contextFree(NewFactionNameArgument::new)));

    public static final RegistryObject<FactionFlagsArgument.Info> FACTION_FLAG_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("factionflag", () ->
            ArgumentTypeInfos.registerByClass(FactionFlagsArgument.class, new FactionFlagsArgument.Info()));
}
