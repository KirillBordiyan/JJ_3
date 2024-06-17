package org.example;

import org.h2.util.json.JsonConstructorUtils;

import javax.swing.text.html.Option;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws SQLException {

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test")) {
            createTable(connection);
            insertData(connection);
        } catch (SQLException e) {
            System.err.println("Что-то не так с БД: " + e.getSQLState());
        }
    }


    private static void createTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    create table person (
                        id bigint not null,
                        name varchar(250),
                        age integer,
                        active boolean
                        )""");
        } catch (SQLException e) {
            System.err.println("Во время создания произошла ошибка: " + e.getSQLState());
            throw e;
        }
    }

    private static void insertData(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            StringBuilder str = new StringBuilder("insert into person(id, name, age, active) values");
            for (int i = 0; i <= 10; i++) {
                int age = ThreadLocalRandom.current().nextInt(20, 60);
                boolean active = ThreadLocalRandom.current().nextBoolean();
                str.append(String.format("(%s, '%s', %s, %s)", i, "Person #" + i, age, active));

                if (i != 10) {
                    str.append(",\n");
                }
            }

            int count = statement.executeUpdate(str.toString());
            System.out.println("Вставлено строк " + count);
        } catch (SQLException e){
            System.err.println("insert: "  +e.getMessage());
            throw e;
        }
    }


}