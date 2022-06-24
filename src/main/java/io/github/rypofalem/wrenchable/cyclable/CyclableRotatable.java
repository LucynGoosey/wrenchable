package io.github.rypofalem.wrenchable.cyclable;

import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rotatable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CyclableRotatable implements Cyclable<Rotatable, BlockFace> {
    private final Rotatable rotatable;
    private static final BlockFace[] orderedRotations = {
            BlockFace.NORTH_WEST, BlockFace.NORTH_NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_EAST,
            BlockFace.EAST_NORTH_EAST, BlockFace.EAST, BlockFace.EAST_SOUTH_EAST,
            BlockFace.SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST,
            BlockFace.WEST_SOUTH_WEST, BlockFace.WEST, BlockFace.WEST_NORTH_WEST
    };

    public CyclableRotatable(Rotatable r){
        this.rotatable = r;
    }

    @Override
    public Set<BlockFace> getValidPositions() {
        return new HashSet<>(Arrays.asList(orderedRotations)); // all BlockFaces except UP and DOWN valid for all Rotatables
    }

    public Rotatable getHolder() {
        return rotatable;
    }

    @Override
    public BlockFace[] getOrdering() {
        return CyclableRotatable.orderedRotations;
    }

    @Override
    public BlockFace getOrientation() {
        return rotatable.getRotation();
    }

    @Override
    public void setOrientation(BlockFace p) {
        rotatable.setRotation(p);
    }
}
