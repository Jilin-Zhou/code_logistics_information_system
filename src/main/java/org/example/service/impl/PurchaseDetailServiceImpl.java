package org.example.service.impl;

import org.example.dao.PurchaseDetailDao;
import org.example.pojo.PurchaseDetail;
import org.example.service.PurchaseDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional
public class PurchaseDetailServiceImpl extends BaseServiceImpl<PurchaseDetail, Integer> implements PurchaseDetailService {
    @Resource(name = "purchaseDetailDaoImpl")
    public void setBaseDao(PurchaseDetailDao dao) {
        super.setBaseDao(dao);
    }
}
