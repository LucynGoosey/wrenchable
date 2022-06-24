package io.github.rypofalem.wrenchable.cyclable;

import org.bukkit.Axis;
import org.bukkit.block.data.Orientable;

import java.util.Set;

public class CyclableOrientable implements Cyclable<Orientable, Axis> {
    private final Orientable orientable;

    public CyclableOrientable(Orientable orientable) {
        this.orientable = orientable;
    }

    @Override
    public Orientable getHolder() {
        return orientable;
    }

    @Override
    public Set<Axis> getValidPositions() {
        return orientable.getAxes();
    }

    @Override
    public Axis[] getOrdering() {
        return Axis.values(); // Any cyclical order of three elements is almost indistinguishable, XYZ or ZYX
    }

    @Override
    public Axis getOrientation() {
        return orientable.getAxis();
    }

    @Override
    public void setOrientation(Axis p) {
        orientable.setAxis(p);
    }
}
