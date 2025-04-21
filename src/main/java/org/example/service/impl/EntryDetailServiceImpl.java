package org.example.service.impl;

import org.example.dao.EntryDetailDao;
import org.example.pojo.EntryDetail;
import org.example.service.EntryDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional
public class EntryDetailServiceImpl extends BaseServiceImpl<EntryDetail, Integer> implements EntryDetailService {
    @Resource(name = "entryDetailDaoImpl")
    public void setBaseDao(EntryDetailDao dao) {
        super.setBaseDao(dao);
    }
}
