package net.iicosahedra.spectrethings.setup;

import net.iicosahedra.spectrethings.SpectreThings;
import net.iicosahedra.spectrethings.block.*;
import net.iicosahedra.spectrethings.item.*;
import net.iicosahedra.spectrethings.util.ResourceLoc;
import net.iicosahedra.spectrethings.worldgen.dim.SpectreData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.iicosahedra.spectrethings.entity.SpiritEntity;
import net.neoforged.neoforge.registries.DeferredHolder;
import java.util.function.UnaryOperator;
import net.minecraft.core.component.DataComponentType;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.iicosahedra.spectrethings.attachment.KeptItemsData;
import net.minecraft.world.level.ItemLike;
import java.util.function.Function;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;


public class Registration {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpectreThings.MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SpectreThings.MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SpectreThings.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SpectreThings.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, SpectreThings.MODID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, SpectreThings.MODID);

    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);
        BLOCKS.register(modEventBus);
        CREATIVE_TAB.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        DATA_COMPONENT_TYPES.register(modEventBus);
    }

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> registerDataComponent(
            String name, UnaryOperator<DataComponentType.Builder<T>> builderUnaryOperator){
        return DATA_COMPONENT_TYPES.register(name, ()-> builderUnaryOperator.apply(DataComponentType.builder()).build());
    }

    //Data Components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ANCHORED = registerDataComponent("anchored",
            builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    //Item
    public static final Holder<Item> ECTOPLASM = ITEMS.register("ectoplasm", EctoplasmItem::new);
    public static final Holder<Item> SPECTRE_KEY = ITEMS.register("spectre_key", SpectreKeyItem::new);
    public static final Holder<Item> SPECTRE_INGOT = ITEMS.register("spectre_ingot", SpectreIngotItem::new);
    public static final Holder<Item> STABLE_ENDER_PEARL = ITEMS.register("stable_ender_pearl", StableEnderPearlItem::new);
    public static final Holder<Item> SPECTRE_ANCHOR = ITEMS.register("spectre_anchor", SpectreAnchorItem::new);
    
    //Tools
    public static final Holder<Item> SPECTRE_SWORD = ITEMS.register("spectre_sword", SpectreSwordItem::new);
    public static final Holder<Item> SPECTRE_AXE = ITEMS.register("spectre_axe", SpectreAxeItem::new);
    public static final Holder<Item> SPECTRE_PICKAXE = ITEMS.register("spectre_pickaxe", SpectrePickaxeItem::new);
    public static final Holder<Item> SPECTRE_SHOVEL = ITEMS.register("spectre_shovel", SpectreShovelItem::new);

    //Block
    public static final Holder<Block> SPECTRE_BLOCK = BLOCKS.register("spectre_block", SpectreBlockBlock::new);
    public static final DeferredItem<BlockItem> SPECTRE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(SPECTRE_BLOCK);

    public static final Holder<Block> SPECTRE_CORE = BLOCKS.register("spectre_core", SpectreCoreBlock::new);
    public static final DeferredItem<BlockItem> SPECTRE_CORE_ITEM = ITEMS.registerSimpleBlockItem(SPECTRE_CORE);

    //Feature
    public static final ResourceKey<ConfiguredFeature<?, ?>> SPECTRE_FEATURE_KEY = registerKey("spectre_tree");

    public static void bootstrapTree(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(context, SPECTRE_FEATURE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(SPECTRE_LOG.get()),
                new StraightTrunkPlacer(5, 2, 0),
                BlockStateProvider.simple(SPECTRE_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1))
                .ignoreVines()
                .build());
    }


    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(SpectreThings.MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }

    //Tree
    public static final DeferredBlock<SpectreLogBlock> SPECTRE_LOG = BLOCKS.register("spectre_log", SpectreLogBlock::new);
    public static final DeferredBlock<Block> SPECTRE_PLANKS = BLOCKS.register("spectre_planks", 
        () -> new Block(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .strength(2.0F, 3.0F)
            .sound(SoundType.WOOD)));
    public static final DeferredBlock<SpectreLeavesBlock> SPECTRE_LEAVES = BLOCKS.register("spectre_leaves", SpectreLeavesBlock::new);
    public static final DeferredBlock<SpectreSaplingBlock> SPECTRE_SAPLING = BLOCKS.register("spectre_sapling", SpectreSaplingBlock::new);

    public static final DeferredItem<BlockItem> SPECTRE_LOG_ITEM = ITEMS.registerSimpleBlockItem(SPECTRE_LOG);
    public static final DeferredItem<BlockItem> SPECTRE_PLANKS_ITEM = ITEMS.registerSimpleBlockItem(SPECTRE_PLANKS);
    public static final DeferredItem<BlockItem> SPECTRE_LEAVES_ITEM = ITEMS.registerSimpleBlockItem(SPECTRE_LEAVES);
    public static final DeferredItem<BlockItem> SPECTRE_SAPLING_ITEM = ITEMS.registerSimpleBlockItem(SPECTRE_SAPLING);

    public static final TreeGrower SPECTRE_TREE = new TreeGrower(SpectreThings.MODID + ":spectre_tree",
            Optional.empty(),
            Optional.of(SPECTRE_FEATURE_KEY),
            Optional.empty());

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
                .specialEffects(new BiomeSpecialEffects.Builder().skyColor(0x83CADE).fogColor(0x83CADE).waterColor(0x83CADE).waterFogColor(0x83CADE).build())
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

    public static final Supplier<AttachmentType<KeptItemsData>> KEPT_ITEMS_DATA =
            ATTACHMENT_TYPES.register("kept_items_data", () ->
                    AttachmentType.builder((holder) -> new KeptItemsData())
                            .serialize(KeptItemsData.CODEC)
                            .build()
            );

    //Tab
    public static final Supplier<CreativeModeTab> SPECTRE_THINGS_TAB = CREATIVE_TAB.register("spectre_things_tab", () ->
            CreativeModeTab.builder().icon(()->new ItemStack(SPECTRE_KEY.value()))
                    .title(Component.translatable("creativetab.spectre_things.general"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ECTOPLASM.value());
                        output.accept(SPECTRE_KEY.value());
                        output.accept(SPECTRE_INGOT.value());
                        output.accept(STABLE_ENDER_PEARL.value());
                        output.accept(SPECTRE_ANCHOR.value());
                        output.accept(SPECTRE_SWORD.value());
                        output.accept(SPECTRE_AXE.value());
                        output.accept(SPECTRE_PICKAXE.value());
                        output.accept(SPECTRE_SHOVEL.value());
                        output.accept(SPECTRE_CORE_ITEM.value());
                        output.accept(SPECTRE_BLOCK_ITEM.value());
                        output.accept(SPECTRE_LOG_ITEM.value());
                        output.accept(SPECTRE_PLANKS_ITEM.value());
                        output.accept(SPECTRE_SAPLING_ITEM.value());
                        output.accept(SPECTRE_LEAVES_ITEM.value());
                    })
                    .build());

    //Tree Configuration
    public static final ResourceKey<ConfiguredFeature<?, ?>> SPECTRE_TREE_CONFIG = 
        ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLoc.create("spectre_tree"));

    //Entity
    public static final DeferredHolder<EntityType<?>, EntityType<SpiritEntity>> SPIRIT = ENTITY_TYPES.register(
            "spirit",
            () -> EntityType.Builder.of(SpiritEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .build(ResourceLocation.fromNamespaceAndPath(SpectreThings.MODID, "spirit").toString())
    );
}