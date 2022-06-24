package io.github.rypofalem.wrenchable.cyclable;

import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;

import java.util.Set;

public class CyclableDirectional implements Cyclable<Directional, BlockFace> {

    private final Directional directional;
    private static final BlockFace[] orderedDirections = {
            BlockFace.NORTH_WEST, BlockFace.NORTH_NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_EAST,
            BlockFace.EAST_NORTH_EAST, BlockFace.EAST, BlockFace.EAST_SOUTH_EAST,
            BlockFace.SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST,
            BlockFace.WEST_SOUTH_WEST, BlockFace.WEST, BlockFace.WEST_NORTH_WEST,
            BlockFace.UP, BlockFace.DOWN
    };

    public CyclableDirectional(Directional directional) {
        this.directional = directional;
    }

    @Override
    public Directional getHolder() {
        return directional;
    }

    @Override
    public Set<BlockFace> getValidPositions() {
        return directional.getFaces();
    }

    @Override
    public BlockFace[] getOrdering() {
        return CyclableDirectional.orderedDirections;
    }

    @Override
    public BlockFace getOrientation() {
        return directional.getFacing();
    }

    @Override
    public void setOrientation(BlockFace p) {
        directional.setFacing(p);
    }
}
