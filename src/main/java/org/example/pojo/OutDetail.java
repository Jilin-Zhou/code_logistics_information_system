package org.example.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;



@Data
@Entity
@Table(name = "t_OutDetail")
public class OutDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", unique = true, nullable = false)
    Integer id;

    @Column(name = "num")
    Integer num;


    @Column(name = "alreadyNum")
    Integer alreadyNum;

    @JoinColumn(name="outPid")
    @ManyToOne(targetEntity=Out.class,fetch = FetchType.LAZY)
    @JsonIgnore
    Out out;

    @JoinColumn(name="saleDetailPid")
    @ManyToOne(targetEntity=SaleDetail.class,fetch = FetchType.LAZY)
    @JsonIgnore
    SaleDetail saleDetail;

    @Transient  // 采购单PId
    public Integer getOutPid(){
        if (out!=null)
            return out.getId();
        else
            return null;
    }
    @Transient      // 采购单号
    public String getOutColor(){
        if (saleDetail!=null)
            return saleDetail.getGoods().getAddition();
        else
            return null;
    }
//    public Float getOutSize(){
//        if (saleDetail!=null)
//            return saleDetail.getGoods().getSize();
//        else
//            return null;
//    }
    @Transient      // 采购单号
    public String getOutSno(){
        if (out!=null)
            return out.getSno();
        else
            return null;
    }

    @Transient      // 采购计划单 id
    public Integer getSalePid(){
        if (saleDetail!=null)
            return saleDetail.getSale().getId();
        else
            return null;
    }
    @Transient      // 采购计划单号
    public String getSaleSno(){
        if (saleDetail!=null)
            return saleDetail.getSale().getSno();
        else
            return null;
    }

    @Transient      // 采购计划单仓库
    public Cang getSaleCang(){
        if (saleDetail!=null)
            return saleDetail.getSale().getCang();
        else
            return null;
    }

    @Transient     //采购计划单Pid
    public Integer getSaleDetailPid(){
        if (saleDetail!=null)
            return saleDetail.getId();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购商品名称
    public String getGoodsName(){
        if (saleDetail!=null)
            return saleDetail.getGoodsName();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购商品名称
    public String getGoodsTypeName(){
        if (saleDetail!=null)
            return saleDetail.getGoodsTypeName();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购量
    public Integer getSaleDetailNum(){
        if (saleDetail!=null)
            return saleDetail.getNum();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购量 的 已采购量
    public Integer getSaleDetailAlreadyNum(){
        if (saleDetail!=null)
            return saleDetail.getAlreadyNum();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购量 的 已采购量
    public Float getSaleDetailPrice(){
        if (saleDetail!=null)
            return saleDetail.getPrice();
        else
            return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OutDetail)) return false;
        OutDetail that = (OutDetail) o;
        return Objects.equals(id, that.id) && Objects.equals(num, that.num);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, num);
    }

    @Override
    public String toString() {
        return "OutDetail{" +
                "id=" + id +
                ", num=" + num +
                '}';
    }
}