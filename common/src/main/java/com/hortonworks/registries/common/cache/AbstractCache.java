package com.hortonworks.registries.common.cache;

import java.util.Collection;

public interface AbstractCache <K,V> {

    V get(K key);

    Collection<V> getAll(Collection<K> keys);

    void put(K key, V value);

    void remove(K key);
}
