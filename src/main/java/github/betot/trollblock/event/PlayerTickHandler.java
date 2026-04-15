package github.betot.trollblock.event;

import github.betot.trollblock.effect.ModEffects;
import github.betot.trollblock.entity.DirtyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerTickHandler {

    public static boolean message_showed = false;
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        var player = event.player;

        if (!player.level().isClientSide) {

            if(!((DirtyEntity)player).trollblock$isDurty()){
                if(player.getPersistentData().getBoolean("dirty")){
                    player.getPersistentData().putBoolean("dirty",false);
                }
            }

            if (player.getPersistentData().getBoolean("dirty")) {
                if (!((DirtyEntity) player).trollblock$isDurty()) {
                    ((DirtyEntity) player).trollblock$setDurty(true);
                }
            }

            if (((DirtyEntity) player).trollblock$isDurty()) {

                player.addEffect(new MobEffectInstance(ModEffects.DIRTY_EFFECT.get()),player);

                if (!message_showed) {
                    player.displayClientMessage(Component.literal("You are dirty"), true);
                    message_showed = true;
                }

                if (player.isInWaterRainOrBubble()) {
                    ((DirtyEntity) player).trollblock$setIsOnWater(true);
                } else {
                    ((DirtyEntity) player).trollblock$setIsOnWater(false);
                }

                if (((DirtyEntity) player).trollblock$isOnWater()) {
                    ((DirtyEntity) player).trollblock$setDurty(false);
                    message_showed = false;
                    player.displayClientMessage(Component.literal("You are not dirty anymore"), true);
                }
            }
        }
    }
}