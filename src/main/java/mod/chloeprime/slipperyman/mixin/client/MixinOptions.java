package mod.chloeprime.slipperyman.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(Options.class)
public class MixinOptions {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void disableAutoJumpByDefault(Minecraft pMinecraft, File pGameDirectory, CallbackInfo ci) {
        autoJump = false;
    }

    @Shadow public boolean autoJump;
}
