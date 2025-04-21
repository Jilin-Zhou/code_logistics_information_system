package org.example.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.pageModel.EntryDetailDto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Data  // Lombok 注解，自动生成所有属性的 getter 和 setter 方法，以及toString, equals 和 hashCode 方法
@EqualsAndHashCode(callSuper = true) // Lombok 注解，用于生成 equals 和 hashCode 方法，并包括父类属性
@Entity  // JPA 注解，表明这是一个实体类
@Table(name = "t_entry")  // JPA 注解，指定实体对应的数据库表名
public class Entry extends BaseEntity implements Serializable {

    @Column(name = "dueDate")  // JPA 注解，将属性映射到数据库表的对应列
    private String dueDate;     // 要求的送货日期

    @OneToMany(mappedBy = "entry", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore  // Jackson 注解，指定序列化时忽略该属性
    private Set<EntryDetail> entryDetailSet = new HashSet<>();  // 关联的采购订单详情集合

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
    @Transient
    public String getCangAddition(){
        if (cang!=null) {
            if (cang.getId() != null){
                return cang.getAddition();
            }
        }
        return "";
    }
    public List<EntryDetail> getEntryDetails() {
        return new ArrayList<>(entryDetailSet);
    }
}