package aspi.cho.resourcescondenser.item;

import aspi.cho.resourcescondenser.ResourcesCondenser;
import aspi.cho.resourcescondenser.item.custom.D20Item;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ResourcesCondenser.MOD_ID);

    public static final RegistryObject<Item> STRANGE_MATTER = ITEMS.register("strange_matter",
            ()-> new Item(new Item.Properties().tab(ModCreativeModeTab.RES_CONDENS_TAB)));

    public static final RegistryObject<Item> D20 = ITEMS.register("d20",
            ()-> new D20Item(new Item.Properties().tab(ModCreativeModeTab.RES_CONDENS_TAB).stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
