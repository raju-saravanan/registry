package com.hortonworks.registries.common.cache.impl;

import com.hortonworks.registries.common.cache.AbstractCache;

import java.util.Collection;

public class HazelcastCache<K,V> implements AbstractCache<K,V> {

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public Collection<V> getAll(Collection<K> keys) {
        return null;
    }

    @Override
    public void put(K key, V value) {

    }

    @Override
    public void remove(K key) {

    }
}
