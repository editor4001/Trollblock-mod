package github.betot.trollblock.item;

import github.betot.trollblock.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static github.betot.trollblock.Trollblock.MODID;

public class ModCreativeModTabs {
    private static DeferredRegister<CreativeModeTab> TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = TAB.register("troll_tab", () -> CreativeModeTab.builder()
            // Set name of tab to display
            .title(Component.literal("Troll blocks"))
            // Set icon of creative tab
            .icon(() -> new ItemStack(ModItems.RAMEN.get()))

            .displayItems((p_270258_, pOutput) -> {
                pOutput.accept(ModItems.RAMEN.get());
                pOutput.accept(ModItems.DIESEL_CANDY.get());
                pOutput.accept((ModItems.PASTA.get()));
                pOutput.accept(ModItems.PLASTIC_CHEET.get());
                pOutput.accept((ModItems.SCREWDRIVER.get()));
                pOutput.accept(ModItems.ENERGY_TABLET.get());
                pOutput.accept(ModItems.ELECTRONIC_CHIP.get());
                pOutput.accept((ModItems.WIRE.get()));

                pOutput.accept(ModBlocks.SOIL.get());
                pOutput.accept(ModBlocks.QUICK_SAND.get());
                pOutput.accept(ModBlocks.TRANSMITTER.get());
                pOutput.accept(ModBlocks.CHARGER.get());
                pOutput.accept((ModBlocks.STEEL.get()));
            })

            .build()
    );

    public static void register(IEventBus eventBus){
        TAB.register(eventBus);
    }
}
