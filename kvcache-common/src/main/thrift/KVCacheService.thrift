namespace java com.github.wuminorb.kvcache

service KVCacheService {
    string get(1: string key)
    bool put(1: string key, 2: string value)
    bool invalidate(1: string key)
    i32 count()
}