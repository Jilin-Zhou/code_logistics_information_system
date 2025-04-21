package org.example.service;

import org.example.pageModel.PurchaseDetailDto;
import org.example.pojo.Purchase;

import java.util.List;



public interface PurchaseService extends BaseService<Purchase, Integer>{

    public boolean editPurchase(Purchase purchase, List<PurchaseDetailDto> lstInserted, List<PurchaseDetailDto> lstUpdated,
                                List<PurchaseDetailDto> lstDeleted);

    public boolean addPurchase(Purchase purchase, List<PurchaseDetailDto> lstInserted);
}



