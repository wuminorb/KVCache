package com.github.wuminorb.kvcache.client;

import com.github.wuminorb.kvcache.client.util.Address;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public class Cluster<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Cluster.class);

    private final int numberOfReplicas;
    private final Map<T, Set<Integer>> nodes = new ConcurrentHashMap<>();
    private final NavigableMap<Integer, T> circle = new ConcurrentSkipListMap<>();

    private final ThreadLocal<MessageDigest> md5 = new ThreadLocal<>();

    public Cluster(int numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }

    public void addNode(T node) {
        if (nodes.containsKey(node)) {
            return;
        }

        Set<Integer> keySet = new HashSet<>();
        for (int i = 0, num = 0; num < numberOfReplicas; i++) {
            int key = hashNode(node, i);
            if (!circle.containsKey(key)) {
                circle.put(key, node);
                keySet.add(key);
                num++;
            }
        }

        nodes.put(node, keySet);
    }

    public void removeNode(T node) {
        if (!nodes.containsKey(node)) {
            return;
        }

        Set<Integer> keySet = nodes.get(node);
        for (Integer key : keySet) {
            circle.remove(key);
        }
        nodes.remove(node);
    }

    private int hashNode(T node, int replicasNum) {
        return new StringBuilder(node.toString() + replicasNum).reverse().toString().hashCode();
    }

    private int hash(String s) {
        MessageDigest m = md5.get();

        if (m == null) {
            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            md5.set(m);
        }

        m.reset();
        m.update(s.getBytes());
        byte[] bytes = m.digest();

        return (bytes[0] & 0xFF)
                | ((bytes[1] & 0xFF) << 8)
                | ((bytes[2] & 0xFF) << 16)
                | ((bytes[3] & 0xFF) << 24);
    }

    public int size() {
        return nodes.size();
    }

    public int partitionNum() {
        return circle.size();
    }

    public double getCircleStandardDeviation() {
        List<Integer> partition = circle.keySet().stream().map(i -> i >> 24).collect(Collectors.toList());
        LOGGER.debug("{}", partition);

        double hotSpot[] = new double[256];
        for (int i = 0; i < partition.size(); i++) {
            hotSpot[partition.get(i) + 128]++;
        }

        LOGGER.debug("{}", hotSpot);
        return new DescriptiveStatistics(hotSpot).getStandardDeviation();
    }

    /**
     * find response node for the hash key.
     *
     * @param key hash key
     * @return response node. if cluster has none node, return null.
     */
    public T find(String key) {
        if (key == null || "".endsWith(key)) {
            return null;
        }

        int hash = hash(key);
        Map.Entry<Integer, T> entry = circle.ceilingEntry(hash);
        if (entry != null) {
            return entry.getValue();
        } else {
            Map.Entry<Integer, T> firstEntry = circle.firstEntry();
            if (firstEntry == null)
                return null;
            return firstEntry.getValue();
        }
    }

    public Collection<T> getAllNodes() {
        return nodes.keySet();
    }
}
