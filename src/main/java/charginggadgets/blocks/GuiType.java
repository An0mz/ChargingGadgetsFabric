package charginggadgets.blocks;

import charginggadgets.ChargingGadgets;
import charginggadgets.blockentity.ChargingStationBlockEntity;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import reborncore.api.blockentity.IMachineGuiHandler;
import reborncore.common.screen.BuiltScreenHandler;
import reborncore.common.screen.BuiltScreenHandlerProvider;

import java.util.HashMap;
import java.util.Map;

public final class GuiType<T extends BlockEntity> implements IMachineGuiHandler {
    public static final Map<ResourceLocation, GuiType<?>> TYPES = new HashMap<>();

    public static final GuiType<ChargingStationBlockEntity> CHARGING_STATION = register("chargingstation");

    private static <T extends BlockEntity> GuiType<T> register(String id) {
        return register(ResourceLocation.fromNamespaceAndPath(ChargingGadgets.MOD_ID, id));
    }

    public static <T extends BlockEntity> GuiType<T> register(ResourceLocation identifier) {
        if (TYPES.containsKey(identifier)) {
            throw new RuntimeException("Duplicate gui type found");
        }
        return new GuiType<>(identifier);
    }

    private final ResourceLocation identifier;
    private final MenuType<BuiltScreenHandler> screenHandlerType;

    private GuiType(ResourceLocation identifier) {
        this.identifier = identifier;
        this.screenHandlerType = Registry.register(BuiltInRegistries.MENU, identifier, new ExtendedScreenHandlerType<>(getScreenHandlerFactory(), BlockPos.STREAM_CODEC));

        TYPES.put(identifier, this);
    }

    public ResourceLocation getResourceLocation() {
        return identifier;
    }

    public MenuType<BuiltScreenHandler> getScreenHandlerType() {
        return screenHandlerType;
    }

    private ExtendedScreenHandlerType.ExtendedFactory<BuiltScreenHandler, BlockPos> getScreenHandlerFactory() {
        return (syncId, playerInventory, blockPos) -> {
            final BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
            assert blockEntity != null;
            BuiltScreenHandler screenHandler = ((BuiltScreenHandlerProvider) blockEntity).createScreenHandler(syncId, playerInventory.player);

            screenHandler.setType(screenHandlerType);

            return screenHandler;
        };
    }

    @Override
    public void open(Player player, BlockPos pos, Level world) {
        if (!world.isClientSide) {
            player.openMenu(new ExtendedScreenHandlerFactory<BlockPos>() {
                @Override
                public BlockPos getScreenOpeningData(ServerPlayer serverPlayer) {
                    return pos;
                }

                @Override
                public Component getDisplayName() {
                    return Component.literal("Charging Station");
                }

                @Override
                public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player menuPlayer) {
                    final BlockEntity blockEntity = menuPlayer.level().getBlockEntity(pos);
                    assert blockEntity != null;
                    BuiltScreenHandler screenHandler = ((BuiltScreenHandlerProvider) blockEntity).createScreenHandler(syncId, menuPlayer);
                    screenHandler.setType(screenHandlerType);
                    return screenHandler;
                }
            });
        }
    }
}
