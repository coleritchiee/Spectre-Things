package net.iicosahedra.spectrethings.item;

import net.iicosahedra.spectrethings.setup.Registration;
import net.iicosahedra.spectrethings.worldgen.dim.SpectreData;
import net.iicosahedra.spectrethings.worldgen.dim.SpectreHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.Objects;

public class SpectreKeyItem extends Item {
    public SpectreKeyItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 100;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingTicks) {
        if (level.isClientSide && remainingTicks < 100) {
            spawnChargingParticles(entity, remainingTicks);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            handleTeleportation(player);
        }
        return stack;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        if (Minecraft.getInstance().player != null) {
            return Minecraft.getInstance().player.level().dimension() == Registration.SPECTRE_LEVEL_KEY;
        }
        return false;
    }
        private void handleTeleportation(ServerPlayer player) {
            if (player.level().dimension() == Registration.SPECTRE_LEVEL_KEY) {
                teleportBack(player);
            } else {
                teleportToSpectre(player);
            }
        }


        private void teleportToSpectre(ServerPlayer player) {
        /* ATTACHMENT HANDLED IN DIM CHANGE EVENT NOW
            player.setData(Registration.SPECTRE_DATA,
                    new SpectreData(
                            player.level().dimension(),
                            player.blockPosition()
                    )
            );

         */

            ServerLevel spectreLevel = player.server.getLevel(Registration.SPECTRE_LEVEL_KEY);
            if (spectreLevel != null) {
                SpectreHandler handler = SpectreHandler.get(Objects.requireNonNull(player.getServer()));
                if (handler != null) {
                    handler.teleportToCube(player);
                }
            }
        }

    private void teleportBack(ServerPlayer player) {
        SpectreData data = player.getData(Registration.SPECTRE_DATA);
        ServerLevel targetLevel = player.server.getLevel(data.originalDimension());

        if (targetLevel != null) {
            player.teleportTo(
                    targetLevel,
                    data.originalPosition().getX() + 0.5,
                    data.originalPosition().getY(),
                    data.originalPosition().getZ() + 0.5,
                    player.getYRot(),
                    player.getXRot()
            );
        }
    }


    @OnlyIn(Dist.CLIENT)
    private void spawnChargingParticles(LivingEntity entity, int remainingTicks) {
        Minecraft mc = Minecraft.getInstance();
        int particlesToSpawn = (100 - remainingTicks) * 2;
        float r = ((0x7AC5CD >> 16) & 0xFF) / 255f;
        float g = ((0x7AC5CD >> 8) & 0xFF) / 255f;
        float b = (0x7AC5CD & 0xFF) / 255f;

        for (int i = 0; i < particlesToSpawn; i++) {
            double x = entity.getX() + (Math.random() * 1.8 - 0.9);
            double y = entity.getY() + Math.random() * 1.8;
            double z = entity.getZ() + (Math.random() * 1.8 - 0.9);

            mc.level.addParticle(
                    new DustParticleOptions(new Vector3f(r, g, b), 1),
                    x, y, z,
                    0, 0, 0
            );
        }
    }
}
