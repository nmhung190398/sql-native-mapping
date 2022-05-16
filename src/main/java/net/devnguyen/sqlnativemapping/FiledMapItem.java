package net.devnguyen.sqlnativemapping;

import java.lang.reflect.Field;

public class FiledMapItem {
    final Field field;
    final FieldMap.TupleElementAliasAttribute tupleElementAlias;

    public FiledMapItem(Field field, FieldMap.TupleElementAliasAttribute tupleElementAlias) {
        this.field = field;
        this.tupleElementAlias = tupleElementAlias;
    }

    public <T> void setValue(T object, Object value, Class<?> vClass) {
        try {
            if (value == null && vClass.equals(Object.class)) {
                return;
            }
            Object newValue = parseObject(value, vClass);
            this.field.set(object, newValue);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException("Can not set value for " + field.getName(), e);
        }
    }

    //core mapping
    private Object parseObject(Object value, Class<?> vClass) {
        if (field.getType().equals(vClass)) {
            return value;
        }
        if (Number.class.isAssignableFrom(field.getType()) && Number.class.isAssignableFrom(vClass)) {
            return ParseUtils.parseNumber((Number) value, field.getType());
        }
        if (ParseUtils.isNumberPrimitiveData(field.getType()) && Number.class.isAssignableFrom(vClass)) {
            return ParseUtils.parseNumber((Number) value, field.getType());
        }

        //target is java.sql.* or java.util.Date
        if ( ParseUtils.isDateOrJava8Date(field.getType()) && java.util.Date.class.isAssignableFrom(vClass)) {
            return ParseUtils.parseDate((java.util.Date) value, field.getType());
        }

        //target is java 8 date
        if ( ParseUtils.isDateOrJava8Date(field.getType()) && ParseUtils.isJava8Date(vClass)) {
            return ParseUtils.parseDateJava8(value, field.getType());
        }

        if (String.class.isAssignableFrom(field.getType())) {
            return value != null ? value.toString() : null;
        }
        if(vClass.equals(Boolean.class) && field.getType().equals(boolean.class)){
            return value == null ? Boolean.FALSE : value;
        }
        throw new SqlNativeMappingException(this.field + "  cannot parse " + vClass.getName() + " to " + field.getType().getName());
    }
}
