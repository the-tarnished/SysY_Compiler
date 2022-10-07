package error;

import java.util.Objects;

public class Pair <K> implements Comparable{
    private final K key;
    private final int value;
    public Pair (K key,int value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(Object o) {
        return value - ((Pair <K>) o).getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?> pair = (Pair<?>) o;

        if (value != pair.value) return false;
        return Objects.equals(key, pair.key);
    }

}
