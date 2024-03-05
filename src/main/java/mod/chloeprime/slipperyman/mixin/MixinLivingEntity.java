package mod.chloeprime.slipperyman.mixin;

import mod.chloeprime.slipperyman.common.CommonProxy;
import mod.chloeprime.slipperyman.common.SlipperyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static java.lang.Math.abs;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    @Shadow public abstract float getSpeed();

    @Unique private Vec3 slipperyMan$capturedSpeedBeforeSpeedDown = Vec3.ZERO;
    @Unique private double slipperyMan$capturedJumpStrength;

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;<init>(Ljava/util/UUID;Ljava/lang/String;DLnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;)V"))
    private static double modifySprintBoost(double original) {
        return 0.5;
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"))
    private void faceTowardsLookOnJump(CallbackInfo ci) {
        SlipperyUtils.ifPlayer(this, p -> {
            var front = slipperyMan$getFront();
            if (front == Vec3.ZERO) {
                return;
            }
            var left = SlipperyUtils.UP.cross(front);

            var motion = getDeltaMovement();
            var hMotion = motion.with(Direction.Axis.Y, 0);
            var zSpeed = abs(hMotion.dot(front));
            var xSpeed = abs(hMotion.dot(left));
            var newZMotion = front.scale(zSpeed * zza);
            var newXMotion = left.scale(xSpeed * xxa);
            setDeltaMovement(new Vec3(newXMotion.x + newZMotion.x, motion.y, newXMotion.z + newZMotion.z));
        });
    }

    @Inject(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSprinting()Z"))
    private void captureSpeedBeforeSpeedDown(CallbackInfo ci) {
        SlipperyUtils.ifPlayer(this, p -> slipperyMan$capturedSpeedBeforeSpeedDown = getDeltaMovement());
    }

    @Inject(method = "jumpFromGround", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 0))
    private void doNotSlowDownWhenSprintJumping(CallbackInfo ci) {
        SlipperyUtils.ifPlayer(this, p -> setDeltaMovement(slipperyMan$capturedSpeedBeforeSpeedDown));
    }

    @Unique private boolean slipperyMan$discardFrictionBefore;

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;shouldDiscardFriction()Z"))
    private void doNotSlowDownWhenInAir(Vec3 pTravelVector, CallbackInfo ci) {
        SlipperyUtils.ifFallingPlayer(this, player -> {
            slipperyMan$discardFrictionBefore = shouldDiscardFriction();
            var slippery = !isOnGround() && (abs(player.xxa) > 1e-3 || abs(player.zza) > 1e-3);
            setDiscardFriction(slippery);
        });
    }

    @Inject(method = "travel", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/LivingEntity;shouldDiscardFriction()Z"))
    private void recoverDiscardFriction(Vec3 pTravelVector, CallbackInfo ci) {
        SlipperyUtils.ifFallingPlayer(this, player -> {
            player.setDiscardFriction(slipperyMan$discardFrictionBefore);
        });
    }

    @Redirect(
            method = "getFrictionInfluencedSpeed",
            at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/entity/LivingEntity;onGround:Z")
    )
    private boolean doNotNerfInAirControl(LivingEntity self) {
        return self instanceof Player player ? !player.getAbilities().flying : self.isOnGround();
    }

    @ModifyArg(
            method = "handleRelativeFrictionAndCalculateMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V"),
            index = 1
    )
    private Vec3 doNotAccelerateInfinitely(Vec3 moveInput) {
        if ((Object) this instanceof Player player) {
            if (!SlipperyUtils.isFallingPlayer(player)) {
                return moveInput;
            }
            var front = slipperyMan$getFront();
            var right = front.cross(SlipperyUtils.UP);
            var speed = getSpeed();
            var motionH = getDeltaMovement().with(Direction.Axis.Y, 0);
            double speedX, speedZ;

            if (front.lengthSqr() < 1e-8) {
                speedZ = motionH.length();
            } else {
                speedZ = motionH.dot(front) / front.length();
            }
            if (abs(speedZ) > speed && Mth.sign(moveInput.z) == Mth.sign(speedZ)) {
                moveInput = moveInput.with(Direction.Axis.Z, 0);
            }

            if (right.lengthSqr() < 1e-8) {
                speedX = motionH.length();
            } else {
                speedX = motionH.dot(right) / right.length();
            }
            if (abs(speedX) > speed && Mth.sign(moveInput.x) + Mth.sign(speedX) == 0) {
                moveInput = moveInput.with(Direction.Axis.X, 0);
            }
        }
        return moveInput;
    }

    @ModifyConstant(method = "getJumpPower", constant = @Constant(floatValue = 0.42F))
    private float modifyBaseJumpPower(float constant) {
        if ((Object) this instanceof Player) {
            return 0.47F;
        }
        return constant;
    }

    @Inject(method = "jumpFromGround", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V"))
    private void captureJumpStrength(CallbackInfo ci) {
        slipperyMan$capturedJumpStrength = getDeltaMovement().y;
    }

    @ModifyVariable(
            method = "travel",
            at = @At(value = "INVOKE_ASSIGN", ordinal = 0, target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;getValue()D")
    )
    @SuppressWarnings("ConstantValue")
    private double modifyGravity(double gravity) {
        if (!((Object) this instanceof Player player)) {
            return gravity;
        }
        if (player.getAbilities().flying) {
            return gravity;
        }
        if (CommonProxy.isJumping(player)) {
            var landGravityScale = (player.isSprinting() ? 0.2 : 0.25);
            var jumpStrength = slipperyMan$capturedJumpStrength;
            if (abs(jumpStrength) < 1e-7) {
                return gravity * landGravityScale;
            }
            return gravity * Mth.lerp(abs(getDeltaMovement().y) / jumpStrength, 1, landGravityScale);
        }
        return gravity;
    }

    @Inject(method = "checkFallDamage", at = @At("HEAD"))
    private void limitSpeedOnFallAndReduceFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos, CallbackInfo ci) {
        // limitSpeedOnFall
        if (pOnGround && fallDistance > 0) {
            var motion = getDeltaMovement();
            var motionH = motion.with(Direction.Axis.Y, 0);
            if (speed > 1e-4 && motionH.lengthSqr() > speed * speed) {
                var newMotionH = motionH.normalize().scale(speed);
                setDeltaMovement(new Vec3(newMotionH.x, motion.y, newMotionH.z));
            }
        }

        // reduceFallDamage
        if (SlipperyUtils.isPlayer(this) && fallDistance > 3 && pOnGround) {
            fallDistance /= 2;
        }
    }

    public MixinLivingEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Unique
    private Vec3 slipperyMan$getFront() {
        var yRot = getYRot();
        return new Vec3(-Mth.sin((float)(Math.toRadians(yRot))), 0, Mth.cos((float)(Math.toRadians(yRot))));
    }

    @Shadow public float xxa;
    @Shadow public float zza;
    @Shadow private float speed;
    @Shadow public abstract void setDiscardFriction(boolean pDiscardFriction);
    @Shadow public abstract boolean shouldDiscardFriction();
}
