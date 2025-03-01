package com.lartimes.test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.ServiceLoader;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/3/1 17:31
 */
public class DriverTest {
    public static void main(String[] args) throws SQLException {
        ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class);
        for (Driver driver : drivers) {
            System.out.println(driver);
        }
        System.out.println("DriverManager....");
        Enumeration<Driver> drivers1 = DriverManager.getDrivers();
        while (drivers1.hasMoreElements()) {
            Driver driver = drivers1.nextElement();
            System.out.println(driver);
        }
//          JDBC spi 的应用
        String url = "jdbc:mysql://localhost:3306/user_storage";
        String user = "root";
        String password = "307314";
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

    }
}
