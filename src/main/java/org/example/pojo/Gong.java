package org.example.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;



@Data
@Entity
@Table(name = "t_Gong")
public class Gong extends BaseEntity implements Serializable {

    @Column(name = "sname")
    String sname;

    @Column(name = "addition")
    String addition;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gong)) return false;
        if (!super.equals(o)) return false;
        Gong gong = (Gong) o;
        return Objects.equals(sname, gong.sname) && Objects.equals(addition, gong.addition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sname, addition);
    }
}
