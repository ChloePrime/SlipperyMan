package mod.chloeprime.slipperyman.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public class ClientProxy {
    public static boolean isJumping(Player player) {
        if (!(player instanceof LocalPlayer localPlayer)) {
            return false;
        }
        return localPlayer.input.jumping;
    }
}
