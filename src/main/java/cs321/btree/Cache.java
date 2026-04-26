package cs321.btree;
import java.util.LinkedList;

public class Cache<K, V extends KeyInterface<K>> implements CacheInterface<K, V>
{
    private LinkedList<V> cache;
    private int maxSize;
    private int numReferences;
    private int numHits;

    public Cache(int maxSize)
    {
        this.maxSize = maxSize;
        this.cache = new LinkedList<>();
        this.numReferences = 0;
        this.numHits = 0;
    }

    @Override
    public V get(K key)
    {
        
        numReferences++; //count a reference

        for(int i = 0; i < cache.size(); i++)
        {
            V value = cache.get(i);
            if(value.getKey().equals(key))
            {
                //count a hit
                numHits++;
                cache.remove(i);
                cache.addFirst(value); //move to front
                return value;
            }
        }
        return null; //miss
    }

    @Override
    public V add(V value)
    { //First check for duplicate values
        for(int i = 0; i < cache.size(); i++)
        {
            if(cache.get(i).getKey().equals(value.getKey()))
            { //remove if duplicate found
                cache.remove(i);
                break;
            }
        }
        
        V evicted = null;
        // Check if adding would exceed maxSize
        if (cache.size() >= maxSize) 
        { 
        // Remove the oldest item (front of LinkedList)
        evicted = cache.removeLast();
        } 
        // Add to the front (most recent)
        cache.addFirst(value);
        return evicted;
    }

    @Override
    public V remove(K key) 
    {
        for(int i = 0; i < cache.size(); i++)
        {
            V value = cache.get(i);
            if(value.getKey().equals(key))
            {
                cache.remove(i);
                return value;
            }
        }
        return null;
    }

    @Override
    public void clear() 
    {
        //clears all caach info
        cache.clear();
        numReferences = 0;
        numHits = 0;
    }

    @Override
    public String toString()
    {
        double hitPercentage = 0.0;

        if(numReferences > 0)
        {
            hitPercentage = ((double)numHits / (double)numReferences) * 100.0;
        }
        StringBuilder sb = new StringBuilder();

        sb.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        sb.append("Cache with ").append(maxSize).append(" entries has been created\n");
        sb.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        sb.append(String.format("Total number of references:        %d\n", numReferences));
        sb.append(String.format("Total number of cache hits:        %d\n", numHits));
        sb.append(String.format("Cache hit percent:                 %.2f%%\n", hitPercentage));
        
        return sb.toString();
    }
}