package org.example.service.impl;

import org.example.dao.OutDetailDao;
import org.example.pojo.OutDetail;
import org.example.service.OutDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional
public class OutDetailServiceImpl extends BaseServiceImpl<OutDetail, Integer> implements OutDetailService {
    @Resource(name = "outDetailDaoImpl")
    public void setBaseDao(OutDetailDao dao) {
        super.setBaseDao(dao);
    }
}