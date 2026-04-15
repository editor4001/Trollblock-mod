package github.betot.trollblock.event;

import github.betot.trollblock.command.ResetCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommandEventHandler {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        ResetCommand.register(event.getDispatcher());
    }
}