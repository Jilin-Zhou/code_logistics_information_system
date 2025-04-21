package org.example.util;

public class Filter {
    private String property;
    private Object value;
    private Operator operator;

    public enum Operator {
        eq, ne, gt, lt, ge, le, like, in
    }

    // Getter和Setter方法
    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }
}