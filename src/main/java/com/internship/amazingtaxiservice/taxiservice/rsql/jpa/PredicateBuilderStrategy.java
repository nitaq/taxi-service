package com.internship.amazingtaxiservice.taxiservice.rsql.jpa;

import com.internship.amazingtaxiservice.taxiservice.rsql.builder.BuilderTools;
import cz.jirutka.rsql.parser.ast.Node;

import javax.persistence.EntityManager;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;


public interface PredicateBuilderStrategy {

    public <T> Predicate createPredicate(Node node, From root, Class<T> entity, EntityManager manager, BuilderTools tools) throws IllegalArgumentException;
}
