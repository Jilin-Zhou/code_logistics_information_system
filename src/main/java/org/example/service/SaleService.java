package org.example.service;

import org.example.pageModel.SaleDetailDto;
import org.example.pojo.Sale;

import java.util.List;

public interface SaleService extends BaseService<Sale, Integer>{
    public boolean editSale(Sale sale, List<SaleDetailDto> lstInserted, List<SaleDetailDto> lstUpdated,
                                    List<SaleDetailDto> lstDeleted);

    public boolean addSale(Sale sale, List<SaleDetailDto> lstInserted);
}