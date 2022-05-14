## Logging with Spring Boot and Elastic Stack  [![Twitter](https://img.shields.io/twitter/follow/piotr_minkowski.svg?style=social&logo=twitter&label=Follow%20Me)](https://twitter.com/piotr_minkowski)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.piomin/logstash-logging-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.piomin/logstash-logging-spring-boot-starter)
[![CircleCI](https://circleci.com/gh/piomin/spring-boot-logging.svg?style=shield)](https://circleci.com/gh/piomin/spring-boot-logging)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=piomin_spring-boot-logging&metric=alert_status)](https://sonarcloud.io/dashboard?id=piomin_spring-boot-logging)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=piomin_spring-boot-logging&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=piomin_spring-boot-logging)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=piomin_spring-boot-logging&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=piomin_spring-boot-logging)

## Main purpose

This library is mapping Tuple to java Object
## Articles

Detailed description can be found here:
1. [Logging with Spring Boot and Elastic Stack](https://piotrminkowski.com/2019/05/07/logging-with-spring-boot-and-elastic-stack/)
2. [Using logstash-logging-spring-boot-starter for logging with Spring Boot and Logstash](https://piotrminkowski.com/2019/10/02/using-logstash-logging-spring-boot-starter-for-logging-with-spring-boot-and-logstash/)

## Features

Support mapping oracle,postgres,mysql
1. sql Number => Number, Primitive Data type (byte, short, double,float , int, long)
2. sql Date => java.time.Instant, java.time.LocalDate, java.time.LocalDateTime, java.time.LocalTime
3. sql Date => java.sql.Date, java.sql.Timestalm, java.sql.Time
4. sql Date => java.util.Date
4. sql Text => String
## Getting started
The library is published on Maven Central. Current version is  b  `1.0-SNAPSHOT`

maven central - coming soon
```
<dependency>
  <groupId>com.github.hungnm</groupId>
  <artifactId>tuple-mapping-spring-boot-starter</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```
coding
step 1. create Entity
```

@Entity
@Table(name = "HNM_TEST")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestEntity {
    @Id
    private Long id;

    @Column(name = "STRING_DATA")
    private String stringData;

    @Column(name = "BOOLEAN_DATA")
    private Boolean booleanData;

    @Column(name = "BOOL_DATA")
    private boolean boolData;

    @Column(name = "INTEGER_DATA")
    private Integer integerData;

    @Column(name = "INT_DATA")
    private int intData;

    @Column(name = "DATE_UTIL_DATA")
    private java.util.Date dateUtilData;

    @Column(name = "DATE_SQL_DATA")
    private java.sql.Date dateSqlData;

    @Column(name = "TIMESTAMP_DATA")
    private java.sql.Timestamp timestampData;

    @Column(name = "TIME_DATA")
    private java.sql.Time timeData;

    @Column(name = "INSTANT_DATA")
    private java.time.Instant instantData;

    @Column(name = "LOCAL_DATE_DATA")
    private java.time.LocalDate localDateData;

    @Column(name = "LOCAL_DATE_TIME_DATA")
    private java.time.LocalDateTime localDateTimeData;

    @Column(name = "LOCAL_TIME_DATA")
    private java.time.LocalTime localTimeData;
}
```

step 2: create DTO and using @TupleElementAlias with name same sql select
1. TestDTO same field TestEntity
2. GroupDTO group data
```
@Data
@NoArgsConstructor
public class TestDTO {

    @TupleElementAlias(name = "ID")
    private Long id;

    @TupleElementAlias(name = "STRING_DATA")
    private String stringData;

    @TupleElementAlias(name = "BOOLEAN_DATA")
    private Boolean booleanData;

    @TupleElementAlias(name = "BOOL_DATA")
    private boolean boolData;

    @TupleElementAlias(name = "INTEGER_DATA")
    private Integer integerData;

    @TupleElementAlias(name = "INT_DATA")
    private int intData;

    @TupleElementAlias(name = "DATE_UTIL_DATA")
    private java.util.Date dateUtilData;

    @TupleElementAlias(name = "DATE_SQL_DATA")
    private java.sql.Date dateSqlData;

    @TupleElementAlias(name = "TIMESTAMP_DATA")
    private java.sql.Timestamp timestampData;

    @TupleElementAlias(name = "TIME_DATA")
    private java.sql.Time timeData;

    @TupleElementAlias(name = "INSTANT_DATA")
    private java.time.Instant instantData;

    @TupleElementAlias(name = "LOCAL_DATE_DATA")
    private java.time.LocalDate localDateData;

    @TupleElementAlias(name = "LOCAL_DATE_TIME_DATA")
    private java.time.LocalDateTime localDateTimeData;

    @TupleElementAlias(name = "LOCAL_TIME_DATA")
    private java.time.LocalTime localTimeData;
}
```

```
@NoArgsConstructor
public class GroupDTO {
    @TupleElementAlias(name = "DATA")
    private Long data;
    @TupleElementAlias(name = "COUNT")
    private BigDecimal count;
}
```
step 3 :  coding

```
//select with Consumer<Query>
List<TestDTO> result01 = TupleMapping.executeNativeQuery(entityManager, "select * FROM HNM_TEST t where t.ID = :id", TestDTO.class, query -> {
    query.setParameter("id", id);
});

//select no Consumer<Query>
List<TestDTO> result02 = TupleMapping.executeNativeQuery(entityManager, "select * FROM HNM_TEST t", TestDTO.class);

//select with custom field name
String sql = "select t.int_data as 'DATA', count(t.int_data) as 'COUNT' from hnm_test t " +
     "group by t.int_data ";
List<GroupDTO> groupDTOS = TupleMapping.executeNativeQuery(entityManager, sql, GroupDTO.class);
```
