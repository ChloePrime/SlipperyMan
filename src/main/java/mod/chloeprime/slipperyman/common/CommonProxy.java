package mod.chloeprime.slipperyman.common;

import mod.chloeprime.slipperyman.client.ClientProxy;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.forgespi.Environment;

public class CommonProxy {
    public static boolean isJumping(Player player) {
        if (player.isShiftKeyDown() || player.getDeltaMovement().y <= 0) {
            return false;
        }
        if (player instanceof ServerPlayer) {
            return false;
        }
        return Environment.get().getDist().isClient() && ClientProxy.isJumping(player);
    }
}
