package charginggadgets.client.gui;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import reborncore.client.gui.GuiBase;
import reborncore.common.screen.BuiltScreenHandler;

public class GuiChargeStation extends GuiBase<BuiltScreenHandler> {

    ChargingStationBlockEntity blockEntity;

    public GuiChargeStation(int syncID, final Player player, final ChargingStationBlockEntity blockEntity) {
        super(player, blockEntity, blockEntity.createScreenHandler(syncID, player));
        this.blockEntity = blockEntity;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, final float f, final int mouseX, final int mouseY) {
        super.renderBg(guiGraphics, f, mouseX, mouseY);

        this.drawSlot(guiGraphics, 65, 43, GuiBase.Layer.BACKGROUND);
        this.drawSlot(guiGraphics, 119, 43, GuiBase.Layer.BACKGROUND);

        this.builder.drawMultiEnergyBar(guiGraphics, this,
                8, 18,
                (int) this.blockEntity.getEnergy(),
                (int) this.blockEntity.getMaxStoredPower(),
                mouseX, mouseY, 0,
                GuiBase.Layer.BACKGROUND);

        this.builder.drawBurnBar(guiGraphics, this,
                this.blockEntity.burnTime,
                this.blockEntity.totalBurnTime,
                67, 26,
                mouseX, mouseY,
                GuiBase.Layer.BACKGROUND);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        this.builder.drawMultiEnergyBar(guiGraphics, this,
                8, 18,
                (int) this.blockEntity.getEnergy(),
                (int) this.blockEntity.getMaxStoredPower(),
                mouseX, mouseY, 0,
                GuiBase.Layer.FOREGROUND);
    }
}