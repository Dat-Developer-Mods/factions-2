package com.datdeveloper.datfactions.tests;

import com.datdeveloper.datfactions.Datfactions;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Datfactions.MOD_ID)
public class FactionTests extends BaseTest {
    @GameTest(template = "empty")
    @PrefixGameTestTemplate(false)
    public static void testSetup(final GameTestHelper helper) {

    }
    
    @GameTest(template = "empty")
    @PrefixGameTestTemplate(false)
    public static void testCommands(final GameTestHelper helper) {
        final Player player = helper.makeMockPlayer();

//        final ServerPlayer serverPlayer = makeMockServerPlayer(player);
//        final CommandSourceStack commandSourceStack = serverPlayer.createCommandSourceStack();
//        final Commands commands = player.getServer().getCommands();
//        final ParseResults<CommandSourceStack> command = commands.getDispatcher().parse("factions create test", commandSourceStack);
//        final int result = commands.performCommand(command, "factions create test");
//        assert (result == 1);
    }
}