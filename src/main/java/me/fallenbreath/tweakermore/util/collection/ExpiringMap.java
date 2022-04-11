package me.fallenbreath.tweakermore.util.collection;

import com.google.common.collect.Maps;
import net.minecraft.util.Util;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ExpiringMap<K, V> implements Map<K, V>
{
    private final Map<K, V> delegate;
    private final Map<K, Long> times = Maps.newLinkedHashMap();
    private final int lifespanMs;

    public ExpiringMap(Map<K, V> map, int lifespanMs)
    {
        this.delegate = map;
        this.lifespanMs = lifespanMs;
    }

    private static long getCurrentMs()
    {
        return Util.getMeasuringTimeMs();
    }

    private void cleanTimeoutEntries()
    {
        long currentMs = getCurrentMs();
        Iterator<Entry<K, Long>> iterator = this.times.entrySet().iterator();
        while (iterator.hasNext())
        {
            Entry<K, Long> timeEntry = iterator.next();
            if (currentMs - timeEntry.getValue() <= this.lifespanMs)
            {
                break;
            }
            iterator.remove();
            this.delegate.remove(timeEntry.getKey());
        }
    }

    @Override
    public int size()
    {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return this.delegate.containsValue(value);
    }

    @Override
    public V get(Object key)
    {
        this.cleanTimeoutEntries();
        return this.delegate.get(key);
    }

    @Nullable
    @Override
    public V put(K key, V value)
    {
        this.cleanTimeoutEntries();
        this.times.put(key, getCurrentMs());
        return this.delegate.put(key, value);
    }

    @Override
    public V remove(Object key)
    {
        this.cleanTimeoutEntries();
        return this.delegate.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m)
    {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void clear()
    {
        this.delegate.clear();
        this.times.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet()
    {
        throw new NotImplementedException("Not implemented");
    }

    @NotNull
    @Override
    public Collection<V> values()
    {
        throw new NotImplementedException("Not implemented");
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet()
    {
        throw new NotImplementedException("Not implemented");
    }
}
