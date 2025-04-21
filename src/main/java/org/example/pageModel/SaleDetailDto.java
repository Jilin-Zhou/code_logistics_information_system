package org.example.pageModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDetailDto {
    Integer id;

    Integer num;

    Float price;

    Integer salePid;

    Integer goodsPid;


}