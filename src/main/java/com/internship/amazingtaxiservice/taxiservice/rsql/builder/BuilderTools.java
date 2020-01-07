package com.internship.amazingtaxiservice.taxiservice.rsql.builder;

import com.github.tennaito.rsql.jpa.PredicateBuilderStrategy;
import com.github.tennaito.rsql.misc.ArgumentParser;
import com.github.tennaito.rsql.misc.Mapper;


public interface BuilderTools {

    public Mapper getPropertiesMapper();

    public void setPropertiesMapper(Mapper mapper);

    public ArgumentParser getArgumentParser();

    public void setArgumentParser(ArgumentParser argumentParser);

    public PredicateBuilderStrategy getPredicateBuilder();

    public void setPredicateBuilder(PredicateBuilderStrategy predicateStrategy);
}
