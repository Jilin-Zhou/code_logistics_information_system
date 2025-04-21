package org.example.service.impl;


import org.example.dao.GongDao;
import org.example.pojo.Gong;
import org.example.service.GongService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class GongServiceImpl extends BaseServiceImpl<Gong, Integer> implements GongService {

    @Resource(name = "gongDaoImpl")
    public void setBaseDao(GongDao dao) {
        super.setBaseDao(dao);
    }

}
