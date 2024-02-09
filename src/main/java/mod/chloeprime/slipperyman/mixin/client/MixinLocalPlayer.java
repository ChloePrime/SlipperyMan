package mod.chloeprime.slipperyman.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = LocalPlayer.class, priority = 0)
public class MixinLocalPlayer {
    /**
     * @author ChloePrime
     * @reason Replaced with auto step up
     */
    @Overwrite
    private boolean canAutoJump() {
        return false;
    }
}
