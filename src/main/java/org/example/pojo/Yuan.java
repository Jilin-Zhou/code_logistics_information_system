package org.example.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;



@Data
@Entity
@Table(name = "t_Yuan")
public class Yuan extends BaseEntity implements Serializable {

    @Column(name = "sname")
    String sname;

    @Column(name = "department")
    String department;

    @Column(name = "addition")
    String addition;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Yuan)) return false;
        if (!super.equals(o)) return false;
        Yuan yuan = (Yuan) o;
        return Objects.equals(sname, yuan.sname) && Objects.equals(addition, yuan.addition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sname, addition);
    }
}

