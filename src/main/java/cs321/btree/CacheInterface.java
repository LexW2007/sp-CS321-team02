package cs321.btree;

/**
 * This is a generic chache ADT that stores values of type V and retrieves them using keys of type K.
 * 
 * <p>This cache uses a Most Recently Used (MRU) policy: when {@link #get(object)} finds a matching value, 
 * that value is moved to the front of the cache. If the key is not found (a cache miss), {@codenull is returned.
 * 
 * <p>The cache has a fixed maximum size. When adding a new entry and the cache is full the Least Recently Used (LRU)
 * entry is removed to make space for the new entry.
 * 
 * <p>Statistic terms: 
 * <ul>
 *   <li><b>NR</b>: number of references (the number of {@code get} operations)</li>
 *   <li><b>NH</b>: number of hits (the number of {@code get} operations that returned a non-null value)</li>
 *   <li><b>HR</b>: hit ratio = NH / NR (often displayed as a percentage)</li>
 * </ul>
 * 
 * @param <K> the type of keys used to identify cached values
 * @param <V> the type of values stored in the cache; must provide {@code getKey()} via {@link KeyInterface}
 * 
 * @author CS321 Instructors
 */

public interface CacheInterface<K, V extends KeyInterface<K>> {

    /**
     * Retieves the value associated with the given key. 
     * 
     * <p>If found, the value is moved to the front of the cache. This is called a cache <b>hit</b>.
     * and the entry is moved to the MRU (front) position in the cache. If they key is not found, this is a cache <b>miss</b> and 
     * {@code null} is returned.
     * 
     * <p> This operation counts as a reference for (NR). and if successful it also increments the hit count (NH).
     * 
     * @param key the key to search for
     * @return the matching chached value (and becomes MRU), or {@code null} if not found
     */
    public V get(K key);

    /**
     * Adds the given value to the cache. 
     * 
     * <p>If the cache is full, the LRU value is removed to make space. IF an entry is a evicted due to the chache being full,
     * that entry is returned; otherwise {@code null} is returned.
     * 
     * @param value the value to add to the cache
     * @return the evicted value (if any), or {@code null} if no eviction occurred
     */
    public V add(V value);

    /**
     * Removes the value associated with the given key from the cache.
     * 
     * <p> If found, the value is removed and returned; otherwise {@code null} is returned.
     * @param key the key of the value to remove
     * @return the removed value (if found), or {@code null} if not found
     */
    public V remove(K key);

    /**
     * Clears all entries from the cache.
     */
    public void clear();

    /**
     * Reutrns a string representation of the cache statistics: 
     * max size, number of references (NR), number of hits (NH), and hit ratio (HR) displayed as a percentage to two decimal places.
     * 
     * @return string representation of cache statistics
     * 
     * {@inheritDoc} 
     */
    public String toString();
}
