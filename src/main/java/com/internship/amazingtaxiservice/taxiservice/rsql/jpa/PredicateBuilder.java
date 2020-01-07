package com.internship.amazingtaxiservice.taxiservice.rsql.jpa;

import com.github.tennaito.rsql.builder.BuilderTools;
import com.internship.amazingtaxiservice.taxiservice.rsql.parser.ast.ComparisonOperatorProxy;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.Node;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class PredicateBuilder {

    private static final Logger LOG = Logger.getLogger(PredicateBuilder.class.getName());

    public static final Character LIKE_WILDCARD = '*';

    private static final Date START_DATE;
    private static final Date END_DATE;

    static {
        Calendar cal = Calendar.getInstance();
        cal.set(9999, Calendar.DECEMBER, 31);
        END_DATE = cal.getTime();
        cal.set(5, Calendar.JANUARY, 1);
        START_DATE = cal.getTime();
    }


    private PredicateBuilder() {
        super();
    }


    public static <T> Predicate createPredicate(Node node, From root, Class<T> entity, EntityManager manager, BuilderTools misc) {
        LOG.log(Level.INFO, "Creating Predicate for: {0}", node);

        if (node instanceof LogicalNode) {
            return createPredicate((LogicalNode) node, root, entity, manager, misc);
        }

        if (node instanceof ComparisonNode) {
            return createPredicate((ComparisonNode) node, root, entity, manager, misc);
        }

        throw new IllegalArgumentException("Unknown expression type: " + node.getClass());
    }


    public static <T> Predicate createPredicate(LogicalNode logical, From root, Class<T> entity, EntityManager entityManager, BuilderTools misc) {
        LOG.log(Level.INFO, "Creating Predicate for logical node: {0}", logical);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        List<Predicate> predicates = new ArrayList<Predicate>();

        LOG.log(Level.INFO, "Creating Predicates from all children nodes.");
        for (Node node : logical.getChildren()) {
            predicates.add(createPredicate(node, root, entity, entityManager, misc));
        }

        switch (logical.getOperator()) {
            case AND:
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            case OR:
                return builder.or(predicates.toArray(new Predicate[predicates.size()]));
        }

        throw new IllegalArgumentException("Unknown operator: " + logical.getOperator());
    }


    public static <T> Predicate createPredicate(ComparisonNode comparison, From startRoot, Class<T> entity, EntityManager entityManager, BuilderTools misc) {
        if (startRoot == null) {
            String msg = "From root node was undefined.";
            LOG.log(Level.SEVERE, msg);
            throw new IllegalArgumentException(msg);
        }
        LOG.log(Level.INFO, "Creating Predicate for comparison node: {0}", comparison);

        LOG.log(Level.INFO, "Property graph path : {0}", comparison.getSelector());
        Expression propertyPath = findPropertyPath(comparison.getSelector(), startRoot, entityManager, misc);

        LOG.log(Level.INFO, "Cast all arguments to type {0}.", propertyPath.getJavaType().getName());
        List<Object> castedArguments = misc.getArgumentParser().parse(comparison.getArguments(), propertyPath.getJavaType());

        try {
            // try to create a predicate
            return PredicateBuilder.createPredicate(propertyPath, comparison.getOperator(), castedArguments, entityManager);
        } catch (IllegalArgumentException e) {
            // if operator dont exist try to delegate
            if (misc.getPredicateBuilder() != null) {
                return misc.getPredicateBuilder().createPredicate(comparison, startRoot, entity, entityManager, misc);
            }
            // if no strategy was defined then there are no more operators.
            throw e;
        }
    }


    public static <T> Path<?> findPropertyPath(String propertyPath, Path startRoot, EntityManager entityManager, BuilderTools misc) {
        String[] graph = propertyPath.split("\\.");

        Metamodel metaModel = entityManager.getMetamodel();
        ManagedType<?> classMetadata = metaModel.managedType(startRoot.getJavaType());

        Path<?> root = startRoot;

        for (String property : graph) {
            String mappedProperty = misc.getPropertiesMapper().translate(property, classMetadata.getJavaType());
            if (!mappedProperty.equals(property)) {
                root = findPropertyPath(mappedProperty, root, entityManager, misc);
            } else {
                if (!hasPropertyName(mappedProperty, classMetadata)) {
                    throw new IllegalArgumentException("Unknown property: " + mappedProperty + " from entity " + classMetadata.getJavaType().getName());
                }

                if (isAssociationType(mappedProperty, classMetadata)) {
                    Class<?> associationType = findPropertyType(mappedProperty, classMetadata);
                    String previousClass = classMetadata.getJavaType().getName();
                    classMetadata = metaModel.managedType(associationType);
                    LOG.log(Level.INFO, "Create a join between {0} and {1}.", new Object[]{previousClass, classMetadata.getJavaType().getName()});

                    if (root instanceof Join) {
                        root = root.get(mappedProperty);
                    } else {
                        root = ((From) root).join(mappedProperty);
                    }
                } else {
                    LOG.log(Level.INFO, "Create property path for type {0} property {1}.", new Object[]{classMetadata.getJavaType().getName(), mappedProperty});
                    root = root.get(mappedProperty);

                    if (isEmbeddedType(mappedProperty, classMetadata)) {
                        Class<?> embeddedType = findPropertyType(mappedProperty, classMetadata);
                        classMetadata = metaModel.managedType(embeddedType);
                    }
                }
            }
        }

        return root;
    }


    private static Predicate createPredicate(Expression propertyPath, ComparisonOperator operator, List<Object> arguments, EntityManager manager) {
        LOG.log(Level.INFO, "Creating predicate: propertyPath {0} {1}", new Object[]{operator, arguments});

        if (ComparisonOperatorProxy.asEnum(operator) != null) {
            switch (ComparisonOperatorProxy.asEnum(operator)) {
                case EQUAL: {
                    Object argument = arguments.get(0);
                    if (argument instanceof String) {
                        return createLike(propertyPath, (String) argument, manager);
                    } else if (isNullArgument(argument)) {
                        return createIsNull(propertyPath, manager);
                    } else {
                        return createEqual(propertyPath, argument, manager);
                    }
                }
                case NOT_EQUAL: {
                    Object argument = arguments.get(0);
                    if (argument instanceof String) {
                        return createNotLike(propertyPath, (String) argument, manager);
                    } else if (isNullArgument(argument)) {
                        return createIsNotNull(propertyPath, manager);
                    } else {
                        return createNotEqual(propertyPath, argument, manager);
                    }
                }
                case GREATER_THAN: {
                    Object argument = arguments.get(0);
                    Predicate predicate;
                    if (argument instanceof Date) {
                        int days = 1;
                        predicate = createBetweenThan(propertyPath, modifyDate(argument, days), END_DATE, manager);
                    } else if (argument instanceof Number || argument == null) {
                        predicate = createGreaterThan(propertyPath, (Number) argument, manager);
                    } else if (argument instanceof Comparable) {
                        predicate = createGreaterThanComparable(propertyPath, (Comparable) argument, manager);
                    } else {
                        throw new IllegalArgumentException(buildNotComparableMessage(operator, argument));
                    }
                    return predicate;
                }
                case GREATER_THAN_OR_EQUAL: {
                    Object argument = arguments.get(0);
                    Predicate predicate;
                    if (argument instanceof Date) {
                        predicate = createBetweenThan(propertyPath, (Date) argument, END_DATE, manager);
                    } else if (argument instanceof Number || argument == null) {
                        predicate = createGreaterEqual(propertyPath, (Number) argument, manager);
                    } else if (argument instanceof Comparable) {
                        predicate = createGreaterEqualComparable(propertyPath, (Comparable) argument, manager);
                    } else {
                        throw new IllegalArgumentException(buildNotComparableMessage(operator, argument));
                    }
                    return predicate;

                }
                case LESS_THAN: {
                    Object argument = arguments.get(0);
                    Predicate predicate;
                    if (argument instanceof Date) {
                        int days = -1;
                        predicate = createBetweenThan(propertyPath, START_DATE, modifyDate(argument, days), manager);
                    } else if (argument instanceof Number || argument == null) {
                        predicate = createLessThan(propertyPath, (Number) argument, manager);
                    } else if (argument instanceof Comparable) {
                        predicate = createLessThanComparable(propertyPath, (Comparable) argument, manager);
                    } else {
                        throw new IllegalArgumentException(buildNotComparableMessage(operator, argument));
                    }
                    return predicate;
                }
                case LESS_THAN_OR_EQUAL: {
                    Object argument = arguments.get(0);

                    Predicate predicate;
                    if (argument instanceof Date) {
                        predicate = createBetweenThan(propertyPath, START_DATE, (Date) argument, manager);
                    } else if (argument instanceof Number || argument == null) {
                        predicate = createLessEqual(propertyPath, (Number) argument, manager);
                    } else if (argument instanceof Comparable) {
                        predicate = createLessEqualComparable(propertyPath, (Comparable) argument, manager);
                    } else {
                        throw new IllegalArgumentException(buildNotComparableMessage(operator, argument));
                    }
                    return predicate;
                }
                case IN:
                    return createIn(propertyPath, arguments, manager);
                case NOT_IN:
                    return createNotIn(propertyPath, arguments, manager);
            }
        }
        throw new IllegalArgumentException("Unknown operator: " + operator);
    }


    private static Predicate createBetweenThan(Expression propertyPath, Date start, Date end, EntityManager manager) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.between(propertyPath, start, end);
    }


    private static Predicate createLike(Expression<String> propertyPath, String argument, EntityManager manager) {
        String like = argument.replace(LIKE_WILDCARD, '%');
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.like(builder.lower(propertyPath), like.toLowerCase());
    }

    private static Predicate createIsNull(Expression<?> propertyPath, EntityManager manager) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.isNull(propertyPath);
    }


    private static Predicate createEqual(Expression<?> propertyPath, Object argument, EntityManager manager) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.equal(propertyPath, argument);
    }


    private static Predicate createNotEqual(Expression<?> propertyPath, Object argument, EntityManager manager) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.notEqual(propertyPath, argument);
    }


    private static Predicate createNotLike(Expression<String> propertyPath, String argument, EntityManager manager) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.not(createLike(propertyPath, argument, manager));
    }


    private static Predicate createIsNotNull(Expression<?> propertyPath, EntityManager manager) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.isNotNull(propertyPath);
    }


    private static Predicate createGreaterThan(Expression<? extends Number> propertyPath, Number argument, EntityManager manager) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.gt(propertyPath, argument);
    }


    private static <Y extends Comparable<? super Y>> Predicate createGreaterThanComparable(Expression<? extends Y> propertyPath, Y argument, EntityManager manager) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.greaterThan(propertyPath, argument);
    }


    private static Predicate createGreaterEqual(Expression<? extends Number> propertyPath, Number argument, EntityManager manager) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.ge(propertyPath, argument);
    }


    private static <Y extends Comparable<? super Y>> Predicate createGreaterEqualComparable(Expression<? extends Y> propertyPath, Y argument, EntityManager manager) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.greaterThanOrEqualTo(propertyPath, argument);
    }


    private static Predicate createLessThan(Expression<? extends Number> propertyPath, Number argument, EntityManager manager) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.lt(propertyPath, argument);
    }


    private static <Y extends Comparable<? super Y>> Predicate createLessThanComparable(Expression<? extends Y> propertyPath, Y argument, EntityManager manager) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.lessThan(propertyPath, argument);
    }


    private static Predicate createLessEqual(Expression<? extends Number> propertyPath, Number argument, EntityManager manager) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.le(propertyPath, argument);
    }


    private static <Y extends Comparable<? super Y>> Predicate createLessEqualComparable(Expression<? extends Y> propertyPath, Y argument, EntityManager manager) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.lessThanOrEqualTo(propertyPath, argument);
    }


    private static Predicate createIn(Expression<?> propertyPath, List<?> arguments, EntityManager manager) {
        return propertyPath.in(arguments);
    }


    private static Predicate createNotIn(Expression<?> propertyPath, List<?> arguments, EntityManager manager) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        return builder.not(createIn(propertyPath, arguments, manager));
    }


    private static <T> boolean isAssociationType(String property, ManagedType<T> classMetadata) {
        return classMetadata.getAttribute(property).isAssociation();
    }


    private static <T> boolean isEmbeddedType(String property, ManagedType<T> classMetadata) {
        return classMetadata.getAttribute(property).getPersistentAttributeType() == PersistentAttributeType.EMBEDDED;
    }


    private static <T> boolean hasPropertyName(String property, ManagedType<T> classMetadata) {
        Set<Attribute<? super T, ?>> names = classMetadata.getAttributes();
        for (Attribute<? super T, ?> name : names) {
            if (name.getName().equals(property)) return true;
        }
        return false;
    }


    private static <T> Class<?> findPropertyType(String property, ManagedType<T> classMetadata) {
        Class<?> propertyType = null;
        if (classMetadata.getAttribute(property).isCollection()) {
            propertyType = ((PluralAttribute) classMetadata.getAttribute(property)).getBindableJavaType();
        } else {
            propertyType = classMetadata.getAttribute(property).getJavaType();
        }
        return propertyType;
    }


    private static boolean isNullArgument(Object argument) {
        return argument == null;
    }


    private static Date modifyDate(Object argument, int days) {
        Date date = (Date) argument;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        date = c.getTime();
        return date;
    }


    private static String buildNotComparableMessage(ComparisonOperator operator, Object argument) {
        return String.format("Invalid type for comparison operator: %s type: %s must implement Comparable<%s>",
            operator,
            argument.getClass().getName(),
            argument.getClass().getSimpleName());
    }
}