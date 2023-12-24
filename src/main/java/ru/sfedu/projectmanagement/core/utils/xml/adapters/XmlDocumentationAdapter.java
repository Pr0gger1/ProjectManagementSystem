package ru.sfedu.projectmanagement.core.utils.xml.adapters;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XmlDocumentationAdapter extends XmlAdapter<XmlDocumentationAdapter.HashMapType, HashMap<String, String>> {
    public static class EntryType {
        @XmlElement(name = "title")
        public String key;

        @XmlElement(name = "content")
        public String value;
    }

    @XmlType
    public static class HashMapType {
        @XmlElement(name = "article")
        public final List<EntryType> entries = new ArrayList<>();
    }


    /**
     * @param hashMapType HashMapType instance
     * @return Map with article titles and articles
     */
    @Override
    public HashMap<String, String> unmarshal(HashMapType hashMapType) {
        HashMap<String, String> output = new HashMap<>();
        hashMapType.entries.forEach(hashmap -> output.put(hashmap.key, hashmap.value));

        return output;
    }

    /**
     * @param map Map of article titles and articles
     * @return HashMapType instance
     */
    @Override
    public HashMapType marshal(HashMap<String, String> map) {
        HashMapType input = new HashMapType();
        map.forEach((k, v) -> {
            EntryType entryType = new EntryType();
            entryType.key = k;
            entryType.value = v;
            input.entries.add(entryType);
        });

        return input;
    }
}
