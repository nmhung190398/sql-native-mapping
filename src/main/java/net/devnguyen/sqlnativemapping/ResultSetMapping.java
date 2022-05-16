package net.devnguyen.sqlnativemapping;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

public class ResultSetMapping {

    public static Map<Class<?>, FieldMap> classObjectClassMappingMap = new HashMap<>();

    public static <T> List<T> map(ResultSet resultSet, Class<T> tClass) throws SQLException {
        List<T> result = new ArrayList<T>();
        Set<String> columnNames = getColumnNames(resultSet);
        while (resultSet.next()) {
            result.add(map(resultSet, columnNames, tClass));
        }
        return result;
    }

    private static <T> T map(ResultSet resultSet, Set<String> columnNames, Class<T> tClass) throws SQLException {
        Constructor<T> constructor = null;
        T object = null;
        try {
            constructor = tClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot no args constructor " + tClass.getName(), e);
        }
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
        for (String columnName : columnNames) {
            Object resultValue = resultSet.getObject(columnName);
            Class<?> type = resultValue == null ? Object.class : resultValue.getClass();
            FiledMapItem filedMapItem = fieldMapper.get(columnName);
            if (filedMapItem != null) {
                filedMapItem.setValue(object, resultValue, type);
            }
        }

        return object;
    }


    private static Set<String> getColumnNames(ResultSet resultSet) throws SQLException {
        Set<String> columnNames = new HashSet<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); ++i) {
            columnNames.add(metaData.getColumnName(i));
        }
        return columnNames;
    }

    private static String DB_URL = "jdbc:mysql://localhost:3306/tuple-mapping";
    private static String USER_NAME = "root";
    private static String PASSWORD = "123456";

    public static Connection getConnection(String dbURL, String userName,
                                           String password) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL, userName, password);
            System.out.println("connect successfully!");
        } catch (Exception ex) {
            System.out.println("connect failure!");
            ex.printStackTrace();
        }
        return conn;
    }


}
