package github.betot.trollblock.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class DirtyEffect extends MobEffect {
    public DirtyEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(-6);
        entity.getAttribute(Attributes.LUCK).setBaseValue(-4);

        if(entity.isInvisible()){
            entity.setInvisible(false);
        }

        entity.setSilent(false);
        entity.setDeltaMovement(entity.getDeltaMovement().x-0.1, entity.getDeltaMovement().y-0.1, entity.getDeltaMovement().z-0.1   );
    }

}
