package com.github.wuminorb.kvcache.client;

import com.github.wuminorb.kvcache.client.util.Address;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KVCache implements Map<String, String> {
    private final Cluster<Address> cluster = new Cluster<>(100);

    public KVCache(Address... servers) {
        for (Address server : servers) {
            cluster.addNode(server);
        }
    }

    @Override
    public int size() {
        Collection<Address> addresses = cluster.getAllNodes();
        int size = 0;

        for (Address address : addresses) {
            size += Connector.count(address);
        }

        return size;
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
        if (key == null) {
            return null;
        }

        Address server = cluster.find((String) key);
        return Connector.get(server, (String) key);
    }

    /**
     * If the value is null, it will expiration the key. If the key is null, will throw null point exception.
     *
     * @param key   the cache key
     * @param value the cache value
     * @return the value
     */
    @Override
    public String put(String key, String value) {
        if (key == null) {
            throw new NullPointerException("can't put null key, value:" + value);
        }
        if (value == null) {
            remove(key);
            return null;
        }

        Address server = cluster.find(key);
        Connector.push(server, key, value);

        return value;
    }

    @Override
    public String remove(Object key) {
        if (key == null) {
            return null;
        }

        Address server = cluster.find((String) key);
        return Connector.invalidate(server, (String) key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        for (Entry<? extends String, ? extends String> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
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
