package org.example.pageModel;

import lombok.Data;



@Data
public class PurchaseDetailDto {
    Integer id;

    Integer num;        // purchase number

    Float price;        // purchase price

    Integer purchasePid;    //采购计划单的 id

    Integer purchasePlanPid;    //采购计划单的ID

    Integer purchasePlanDetailPid;  //采购计划明细单 的 id
}
