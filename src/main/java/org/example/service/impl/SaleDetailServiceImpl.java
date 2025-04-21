package org.example.service.impl;

import org.example.dao.SaleDetailDao;
import org.example.pojo.SaleDetail;
import org.example.service.SaleDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional
public class SaleDetailServiceImpl extends BaseServiceImpl<SaleDetail, Integer> implements SaleDetailService {
    @Resource(name = "saleDetailDaoImpl")
    public void setBaseDao(SaleDetailDao dao) {
        super.setBaseDao(dao);
    }
}