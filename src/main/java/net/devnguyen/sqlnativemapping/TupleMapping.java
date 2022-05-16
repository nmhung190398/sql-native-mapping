package net.devnguyen.sqlnativemapping;

import javax.persistence.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

public class TupleMapping {

    public static Map<Class<?>, FieldMap> classObjectClassMappingMap = new HashMap<>();

    public static <T> T map(javax.persistence.Tuple tuple, Class<T> tClass) {
        Constructor<T> constructor = null;
        try {
            constructor = tClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot no args constructor " + tClass.getName(), e);
        }
        T object = null;
        try {
            object = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        FieldMap fieldMapper = classObjectClassMappingMap.get(tClass);
        if (fieldMapper == null) {
            fieldMapper = new FieldMap(tClass);
            classObjectClassMappingMap.put(tClass, fieldMapper);
        }
        Set<String> checkDuplicateAlias = new HashSet<>();
        for (TupleElement<?> element : tuple.getElements()) {
            if (checkDuplicateAlias.contains(element.getAlias())) {
                throw new RuntimeException("Duplicate alias : " + element.getAlias());
            }
            checkDuplicateAlias.add(element.getAlias());
            FiledMapItem filedMapItem = fieldMapper.get(element.getAlias());
            if (filedMapItem != null) {
                filedMapItem.setValue(object, tuple.get(element.getAlias()), element.getJavaType());
            }
        }
        return object;
    }

    public static <T> List<T> map(List<Tuple> tuples, Class<T> zClass) {
        List<T> result = new ArrayList<>();
        for (Tuple tuple : tuples) {
            result.add(map(tuple, zClass));
        }
        return result;
    }

    public static <T> List<T> executeNativeQuery(EntityManager entityManager, String sql, Class<T> tClass, Consumer<Query> queryConsumer) {
        Query query = entityManager.createNativeQuery(sql, Tuple.class);
        if(queryConsumer != null){
            queryConsumer.accept(query);
        }
        List<Tuple> list = (List<Tuple>) query.getResultList();
        return map(list, tClass);
    }

    public static <T> List<T> executeNativeQuery(EntityManager entityManager, String sql, Class<T> tClass) {
        return executeNativeQuery(entityManager,sql,tClass);
    }

}
