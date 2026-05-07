package charginggadgets.init;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import charginggadgets.blocks.ChargingStationBlock;
import charginggadgets.blocks.GenericMachineBlock;
import charginggadgets.blocks.GuiType;
import charginggadgets.utils.InitUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Locale;

public class CGContent {
    public enum Machine implements ItemLike {
        CHARGING_STATION(new ChargingStationBlock());

        public final String name;
        public final Block block;

        <B extends Block> Machine(B block) {
            this.name = this.toString().toLowerCase(Locale.ROOT);
            this.block = block;
            InitUtils.setup(block, name);
        }

        public ItemStack getStack() {
            return new ItemStack(block);
        }

        @Override
        public Item asItem() {
            return block.asItem();
        }
    }
}