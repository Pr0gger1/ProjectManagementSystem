package ru.sfedu.projectmanager.model;

import java.util.HashMap;
import java.util.Objects;

public class TrackInfo<K, V> {
    private HashMap<K, V> data = new HashMap<>();

    TrackInfo(K key, V value) {
        data.put(key, value);
    }

    TrackInfo(HashMap<K, V> data) {
        this.data = data;
    }

    TrackInfo() {}

    public HashMap<K, V> getData() {
        return data;
    }

    public void addData(K key, V value) {
        data.put(key, value);
    }

    public void setData(HashMap<K, V> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TrackInfo<?, ?> trackInfo = (TrackInfo<?, ?>) object;
        return Objects.equals(data, trackInfo.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
