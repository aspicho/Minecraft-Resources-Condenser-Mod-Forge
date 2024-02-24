package aspi.cho.resourcescondenser.block.entity;

import aspi.cho.resourcescondenser.config.ResourcesCondenserCommonConfigs;
import aspi.cho.resourcescondenser.screen.CondenserMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jline.utils.Log;

import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import static aspi.cho.resourcescondenser.ResourcesCondenser.CONDENSE_ITEM_DIMENSION_DEPENDENCY;


public class CondenserBlockEntity extends BlockEntity implements MenuProvider {

    private static final Random rand = new Random();

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap =
            Map.of(Direction.DOWN, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 0, (i, s) -> false)),
                    Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 0, (i, s) -> false)),
                    Direction.SOUTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 0, (i, s) -> false)),
                    Direction.EAST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 0, (i, s) -> false)),
                    Direction.WEST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 0, (i, s) -> false)));


    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = ResourcesCondenserCommonConfigs.CONDENSE_SPEED.get();

    public CondenserBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.CONDENSER.get(), blockPos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> CondenserBlockEntity.this.progress;
                    case 1 -> CondenserBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> CondenserBlockEntity.this.progress = value;
                    case 1 -> CondenserBlockEntity.this.maxProgress = value;
                };
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.resources_condenser.condenser");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player p_39956_) {
        return new CondenserMenu(id, inventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null) {
                return lazyItemHandler.cast();
            }

            if(directionWrappedHandlerMap.containsKey(side)) {
                return directionWrappedHandlerMap.get(side).cast();
            }
        }
        return super.getCapability(cap);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++){
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CondenserBlockEntity pEntity) {
        if (level.isClientSide()){
            return;
        }

        ResourceKey<Level> dimType = level.dimension();
        String dimId = dimType.location().toString();

        if (CONDENSE_ITEM_DIMENSION_DEPENDENCY.containsKey(dimId)){

            pEntity.progress++;
            setChanged(level, pos, state);

            if(pEntity.progress < pEntity.maxProgress) { return; }

            Double rDouble = rand.nextDouble();

            Hashtable<String, Double> items = CONDENSE_ITEM_DIMENSION_DEPENDENCY.get(dimId);

            for (Map.Entry<String, Double> entry: items.entrySet()) {
                String item = entry.getKey();
                Double chance = entry.getValue();

                if (rDouble > chance) {
                    rDouble -= chance;
                } else {
                    createItem(pEntity, item);
                    pEntity.resetProgress();
                    setChanged(level, pos, state);
                    return;
                }
            }

        } else {
            level.destroyBlock(pos, true);
        }
    }

    private static boolean canInsert(CondenserBlockEntity pEntity, String Item_name) {
        SimpleContainer inventory = new SimpleContainer(pEntity.itemHandler.getSlots());
        for (int i = 0; i < pEntity.itemHandler.getSlots(); i++ ){
            inventory.setItem(i, pEntity.itemHandler.getStackInSlot(i));
        }

        return canInsertAmountToOutputSlot(inventory) && canInsertItemToOutputSlot(inventory,
                new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Item_name))));
    }

    private static boolean canInsertItemToOutputSlot(SimpleContainer inventory, ItemStack itemStack) {
        return inventory.getItem(0).getItem() == itemStack.getItem() || inventory.getItem(0).isEmpty();
    }

    private static boolean canInsertAmountToOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(0).getMaxStackSize() > inventory.getItem(0).getCount();
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void createItem(CondenserBlockEntity pEntity, String Item_name) {
        if (canInsert(pEntity, Item_name)){
            pEntity.itemHandler.setStackInSlot(0, new ItemStack(
                    ForgeRegistries.ITEMS.getValue(new ResourceLocation(Item_name)),
                    pEntity.itemHandler.getStackInSlot(0).getCount()
                            + ResourcesCondenserCommonConfigs.CONDENSE_PER_OPERATION.get()));
            pEntity.resetProgress();
        }
    }
}
