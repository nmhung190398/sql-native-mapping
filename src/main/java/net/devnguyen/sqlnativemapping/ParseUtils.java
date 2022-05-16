package net.devnguyen.sqlnativemapping;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParseUtils {
    public static boolean isNumberPrimitiveData(Class<?> aClass) {
        return numberPrimitiveData.contains(aClass);
    }

    public static boolean isJava8Date(Class<?> aClass) {
        return java8DateClass.contains(aClass);
    }

    private static final Set<Class<?>> numberPrimitiveData = Stream.of(int.class, long.class, double.class, float.class, short.class, byte.class)
            .collect(Collectors.toSet());
    private static final Set<Class<?>> java8DateClass = Stream.of(Instant.class, LocalDate.class, LocalTime.class, LocalDateTime.class).collect(Collectors.toSet());
    private static final Map<Class<?>, Function<Number, ? extends Number>> numberMap = new HashMap<>();
    private static final Map<Class<?>, Function<java.util.Date, ?>> dateMap = new HashMap<>();


    private static final Map<Class<?>, Function<LocalDateTime, ?>> localDateTimeMap = new HashMap<>();
    private static final Map<Class<?>, Function<LocalTime, ?>> localTimeMap = new HashMap<>();
    private static final Map<Class<?>, Function<LocalDate, ?>> localDateMap = new HashMap<>();
    private static final Map<Class<?>, Function<Instant, ?>> instantMap = new HashMap<>();


    static {
        numberMap.put(int.class, x -> functionOrDefault(x, Number::intValue, 0));
        numberMap.put(long.class, x -> functionOrDefault(x, Number::longValue, 0));
        numberMap.put(double.class, x -> functionOrDefault(x, Number::doubleValue, 0));
        numberMap.put(float.class, x -> functionOrDefault(x, Number::floatValue, 0));
        numberMap.put(short.class, x -> functionOrDefault(x, Number::shortValue, 0));
        numberMap.put(byte.class, x -> functionOrDefault(x, Number::byteValue, 0));
        numberMap.put(Integer.class, x -> functionOrDefault(x, Number::intValue, null));
        numberMap.put(Long.class, x -> functionOrDefault(x, Number::longValue, null));
        numberMap.put(Double.class, x -> functionOrDefault(x, Number::doubleValue, null));
        numberMap.put(Float.class, x -> functionOrDefault(x, Number::floatValue, null));
        numberMap.put(Short.class, x -> functionOrDefault(x, Number::shortValue, null));
        numberMap.put(Byte.class, x -> functionOrDefault(x, Number::byteValue, null));
        numberMap.put(BigDecimal.class, x -> functionOrDefault(x, a -> BigDecimal.valueOf(a.doubleValue()), null));
        numberMap.put(BigInteger.class, x -> functionOrDefault(x, a -> BigInteger.valueOf(a.intValue()), null));


        dateMap.put(java.util.Date.class, x -> functionOrDefault(x, a -> (java.util.Date) a.clone(), null));
        dateMap.put(java.sql.Date.class, x -> functionOrDefault(x, a -> new java.sql.Date(a.getTime()), null));
        dateMap.put(java.sql.Timestamp.class, x -> functionOrDefault(x, a -> new java.sql.Timestamp(a.getTime()), null));
        dateMap.put(java.sql.Time.class, x -> functionOrDefault(x, a -> new java.sql.Time(a.getTime()), null));
        dateMap.put(Instant.class, x -> functionOrDefault(x, a -> Instant.ofEpochMilli(a.getTime()), null));
        dateMap.put(LocalDate.class, x -> functionOrDefault(x, a -> Instant.ofEpochMilli(a.getTime()).atZone(ZoneId.systemDefault()).toLocalDate(), null));
        dateMap.put(LocalTime.class, x -> functionOrDefault(x, a -> Instant.ofEpochMilli(a.getTime()).atZone(ZoneId.systemDefault()).toLocalTime(), null));
        dateMap.put(LocalDateTime.class, x -> functionOrDefault(x, a -> Instant.ofEpochMilli(a.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime(), null));

        localDateTimeMap.put(LocalTime.class, x -> functionOrDefault(x, LocalDateTime::toLocalTime, null));
        localDateTimeMap.put(LocalDate.class, x -> functionOrDefault(x, LocalDateTime::toLocalDate, null));
        localDateTimeMap.put(Instant.class, x -> functionOrDefault(x, a -> {
            OffsetDateTime odt = OffsetDateTime.now(ZoneId.systemDefault());
            return a.toInstant(odt.getOffset());
        }, null));

        localDateTimeMap.put(java.sql.Date.class, x -> functionOrDefault(x, a -> java.sql.Date.valueOf(a.toLocalDate()), null));
        localDateTimeMap.put(java.sql.Timestamp.class, x -> functionOrDefault(x, Timestamp::valueOf, null));
        localDateTimeMap.put(java.sql.Time.class, x -> functionOrDefault(x, a -> java.sql.Time.valueOf(a.toLocalTime()), null));
        localDateTimeMap.put(java.util.Date.class, x -> functionOrDefault(x, a -> java.util.Date.from((Instant) localDateTimeMap.get(Instant.class).apply(a)), null));


        localTimeMap.put(LocalDateTime.class, x -> functionOrDefault(x, a -> a.atDate(LocalDate.MIN), null));
        localTimeMap.put(LocalDate.class, x -> new SqlNativeMappingException("cannot parse LocalDate to LocalTime"));
        localTimeMap.put(Instant.class, x -> functionOrDefault(x, a -> {
            OffsetDateTime odt = OffsetDateTime.now(ZoneId.systemDefault());
            return a.atDate(LocalDate.MIN).toInstant(odt.getOffset());
        }, null));

        localTimeMap.put(java.sql.Date.class, x -> functionOrDefault(x, a -> new java.sql.Date(Time.valueOf(a).getTime()), null));
        localTimeMap.put(java.sql.Timestamp.class, x -> functionOrDefault(x, a -> new Timestamp(Time.valueOf(a).getTime()), null));
        localTimeMap.put(java.sql.Time.class, x -> functionOrDefault(x, Time::valueOf, null));
        localTimeMap.put(java.util.Date.class, x -> functionOrDefault(x, a -> new java.util.Date(Time.valueOf(a).getTime()), null));

        localDateMap.put(LocalDateTime.class, x -> functionOrDefault(x, LocalDate::atStartOfDay, null));
        localDateMap.put(LocalTime.class, x -> new SqlNativeMappingException("cannot parse LocalTime to LocalDate"));
        localDateMap.put(Instant.class, x -> functionOrDefault(x, a -> {
            OffsetDateTime odt = OffsetDateTime.now(ZoneId.systemDefault());
            return a.atStartOfDay().toInstant(odt.getOffset());
        }, null));

        localDateMap.put(java.sql.Date.class, x -> functionOrDefault(x, java.sql.Date::valueOf, null));
        localDateMap.put(java.sql.Timestamp.class, x -> functionOrDefault(x, a -> new Timestamp(java.sql.Date.valueOf(a).getTime()), null));
        localDateMap.put(java.sql.Time.class, x -> functionOrDefault(x, a -> new Time(java.sql.Date.valueOf(a).getTime()), null));
        localDateMap.put(java.util.Date.class, x -> functionOrDefault(x, a -> new java.util.Date(java.sql.Date.valueOf(a).getTime()), null));


        instantMap.put(LocalDateTime.class, x -> functionOrDefault(x, a -> LocalDateTime.ofInstant(a,ZoneId.systemDefault()), null));
        instantMap.put(LocalDate.class, x -> functionOrDefault(x, a -> LocalDateTime.ofInstant(a,ZoneId.systemDefault()).toLocalDate(), null));
        instantMap.put(LocalTime.class, x -> functionOrDefault(x, a -> LocalDateTime.ofInstant(a,ZoneId.systemDefault()).toLocalTime(), null));

        instantMap.put(java.sql.Date.class, x -> functionOrDefault(x, a ->new java.sql.Date(java.util.Date.from(a).getTime()), null));
        instantMap.put(java.sql.Timestamp.class, x -> functionOrDefault(x, a -> new Timestamp(java.util.Date.from(a).getTime()), null));
        instantMap.put(java.sql.Time.class, x -> functionOrDefault(x,a -> new Time(java.util.Date.from(a).getTime()), null));
        instantMap.put(java.util.Date.class, x -> functionOrDefault(x, java.util.Date::from, null));


    }

    public static boolean isDateOrJava8Date(Class<?> aClass){
        return java.util.Date.class.isAssignableFrom(aClass) || ParseUtils.isJava8Date(aClass);
    }

    public static Object parseDateJava8(Object value,Class<?> vClass){
        if(value == null){
            return null;
        }
        Function fn = null;
        if(Instant.class.equals(value.getClass())){
            fn = instantMap.get(vClass);
        }
        if(LocalDateTime.class.equals(value.getClass())){
            fn = localDateTimeMap.get(vClass);
        }
        if(LocalDate.class.equals(value.getClass())){
            fn = localDateMap.get(vClass);
        }
        if(LocalTime.class.equals(value.getClass())){
            fn = localTimeMap.get(vClass);
        }
        if(fn == null){
            throw new SqlNativeMappingException("cannot parse " + vClass);
        }
        return fn.apply(value);
    }

    public static Object parseDate(java.util.Date value, Class<?> vClass) {
        Function<java.util.Date, ?> fn = dateMap.get(vClass);
        if (fn == null) {
            throw new RuntimeException("not support parse " + vClass + " to " + Number.class);
        }
        return fn.apply(value);
    }

    public static Object parseNumber(Number value, Class<?> vClass) {
        Function<Number, ? extends Number> fn = numberMap.get(vClass);
        if (fn == null) {
            throw new RuntimeException("not support parse " + vClass + " to " + Number.class);
        }
        return fn.apply(value);
    }

    private static <T, K> K functionOrDefault(T value, Function<T, K> function, K defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return function.apply(value);
    }


}
