package com.internship.amazingtaxiservice.taxiservice.rsql.jpa;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JpaCriteriaCountQueryVisitor<T> extends AbstractJpaVisitor<CriteriaQuery<Long>, T> implements RSQLVisitor<CriteriaQuery<Long>, EntityManager> {

    private static final Logger LOG = Logger.getLogger(JpaCriteriaCountQueryVisitor.class.getName());

    private final JpaPredicateVisitor<T> predicateVisitor;

    private Root<T> root;

    @SafeVarargs
    public JpaCriteriaCountQueryVisitor(T... t) {
        super(t);
        this.predicateVisitor = new JpaPredicateVisitor<T>(t);
    }

    protected JpaPredicateVisitor<T> getPredicateVisitor() {
        this.predicateVisitor.setBuilderTools(this.getBuilderTools());
        return this.predicateVisitor;
    }

    public CriteriaQuery<Long> visit(AndNode node, EntityManager entityManager) {
        LOG.log(Level.INFO, "Creating CriteriaQuery for AndNode: {0}", node);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        root = cq.from(entityClass);
        cq.select(cb.countDistinct(root));
        cq.where(this.getPredicateVisitor().defineRoot(root).visit(node, entityManager));

        return cq;
    }

    public CriteriaQuery<Long> visit(OrNode node, EntityManager entityManager) {
        LOG.log(Level.INFO, "Creating CriteriaQuery for OrNode: {0}", node);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        root = cq.from(entityClass);
        cq.select(cb.countDistinct(root));
        root = cq.from(entityClass);
        cq.where(this.getPredicateVisitor().defineRoot(root).visit(node, entityManager));
        return cq;
    }

    public CriteriaQuery<Long> visit(ComparisonNode node, EntityManager entityManager) {
        LOG.log(Level.INFO, "Creating CriteriaQuery for ComparisonNode: {0}", node);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        root = cq.from(entityClass);
        cq.select(cb.countDistinct(root));
        cq.where(this.getPredicateVisitor().defineRoot(root).visit(node, entityManager));
        return cq;
    }

    public Root<T> getRoot() {
        return root;
    }

    public void setRoot(Root<T> root) {
        this.root = root;
    }

}