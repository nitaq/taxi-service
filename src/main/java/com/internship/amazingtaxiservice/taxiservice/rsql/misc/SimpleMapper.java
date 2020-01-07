package com.internship.amazingtaxiservice.taxiservice.rsql.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SimpleMapper implements Mapper {

    private static final Logger LOG = Logger.getLogger(SimpleMapper.class.getName());

    private Map<Class<?>, Map<String, String>> mapping;


    public SimpleMapper() {
        this(0);
    }


    public SimpleMapper(int initialCapacity) {
        mapping = new HashMap<Class<?>, Map<String, String>>(initialCapacity);
    }

    public String translate(String selector, Class<?> entityClass) {
        if (mapping.isEmpty()) return selector;

        Map<String, String> map = mapping.get(entityClass);
        String property = (map != null) ? map.get(selector) : null;

        if (property != null) {
            LOG.log(Level.INFO, "Found mapping {0} -> {1}", new Object[]{selector, property});
            return property;
        }

        return selector;
    }



    public void addMapping(Class<?> entityClass, Map<String, String> mapping) {
        this.mapping.put(entityClass, mapping);
    }


    public void addMapping(Class<?> entityClass, String selector, String property) {
        mapping.get(entityClass).put(selector, property);
    }


    public Map<Class<?>, Map<String, String>> getMapping() {
        return mapping;
    }


    public void setMapping(Map<Class<?>, Map<String, String>> mapping) {
        this.mapping = mapping;
    }

}