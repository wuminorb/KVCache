package com.github.wuminorb.kvcache.client;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public class Cluster<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Cluster.class);

    private final int numberOfReplicas;
    private final Map<T, Set<Integer>> nodes = new ConcurrentHashMap<>();
    private final NavigableMap<Integer, T> circle = new ConcurrentSkipListMap<>();

    public Cluster(int numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }

    public void addNode(T node) {
        if (nodes.containsKey(node)) {
            return;
        }

        Set<Integer> keySet = new HashSet<>();
        for (int i = 0, num = 0; num < numberOfReplicas; i++) {
            int key = hash(node.toString() + i);
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

    private int hash(String s) {
        return new StringBuilder(s).reverse().toString().hashCode();
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
    public T find(int key) {
        Map.Entry<Integer, T> entry = circle.ceilingEntry(key);
        if (entry != null) {
            return entry.getValue();
        } else {
            Map.Entry<Integer, T> firstEntry = circle.firstEntry();
            if (firstEntry == null)
                return null;
            return firstEntry.getValue();
        }
    }
}
