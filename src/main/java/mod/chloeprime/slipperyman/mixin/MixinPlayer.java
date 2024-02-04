package mod.chloeprime.slipperyman.mixin;

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
        var recordedStepHeight = maxUpStep;
        if (recordedStepHeight >= 1) {
            return super.getStepHeight();
        }
        try {
            maxUpStep = 1;
            return super.getStepHeight();
        } finally {
            maxUpStep = recordedStepHeight;
        }
    }

    protected MixinPlayer(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}
