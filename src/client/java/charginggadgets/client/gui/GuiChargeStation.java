package charginggadgets.client.gui;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import charginggadgets.ChargingGadgets;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import reborncore.client.gui.GuiBase;
import reborncore.common.screen.BuiltScreenHandler;

public class GuiChargeStation extends GuiBase<BuiltScreenHandler> {
    private static final ResourceLocation background = ResourceLocation.fromNamespaceAndPath(ChargingGadgets.MOD_ID, "textures/gui/charging_station.png");

    // Energy bar bounds (relative to GUI origin)
    private static final int ENERGY_BAR_X = 8;
    private static final int ENERGY_BAR_Y_TOP = 8;   // topPos + (78 - 70)
    private static final int ENERGY_BAR_Y_BOTTOM = 78;
    private static final int ENERGY_BAR_WIDTH = 16;

    ChargingStationBlockEntity blockEntity;

    public GuiChargeStation(int syncID, final Player player, final ChargingStationBlockEntity blockEntity) {
        super(player, blockEntity, blockEntity.createScreenHandler(syncID, player));
        this.blockEntity = blockEntity;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, final float f, final int mouseX, final int mouseY) {
        super.renderBg(guiGraphics, f, mouseX, mouseY);

        guiGraphics.blit(background, getGuiLeft(), getGuiTop(), 0, 0, this.imageWidth, 80);

        int maxHeight = 13;
        if (this.blockEntity.totalBurnTime > 0) {
            int remaining = (this.blockEntity.burnTime * maxHeight) / this.blockEntity.totalBurnTime;
            guiGraphics.blit(background, getGuiLeft() + 66, getGuiTop() + 26 + 13 - remaining, 176, 13 - remaining, 14, remaining + 1);
        }

        int maxEnergy = (int) this.blockEntity.getMaxStoredPower(), height = 70;

        if (maxEnergy > 0) {
            int remaining = (int) ((this.blockEntity.getEnergy() * height) / maxEnergy);
            guiGraphics.blit(background, getGuiLeft() + 8, getGuiTop() + 78 - remaining, 176, 84 - remaining, 16, remaining + 1);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        int barLeft   = getGuiLeft()  + ENERGY_BAR_X;
        int barRight  = barLeft       + ENERGY_BAR_WIDTH;
        int barTop    = getGuiTop()   + ENERGY_BAR_Y_TOP;
        int barBottom = getGuiTop()   + ENERGY_BAR_Y_BOTTOM;

        if (mouseX >= barLeft && mouseX <= barRight && mouseY >= barTop && mouseY <= barBottom) {
            long energy    = this.blockEntity.getEnergy();
            long maxEnergy = this.blockEntity.getMaxStoredPower();
            guiGraphics.renderTooltip(font,
                Component.literal(String.format("%,d / %,d E", energy, maxEnergy)),
                mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
    }
}
