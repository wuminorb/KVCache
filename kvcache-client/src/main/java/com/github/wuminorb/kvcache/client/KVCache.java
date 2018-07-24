package com.github.wuminorb.kvcache.client;

import com.github.wuminorb.kvcache.client.util.Address;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class KVCache implements Map<String, String>{
    private final Cluster<Address> cluster = new Cluster<>(100);

    public KVCache() {

    }

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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String get(Object key) {
        return null;
    }

    /** Both the key and the value can be null.If the value is null, it will expiration the key. If the key is null, it will do nothing.
     * @param key the cache key
     * @param value the cache value
     * @return the value
     */
    @Override
    public String put(String key, String value) {
        if (key == null) {
            return value;
        }

        if (value == null) {
            remove(key);
            return value;
        }

        Address server = cluster.find(key);

        return value;
    }

    @Override
    public String remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<String> values() {
        return null;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return null;
    }
}
