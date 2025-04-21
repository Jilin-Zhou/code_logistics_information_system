package org.example.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;



@Data
@Entity
@Table(name = "t_ShipDetail")
public class ShipDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", unique = true, nullable = false)
    Integer id;

    @Column(name = "num")
    Integer num;

    @JoinColumn(name="shipPid")
    @ManyToOne(targetEntity=Ship.class,fetch = FetchType.LAZY)
    @JsonIgnore
    Ship ship;

    @JoinColumn(name="outDetailPid")
    @ManyToOne(targetEntity=OutDetail.class,fetch = FetchType.LAZY)
    @JsonIgnore
    OutDetail outDetail;

    @Transient
    public String getShipColor(){
        if (outDetail!=null)
            return outDetail.getSaleDetail().getGoods().getAddition();
        else
            return null;
    }
//    public Float getShipSize(){
//        if (outDetail!=null)
//            return outDetail.getSaleDetail().getGoods().getSize();
//        else
//            return null;
//    }
    @Transient  // 采购单PId
    public Integer getShipPid(){
        if (ship!=null)
            return ship.getId();
        else
            return null;
    }

    @Transient      // 采购单号
    public String getShipSno(){
        if (ship!=null)
            return ship.getSno();
        else
            return null;
    }

    @Transient      // 采购计划单 id
    public Integer getOutPid(){
        if (outDetail!=null)
            return outDetail.getOut().getId();
        else
            return null;
    }
    @Transient      // 采购计划单号
    public String getOutSno(){
        if (outDetail!=null)
            return outDetail.getOut().getSno();
        else
            return null;
    }

    @Transient     //采购计划单Pid
    public Integer getOutDetailPid(){
        if (outDetail!=null)
            return outDetail.getId();
        else
            return null;
    }

    @Transient      // 采购计划单明细对应的计划采购商品名称
    public String getGoodsName(){
        if (outDetail!=null)
            return outDetail.getGoodsName();
        else
            return null;
    }

    @Transient
    public String getGoodsTypeName(){
        if (outDetail!=null)
            return outDetail.getGoodsTypeName();
        else
            return null;
    }

    @Transient
    public Integer getOutDetailNum(){
        if (outDetail!=null)
            return outDetail.getNum();
        else
            return null;
    }

    @Transient
    public Integer getOutDetailAlreadyNum(){
        if (outDetail!=null)
            return outDetail.getAlreadyNum();
        else
            return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShipDetail)) return false;
        ShipDetail that = (ShipDetail) o;
        return Objects.equals(id, that.id) && Objects.equals(num, that.num);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, num);
    }

    @Override
    public String toString() {
        return "ShipDetail{" +
                "id=" + id +
                ", num=" + num +
                '}';
    }
}