package org.example.pageModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePlanDetailDto {
    Integer id;

    Integer num;

    Float price;

    Integer purchasePlanPid;

    Integer goodsPid;


}
