package com.github.wuminorb.kvcache.client;

import com.github.wuminorb.kvcache.KVCacheService;
import com.github.wuminorb.kvcache.client.util.Address;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Connector {
    private static final Map<Address, KVCacheService.Client> servers = new ConcurrentHashMap<>();

    private static KVCacheService.Client getClient(Address address) throws TTransportException {
        KVCacheService.Client client = servers.get(address);

        if (client == null) {
            synchronized (Connector.class) {
                client = servers.get(address);
                if (client != null) {
                    return client;
                }

                TTransport transport = new TSocket(address.host, address.port);
                transport.open();
                TProtocol protocol = new TBinaryProtocol(transport);
                client = new KVCacheService.Client(protocol);
                servers.put(address, client);
            }
        }

        return client;
    }

    public static void push(Address address, String key, String value) {
        try {
            KVCacheService.Client client = getClient(address);
            client.put(key, value);
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(Address address, String key) {
        try {
            KVCacheService.Client client = getClient(address);
            return client.get(key).getValue();
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    public static int count(Address address) {
        try {
            KVCacheService.Client client = getClient(address);
            return client.count();
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    public static String invalidate(Address address, String key) {
        try {
            String value = get(address, key);
            KVCacheService.Client client = getClient(address);
            client.invalidate(key);
            return value;
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }
}
