package com.hortonworks.registries.common.cache.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hortonworks.registries.common.cache.AbstractCache;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class GuavaCache<K,V> implements AbstractCache<K,V> {

    private LoadingCache<K,V> loadingCache;

    public GuavaCache(int schemaCacheSize, long schemaCacheExpiryInMills) {
        loadingCache = CacheBuilder.newBuilder()
                .maximumSize(schemaCacheSize)
                .expireAfterAccess(schemaCacheExpiryInMills, TimeUnit.MILLISECONDS)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K key) throws Exception {
                        return null;
                    }
                });
    }

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



    private final LoadingCache<Key, SchemaVersionInfo> loadingCache;
    private final ConcurrentMap<SchemaIdVersion, SchemaVersionKey> idWithNameVersion;
    private final ConcurrentMap<SchemaVersionKey, List<SchemaIdVersion>> nameVersionWithIds;

    public SchemaVersionInfoCache(final SchemaVersionRetriever schemaRetriever,
                                  final int schemaCacheSize,
                                  final long schemaCacheExpiryInMilliSecs) {
        idWithNameVersion = new ConcurrentHashMap<>(schemaCacheSize);
        nameVersionWithIds = new ConcurrentHashMap<>(schemaCacheSize);
        loadingCache = createLoadingCache(schemaRetriever, schemaCacheSize, schemaCacheExpiryInMilliSecs);
    }

    private LoadingCache<Key, SchemaVersionInfo> createLoadingCache(SchemaVersionRetriever schemaRetriever,
                                                                    int schemaCacheSize,
                                                                    long schemaCacheExpiryInMilliSecs) {
        return CacheBuilder.newBuilder()
                .maximumSize(schemaCacheSize)
                .expireAfterAccess(schemaCacheExpiryInMilliSecs, TimeUnit.MILLISECONDS)
                .build(new CacheLoader<Key, SchemaVersionInfo>() {
                    @Override
                    public SchemaVersionInfo load(Key key) throws Exception {
                        LOG.info("Loading entry for cache with key [{}] from target service", key);
                        SchemaVersionInfo schemaVersionInfo;
                        if (key.schemaVersionKey != null) {
                            schemaVersionInfo = schemaRetriever.retrieveSchemaVersion(key.schemaVersionKey);
                        } else if (key.schemaIdVersion != null) {
                            schemaVersionInfo = schemaRetriever.retrieveSchemaVersion(key.schemaIdVersion);
                        } else {
                            throw new IllegalArgumentException("Given argument is not valid: " + key);
                        }

                        updateCacheInvalidationEntries(schemaVersionInfo);
                        return schemaVersionInfo;
                    }
                });
    }
}
