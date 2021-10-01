import java.io.Console;
import java.math.BigInteger;

/**
 *
 * @author colegilbert
 */

public class HashMap<Key, Value> {

    private double maxLoad    = 0.5;    // Load factor at which table is resized

    private int capacity;
    private int size;
    private Key[] keys;
    private Value[] values;

    private int nextPrime(int n) {  // Returns next prime number following n
        return BigInteger.valueOf(n).nextProbablePrime().intValue();
    }

    public HashMap(int capacity) {
        capacity = nextPrime(capacity);
        this.keys = (Key[]) new Object[capacity];
        this.values = (Value[]) new Object[capacity];
        this.capacity = capacity;
        this.size = size;
    }

    public HashMap() {
        this(16);
    }


    public int capacity() {
        return this.capacity;
    }

    public int size() {
        return this.size;
    }

    public double loadFactor() {
        return ((double) this.size) / ((double) this.capacity);
    }

    public boolean isEmpty() {
        return this.size == 0;
    }


    private int hash(Key key) {
        return (key.hashCode() & 0x7FFFFFFF) % this.capacity;
    }


    private void resize(int capacity) {
        Key[] oldKeys = this.keys;
        Value[] oldValues = this.values;

        this.keys = (Key[]) new Object[capacity];
        this.values = (Value[]) new Object[capacity];
        this.capacity = capacity;
        this.size = 0;

        for (int i = 0; i < oldKeys.length; i++) {
            if (oldKeys[i] != null) {
                add(oldKeys[i], oldValues[i]);
            }
        }
    }

    private void resize() {
        resize(nextPrime(2*this.capacity));
    }


    private int locate(Key key) {
        int index = hash(key);
        while (this.keys[index] != null && !this.keys[index].equals(key)) {
            index = (index + 1) % this.capacity;
        }
        return index;
    }

    public boolean contains(Key key) {
        int index = locate(key);
        return this.keys[index] != null;
    }


    public Value find(Key key) {
        int index = locate(key);
        return this.values[index];
    }


    public void add(Key key, Value value) {
        if (loadFactor() > this.maxLoad) {
            resize();
        }

        int index = locate(key);
        if (this.keys[index] == null) {
            this.keys[index] = key;
        }
        this.values[index] = value;
        this.size++;
    }


    public void remove(Key key) {
        int index = hash(key);
        while (keys[index] != null & !keys[index].equals(key)) {
            index = (index + 1) % this.capacity;
        }

        keys[index] = null;
        values[index] = null;
        this.size--;

        index = (index + 1) % this.capacity;
        while (keys[index] != null) {
            Key savedKey = keys[index];
            Value savedValue = values[index];
            keys[index] = null;
            values[index] = null;
            this.size--;

            index = (index + 1) % this.capacity;
            add(savedKey, savedValue);
        }
    }
}
