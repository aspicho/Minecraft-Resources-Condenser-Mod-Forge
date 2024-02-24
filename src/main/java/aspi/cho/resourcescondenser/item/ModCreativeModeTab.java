package aspi.cho.resourcescondenser.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeModeTab {
    public static final CreativeModeTab RES_CONDENS_TAB = new CreativeModeTab("resources_condenser_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.STRANGE_MATTER.get());
        }
    };
}
