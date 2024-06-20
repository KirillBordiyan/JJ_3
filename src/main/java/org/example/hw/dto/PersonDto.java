package org.example.hw.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.*;

@Getter
@Setter
public class PersonDto {
    private String name;
    private Integer age;
    private Long department_id;

    public PersonDto(String name, Integer age, Long department_id) {
        this.name = name;
        this.age = age;
        this.department_id = department_id;
        insertInDataBase(this);
    }

    @Override
    public String toString() {
        return "Person: " +
                "name='" + name + '\'' +
                ", age=" + age +
                ", department_id=" + department_id;
    }

    private void insertInDataBase(PersonDto person) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:hw");
             PreparedStatement ps = connection.prepareStatement(
                     "insert into person(name, age, department_id) values (?,?,?)")) {

            ps.setString(1, person.getName());
            ps.setInt(2, person.getAge());
            ps.setLong(3, person.getDepartment_id());

            ps.execute();

        } catch (SQLException e) {
            System.err.println("insert person wrong " + e.getMessage());
        }
    }
}
