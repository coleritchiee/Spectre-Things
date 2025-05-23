package net.iicosahedra.spectrethings.entity;

import net.iicosahedra.spectrethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Attr;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class SpiritEntity extends Monster implements FlyingAnimal {

    private static final int MAX_LIFETIME = 20 * 20;
    private int lifeTicks = 0;
    private boolean killedBySpectreTool = false;

    private static final EntityDataAccessor<BlockPos> SPAWN_POS = SynchedEntityData.defineId(SpiritEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> FADING = SynchedEntityData.defineId(SpiritEntity.class, EntityDataSerializers.BOOLEAN);

    public SpiritEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 1;
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPAWN_POS, BlockPos.ZERO);
        builder.define(FADING, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D) 
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 16D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new WaterAvoidingRandomFlyingGoal(this, 1.2D));
        this.targetSelector.addGoal(1, new PanicGoal(this, 3));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    protected @NotNull PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LifeTicks", this.lifeTicks);
        compound.putLong("SpawnPosX", this.getEntityData().get(SPAWN_POS).getX());
        compound.putLong("SpawnPosY", this.getEntityData().get(SPAWN_POS).getY());
        compound.putLong("SpawnPosZ", this.getEntityData().get(SPAWN_POS).getZ());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.lifeTicks = compound.getInt("LifeTicks");
        long x = compound.getLong("SpawnPosX");
        long y = compound.getLong("SpawnPosY");
        long z = compound.getLong("SpawnPosZ");
        this.getEntityData().set(SPAWN_POS, new BlockPos((int)x, (int)y, (int)z));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            this.lifeTicks++;
            if (this.lifeTicks > MAX_LIFETIME) {
                this.getEntityData().set(FADING, true);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.NEUTRAL, 0.5f, 1.0f);
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    private boolean isSpectreWeapon(ItemStack itemStack) {
        return itemStack.is(Registration.SPECTRE_SWORD.value()) ||
               itemStack.is(Registration.SPECTRE_AXE.value()) ||
               itemStack.is(Registration.SPECTRE_PICKAXE.value()) ||
               itemStack.is(Registration.SPECTRE_SHOVEL.value());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source.is(DamageTypes.GENERIC_KILL) || source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
             return super.hurt(source, Float.MAX_VALUE);
        }

        if (source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.ARROW)) {
            // Check if attack is from a player with a Spectre weapon
            if (source.getEntity() instanceof Player player) {
                ItemStack heldItem = player.getMainHandItem();
                if (isSpectreWeapon(heldItem)) {
                    killedBySpectreTool = true;
                    return super.hurt(source, amount);
                }
            }
            
            // Otherwise, immune to normal attacks
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.NEUTRAL, 0.5f, 1.5f);
            return false;
        }

        if (source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC) || source.is(DamageTypes.WITHER)) {
            return super.hurt(source, amount);
        }

        return false;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean wasRecentlyHit) {
        super.dropCustomDeathLoot(level, source, wasRecentlyHit);

        if (!this.getEntityData().get(FADING)) {
            // Always drop at least one ectoplasm
            this.spawnAtLocation(Registration.ECTOPLASM.value());
            
            // If killed by Spectre tool, drop 1-3 more ectoplasm
            if (killedBySpectreTool) {
                int extraDrops = level.random.nextInt(3) + 1;
                for (int i = 0; i < extraDrops; i++) {
                    this.spawnAtLocation(Registration.ECTOPLASM.value());
                }
            } 
            // Otherwise small chance to drop one extra
            else if (level.random.nextFloat() < 0.1f) {
                this.spawnAtLocation(Registration.ECTOPLASM.value());
            }
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected boolean isAffectedByFluids() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
    public void setSpawnPos(BlockPos pos) {
        this.getEntityData().set(SPAWN_POS, pos);
    }

    @Override
    public boolean isNoGravity() {
        return true; 
    }

    @Override
    public boolean isFlying() {
        return true;
    }
}