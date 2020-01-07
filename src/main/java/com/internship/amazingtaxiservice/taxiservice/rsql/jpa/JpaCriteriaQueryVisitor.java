package com.internship.amazingtaxiservice.taxiservice.rsql.jpa;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JpaCriteriaQueryVisitor<T> extends AbstractJpaVisitor<CriteriaQuery<T>, T> implements RSQLVisitor<CriteriaQuery<T>, EntityManager> {

    private static final Logger LOG = Logger.getLogger(JpaCriteriaQueryVisitor.class.getName());

    private final JpaPredicateVisitor<T> predicateVisitor;

    public JpaCriteriaQueryVisitor(T... t) {
        super(t);
        this.predicateVisitor = new JpaPredicateVisitor<T>(t);
    }

    protected JpaPredicateVisitor<T> getPredicateVisitor() {
        this.predicateVisitor.setBuilderTools(this.getBuilderTools());
        return this.predicateVisitor;
    }


    public CriteriaQuery<T> visit(AndNode node, EntityManager entityManager) {
        LOG.log(Level.INFO, "Creating CriteriaQuery for AndNode: {0}", node);
        CriteriaQuery<T> criteria = entityManager.getCriteriaBuilder().createQuery(entityClass);
        From root = criteria.from(entityClass);
        return criteria.where(this.getPredicateVisitor().defineRoot(root).visit(node, entityManager));
    }


    public CriteriaQuery<T> visit(OrNode node, EntityManager entityManager) {
        LOG.log(Level.INFO, "Creating CriteriaQuery for OrNode: {0}", node);
        CriteriaQuery<T> criteria = entityManager.getCriteriaBuilder().createQuery(entityClass);
        From root = criteria.from(entityClass);
        return criteria.where(this.getPredicateVisitor().defineRoot(root).visit(node, entityManager));
    }


    public CriteriaQuery<T> visit(ComparisonNode node, EntityManager entityManager) {
        LOG.log(Level.INFO, "Creating CriteriaQuery for ComparisonNode: {0}", node);
        CriteriaQuery<T> criteria = entityManager.getCriteriaBuilder().createQuery(entityClass);
        From root = criteria.from(entityClass);
        return criteria.where(this.getPredicateVisitor().defineRoot(root).visit(node, entityManager));
    }
}