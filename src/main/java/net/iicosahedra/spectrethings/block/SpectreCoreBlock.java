package net.iicosahedra.spectrethings.block;

import net.iicosahedra.spectrethings.setup.Registration;
import net.iicosahedra.spectrethings.worldgen.dim.SpectreCube;
import net.iicosahedra.spectrethings.worldgen.dim.SpectreData;
import net.iicosahedra.spectrethings.worldgen.dim.SpectreHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SpectreCoreBlock extends Block {
    public enum Orientation implements StringRepresentable {
        NW("nw"), NE("ne"), ES("es"), SW("sw");

        private final String name;

        Orientation(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static final EnumProperty<Orientation> ORIENTATION =
            EnumProperty.create("orientation", Orientation.class);

    public SpectreCoreBlock() {
        super(Properties.of()
                .mapColor(MapColor.STONE)
                .strength(-1.0F, 6000000.0F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .pushReaction(PushReaction.BLOCK)
        );

        registerDefaultState(stateDefinition.any()
                .setValue(ORIENTATION, Orientation.NW));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateOrientation(context.getLevel(), context.getClickedPos(), defaultBlockState());
    }

    private BlockState updateOrientation(Level level, BlockPos pos, BlockState state) {
        if (level.getBlockState(pos.north()).getBlock() != this &&
                level.getBlockState(pos.west()).getBlock() != this) {
            return state.setValue(ORIENTATION, Orientation.NW);
        }
        if (level.getBlockState(pos.north()).getBlock() != this &&
                level.getBlockState(pos.east()).getBlock() != this) {
            return state.setValue(ORIENTATION, Orientation.NE);
        }
        if (level.getBlockState(pos.east()).getBlock() != this &&
                level.getBlockState(pos.south()).getBlock() != this) {
            return state.setValue(ORIENTATION, Orientation.ES);
        }
        return state.setValue(ORIENTATION, Orientation.SW);
    }


    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.dimension() == Registration.SPECTRE_LEVEL_KEY) {
            if (stack.is(Registration.ECTOPLASM.value())) {
                if (!level.isClientSide) {
                    SpectreHandler handler = SpectreHandler.get(level.getServer());
                    SpectreCube cube = handler.getCurrentCube(pos.above());

                    if (cube != null) {
                        int amount = stack.getCount();
                        int used = cube.increaseHeight(amount);
                        stack.shrink(used);
                    }
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            } else if (stack.isEmpty()) {
                if (!level.isClientSide) {
                    SpectreHandler handler = SpectreHandler.get(level.getServer());
                    handler.teleportBack((ServerPlayer) player);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
        return adjacentState.getBlock() == this ||
                adjacentState.getBlock() == Registration.SPECTRE_BLOCK.value();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
        return false;
    }
}