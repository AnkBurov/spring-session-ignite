package io.ankburov.springsessionignite;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.cache.Cache;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@SuppressWarnings("unchecked")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CacheMapAdapter<K, V> implements Map<K, V> {

    Cache<K, V> cache;

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        return cache.containsKey((K) key);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(Object key) {
        return cache.get((K) key);
    }

    @Override
    public V put(K key, V value) {
        cache.put(key, value);
        return value;
    }

    @Override
    public V remove(Object key) {
        return cache.getAndRemove((K) key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        cache.putAll(m);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        cache.spliterator()
             .forEachRemaining(entry -> keys.add(entry.getKey()));
        return keys;
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = new HashSet<>();
        cache.spliterator()
             .forEachRemaining(entry -> values.add(entry.getValue()));
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entries = new HashSet<>();
        cache.spliterator()
             .forEachRemaining(entry -> entries.add(new AbstractMap.SimpleEntry(entry.getKey(), entry.getValue())));
        return entries;
    }
}
