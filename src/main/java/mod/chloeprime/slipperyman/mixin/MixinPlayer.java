package mod.chloeprime.slipperyman.mixin;

import mod.chloeprime.slipperyman.common.SlipperyUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity {
    @Override
    @SuppressWarnings("deprecation")
    public float getStepHeight() {
        if (!SlipperyUtils.isPlayer(this)) {
            return super.getStepHeight();
        }
        // 蹲下时会无视其他模组的 stepHeight
        if (isShiftKeyDown()) {
            return maxUpStep;
        }
        var recordedStepHeight = maxUpStep;
        if (recordedStepHeight >= 1) {
            return super.getStepHeight();
        }
        try {
            maxUpStep = isShiftKeyDown() ? 0.6F : 1F;
            return super.getStepHeight();
        } finally {
            maxUpStep = recordedStepHeight;
        }
    }

    protected MixinPlayer(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}
