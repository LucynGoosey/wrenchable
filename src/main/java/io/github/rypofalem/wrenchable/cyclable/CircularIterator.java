package io.github.rypofalem.wrenchable.cyclable;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

// treating an array as if it's a circle
// very minimal with just the stuff I needed for this project :P
// not the best way to implement things more generally
public class CircularIterator<T> implements Iterator<T> {
    private final T[] a;
    private final int start;
    private int offset = 0;

    public CircularIterator(T[] array, int start){
        this.a = array;
        this.start = start % array.length;
    }

    private int index(){
        return (start + offset) % a.length;
    }

    @Override
    public boolean hasNext() {
        return offset < a.length;
    }

    @Override
    public T next() {
        T t = a[index()];
        offset++;
        return t;
    }

    // find the first element that matches `find` that comes after the first element that matches `start`
    // (wrapping around the array if needed)
    public static <T> Optional<T> findAfter(T[] a, Predicate<T> start, Predicate<T> find) {
        int first = -1;
        for(int i = 0; i < a.length; i++){
            if(start.test(a[i])){
                first = i + 1; // the next element, won't produce indexoutofboundsexception
                break;
            }
        }
        if(first == -1) return Optional.empty(); // Nothing matched start predicate so we can't continue
        for (Iterator<T> it = new CircularIterator<>(a, first); it.hasNext(); ) {
            T t = it.next();
            if(find.test(t)) return Optional.of(t); // found it
        }
        return Optional.empty(); // Nothing matched the find predicate
    }
}
