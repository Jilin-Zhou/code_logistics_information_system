package org.example.service.impl;


import org.example.dao.GuDao;
import org.example.pojo.Gu;
import org.example.service.GuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class GuServiceImpl extends BaseServiceImpl<Gu, Integer> implements GuService {

    @Resource(name = "guDaoImpl")
    public void setBaseDao(GuDao dao) {
        super.setBaseDao(dao);
    }

}