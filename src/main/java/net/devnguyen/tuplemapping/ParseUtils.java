package net.devnguyen.tuplemapping;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParseUtils {
    public static boolean isNumberPrimitiveData(Class<?> aClass){
        return numberPrimitiveData.contains(aClass);
    }
    public static boolean isJava8Date(Class<?> aClass){
        return java8DateClass.contains(aClass);
    }
    private static final Set<Class<?>> numberPrimitiveData = Stream.of(int.class,long.class,double.class,float.class,short.class,byte.class)
            .collect(Collectors.toSet());
    private static final Set<Class<?>> java8DateClass = Stream.of(Instant.class,LocalDate.class,LocalTime.class,LocalDateTime.class).collect(Collectors.toSet());
    private static final Map<Class<?>, Function<Number,? extends Number>> numberMap = new HashMap<>();
    private static final Map<Class<?>, Function<java.util.Date,?>> dateMap = new HashMap<>();
    static {
        numberMap.put(int.class,x -> functionOrDefault(x, Number::intValue,0));
        numberMap.put(long.class,x -> functionOrDefault(x, Number::longValue,0));
        numberMap.put(double.class,x -> functionOrDefault(x, Number::doubleValue,0));
        numberMap.put(float.class,x -> functionOrDefault(x, Number::floatValue,0));
        numberMap.put(short.class,x -> functionOrDefault(x, Number::shortValue,0));
        numberMap.put(byte.class,x -> functionOrDefault(x, Number::byteValue,0));
        numberMap.put(Integer.class,x -> functionOrDefault(x, Number::intValue,null));
        numberMap.put(Long.class,x -> functionOrDefault(x, Number::longValue,null));
        numberMap.put(Double.class,x -> functionOrDefault(x, Number::doubleValue,null));
        numberMap.put(Float.class,x -> functionOrDefault(x, Number::floatValue,null));
        numberMap.put(Short.class,x -> functionOrDefault(x, Number::shortValue,null));
        numberMap.put(Byte.class,x -> functionOrDefault(x, Number::byteValue,null));
        numberMap.put(BigDecimal.class, x -> functionOrDefault(x, a -> BigDecimal.valueOf(a.doubleValue()),null));
        numberMap.put(BigInteger.class, x -> functionOrDefault(x, a -> BigInteger.valueOf(a.intValue()),null));


        dateMap.put(java.util.Date.class, x -> functionOrDefault(x, a -> (java.util.Date) a.clone(),null));
        dateMap.put(java.sql.Date.class, x -> functionOrDefault(x, a -> new java.sql.Date(a.getTime()),null));
        dateMap.put(java.sql.Timestamp.class, x -> functionOrDefault(x, a -> new java.sql.Timestamp(a.getTime()),null));
        dateMap.put(java.sql.Time.class, x -> functionOrDefault(x, a -> new java.sql.Time(a.getTime()),null));
        dateMap.put(Instant.class, x -> functionOrDefault(x, a -> Instant.ofEpochMilli(a.getTime()),null));
        dateMap.put(LocalDate.class, x -> functionOrDefault(x, a -> Instant.ofEpochMilli(a.getTime()).atZone(ZoneId.systemDefault()).toLocalDate(),null));
        dateMap.put(LocalTime.class, x -> functionOrDefault(x, a -> Instant.ofEpochMilli(a.getTime()).atZone(ZoneId.systemDefault()).toLocalTime(),null));
        dateMap.put(LocalDateTime.class, x -> functionOrDefault(x, a -> Instant.ofEpochMilli(a.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime(),null));
    }
    public static Object parseDate(java.util.Date value, Class<?> vClass) {
        Function<java.util.Date,?> fn = dateMap.get(vClass);
        if(fn == null){
            throw new RuntimeException("not support parse " + vClass + " to " + Number.class);
        }
        return fn.apply(value);
    }
    public static Object parseNumber(Number value, Class<?> vClass) {
        Function<Number,? extends Number> fn = numberMap.get(vClass);
        if(fn == null){
            throw new RuntimeException("not support parse " + vClass + " to " + Number.class);
        }
        return fn.apply(value);
    }
    private static <T,K> K functionOrDefault(T value,Function<T,K> function,K defaultValue){
        if(value == null){
            return defaultValue;
        }
        return function.apply(value);
    }


}
