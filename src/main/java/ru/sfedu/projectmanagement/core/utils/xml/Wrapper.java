package ru.sfedu.projectmanagement.core.utils.xml;

import jakarta.xml.bind.annotation.*;
import ru.sfedu.projectmanagement.core.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "wrapper")
public class Wrapper<T> {
    @XmlElements({
            @XmlElement(name = "project", type = Project.class),
            @XmlElement(name = "bug_report", type = BugReport.class),
            @XmlElement(name = "task", type = Task.class),
            @XmlElement(name = "event", type = Event.class),
            @XmlElement(name = "documentation", type = Documentation.class),
            @XmlElement(name = "employee", type = Employee.class)
    })
    private List<T> list = new ArrayList<>();

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
    public void addNode(T node) {
        this.list.add(node);
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
