package net.iicosahedra.spectrethings.setup;

import net.iicosahedra.spectrethings.SpectreThings;
import net.iicosahedra.spectrethings.block.SpectreBlockBlock;
import net.iicosahedra.spectrethings.block.SpectreCoreBlock;
import net.iicosahedra.spectrethings.item.*;
import net.iicosahedra.spectrethings.util.ResourceLoc;
import net.iicosahedra.spectrethings.worldgen.biome.SpectralEffects;
import net.iicosahedra.spectrethings.worldgen.dim.SpectreData;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;


public class Registration {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpectreThings.MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SpectreThings.MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SpectreThings.MODID);

    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);
        BLOCKS.register(modEventBus);

    }

    /*
    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(
            String name, UnaryOperator<DataComponentType.Builder<T>> builderUnaryOperator){
        return DATA_TYPES.register(name, ()-> builderUnaryOperator.apply(DataComponentType.builder()).build());
    }
    */

    //Item
    public static final Holder<Item> ECTOPLASM = ITEMS.register("ectoplasm", EctoplasmItem::new);
    public static final Holder<Item> SPECTRE_KEY = ITEMS.register("spectre_key", SpectreKeyItem::new);
    public static final Holder<Item> SPECTRE_INGOT = ITEMS.register("spectre_ingot", SpectreIngotItem::new);
    public static final Holder<Item> STABLE_ENDER_PEARL = ITEMS.register("stable_ender_pearl", StableEnderPearlItem::new);

    //Block
    public static final Holder<Block> SPECTRE_BLOCK = BLOCKS.register("spectre_block", SpectreBlockBlock::new);

    public static final Holder<Block> SPECTRE_CORE = BLOCKS.register("spectre_core", SpectreCoreBlock::new);

    //Dim
    public static final ResourceKey<LevelStem> SPECTRE_DIM_KEY = ResourceKey.create(Registries.LEVEL_STEM, ResourceLoc.create("spectre_dim"));
    public static final ResourceKey<Level> SPECTRE_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION, ResourceLoc.create("spectre_dim"));
    public static final ResourceKey<DimensionType> SPECTRE_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, ResourceLoc.create("spectre_dim_type"));
    public static final ResourceLocation SPECTRE_EFFECTS = ResourceLoc.create("spectre_effects");

    public static void bootstrapType(BootstrapContext<DimensionType> context){
        context.register(SPECTRE_DIM_TYPE, new DimensionType(
                OptionalLong.of(6000),
                true,
                false,
                false,
                false,
                1,
                false,
                false,
                0,
                256,
                256,
                BlockTags.INFINIBURN_OVERWORLD,
                SPECTRE_EFFECTS,
                1,
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0)
        ));
    }

    public static void bootstrapStem(BootstrapContext<LevelStem> context){
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimType = context.lookup(Registries.DIMENSION_TYPE);
        FlatLevelGeneratorSettings settings = new FlatLevelGeneratorSettings(
                Optional.empty(),
                biomeRegistry.getOrThrow(Registration.SPECTRAL_BIOME),
                List.of()
        );
        ChunkGenerator chunkGenerator = new FlatLevelSource(settings);

        context.register(SPECTRE_DIM_KEY, new LevelStem(
                dimType.getOrThrow(SPECTRE_DIM_TYPE),
                chunkGenerator
        ));
    }

    //Biome
    public static final ResourceKey<Biome> SPECTRAL_BIOME = ResourceKey.create(Registries.BIOME, ResourceLoc.create("spectral_biome"));

    public static void bootstrapBiome(BootstrapContext<Biome> context){
        context.register(SPECTRAL_BIOME, spectralBiome(context));
    }

    private static Biome spectralBiome(BootstrapContext<Biome> context) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .downfall(0f)
                .temperature(0.2f)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .specialEffects(new BiomeSpecialEffects.Builder().skyColor(0x7AC5CD).fogColor(0x7AC5CD).waterColor(0x7AC5CD).waterFogColor(0x7AC5CD).build())
                .build();
    }

    //Attachments
    public static final Supplier<AttachmentType<SpectreData>> SPECTRE_DATA =
            ATTACHMENT_TYPES.register("spectre_data", () ->
                    AttachmentType.builder(SpectreData::new)
                            .serialize(SpectreData.CODEC)
                            .copyOnDeath()
                            .build()
            );
}