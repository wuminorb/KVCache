package com.github.wuminorb.kvcache.server;

import com.github.wuminorb.kvcache.KVCacheService;
import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KVCacheServer implements KVCacheService.Iface, Closeable {

    private Map<String, String> data;
    private TServer tServer;

    public static KVCacheServer createAndStart(int port) throws TTransportException {
        KVCacheServer server = create(port);
        server.start();
        return server;
    }

    public static KVCacheServer create(int port) throws TTransportException {
        KVCacheServer server = new KVCacheServer();
        TServerSocket serverTransport = new TServerSocket(port);
        TServer tServer = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(new KVCacheService.Processor<>(server)));

        // maybe set initialCapacity base on cpu cache size
        server.data = new ConcurrentHashMap<>(4096);
        server.tServer = tServer;

        return server;
    }

    public void start() {
        new Thread() {
            public void run() {
                tServer.serve();
            }
        }.start();
    }

    public void stop() {
        tServer.stop();
    }

    @Override
    public void close() throws IOException {
        stop();
    }

    private KVCacheServer() {

    }

    @Override
    public String get(String key) throws TException {
        return data.get(key);
    }

    @Override
    public boolean put(String key, String value) throws TException {
        data.put(key, value);
        return true;
    }

    @Override
    public boolean invalidate(String key) throws TException {
        data.remove(key);
        return true;
    }

    @Override
    public int count() throws TException {
        return data.size();
    }
}
