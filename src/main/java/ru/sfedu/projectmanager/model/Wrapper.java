package ru.sfedu.projectmanager.model;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "wrapper")
public class Wrapper<T> {
    @XmlElements({
            @XmlElement(name = "project", type = Project.class)
    })
    private ArrayList<T> list = new ArrayList<>();

    public ArrayList<T> getList() {
        return list;
    }

    public void setList(ArrayList<T> list) {
        this.list = list;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Wrapper<?> wrapper = (Wrapper<?>) object;
        return Objects.equals(list, wrapper.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(list);
    }

    @Override
    public String toString() {
        return "Wrapper{" +
                "list=" + list +
                '}';
    }
}
