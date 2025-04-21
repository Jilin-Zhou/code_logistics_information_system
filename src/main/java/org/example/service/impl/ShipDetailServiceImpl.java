package org.example.service.impl;

import org.example.dao.ShipDetailDao;
import org.example.pojo.ShipDetail;
import org.example.service.ShipDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional
public class ShipDetailServiceImpl extends BaseServiceImpl<ShipDetail, Integer> implements ShipDetailService {
    @Resource(name = "shipDetailDaoImpl")
    public void setBaseDao(ShipDetailDao dao) {
        super.setBaseDao(dao);
    }
}
