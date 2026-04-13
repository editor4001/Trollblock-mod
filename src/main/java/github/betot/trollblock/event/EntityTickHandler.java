package github.betot.trollblock.event;

import github.betot.trollblock.entity.DirtyEntity;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EntityTickHandler {

    @Mod.EventBusSubscriber(modid = "trollblock")
    public static class DirtyCollisionHandler {

        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {

            Entity entity = event.getEntity();

            // serveur only
            if (entity.level().isClientSide) return;

            // déjà dirty
            if (entity.getPersistentData().getBoolean("dirty")) return;

            var nearby = entity.level().getEntitiesOfClass(
                    Entity.class,
                    entity.getBoundingBox().inflate(0.3)
            );

            for (Entity e : nearby) {

                // ne pas se check soi-même
                if (e == entity) continue;

                // vérifier que l'entité implémente DirtyEntity
                if (e instanceof DirtyEntity dirty) {

                    if (dirty.trollblock$isDurty()) {

                        entity.getPersistentData().putBoolean("dirty", true);
                        break;
                    }
                }
            }
        }
    }
}