package org.example.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;



@Data
@Entity
@Table(name = "t_Goods")
public class Goods extends BaseEntity implements Serializable {

    @Column(name = "sname")
    String sname;

    @JoinColumn(name="goodsTypePid")
    @ManyToOne(targetEntity=GoodsType.class,fetch = FetchType.LAZY)
    @JsonIgnore
    GoodsType goodsType;

    @Column(name = "manufacturer")
    String manufacturer;    // 生产商



    @Column(name = "jprice")
    Float jprice;

    @Column(name = "sprice")
    Float sprice;

    @Column(name = "addition")
    String addition;   //附加说明

    @Column(name = "num")
    Integer num;   //在库数量

    @Column(name = "picurl")
    String picurl;


    @Transient
    public String getGoodType(){
        if (goodsType!=null)
            return goodsType.getSname();
        else
            return "";
    }

    @Transient
    public Integer getGoodsTypePid(){
        if (goodsType!=null)
            return goodsType.getId();
        else
            return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Goods)) return false;
        if (!super.equals(o)) return false;
        Goods goods = (Goods) o;
        return Objects.equals(sname, goods.sname) && Objects.equals(manufacturer, goods.manufacturer) &&  Objects.equals(addition, goods.addition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sname, manufacturer, addition);
    }

}
