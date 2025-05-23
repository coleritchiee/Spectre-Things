package net.iicosahedra.spectrethings.event;

import net.iicosahedra.spectrethings.SpectreThings;
import net.iicosahedra.spectrethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.iicosahedra.spectrethings.entity.SpiritEntity;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.minecraft.world.entity.item.ItemEntity;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.iicosahedra.spectrethings.attachment.KeptItemsData;

@EventBusSubscriber(modid = SpectreThings.MODID, bus = EventBusSubscriber.Bus.GAME)
public class SpectreEvents {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        
        if (stack.is(Registration.ECTOPLASM.value())) {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof SaplingBlock) {
                state.getBlock();
                if (!level.isClientSide) {
                    level.setBlock(pos, Registration.SPECTRE_SAPLING.value().defaultBlockState(), 3);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.level();

        if (level.isClientSide || !(level instanceof ServerLevel serverLevel) || entity instanceof SpiritEntity) {
            return;
        }

        float chance = 1.0f;

        if (serverLevel.getDragonFight() != null && serverLevel.getDragonFight().hasPreviouslyKilledDragon()) {
            chance += 6.0f;
        }

        BlockPos deathPos = entity.blockPosition();
        if (level.canSeeSky(deathPos)) {
            int moonPhase = level.getMoonPhase();
            float moonBonusPercent = 3.0f * (float)Math.cos(Math.PI * moonPhase / 4.0) * 0.5f + 1.5f;
            chance += moonBonusPercent;
        }

        if (level.random.nextFloat() * 100.0f < chance) {
            SpiritEntity spirit = Registration.SPIRIT.get().create(level);
            if (spirit != null) {
                spirit.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                spirit.setSpawnPos(deathPos);
                level.addFreshEntity(spirit);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }
        Player originalPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();
        KeptItemsData keptData = originalPlayer.getData(Registration.KEPT_ITEMS_DATA);
        if (keptData != null && !keptData.items().isEmpty()) {
            List<ItemStack> itemsToRestore = keptData.items(); 
            for (ItemStack stack : itemsToRestore) {
                if (!newPlayer.getInventory().add(stack)) {
                    newPlayer.drop(stack, true, false);
                }
            }
            originalPlayer.removeData(Registration.KEPT_ITEMS_DATA);
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof Player player) {
            Iterator<ItemEntity> iterator = event.getDrops().iterator();
            while (iterator.hasNext()) {
                ItemEntity itemEntity = iterator.next();
                ItemStack stack = itemEntity.getItem();
                if (stack.has(Registration.ANCHORED.get()) && stack.getOrDefault(Registration.ANCHORED.get(), false)) {
                    KeptItemsData data = player.getData(Registration.KEPT_ITEMS_DATA);
                    if (data == null) {
                        data = new KeptItemsData();
                        player.setData(Registration.KEPT_ITEMS_DATA, data);
                    }
                    data.items().add(stack.copy());

                    iterator.remove(); 
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack leftStack = event.getLeft();
        ItemStack rightStack = event.getRight();
        Player player = event.getPlayer();

        if (leftStack.isEmpty() || rightStack.isEmpty()) {
            return;
        }

        if (!rightStack.is(Registration.SPECTRE_ANCHOR.value())) {
            return;
        }

        if (leftStack.has(Registration.ANCHORED.get()) && leftStack.getOrDefault(Registration.ANCHORED.get(), false)) {
            return;
        }

        ItemStack outputStack = leftStack.copy();
        outputStack.set(Registration.ANCHORED.get(), true);

        event.setOutput(outputStack);
        event.setCost(5);
        event.setMaterialCost(1);
    }
} 