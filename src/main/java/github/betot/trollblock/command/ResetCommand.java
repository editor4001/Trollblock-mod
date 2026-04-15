package github.betot.trollblock.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ResetCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("resetTransmitterValues")
                        .executes(ctx -> {

                            Player player = ctx.getSource().getPlayer();
                            CompoundTag tag = player.getPersistentData();
                            tag.remove("linking");
                            tag.remove("link_source");
                            tag.remove("dirty");
                            tag.remove("back_saving");

                            return 1;
                        })
        );
    }
}