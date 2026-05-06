package charginggadgets.blocks;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import charginggadgets.init.CGContent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ChargingStationBlock extends GenericMachineBlock {
    public static final EnumProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ChargingStationBlock() {
        super(GuiType.CHARGING_STATION, ChargingStationBlockEntity::new);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChargingStationBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderType() {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("blockEntity_data")) {
            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (blockEntity instanceof ChargingStationBlockEntity) {
                CompoundTag stackTag = customData.copyTag();
                CompoundTag nbt = stackTag.getCompound("blockEntity_data");
                long energy = stackTag.getLong("energy");
                ((ChargingStationBlockEntity) blockEntity).setEnergy(energy);
                this.injectLocationData(nbt, pos);
                blockEntity.loadWithComponents(nbt, worldIn.registryAccess());
                blockEntity.setChanged();
            }
        }
    }

    @Override
    @SuppressWarnings("deprecated")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY);

        List<ItemStack> drops = super.getDrops(state, builder);
        if (blockEntity instanceof ChargingStationBlockEntity chargingStationBlockEntity) {
            drops.stream()
                    .filter(e -> e.getItem() == CGContent.Machine.CHARGING_STATION.asItem())
                    .findFirst()
                    .ifPresent(e -> CustomData.update(DataComponents.CUSTOM_DATA, e, tag -> tag.putLong("energy", chargingStationBlockEntity.getEnergy())));
        }

        return drops;
    }

    @Override
    public Optional<ItemStack> getDropWithContents(Level world, BlockPos pos, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity == null) {
            return Optional.empty();
        } else {
            ItemStack newStack = stack.copy();
            CompoundTag blockEntityData = blockEntity.saveWithoutMetadata(world.registryAccess());
            this.stripLocationData(blockEntityData);
            CompoundTag stackTag = newStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            stackTag.put("blockEntity_data", blockEntityData);
            if (blockEntity instanceof ChargingStationBlockEntity) {
                stackTag.putDouble("energy", ((ChargingStationBlockEntity) blockEntity).getEnergy());
            }
            newStack.set(DataComponents.CUSTOM_DATA, CustomData.of(stackTag));
            return Optional.of(newStack);
        }
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
