package org.example.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Data
@Entity
@Table(name = "t_PurchasePlan")
public class PurchasePlan extends BaseEntity implements Serializable {

    @Column(name = "dueDate")
    String dueDate;

    @Column(name = "addition")
    String addition;   //附加说明

    @OneToMany(mappedBy = "purchasePlan", fetch = FetchType.LAZY,cascade = {CascadeType.ALL})
    @JsonIgnore
    Set<PurchasePlanDetail> purchasePlanDetailSet = new HashSet<>();

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
            if (cang.getId() != null){
                return cang.getId();
            }
        }
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
        if (!(o instanceof PurchasePlan)) return false;
        if (!super.equals(o)) return false;
        PurchasePlan that = (PurchasePlan) o;
        return Objects.equals(cang, that.cang) && Objects.equals(dueDate, that.dueDate) && Objects.equals(addition, that.addition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cang, dueDate, addition);
    }
    
}
