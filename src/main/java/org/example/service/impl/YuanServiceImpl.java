package org.example.service.impl;


import org.example.dao.YuanDao;
import org.example.pojo.Yuan;
import org.example.service.YuanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class YuanServiceImpl extends BaseServiceImpl<Yuan, Integer> implements YuanService {

    @Resource(name = "yuanDaoImpl")
    public void setBaseDao(YuanDao dao) {
        super.setBaseDao(dao);
    }

}