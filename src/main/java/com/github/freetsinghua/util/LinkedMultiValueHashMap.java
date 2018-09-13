package com.github.freetsinghua.util;

import org.jetbrains.annotations.Nullable;

import java.util.*;

/** @ClassName @Description @Author z.tsinghua @Date 2018/9/13 */
public class LinkedMultiValueHashMap<K, V> implements MultiValueMap<K, V> {

    private LinkedHashMap<K, List<V>> map = new LinkedHashMap<>();

    @Override
    public void add(K key, V value) {
        if (map.containsKey(key)) {
            ArrayList<V> vs = new ArrayList<>(map.get(key));
            vs.add(value);
            map.remove(key);
            map.put(key, vs);

        } else {
            map.put(key, Collections.singletonList(value));
        }
    }

    @Override
    public void add(K key, List<V> values) {
        map.put(key, values);
    }

    @Override
    public void set(K key, V value) {
        if (map.containsKey(key)) {
            map.remove(key);
            this.add(key, value);
        } else {
            this.add(key, value);
        }
    }

    @Override
    public void set(K key, List<V> values) {
        for (V v : values) {
            set(key, v);
        }
    }

    @Override
    public void set(Map<K, List<V>> values) {
        map.clear();
        map = null;
        map = new LinkedHashMap<>(values);
    }

    @Override
    public List<V> remove(K key) {
        return map.remove(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public List<V> values() {
        Collection<List<V>> values = map.values();

        ArrayList<V> vs = new ArrayList<>();

        for (List<V> list : values) {
            for (V v : list) {
                vs.add(v);
            }
        }

        return vs;
    }

    @Nullable
    @Override
    public V getValue(K key, int index) {

        if (map.containsKey(key)) {
            return map.get(key).get(index);
        }

        return null;
    }

    @Override
    public List<V> getValues(K key) {

        if (map.containsKey(key)) {
            return map.get(key);
        }

        return Collections.emptyList();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }
}
