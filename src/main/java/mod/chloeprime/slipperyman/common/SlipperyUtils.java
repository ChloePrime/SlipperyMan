package mod.chloeprime.slipperyman.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class SlipperyUtils {
    public static final Vec3 UP = new Vec3(0, 1, 0);

    public static boolean isPlayer(Entity entity) {
        return entity instanceof Player;
    }

    public static boolean isFallingPlayer(Entity entity) {
        return entity instanceof Player player && !player.isOnGround() && !player.getAbilities().flying;
    }

    public static void ifPlayer(Entity entity, Consumer<Player> action) {
        if (entity instanceof Player player) {
            action.accept(player);
        }
    }

    public static void ifFallingPlayer(Entity entity, Consumer<Player> action) {
        if (entity instanceof Player player && !player.isOnGround() && !player.getAbilities().flying) {
            action.accept(player);
        }
    }

    public static boolean isOnGroundPlayer(Entity entity) {
        return entity instanceof Player player && player.isOnGround();
    }
}
