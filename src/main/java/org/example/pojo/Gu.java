package org.example.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;



@Data
@Entity
@Table(name = "t_Gu")
public class Gu extends BaseEntity implements Serializable {

    @Column(name = "sname")
    String sname;

    @Column(name = "address")
    String address;

    @Column(name = "phone")
    String phone;

    @Column(name = "addition")
    String addition;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gu)) return false;
        if (!super.equals(o)) return false;
        Gu gu = (Gu) o;
        return Objects.equals(sname, gu.sname) && Objects.equals(addition, gu.addition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sname, addition);
    }
}

