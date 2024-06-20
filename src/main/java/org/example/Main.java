package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) throws SQLException {

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test")) {
            createTable(connection);
            insertData(connection);
            String age = "55";
            System.out.println(selectData(connection, age));

            updatePersons(connection);
            selectActivePerson(connection);
        } catch (SQLException e) {
            System.err.println("Что-то не так с БД: " + e.getSQLState());
        }
    }

    private static List<String> selectData(Connection connection, String age) throws  SQLException{
        try (PreparedStatement statement = connection.prepareStatement(
                "select name from person where age = ?")) {
            statement.setInt(1, Integer.parseInt(age));
            ResultSet res = statement.executeQuery();

            List<String> list = new ArrayList<>();
            while(res.next()){
                list.add(res.getString("name"));
            }
            return list;
        }
    }

    private static void updatePersons(Connection connection) throws SQLException{
        try (Statement statement = connection.createStatement()) {
            int count = statement.executeUpdate("""
                    update person set active = true where id between 5 and 10
                    """);
            System.out.println(count);
        }
    }


    private static void selectActivePerson(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet res = statement.executeQuery("""
                    select id, name, age
                    from person
                    where active is true
                    """);
            while (res.next()) {
                long id = res.getLong("id");
                String name = res.getString("name");
                int age = res.getInt("age");
                System.out.printf("%s, %s, %s\n", id, name, age);
            }
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
        } catch (SQLException e) {
            System.err.println("insert: " + e.getMessage());
            throw e;
        }
    }
}