package net.iicosahedra.spectrethings.worldgen.dim;

import net.iicosahedra.spectrethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpectreCube {
    private final UUID owner;
    private int height;
    private final int position;
    private final SimpleContainer inventory = new SimpleContainer(2 + 9);
    private BlockPos spawnPos;
    private final ServerLevel level;

    public SpectreCube(ServerLevel level, UUID owner, int position) {
        this.level = level;
        this.owner = owner;
        this.position = position;
        this.height = 4;
        this.spawnPos = new BlockPos(position * 16 + 8, 65, 8);
    }

    public void generate() {
        BlockPos corner = new BlockPos(position * 16, 64, 0);
        generateCube(corner, corner.offset(15, height, 15), Registration.SPECTRE_BLOCK.value().defaultBlockState());
        generateCube(corner.offset(7, 0, 7), corner.offset(8, 0, 8), Registration.SPECTRE_CORE.value().defaultBlockState());
    }

    private void generateCube(BlockPos min, BlockPos max, BlockState state) {
        BlockPos.betweenClosedStream(min, max).forEach(pos -> {
            if (pos.getX() == min.getX() || pos.getX() == max.getX() ||
                    pos.getY() == min.getY() || pos.getY() == max.getY() ||
                    pos.getZ() == min.getZ() || pos.getZ() == max.getZ()) {
                level.setBlock(pos, state, Block.UPDATE_ALL);
            }
        });
    }

    public int increaseHeight(int amount) {
        int maxAllowed = level.getMaxBuildHeight() - spawnPos.getY();
        int actualIncrease = Math.min(amount, maxAllowed - height);

        if (actualIncrease > 0) {
            clearOldWalls();
            height += actualIncrease;
            generate();
            return actualIncrease;
        }
        return 0;
    }

    private void clearOldWalls() {
        BlockPos oldMin = new BlockPos(position * 16, 64, 0);
        BlockPos oldMax = new BlockPos(position * 16 + 15, 64 + height, 15);
        generateCube(oldMin, oldMax, Blocks.AIR.defaultBlockState());
    }

    public CompoundTag save(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Owner", owner);
        tag.putInt("Position", position);
        tag.putInt("Height", height);
        tag.put("SpawnPos", NbtUtils.writeBlockPos(spawnPos));

        tag.put("Inventory", inventory.createTag(provider));
        return tag;
    }

    public static SpectreCube load(ServerLevel level, CompoundTag tag, HolderLookup.Provider provider) {
        SpectreCube plot = new SpectreCube(
                level,
                tag.getUUID("Owner"),
                tag.getInt("Position")
        );

        plot.height = tag.getInt("Height");
        Optional<BlockPos> spawn = NbtUtils.readBlockPos(tag, "SpawnPos");
        plot.spawnPos = spawn.orElseGet(()->new BlockPos(tag.getInt("Position") * 16 + 8, 65, 8));

        plot.inventory.fromTag(tag.getList("Inventory", Tag.TAG_COMPOUND), provider);
        return plot;
    }

    public boolean isPlayerInside(Player player) {
        BlockPos playerPos = player.blockPosition();

        int minX = this.position;
        int maxX = this.position + 16 - 1;
        int minZ = this.position;
        int maxZ = this.position + 16 - 1;
        int minY = 64;
        int maxY = 64 + this.height;

        return playerPos.getX() >= minX && playerPos.getX() <= maxX &&
                playerPos.getZ() >= minZ && playerPos.getZ() <= maxZ &&
                playerPos.getY() >= minY && playerPos.getY() <= maxY;
    }

    public MenuProvider getMenuProvider() {
        return new SimpleMenuProvider(
                (containerId, playerInv, player) ->
                        ChestMenu.threeRows(containerId, playerInv, inventory),
                Component.translatable("container.spectre_plot")
        );
    }

    public UUID getOwner() { return owner; }
    public BlockPos getSpawnPos() { return spawnPos; }
    public int getHeight() { return height; }
    public SimpleContainer getInventory() { return inventory; }
    public int getPosition() { return position; }
}