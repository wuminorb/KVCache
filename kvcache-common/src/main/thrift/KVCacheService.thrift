namespace java com.github.wuminorb.kvcache

struct Value { // 1
 1: optional string value;
}

service KVCacheService {
    Value get(1: string key)
    bool put(1: string key, 2: string value)
    bool invalidate(1: string key)
    i32 count()
}