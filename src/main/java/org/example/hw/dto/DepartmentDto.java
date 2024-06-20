package org.example.hw.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Getter
@Setter
public class DepartmentDto {
    private String name;

    public DepartmentDto(String name){
        this.name = name;
        insertInDataBase(this);
    }

    @Override
    public String toString() {
        return "Department:" +
                "name='" + name + '\'';
    }

    private void insertInDataBase(DepartmentDto department) {
        try(Connection connection = DriverManager.getConnection("jdbc:h2:mem:hw");
            PreparedStatement ps = connection.prepareStatement(
                    "insert into department(name) values (?)")){

            ps.setString(1, name);

            ps.execute();

        } catch (SQLException e){
            System.err.println("insert department wrong");
        }
    }
}
