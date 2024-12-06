package mod.chloeprime.slipperyman.mixin;

import mod.chloeprime.slipperyman.common.CommonProxy;
import mod.chloeprime.slipperyman.common.SlipperyUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity {
    @Override
    public float getStepHeight() {
        if (!SlipperyUtils.isPlayer(this) || !CommonProxy.canStepUp(this)) {
            return super.getStepHeight();
        }
        float vanillaStep = maxUpStep();
        // 蹲下时会无视其他模组的 stepHeight
        if (isShiftKeyDown()) {
            return vanillaStep;
        }
        if (vanillaStep >= 1) {
            return super.getStepHeight();
        }
        try {
            super.setMaxUpStep(isShiftKeyDown() ? 0.6F : 1F);
            return super.getStepHeight();
        } finally {
            super.setMaxUpStep(vanillaStep);
        }
    }

    protected MixinPlayer(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}
