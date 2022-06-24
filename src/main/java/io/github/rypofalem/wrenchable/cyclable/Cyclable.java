package io.github.rypofalem.wrenchable.cyclable;

import java.util.Optional;
import java.util.Set;

// represents an object Holder that has an Orientation and a set order that the orientations can be cycled through
// it's actually a bit more abstract than that but I don't want to think about it right now. That's what it's used for
// anyway :P
public interface Cyclable<Holder, Orientation> {

    // todo: cycle gets stuck if there are duplicate entries. not an issue for this project but more generally should be looked at
    default Holder cycle(){
        Optional<Orientation> orientation = CircularIterator.findAfter(
                getOrdering(),
                f -> getOrientation().equals(f),
                f -> getValidPositions().contains(f));

        if(orientation.isEmpty())
            // we failed to find a valid replacement face.
            // this should only happen if there are no valid faces in the array (API changed or bugged)
            throw new IllegalStateException("Couldn't find an appropriate facing to rotate to.\n");
        setOrientation(orientation.get());
        return getHolder();
    }

    Holder getHolder();

    Set<Orientation> getValidPositions();

    Orientation[] getOrdering();

    Orientation getOrientation();

    void setOrientation(Orientation p);

}
