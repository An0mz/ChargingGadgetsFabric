package charginggadgets;

import charginggadgets.config.CGConfig;
import charginggadgets.init.CGBlockEntities;
import charginggadgets.init.CGContent;
import charginggadgets.events.ModRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reborncore.common.config.Configuration;

public class ChargingGadgets implements ModInitializer {
	public final static String MOD_ID = "charginggadgets";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static ChargingGadgets INSTANCE;

    public static CreativeModeTab ITEMGROUP;

    @Override
	public void onInitialize() {
        INSTANCE = this;
        new Configuration(CGConfig.class, "charging_gadgets");

        ModRegistry.setup();
        CGBlockEntities.initBE();

        ITEMGROUP = Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                Identifier.fromNamespaceAndPath(MOD_ID, "item_group"),
                FabricItemGroup.builder()
                        .title(net.minecraft.network.chat.Component.translatable("itemGroup.charginggadgets.item_group"))
                        .icon(() -> new ItemStack(CGContent.Machine.CHARGING_STATION))
                        .displayItems((params, output) -> {
                            for (CGContent.Machine machine : CGContent.Machine.values()) {
                                output.accept(machine.getStack());
                            }
                        })
                        .build()
        );
    }
}
