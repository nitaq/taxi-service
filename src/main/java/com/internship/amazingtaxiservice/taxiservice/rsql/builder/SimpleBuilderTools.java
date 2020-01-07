package com.internship.amazingtaxiservice.taxiservice.rsql.builder;

import com.github.tennaito.rsql.jpa.PredicateBuilderStrategy;
import com.github.tennaito.rsql.misc.ArgumentParser;
import com.github.tennaito.rsql.misc.DefaultArgumentParser;
import com.github.tennaito.rsql.misc.Mapper;
import com.github.tennaito.rsql.misc.SimpleMapper;


public class SimpleBuilderTools implements BuilderTools {

    private Mapper mapper;
    private ArgumentParser argumentParser;
    private PredicateBuilderStrategy delegate;


    public void setPropertiesMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    public void setArgumentParser(ArgumentParser argumentParser) {
        this.argumentParser = argumentParser;
    }

    public void setPredicateBuilder(PredicateBuilderStrategy predicateStrategy) {
        this.delegate = predicateStrategy;
    }


    public Mapper getPropertiesMapper() {
        if (this.mapper == null) {
            this.mapper = new SimpleMapper();
        }
        return this.mapper;
    }


    public ArgumentParser getArgumentParser() {
        if (this.argumentParser == null) {
            this.argumentParser = new DefaultArgumentParser();
        }
        return this.argumentParser;
    }


    public PredicateBuilderStrategy getPredicateBuilder() {
        return this.delegate;
    }
}
