package br.edu.utfpr.recominer.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class GenericCacher<K, V> {

    private final Map<K, V> cache;

    public GenericCacher() {
        this.cache = new HashMap<>();
    }

    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    public V get(K key) {
        return cache.get(key);
    }

    public V put(K key, V value) {
        return cache.put(key, value);
    }

    public Set<K> keySet() {
        return cache.keySet();
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return cache.entrySet();
    }

}
