package github.betot.trollblock.effect;

import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

import static github.betot.trollblock.Trollblock.MODID;

public class ModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);

    public static final RegistryObject<MobEffect> DIRTY_EFFECT =
            EFFECTS.register("dirty", () ->
                    new DirtyEffect(MobEffectCategory.HARMFUL, 0x5A4A3A)
                            .addAttributeModifier(
                                    Attributes.MOVEMENT_SPEED,
                                    String.valueOf(UUID.fromString("1373443e-3f16-40de-9558-b91eac3ad008")),
                                    -0.6,
                                    AttributeModifier.Operation.MULTIPLY_TOTAL
                            )
            );

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}