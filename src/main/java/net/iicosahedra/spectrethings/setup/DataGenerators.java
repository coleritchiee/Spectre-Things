package net.iicosahedra.spectrethings.setup;

import net.iicosahedra.spectrethings.SpectreThings;
import net.iicosahedra.spectrethings.block.SpectreBlockBlock;
import net.iicosahedra.spectrethings.block.SpectreLeavesBlock;
import net.iicosahedra.spectrethings.block.SpectreSaplingBlock;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.*;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.common.data.*;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import net.iicosahedra.spectrethings.entity.SpiritEntity;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@EventBusSubscriber(modid = SpectreThings.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ModBlockTagProvider blockTagProvider = new ModBlockTagProvider(packOutput, lookupProvider, helper);
        gen.addProvider(event.includeServer(), blockTagProvider);
        gen.addProvider(event.includeServer(), new ModItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), helper));

        gen.addProvider(event.includeClient(), new ModBlockStates(gen, event.getExistingFileHelper()));
        gen.addProvider(event.includeClient(), new ModLanguageProvider(packOutput, "en_us"));
        gen.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, helper));
        gen.addProvider(event.includeServer(), new ModGlobalLootModifierProvider(packOutput, event.getLookupProvider()));
        gen.addProvider(event.includeServer(), new ModWorldGenProvider(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
    }

}
class ModBlockStates extends BlockStateProvider {
    public ModBlockStates(DataGenerator gen, ExistingFileHelper helper){
        super(gen.getPackOutput(), SpectreThings.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels(){
        simpleBlock(Registration.SPECTRE_BLOCK.value());
        simpleBlock(Registration.SPECTRE_CORE.value());
        

        logBlock(Registration.SPECTRE_LOG.value());
        simpleBlock(Registration.SPECTRE_PLANKS.value());
        leavesBlock(Registration.SPECTRE_LEAVES);
        saplingBlock(Registration.SPECTRE_SAPLING);
    }

    private void saplingBlock(DeferredBlock<SpectreSaplingBlock> blockRegistryObject) {
        simpleBlock(blockRegistryObject.get(),
                models().cross(BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(), blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    private void leavesBlock(DeferredBlock<SpectreLeavesBlock> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(),
                models().singleTexture(BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(), ResourceLocation.parse("minecraft:block/leaves"),
                        "all", blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }
}

class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Registration.SPECTRE_PLANKS.get(), 4)
                .requires(Registration.SPECTRE_LOG.get())
                .group(SpectreThings.MODID)
                .unlockedBy(getHasName(Registration.SPECTRE_LOG), has(Registration.SPECTRE_LOG.get())).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.SPECTRE_INGOT.value(), 1)
                .pattern(" B ")
                .pattern(" C ")
                .pattern(" D ")
                .define('B', Items.LAPIS_LAZULI)
                .define('C', Items.GOLD_INGOT)
                .define('D', Registration.ECTOPLASM.value())
                .group(SpectreThings.MODID)
                .unlockedBy(getHasName(Registration.ECTOPLASM.value()), has(Registration.ECTOPLASM.value()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.SPECTRE_KEY.value(), 1)
                .pattern("A  ")
                .pattern("AB ")
                .pattern("  A")
                .define('A', Registration.SPECTRE_INGOT.value())
                .define('B', Registration.STABLE_ENDER_PEARL.value())
                .group(SpectreThings.MODID)
                .unlockedBy(getHasName(Registration.SPECTRE_INGOT.value()), has(Registration.SPECTRE_INGOT.value()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.STABLE_ENDER_PEARL.value(), 1)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', Items.OBSIDIAN)
                .define('B', Items.LAPIS_LAZULI)
                .define('C', Items.ENDER_PEARL)
                .group(SpectreThings.MODID)
                .unlockedBy(getHasName(Items.ENDER_PEARL), has(Items.ENDER_PEARL))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.SPECTRE_ANCHOR.value(), 1)
                .pattern(" A ")
                .pattern("ABA")
                .pattern("AAA")
                .define('A', Registration.SPECTRE_INGOT.value())
                .define('B', Registration.ECTOPLASM.value())
                .group(SpectreThings.MODID)
                .unlockedBy(getHasName(Registration.ECTOPLASM.value()), has(Registration.ECTOPLASM.value()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Registration.SPECTRE_SWORD.value(), 1)
                .pattern("A")
                .pattern("A")
                .pattern("B")
                .define('A', Registration.SPECTRE_INGOT.value())
                .define('B', Items.OBSIDIAN)
                .group(SpectreThings.MODID)
                .unlockedBy(getHasName(Registration.SPECTRE_INGOT.value()), has(Registration.SPECTRE_INGOT.value()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.SPECTRE_PICKAXE.value(), 1)
                .pattern("AAA")
                .pattern(" B ")
                .pattern(" B ")
                .define('A', Registration.SPECTRE_INGOT.value())
                .define('B', Items.OBSIDIAN)
                .group(SpectreThings.MODID)
                .unlockedBy(getHasName(Registration.SPECTRE_INGOT.value()), has(Registration.SPECTRE_INGOT.value()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.SPECTRE_AXE.value(), 1)
                .pattern("AA")
                .pattern("AB")
                .pattern(" B")
                .define('A', Registration.SPECTRE_INGOT.value())
                .define('B', Items.OBSIDIAN)
                .group(SpectreThings.MODID)
                .unlockedBy(getHasName(Registration.SPECTRE_INGOT.value()), has(Registration.SPECTRE_INGOT.value()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.SPECTRE_SHOVEL.value(), 1)
                .pattern("A")
                .pattern("B")
                .pattern("B")
                .define('A', Registration.SPECTRE_INGOT.value())
                .define('B', Items.OBSIDIAN)
                .group(SpectreThings.MODID)
                .unlockedBy(getHasName(Registration.SPECTRE_INGOT.value()), has(Registration.SPECTRE_INGOT.value()))
                .save(recipeOutput);
    }
}


class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SpectreThings.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(Registration.ECTOPLASM.value());
        handheldItem(Registration.SPECTRE_KEY.value());
        basicItem(Registration.SPECTRE_INGOT.value());
        basicItem(Registration.STABLE_ENDER_PEARL.value());
        basicItem(Registration.SPECTRE_ANCHOR.value());
        simpleBlockItem(Registration.SPECTRE_BLOCK.value());
        simpleBlockItem(Registration.SPECTRE_CORE.value());
        simpleBlockItem(Registration.SPECTRE_PLANKS.value());
        simpleBlockItem(Registration.SPECTRE_LEAVES.value());
        basicItem(Registration.SPECTRE_SAPLING_ITEM.value());
        simpleBlockItem(Registration.SPECTRE_LOG.value());
        
        // Spectre Tools models
        handheldItem(Registration.SPECTRE_SWORD.value());
        handheldItem(Registration.SPECTRE_AXE.value());
        handheldItem(Registration.SPECTRE_PICKAXE.value());
        handheldItem(Registration.SPECTRE_SHOVEL.value());
    }
}

class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, SpectreThings.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ItemTags.LOGS_THAT_BURN).add(Registration.SPECTRE_LOG.value().asItem());
        tag(ItemTags.LOGS).add(Registration.SPECTRE_LOG.value().asItem());
        tag(ItemTags.PLANKS).add(Registration.SPECTRE_PLANKS.value().asItem());
        
        // Add Spectre tools to the relevant tags
        tag(ItemTags.SWORDS).add(Registration.SPECTRE_SWORD.value());
        tag(ItemTags.AXES).add(Registration.SPECTRE_AXE.value());
        tag(ItemTags.PICKAXES).add(Registration.SPECTRE_PICKAXE.value());
        tag(ItemTags.SHOVELS).add(Registration.SPECTRE_SHOVEL.value());
    }
}

class ModBlockLootTableProvider extends BlockLootSubProvider{
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(Registration.SPECTRE_LOG.value());
        dropSelf(Registration.SPECTRE_PLANKS.value());
        dropSelf(Registration.SPECTRE_SAPLING.value());
        dropSelf(Registration.SPECTRE_BLOCK.value());
        dropSelf(Registration.SPECTRE_CORE.value());
        add(Registration.SPECTRE_LEAVES.get(), block -> createLeavesDrops(block, Registration.SPECTRE_SAPLING.value(), NORMAL_LEAVES_SAPLING_CHANCES));
    }
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Registration.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}

class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, SpectreThings.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add(Registration.ECTOPLASM.value(), "Ectoplasm");
        add(Registration.SPECTRE_INGOT.value(), "Spectre Ingot");
        add(Registration.STABLE_ENDER_PEARL.value(), "Stable Ender Pearl");
        add(Registration.SPECTRE_KEY.value(), "Spectre Key");
        add(Registration.SPECTRE_ANCHOR.value(), "Spectre Anchor");
        add(Registration.SPECTRE_CORE.value(), "Spectre Core");
        add(Registration.SPECTRE_BLOCK.value(), "Spectre Block");
        add("creativetab.spectre_things.general", "Spectre Things");
        add("spectre_things_tab", "Spectre Things");
        
        // Spectre Tools translations
        add(Registration.SPECTRE_SWORD.value(), "Spectre Sword");
        add(Registration.SPECTRE_AXE.value(), "Spectre Axe");
        add(Registration.SPECTRE_PICKAXE.value(), "Spectre Pickaxe");
        add(Registration.SPECTRE_SHOVEL.value(), "Spectre Shovel");
        

        add(Registration.SPECTRE_LOG.value(), "Spectre Log");
        add(Registration.SPECTRE_PLANKS.value(), "Spectre Planks");
        add(Registration.SPECTRE_LEAVES.value(), "Spectre Leaves");
        add(Registration.SPECTRE_SAPLING.value(), "Spectre Sapling");

        add(Registration.SPIRIT.get(), "Spirit");

        add(Registration.SPECTRAL_BIOME.toString(), "Spectre");

        add("tooltip.spectrethings.anchored", "Anchored");
    }
}

class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {

    public ModGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, SpectreThings.MODID);
    }

    @Override
    protected void start() {
    }
}

class ModWorldGenProvider extends DatapackBuiltinEntriesProvider{
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.BIOME, Registration::bootstrapBiome)
            .add(Registries.DIMENSION_TYPE, Registration::bootstrapType)
            .add(Registries.LEVEL_STEM, Registration::bootstrapStem)
            .add(Registries.CONFIGURED_FEATURE, Registration::bootstrapTree);

    public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries,BUILDER, Set.of(SpectreThings.MODID));
    }
}

class ModBlockTagProvider extends IntrinsicHolderTagsProvider<Block> {

    @SuppressWarnings("deprecation")
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.BLOCK, lookupProvider, block -> block.builtInRegistryHolder().key(), SpectreThings.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.LOGS_THAT_BURN)
            .add(Registration.SPECTRE_LOG.get());
        this.tag(BlockTags.LOGS)
            .add(Registration.SPECTRE_LOG.get());

        this.tag(BlockTags.MINEABLE_WITH_AXE)
            .add(Registration.SPECTRE_LOG.get())
            .add(Registration.SPECTRE_PLANKS.get());
    }
}
