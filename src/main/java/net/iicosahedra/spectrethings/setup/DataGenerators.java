package net.iicosahedra.spectrethings.setup;

import net.iicosahedra.spectrethings.SpectreThings;
import net.iicosahedra.spectrethings.block.SpectreBlockBlock;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

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

        gen.addProvider(event.includeClient(), new ModBlockStates(gen, event.getExistingFileHelper()));
        gen.addProvider(event.includeClient(), new ModLanguageProvider(packOutput, "en_us"));
        gen.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, helper));
        gen.addProvider(event.includeServer(), new ModGlobalLootModifierProvider(packOutput, event.getLookupProvider()));
        gen.addProvider(event.includeServer(), new ModWorldGenProvider(packOutput, lookupProvider));
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
        simpleBlockItem(Registration.SPECTRE_BLOCK.value());
        simpleBlockItem(Registration.SPECTRE_CORE.value());
    }
}

class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, SpectreThings.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
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
            .add(Registries.LEVEL_STEM, Registration::bootstrapStem);

    public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries,BUILDER, Set.of(SpectreThings.MODID));
    }
}