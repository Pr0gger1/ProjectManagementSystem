package ru.sfedu.projectmanager.model;

import java.util.HashMap;
import java.util.Objects;

public class TrackInfo<E, S> {
    private HashMap<E, S> data = new HashMap<>();

    TrackInfo(E key, S value) {
        data.put(key, value);
    }
//    public TrackInfo(String key, Object object) {
//        this.data.put((E)key, (S)object);
//    }

    public TrackInfo(HashMap<E, S> data) {
        this.data = data;
    }

    public TrackInfo() {

    }

    public HashMap<E, S> getData() {
        return data;
    }

    public void addData(E key, S value) {
        data.put(key, value);
    }
    public void addData(String key, Object object) {
        this.data.put((E)key, (S)object);
    }


    public void setData(HashMap<E, S> data) {
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

    @Override
    public String toString() {
        return "TrackInfo{" +
                "data=" + data +
                '}';
    }
}
