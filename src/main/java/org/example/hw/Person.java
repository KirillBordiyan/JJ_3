package org.example.hw;


public record Person(Long id, String name, Integer age, Long department_id) implements DBEntity {

    @Override
    public String toString() {
        return "Person(" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", department_id=" + department_id + ")";
    }
}
