package org.example.hw;


public record Department(Long id, String name) implements DBEntity {

    @Override
    public String toString() {
        return "Department(" +
                "id=" + id +
                ", name='" + name + '\'' + ")";
    }
}
