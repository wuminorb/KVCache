package com.github.wuminorb.kvcache.client;

import com.github.wuminorb.kvcache.client.util.Address;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KVCacheTest {
    KVCache cache;

    @Before
    public void testInit() {
        cache = new KVCache(new Address("127.0.0.1", 10000));
    }

    @Test
    public void testPut() {
        cache.put("a", "b");
        cache.put("a", "b");
        cache.put("a", "c");
        cache.put("c", "d");
        cache.put("e", "f");
        cache.put("e", null);
        cache.put(null, "f");
        cache.put(null, null);

        assertEquals(2, cache.size());
    }

    @Test
    public void testGet() {
        assertEquals(null, cache.get("a"));

        cache.put("a", "b");
        assertEquals("b", cache.get("a"));

        cache.put("a", "c");
        assertEquals("c", cache.get("a"));

        cache.put("a", null);
        assertEquals(null, cache.get("a"));
    }
}
