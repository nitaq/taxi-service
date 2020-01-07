package com.internship.amazingtaxiservice.taxiservice.rsql.jpa;

import com.github.tennaito.rsql.builder.BuilderTools;
import com.github.tennaito.rsql.builder.SimpleBuilderTools;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;

import javax.persistence.EntityManager;


public abstract class AbstractJpaVisitor<T, E> implements RSQLVisitor<T, EntityManager> {

    protected Class<E> entityClass;

    protected BuilderTools builderTools;


    public AbstractJpaVisitor(E... e) {
        // getting class from template... :P
        if (e.length == 0) {
            entityClass = (Class<E>) e.getClass().getComponentType();
        } else {
            entityClass = (Class<E>) e[0].getClass();
        }
    }


    public void setEntityClass(Class<E> clazz) {
        entityClass = clazz;
    }


    public BuilderTools getBuilderTools() {
        if (this.builderTools == null) {
            this.builderTools = new SimpleBuilderTools();
        }
        return this.builderTools;
    }


    public void setBuilderTools(BuilderTools delegate) {
        this.builderTools = delegate;
    }
}