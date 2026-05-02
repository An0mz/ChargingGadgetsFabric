package charginggadgets.events;

import charginggadgets.init.CGContent.*;
import reborncore.RebornRegistry;

import java.util.Arrays;
import net.minecraft.world.item.Item;

public class ModRegistry {

    public static void setup() {
        registerBlocks();
        registerItems();
    }

    private static void registerBlocks() {
        Item.Properties itemProperties = new Item.Properties();
        Arrays.stream(Machine.values()).forEach(value -> RebornRegistry.registerBlock(value.block, itemProperties));
    }

    private static void registerItems() {

    }

}
