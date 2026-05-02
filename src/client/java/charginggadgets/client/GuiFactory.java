package charginggadgets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import reborncore.common.screen.BuiltScreenHandler;

@Environment(EnvType.CLIENT)
public interface GuiFactory<T extends BlockEntity> extends MenuScreens.ScreenConstructor<BuiltScreenHandler, AbstractContainerScreen<BuiltScreenHandler>> {
    AbstractContainerScreen<?> create(int syncId, Player playerEntity, T blockEntity);

    @Override
    @SuppressWarnings("unchecked")
    default AbstractContainerScreen<BuiltScreenHandler> create(BuiltScreenHandler builtScreenHandler, Inventory playerInventory, Component text) {
        Player playerEntity = playerInventory.player;
        T blockEntity = (T) builtScreenHandler.getBlockEntity();
        return (AbstractContainerScreen<BuiltScreenHandler>) create(builtScreenHandler.containerId, playerEntity, blockEntity);
    }
}
