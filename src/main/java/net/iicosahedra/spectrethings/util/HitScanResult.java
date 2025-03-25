package net.iicosahedra.spectrethings.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class HitScanResult {
    public static EntityHitResult getPlayerPOVHitResult(Player player, double range) {
        Vec3 eyePosition = player.getEyePosition(1.0F);
        Vec3 lookVector = player.getLookAngle().scale(range);
        Vec3 rayEnd = eyePosition.add(lookVector);

        AABB rayBoundingBox = new AABB(eyePosition, rayEnd).inflate(1.0D);

        for (Entity entity : player.level().getEntities(player, rayBoundingBox, e -> e instanceof LivingEntity)) {
            AABB entityBoundingBox = entity.getBoundingBox().inflate(0.5D);
            if (entityBoundingBox.clip(eyePosition, rayEnd).isPresent()) {
                return new EntityHitResult(entity);
            }
        }
        return null;
    }

    public static List<LivingEntity> getEntitiesWithinSphere(Player player, double radius) {
        Level level = player.level();
        Vec3 center = player.position();
        AABB sphereBoundingBox = new AABB(
                center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius
        );

        return level.getEntitiesOfClass(LivingEntity.class, sphereBoundingBox, entity -> {
            double distance = entity.position().distanceTo(center);
            return distance <= radius && entity != player;
        });
    }
}
