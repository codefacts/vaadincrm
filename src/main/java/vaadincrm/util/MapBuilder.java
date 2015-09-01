package vaadincrm.util;

import java.util.Map;

/**
 * Created by someone on 30/08/2015.
 */
public class MapBuilder<K, V> {
    private final Map<K, V> map;

    public MapBuilder(Map<K, V> map) {
        if (map == null) throw new NullPointerException("Map Builder: Map is null.");
        this.map = map;
    }

    public V putAndReturn(K key, V value) {
        map.put(key, value);
        return value;
    }

    public MapBuilder put(K key, V value) {
        map.put(key, value);
        return this;
    }
}
