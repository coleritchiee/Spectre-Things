package net.iicosahedra.spectrethings.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class ClientSidedProxy implements ISidedProxy {
    private final Minecraft mc = Minecraft.getInstance();


    @Nullable
    @Override
    public Player getClientPlayer() {
        return this.mc.player;
    }

    @Nullable
    @Override
    public ClientLevel getClientWorld() {
        return this.mc.level;
    }
}
