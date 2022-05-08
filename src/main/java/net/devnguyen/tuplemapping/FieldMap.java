package net.devnguyen.tuplemapping;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FieldMap extends HashMap<String, FiledMapItem> {
    private final Class<?> type;

    @Override
    public FiledMapItem get(Object key) {
        if(key != null){
            key = key.toString().toUpperCase();
        }
        return super.get(key);
    }

    @Override
    public FiledMapItem put(String key, FiledMapItem value) {
        if(key != null){
            key = key.toString().toUpperCase();
        }
        return super.put(key, value);
    }

    @Override
    public FiledMapItem getOrDefault(Object key, FiledMapItem defaultValue) {
        if(key != null){
            key = key.toString().toUpperCase();
        }
        return super.getOrDefault(key, defaultValue);
    }

    public FieldMap(Class<?> type) {
        this.type = type;
        Field[] fields = type.getDeclaredFields();
        Set<String> checkDuplicateAlias = new HashSet<>();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(TupleElementAlias.class)){
                return;
            }
            TupleElementAliasAttribute tupleElementAliasAttribute = new TupleElementAliasAttribute(field);

            if (checkDuplicateAlias.contains(tupleElementAliasAttribute.getName())) {
                throw new TupleMappingException("Duplicate alias name : " + tupleElementAliasAttribute.getName() + " " + type.getName());
            }
            checkDuplicateAlias.add(tupleElementAliasAttribute.getName());
            field.setAccessible(true);
            this.put(tupleElementAliasAttribute.getName(), new FiledMapItem(field, tupleElementAliasAttribute));

        }
        if (this.isEmpty()) {
            throw new TupleMappingException("Cannot find TupleElementAlias annotation in " + type.getName());
        }
    }

    public static class TupleElementAliasAttribute{
        private Field field;
        private TupleElementAlias tupleElementAlias;
        private String name;


        public TupleElementAliasAttribute(Field field) {
            this.field = field;
            this.tupleElementAlias = field.getDeclaredAnnotation(TupleElementAlias.class);
            detectAttribute();
        }

        private void detectAttribute() {
            if(StringUtils.isBlank(tupleElementAlias.name())){
                throw new TupleMappingException("TupleElementAlias name is not blank");
            }else{
                this.name = tupleElementAlias.name().toUpperCase();
            }
        }

        public String getName() {
            return name;
        }
    }
}
