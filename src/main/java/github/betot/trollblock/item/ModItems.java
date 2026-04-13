package github.betot.trollblock.item;

import github.betot.trollblock.item.custom.DieselCandyItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static github.betot.trollblock.Trollblock.MODID;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> RAMEN = ITEMS.register("ramen",
            () -> new Item(new Item.Properties().food(ModFoods.RAMEN).stacksTo(1)));

    public static final RegistryObject<Item> DIESEL_CANDY = ITEMS.register("diesel_candy",
            () -> new DieselCandyItem(new Item.Properties().food(ModFoods.DIESEL_CANDY).stacksTo(62)));

    public static final RegistryObject<Item> PASTA = ITEMS.register("pasta",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PLASTIC_CHEET = ITEMS.register("plastic_cheet",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}