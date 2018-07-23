package com.github.wuminorb.kvstore.client;

import com.github.wuminorb.kvstore.client.util.Address;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class ClusterTest {
    Cluster<Address> cluster;
    final int numberOfReplicas = 100;

    @Before
    public void testInitCluster() {
        cluster = new Cluster<>(numberOfReplicas);
    }

    @Test
    public void testAddNode() {
        int oldSize = cluster.size();
        int oldPartitionNum = cluster.partitionNum();

        Address newNode = new Address("127.0.0.1", 10000);
        cluster.addNode(newNode);
        cluster.addNode(newNode);
        cluster.addNode(new Address("127.0.0.2", 10000));
        cluster.addNode(new Address("127.0.0.3", 10000));
        cluster.addNode(new Address("127.0.0.4", 10000));
        cluster.addNode(new Address("127.0.0.5", 10000));
        cluster.addNode(new Address("127.0.0.6", 10000));
        cluster.addNode(new Address("127.0.0.7", 10000));
        cluster.addNode(new Address("127.0.0.8", 10000));
        cluster.addNode(new Address("127.0.0.9", 10000));
        cluster.addNode(new Address("127.0.0.10", 10000));
        cluster.addNode(new Address("127.0.0.11", 10000));
        cluster.addNode(new Address("127.0.0.12", 10000));
        cluster.addNode(new Address("127.0.0.13", 10000));

        int newSize = cluster.size();
        int newPartitionNum = cluster.partitionNum();

        double std = cluster.getCircleStandardDeviation();
        System.out.println(std);

        assertTrue(3 > std);
        assertEquals(oldSize + 13, newSize);
        assertEquals(oldPartitionNum + 13 * numberOfReplicas, newPartitionNum);
    }

    @Test
    public void testRemoveNode() {
        Address node = new Address("127.0.0.1", 10000);
        cluster.addNode(node);
        cluster.addNode(new Address("127.0.0.2", 10000));
        cluster.addNode(new Address("127.0.0.3", 10000));
        cluster.removeNode(node);

        int newSize = cluster.size();
        int newPartitionNum = cluster.partitionNum();

        assertEquals(2, newSize);
        assertEquals(2 * numberOfReplicas, newPartitionNum);
    }

    @Test
    public void testFind() {
        cluster.addNode(new Address("127.0.0.2", 10000));
        cluster.addNode(new Address("127.0.0.3", 10000));
        cluster.addNode(new Address("127.0.0.4", 10000));

        int key = new Random().nextInt();

        Address server0 = cluster.find(key);

        cluster.addNode(new Address("127.0.0.5", 10000));
        cluster.addNode(new Address("127.0.0.6", 10000));

        Address server1 = cluster.find(key);
        assertEquals(server1, server0);

        System.out.println(server0);

        cluster.removeNode(server0);
        Address server2 = cluster.find(key);
        System.out.println(server2);
        assertNotEquals(server2, server0);
    }
}
