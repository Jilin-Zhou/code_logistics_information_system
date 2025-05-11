package org.example.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;



@Data
@Entity
@Table(name = "t_PurchaseDetail")
public class PurchaseDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", unique = true, nullable = false)
    Integer id;

    @Column(name = "num")
    Integer num;

    @Column(name = "price")
    Float price;

    @Column(name = "alreadyNum")
    Integer alreadyNum;

    @Column(name = "isClose")
    int isClose;            // 1: 本采购项目已经完结， 否则为 0；

    @JoinColumn(name="purchasePid")
    @ManyToOne(targetEntity=Purchase.class,fetch = FetchType.LAZY)
    @JsonIgnore
    Purchase purchase;

    @JoinColumn(name="purchasePlanDetailPid")
    @ManyToOne(targetEntity=PurchasePlanDetail.class,fetch = FetchType.LAZY)
    @JsonIgnore
    PurchasePlanDetail purchasePlanDetail;

    @PreUpdate
    public void preUpdate(){
        if (this.alreadyNum - this.num >=0)
            this.isClose = 1;
        else
            this.isClose = 0;
    }

    @Transient  // 采购单PId
    public Integer getPurchasePid(){
        if (purchase!=null)
            return purchase.getId();
        else
            return null;
    }

    @Transient      // 采购单号
    public String getPurchaseSno(){
        if (purchase!=null)
            return purchase.getSno();
        else
            return null;
    }
    @Transient      // 采购单号
    public String getPurchaseColor(){
        if (purchasePlanDetail!=null)
            return purchasePlanDetail.getGoods().getAddition();
        else
            return null;
    }
//    public Float getPurchaseSize(){
//        if (purchasePlanDetail!=null)
//            return purchasePlanDetail.getGoods().getSize();
//        else
//            return null;
//    }

    @Transient      // 采购计划单 id
    public Integer getPurchasePlanPid(){
        if (purchasePlanDetail!=null)
            return purchasePlanDetail.getPurchasePlan().getId();
        else
            return null;
    }
    @Transient      // 采购计划单号
    public String getPurchasePlanSno(){
        if (purchasePlanDetail!=null)
            return purchasePlanDetail.getPurchasePlan().getSno();
        else
            return null;
    }

    @Transient      // 采购计划单仓库
    public Cang getPurchasePlanCang(){
        if (purchasePlanDetail!=null)
            return purchasePlanDetail.getPurchasePlan().getCang();
        else
            return null;
    }

    @Transient     //采购计划单Pid
    public Integer getPurchasePlanDetailPid(){
        if (purchasePlanDetail!=null)
            return purchasePlanDetail.getId();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购商品名称
    public String getGoodsName(){
        if (purchasePlanDetail!=null)
            return purchasePlanDetail.getGoodsName();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购商品名称
    public String getGoodsTypeName(){
        if (purchasePlanDetail!=null)
            return purchasePlanDetail.getGoodsTypeName();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购量
    public Integer getPurchasePlanDetailNum(){
        if (purchasePlanDetail!=null)
            return purchasePlanDetail.getNum();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购量 的 已采购量
    public Integer getPurchasePlanDetailAlreadyNum(){
        if (purchasePlanDetail!=null)
            return purchasePlanDetail.getAlreadyNum();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购量 的 已采购量
    public Float getPurchasePlanDetailPrice(){
        if (purchasePlanDetail!=null)
            return purchasePlanDetail.getPrice();
        else
            return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchaseDetail)) return false;
        PurchaseDetail that = (PurchaseDetail) o;
        return Objects.equals(id, that.id) && Objects.equals(num, that.num) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, num, price);
    }

    @Override
    public String toString() {
        return "PurchaseDetail{" +
                "id=" + id +
                ", num=" + num +
                ", price=" + price +
                '}';
    }
}
