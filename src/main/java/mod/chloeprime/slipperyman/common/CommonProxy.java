package mod.chloeprime.slipperyman.common;

import mod.chloeprime.slipperyman.client.ClientProxy;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.forgespi.Environment;

public class CommonProxy {
    public static final boolean IS_CLIENT = Environment.get().getDist().isClient();

    public static boolean isJumping(Player player) {
        if (player.isShiftKeyDown() || player.getDeltaMovement().y <= 0) {
            return false;
        }
        if (player instanceof ServerPlayer) {
            return false;
        }
        return IS_CLIENT && ClientProxy.isJumping(player);
    }

    public static boolean canStepUp(Entity player) {
        return !IS_CLIENT || ClientProxy.canStepUp(player);
    }
}
