package org.example.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.example.pageModel.SaleDetailDto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Data
@Entity
@Table(name = "t_Sale")
public class Sale extends BaseEntity implements Serializable {

    @Column(name = "dueDate")
    String dueDate;

    @Column(name = "addition")
    String addition;   //附加说明

    @Column(name = "guPid")
    String guPid;   //附加说明

    @Column(name = "phone")
    String phone;

    @Column(name = "address")
    String address;

    @Column(name = "total")
    Float total;

    @OneToMany(mappedBy = "sale", fetch = FetchType.LAZY,cascade = {CascadeType.ALL})
    @JsonIgnore
    Set<SaleDetail> saleDetailSet = new HashSet<>();

    @JoinColumn(name="cangPid")
    @ManyToOne(targetEntity=Cang.class,fetch = FetchType.LAZY)
    @JsonIgnore
    Cang cang;

    @JoinColumn(name="yuanPid")
    @ManyToOne(targetEntity=Yuan.class,fetch = FetchType.LAZY)
    @JsonIgnore
    Yuan yuan;

    @Transient
    public String getCangName(){
        if (cang!=null) {
            if (cang.getId() != null){
                return cang.getSname();
            }
        }
        return "";
    }
    @Transient
    public Integer getCangPid(){
        if (cang!=null) {
            return cang.getId();}
        return null;
    }
    @Transient
    public String getYuanName(){
        if (yuan!=null) {
            if (yuan.getId() != null){
                return yuan.getSname();
            }
        }
        return "";
    }
    @Transient
    public Integer getYuanPid(){
        if (yuan!=null) {
            if (yuan.getId() != null){
                return yuan.getId();
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sale)) return false;
        if (!super.equals(o)) return false;
        Sale that = (Sale) o;
        return Objects.equals(cang, that.cang) && Objects.equals(dueDate, that.dueDate) && Objects.equals(addition, that.addition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cang, dueDate, addition);
    }


}