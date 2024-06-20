package org.example.hw;

import org.example.hw.dto.DepartmentDto;
import org.example.hw.dto.PersonDto;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Homework {

    /**
     * С помощью JDBC, выполнить следующие пункты:<p>
     * 1. Создать таблицу Person (скопировать код с семниара)<p>
     * 2. Создать таблицу Department (id bigint primary key, name varchar(128) not null)<p>
     * 3. Добавить в таблицу Person поле department_id типа bigint (внешний ключ)<p>
     * 4. Написать метод, который загружает Имя department по Идентификатору person <p>
     * 5. * Написать метод, который загружает Map<String, String>, в которой маппинг person.name -> department.name<p>
     * Пример: [{"person #1", "department #1"}, {"person #2", "department #3}]<p>
     * 6. ** Написать метод, который загружает Map<String, List<String>>, в которой маппинг department.name -> <person.name><p>
     * Пример:<p>
     * [
     * {"department #1", ["person #1", "person #2"]},
     * {"department #2", ["person #3", "person #4"]}
     * ]
     * <p>
     * 7. *** Создать классы-обертки над таблицами, и в пунктах 4, 5, 6 возвращать объекты.<p>
     */
    public static void main(String[] args) throws SQLException {

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:hw")) {
            //входящие данные и проверить
            firstInputMethod(connection);
            listPrinter(selectDepartment(connection));
            listPrinter(selectPerson(connection));

            //4,5,6
            System.out.println();
            System.out.println(getPersonDepartmentName(connection, 1L));
            mapPrinter(getPersonDepartments(connection));
            System.out.println();
            mapPrinter(getDepartmentPersons(connection));

        } catch (SQLException e) {
            System.err.println("problem " + e.getMessage());
        }

    }

    /**
     * Пункт 4
     */
    private static Department getPersonDepartmentName(Connection connection, Long personId) throws SQLException {

        try (PreparedStatement personStatement = connection.prepareStatement("""
                select department.id, department.name
                from person inner join department on person.department_id = department.id
                where person.id = ?""")) {
            //TODO пишет нет данных, хотя вроде все должно работать норм, поискать причину

            personStatement.setLong(1, personId);
            ResultSet depInfo = personStatement.executeQuery();

            if (depInfo.next()) {
                return new Department(depInfo.getLong("id"),
                        depInfo.getString("name"));
            }
            throw new SQLException("Соответствующих данных не получено!");
        }
    }

    /**
     * Пункт 5
     */
    private static Map<Person, Department> getPersonDepartments(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("""
                select person.id, person.name, person.age, person.department_id, department.id, department.name
                from person inner join department
                on person.department_id = department.id""")) {
            ResultSet resultSet = ps.executeQuery();

            Map<Person, Department> map = new HashMap<>();

            while (resultSet.next()) {
                map.put(new Person(
                                resultSet.getLong("person.id"),
                                resultSet.getString("person.name"),
                                resultSet.getInt("person.age"),
                                resultSet.getLong("person.department_id")),
                        new Department(
                                resultSet.getLong("department.id"),
                                resultSet.getString("department.name")
                        ));
            }
            return map;
        }
    }

    /**
     * Пункт 6
     */
    private static Map<Department, List<Person>> getDepartmentPersons(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("""
                select person.id, person.name, person.age, person.department_id, department.id, department.name
                from person inner join department
                on person.department_id = department.id""")) {
            ResultSet resultSet = ps.executeQuery();

            Map<Department, List<Person>> map = new HashMap<>();
            while (resultSet.next()) {

                Department department = new Department(
                        resultSet.getLong("department.id"),
                        resultSet.getString("department.name"));

                Person person = new Person(
                        resultSet.getLong("person.id"),
                        resultSet.getString("person.name"),
                        resultSet.getInt("person.age"),
                        resultSet.getLong("person.department_id"));

                List<Person> list = map.getOrDefault(department, new ArrayList<>());
                list.add(person);

                map.put(department,
                        map.getOrDefault(department, list));


            }
            return map;
        }
    }

    private static List<Department> selectDepartment(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("""
                    select id, name
                    from department""");

            List<Department> list = new ArrayList<>();

            while (resultSet.next()) {
                Department dto = new Department(
                        resultSet.getLong("id"),
                        resultSet.getString("name"));
                list.add(dto);
            }

            return list;
        }
    }

    private static List<Person> selectPerson(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                select id, name, age, department_id
                from person""")) {

            ResultSet resultSet = statement.executeQuery();

            List<Person> list = new ArrayList<>();
            while (resultSet.next()) {
                Person dto = new Person(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("age"),
                        resultSet.getLong("department_id"));

                list.add(dto);
            }

            return list;
        }
    }

    private static void createTableDepartment(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("""
                create table department (
                id bigint auto_increment primary key,
                name varchar(128) not null)""")) {
            ps.execute();
            System.out.println("table department create: OK");
        }
    }

    private static void createTablePerson(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("""
                create table if not exists person (
                id bigint primary key auto_increment,
                name varchar(128) not null,
                age integer,
                department_id bigint not null,
                foreign key (department_id) references department(id))""")
        ) {
            ps.execute();
            System.out.println("table person create: OK");
        }
    }

    private static void firstInputMethod(Connection connection) throws SQLException {

        createTableDepartment(connection);

        List<String> deps = Arrays.asList("Sales", "Product", "Accounting", "Analytics");

        deps.forEach(DepartmentDto::new);

        createTablePerson(connection);

        List<String> names = Arrays.asList(
                "person name 1", "person name 2", "person name 3",
                "person name 4", "person name 5", "person name 6",
                "person name 7", "person name 8", "person name 9", "person name 10");

        names.forEach(name -> new PersonDto(
                name,
                ThreadLocalRandom.current().nextInt(25, 61),
                ThreadLocalRandom.current().nextLong(1, 5)));
    }

    private static void mapPrinter(Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            System.out.print("\n"+entry.getKey() + ": ");

            if(entry.getValue() instanceof List) {

                List<Person> val = (List<Person>) entry.getValue();
                val.forEach(el -> System.out.print("\n"+el));

            } else {
                System.out.print(entry.getValue());
            }
        }
    }

    private static void listPrinter(List<? extends DBEntity> list) {
        list.forEach(System.out::println);
    }
}
