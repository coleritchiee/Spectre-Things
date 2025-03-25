package net.iicosahedra.spectrethings.worldgen.dim;

import net.iicosahedra.spectrethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpectreHandler extends SavedData {
    public static final String ID = "spectre_handler";
    public static final int PLOT_SPACING = 32;

    private final Map<UUID, SpectreCube> playerCubes = new HashMap<>();
    private int nextPosition;
    private final ServerLevel level;

    public SpectreHandler(MinecraftServer server) {
        this.level = server.getLevel(Registration.SPECTRE_LEVEL_KEY);
    }

    public static SpectreHandler get(MinecraftServer server) {
        return server.getLevel(Registration.SPECTRE_LEVEL_KEY).getDataStorage().computeIfAbsent(
                new Factory<SpectreHandler>(
                        () -> new SpectreHandler(server),
                        (tag, provider) -> SpectreHandler.load(tag, server, provider)),
                        ID
                );
    }

    private static SpectreHandler load(CompoundTag tag, MinecraftServer server, HolderLookup.Provider provider) {
        SpectreHandler handler = new SpectreHandler(server);
        handler.nextPosition = tag.getInt("NextPosition");

        ListTag cubesTag = tag.getList("Cubes", Tag.TAG_COMPOUND);
        cubesTag.forEach(cubeTag -> {
            SpectreCube cube = SpectreCube.load(server.getLevel(Registration.SPECTRE_LEVEL_KEY),(CompoundTag) cubeTag,provider);
            handler.playerCubes.put(cube.getOwner(), cube);
        });
        return handler;
    }
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putInt("NextPosition", nextPosition);

        ListTag cubesTag = new ListTag();
        playerCubes.values().forEach(cube ->
                cubesTag.add(cube.save(provider))
        );
        tag.put("Cubes", cubesTag);
        return tag;
    }

    public void teleportToCube(ServerPlayer player) {
        UUID uuid = player.getUUID();
        SpectreCube cube = playerCubes.computeIfAbsent(uuid, k -> {
            SpectreCube newCube = new SpectreCube(level, uuid, nextPosition);
            newCube.generate();
            nextPosition += PLOT_SPACING;
            setDirty();
            return newCube;
        });

        storePlayerData(player);
        teleportPlayer(player, cube.getSpawnPos());
    }

    private void storePlayerData(ServerPlayer player) {
        player.setData(Registration.SPECTRE_DATA,
                new SpectreData(
                        player.level().dimension(),
                        player.blockPosition()
                )
        );
    }

    private void teleportPlayer(ServerPlayer player, BlockPos pos) {
        player.teleportTo(
                level,
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()
        );
    }

    public void checkAccess(ServerPlayer player) {
        if (player.gameMode.isCreative()) return;

        SpectreCube cube = getCurrentCube(player.blockPosition());
        SpectreCube playerCube = playerCubes.get(player.getUUID());
        if (cube == null || !cube.equals(playerCube) || !isPlayerInCube(player, playerCube)){
            if (playerCube != null) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40));
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40));
                teleportPlayer(player, playerCube.getSpawnPos());
            } else {
                teleportBack(player);
            }
        }
    }

    public boolean isPlayerInCube(ServerPlayer player, SpectreCube cube) {
        return cube.isPlayerInside(player);
    }

    public SpectreCube getCurrentCube(BlockPos pos) {
        int gridX = pos.getX() / PLOT_SPACING;
        int gridZ = pos.getZ() / PLOT_SPACING;

        return playerCubes.values().stream()
                .filter(cube ->
                        cube.getPosition() / PLOT_SPACING == gridX &&
                                cube.getPosition() / PLOT_SPACING == gridZ)
                .findFirst()
                .orElse(null);
    }

    public void teleportBack(ServerPlayer player) {
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
            player.removeData(Registration.SPECTRE_DATA);
        }
    }

    public SpectreCube getPlayerCube(UUID playerId) {
        return playerCubes.get(playerId);
    }
}