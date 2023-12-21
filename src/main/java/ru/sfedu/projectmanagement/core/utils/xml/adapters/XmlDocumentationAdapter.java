package ru.sfedu.projectmanagement.core.utils.xml.adapters;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class EntryType {
    @XmlElement(name = "title")
    public String key;

    @XmlElement(name = "content")
    public String value;
}

@XmlType
class HashMapType {
    @XmlElement(name = "article")
    public final List<EntryType> entries = new ArrayList<>();
}

public class XmlDocumentationAdapter extends XmlAdapter<HashMapType, HashMap<String, String>> {
    /**
     * @param hashMapType 
     * @return
     * @throws Exception
     */
    @Override
    public HashMap<String, String> unmarshal(HashMapType hashMapType) throws Exception {
        HashMap<String, String> output = new HashMap<>();
        hashMapType.entries.forEach(hashmap -> {
            output.put(hashmap.key, hashmap.value);
        });

        return output;
    }

    /**
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public HashMapType marshal(HashMap<String, String> map) throws Exception {
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
