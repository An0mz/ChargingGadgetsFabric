package charginggadgets.blocks;

import net.minecraft.world.level.block.state.BlockBehaviour;
import reborncore.api.blockentity.IMachineGuiHandler;
import reborncore.common.blocks.BlockMachineBase;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GenericMachineBlock extends BlockMachineBase {

    private final IMachineGuiHandler gui;
    BiFunction<BlockPos, BlockState, BlockEntity> blockEntityClass;

    public GenericMachineBlock(BlockBehaviour.Properties settings, IMachineGuiHandler gui, BiFunction<BlockPos, BlockState, BlockEntity> blockEntityClass) {
        super(settings);
        this.blockEntityClass = blockEntityClass;
        this.gui = gui;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (blockEntityClass == null) {
            return null;
        }
        return blockEntityClass.apply(pos, state);
    }


    @Override
    public IMachineGuiHandler getGui() {
        return gui;
    }

    @Override
    public int getRenderType() {
        // Base mappings expect an int; return ordinal value for RenderShape.MODEL
        return RenderShape.MODEL.ordinal();
    }
}
