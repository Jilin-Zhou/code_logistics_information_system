package org.example.pageModel;

import lombok.Data;



@Data
public class OutDetailDto {
    Integer id;

    Integer num;        // out number

    Integer outPid;

    Integer salePid;

    Integer saleDetailPid;
}