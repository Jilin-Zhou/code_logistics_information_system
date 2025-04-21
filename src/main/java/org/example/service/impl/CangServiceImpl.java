package org.example.service.impl;


import org.example.dao.CangDao;
import org.example.pojo.Cang;
import org.example.service.CangService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class CangServiceImpl extends BaseServiceImpl<Cang, Integer> implements CangService {

    @Resource(name = "cangDaoImpl")
    public void setBaseDao(CangDao dao) {
        super.setBaseDao(dao);
    }

}
