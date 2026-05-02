package charginggadgets.client;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import charginggadgets.blocks.GuiType;
import charginggadgets.client.gui.GuiChargeStation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ClientGuiType<T extends BlockEntity> {
    public static final Map<ResourceLocation, ClientGuiType<?>> TYPES = new HashMap<>();

    public static final ClientGuiType<ChargingStationBlockEntity> CHARGING_STATION = register(GuiType.CHARGING_STATION, GuiChargeStation::new);

    private static <T extends BlockEntity> ClientGuiType<T> register(GuiType<T> type, GuiFactory<T> factory) {
        return new ClientGuiType<>(type, factory);
    }

    public static void validate() {
        for (ResourceLocation identifier : GuiType.TYPES.keySet()) {
            Objects.requireNonNull(TYPES.get(identifier), "No ClientGuiType for " + identifier);
        }
    }

    private final GuiFactory<T> guiFactory;

    public ClientGuiType(GuiType<T> guiType, GuiFactory<T> guiFactory) {
        Objects.requireNonNull(guiType);
        this.guiFactory = Objects.requireNonNull(guiFactory);

        MenuScreens.register(guiType.getScreenHandlerType(), getGuiFactory());

        TYPES.put(guiType.getResourceLocation(), this);
    }

    public GuiFactory<T> getGuiFactory() {
        return guiFactory;
    }
}
