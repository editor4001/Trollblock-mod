package github.betot.trollblock.item;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties RAMEN = new FoodProperties.Builder()
            .alwaysEat()
            .saturationMod(0.3f)
            .nutrition(5)
            .fast()
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 500, 2), 1.0F)
            .build();

    public static final FoodProperties DIESEL_CANDY = new FoodProperties.Builder()
            .fast()
            .nutrition(0)
            .saturationMod(0.0f)
            .alwaysEat()
            .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 100, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 100, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 100, 5), 1.0F)
            .build();
}
