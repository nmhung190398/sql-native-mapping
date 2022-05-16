package net.devnguyen.sqlnativemapping;

import java.sql.SQLException;

@FunctionalInterface
public interface SupplierSQLException<T> {
    T get() throws SQLException;
}
