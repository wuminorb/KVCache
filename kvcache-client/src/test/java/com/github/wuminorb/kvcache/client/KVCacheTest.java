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

        assertEquals(2, cache.size());
    }

    @Test(expected = NullPointerException.class)
    public void testPutNullKey() {
        cache.put(null, "f");
    }

    @Test
    public void testGet() {
        assertEquals(null, cache.get("a-get"));

        cache.put("a-get", "b");
        assertEquals("b", cache.get("a-get"));

        cache.put("a-get", "c");
        assertEquals("c", cache.get("a-get"));

        cache.put("a-get", null);
        assertEquals(null, cache.get("a-get"));
    }
}
