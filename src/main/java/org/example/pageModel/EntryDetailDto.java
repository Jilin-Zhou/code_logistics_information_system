package org.example.pageModel;

import lombok.Data;



@Data
public class EntryDetailDto {
    Integer id;

    Integer num;        // purchase number

    String price;//存储货架位置

    Integer entryPid;    //采购计划单的 id

    Integer purchasePid;    //采购计划单的ID

    Integer purchaseDetailPid;  //采购计划明细单 的 id
}
