package org.example.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_SaleDetail")
public class SaleDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    Integer id;

    @Column(name = "num")
    Integer num;

    @Column(name = "price")
    Float price;

    @Column(name = "alreadyNum")
    Integer alreadyNum;



    @Column(name = "isClose")
    int isClose;            // 1: 本采购项目已经完结， 否则为 0；

    @JoinColumn(name="salePid")
    @ManyToOne(targetEntity=Sale.class,fetch = FetchType.LAZY)
    @JsonIgnore
    Sale sale;

    @JoinColumn(name="goodsPid")
    @ManyToOne(targetEntity=Goods.class,fetch = FetchType.LAZY)
    @JsonIgnore
    Goods goods;


    @PreUpdate
    public void preUpdate(){
        if (this.alreadyNum - this.num >=0)
            this.isClose = 1;
        else
            this.isClose = 0;
    }

    @Transient
    public Integer getGoodsPid(){
        if (goods!=null)
            return goods.getId();
        else
            return null;
    }

    @Transient
    public Integer getSalePid(){
        if (sale!=null)
            return sale.getId();
        else
            return null;
    }
    @Transient
    public String getSaleSno(){
        if (sale!=null)
            return sale.getSno();
        else
            return null;
    }
    @Transient
    public String getGoodsName(){
        if (goods!=null)
            return goods.getSname();
        else
            return "";
    }

    @Transient
    public String getGoodsTypeName(){
        if (goods!=null) {
            if (goods.getGoodsType() != null){
                GoodsType type = goods.getGoodsType();
                return type.getSname();
            }
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SaleDetail)) return false;
        return id != null && id.equals(((Sale) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, num, price, goods);
    }

    @Override
    public String toString() {
        return "SaleDetail{" +
                "id=" + id +
                ", num=" + num +
                ", price=" + price +
                ", goods=" + goods +
                ", alreadyNum=" + alreadyNum +
                '}';
    }
}