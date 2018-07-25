package com.github.wuminorb.kvcache.server;

import com.github.wuminorb.kvcache.KVCacheService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class KVCacheServerTest {
    @Test
    public void testStartServer() throws Exception {
        KVCacheServer server = KVCacheServer.createAndStart(10000);

        TTransport transport = new TSocket("localhost", 10000);
        transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        KVCacheService.Client client = new KVCacheService.Client(protocol);

        assertEquals(0, client.count());

        transport.close();
        server.stop();
    }

    @Test
    public void testGet() throws Exception {

    }

    @Test
    public void testPut() throws Exception {

    }

    @Test
    public void testInvalidate() throws Exception {

    }

    @Test
    public void testCount() throws Exception {

    }

    public static void main(String[] args) throws TTransportException {
        KVCacheServer server = KVCacheServer.createAndStart(10000);
    }
}