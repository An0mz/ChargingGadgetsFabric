package charginggadgets.blocks;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import charginggadgets.init.CGContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;
import reborncore.common.blocks.BlockMachineBase;

import java.util.List;
import java.util.Optional;

public class ChargingStationBlock extends GenericMachineBlock {

    public ChargingStationBlock() {
        super(
                BlockBehaviour.Properties.of().setId(
                        ResourceKey.create(Registries.BLOCK,
                                Identifier.fromNamespaceAndPath("charginggadgets", "charging_station"))
                ),
                GuiType.CHARGING_STATION,
                ChargingStationBlockEntity::new
        );
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(BlockMachineBase.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChargingStationBlockEntity(pos, state);
    }

    // 1: getRenderType() in GenericMachineBlock returns int, not RenderShape.
    // The int return is a legacy workaround — just remove the override here,
    // GenericMachineBlock already returns RenderShape.MODEL.ordinal().
    // If you need to change render type, fix it in GenericMachineBlock instead.

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        // 2: CustomData has no .contains() — call .copyTag() and check the tag directly
        if (customData != null) {
            CompoundTag stackTag = customData.copyTag();
            if (stackTag.contains("blockEntity_data")) {
                BlockEntity blockEntity = worldIn.getBlockEntity(pos);
                if (blockEntity instanceof ChargingStationBlockEntity chargingBE) {
                    // 3: CompoundTag.getCompound() and getLong() now return Optionals in 1.21.1
                    CompoundTag nbt = stackTag.getCompound("blockEntity_data").orElse(new CompoundTag());
                    long energy = stackTag.getLong("energy").orElse(0L);
                    chargingBE.setEnergy(energy);
                    injectLocationData(nbt, pos);
                    // 4: loadWithComponents now takes a single ValueInput, not (CompoundTag, RegistryAccess)
                    // Use the registry-aware NBT loader via HolderLookup.Provider instead
                    blockEntity.loadWithComponents(
                            net.minecraft.world.level.storage.TagValueInput.create(
                                    net.minecraft.util.ProblemReporter.DISCARDING,
                                    worldIn.registryAccess(),
                                    nbt
                            )
                    );
                    blockEntity.setChanged();
                }
            }
        }
    }

    @Override
    @SuppressWarnings("deprecated")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY);
        List<ItemStack> drops = super.getDrops(state, builder);
        if (blockEntity instanceof ChargingStationBlockEntity chargingBE) {
            drops.stream()
                    .filter(e -> e.getItem() == CGContent.Machine.CHARGING_STATION.asItem())
                    .findFirst()
                    .ifPresent(e -> CustomData.update(DataComponents.CUSTOM_DATA, e,
                            tag -> tag.putLong("energy", chargingBE.getEnergy())));
        }
        return drops;
    }

    @Override
    public Optional<ItemStack> getDropWithContents(Level world, BlockPos pos, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return Optional.empty();
        }
        ItemStack newStack = stack.copy();
        CompoundTag blockEntityData = blockEntity.saveWithoutMetadata(world.registryAccess());
        stripLocationData(blockEntityData);
        CompoundTag stackTag = newStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        stackTag.put("blockEntity_data", blockEntityData);
        if (blockEntity instanceof ChargingStationBlockEntity chargingBE) {
            stackTag.putDouble("energy", chargingBE.getEnergy());
        }
        newStack.set(DataComponents.CUSTOM_DATA, CustomData.of(stackTag));
        return Optional.of(newStack);
    }

    private void injectLocationData(CompoundTag compound, BlockPos pos) {
        compound.putInt("x", pos.getX());
        compound.putInt("y", pos.getY());
        compound.putInt("z", pos.getZ());
    }

    private void stripLocationData(CompoundTag compound) {
        compound.remove("x");
        compound.remove("y");
        compound.remove("z");
    }
}