package org.example.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;


@Data
@Entity
@Table(name = "t_EntryDetail")
public class EntryDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", unique = true, nullable = false)
    Integer id;

    @Column(name = "num")
    Integer num;
//这下面这个貌似有点问题，应该是货架来着
    @Column(name = "price")
    String price;

    @Column(name = "loaded")
    Integer loaded;     // 已入库数量

    @JoinColumn(name="entryPid")
    @ManyToOne(targetEntity=Entry.class,fetch = FetchType.LAZY)
    @JsonIgnore
    Entry entry;

    @JoinColumn(name="purchaseDetailPid")
    @ManyToOne(targetEntity=PurchaseDetail.class,fetch = FetchType.LAZY)
    @JsonIgnore
    PurchaseDetail purchaseDetail;

    @Transient  // 采购单PId
    public Integer getEntryPid(){
        if (entry!=null)
            return entry.getId();
        else
            return null;
    }

    @Transient      // 采购单号
    public String getEntrySno(){
        if (entry!=null)
            return entry.getSno();
        else
            return null;
    }
    @Transient
    public String getEntryColor(){
        if (purchaseDetail!=null)
            return purchaseDetail.getPurchasePlanDetail().getGoods().getAddition();
        else
            return null;
    }
//    public Float getEntrySize(){
//        if (purchaseDetail!=null)
//            return purchaseDetail.getPurchasePlanDetail().getGoods().getSize();
//        else
//            return null;
//    }

    @Transient      // 采购计划单 id
    public Integer getPurchasePid(){
        if (purchaseDetail!=null)
            return purchaseDetail.getPurchase().getId();
        else
            return null;
    }
    @Transient      // 采购计划单号
    public String getPurchaseSno(){
        if (purchaseDetail!=null)
            return purchaseDetail.getPurchase().getSno();
        else
            return null;
    }

    @Transient     //采购计划单Pid
    public Integer getPurchaseDetailPid(){
        if (purchaseDetail!=null)
            return purchaseDetail.getId();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购商品名称
    public String getGoodsName(){
        if (purchaseDetail!=null)
            return purchaseDetail.getGoodsName();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购商品名称
    public String getGoodsTypeName(){
        if (purchaseDetail!=null)
            return purchaseDetail.getGoodsTypeName();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购量
    public Integer getPurchaseDetailNum(){
        if (purchaseDetail!=null)
            return purchaseDetail.getNum();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购量 的 已采购量
    public Integer getPurchaseDetailAlreadyNum(){
        if (purchaseDetail!=null)
            return purchaseDetail.getAlreadyNum();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购量 的 已采购量
    public Float getPurchaseDetailPrice(){
        if (purchaseDetail!=null)
            return purchaseDetail.getPrice();
        else
            return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntryDetail)) return false;
        EntryDetail that = (EntryDetail) o;
        return Objects.equals(id, that.id) && Objects.equals(num, that.num) && Objects.equals(loaded, that.loaded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, num, loaded);
    }

    @Override
    public String toString() {
        return "EntryDetail{" +
                "id=" + id +
                ", num=" + num +
                ", loaded=" + loaded +
                '}';
    }
}