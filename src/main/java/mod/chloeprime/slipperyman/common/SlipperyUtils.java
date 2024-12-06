package mod.chloeprime.slipperyman.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class SlipperyUtils {
    public static final Vec3 UP = new Vec3(0, 1, 0);

    /**
     * instanceof，但是避免在 mixin 类中直接使用 instanceof 并进行后续操作会导致 idea 产生 {@value "ConstantValue"} 警告的缺陷。
     * <p>
     * @param entity 要判断是否为 {@link Player} 的实体
     * @return entity 是否为 {@link Player}
     */
    public static boolean isPlayer(Entity entity) {
        return entity instanceof Player;
    }

    public static boolean isFallingPlayer(Entity entity) {
        return entity instanceof Player player && !player.onGround() && !player.getAbilities().flying;
    }

    public static void ifPlayer(Entity entity, Consumer<Player> action) {
        if (entity instanceof Player player) {
            action.accept(player);
        }
    }

    public static void ifFallingPlayer(Entity entity, Consumer<Player> action) {
        if (entity instanceof Player player && !player.onGround() && !player.getAbilities().flying) {
            action.accept(player);
        }
    }

    public static boolean isOnGroundPlayer(Entity entity) {
        return entity instanceof Player player && player.onGround();
    }
}
