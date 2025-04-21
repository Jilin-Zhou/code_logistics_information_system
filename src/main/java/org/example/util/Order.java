package org.example.util;

public class Order {
    private String property;
    private Direction direction;

    public enum Direction {
        asc, desc
    }

    // Constructor
    public Order(String property, Direction direction) {
        this.property = property;
        this.direction = direction;
    }

    // Getter和Setter方法
    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
