package org.example.pojo;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@Table(name="t_Login")
public class Login extends BaseEntity implements Serializable {
    @Column(name = "sname")
    String sname;

    @Column(name = "user")
    String user;

    @Column(name="password")
    String password;

    @Column(name="addition")
    String addition;


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(!(obj instanceof Login)) return false;
        if(!super.equals(obj)) return false;
        Login login = (Login)obj;
        return Objects.equals(sname, login.sname) && Objects.equals(addition,login.addition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sname, addition);
    }

}
