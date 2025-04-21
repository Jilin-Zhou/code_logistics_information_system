package org.example.dao.impl;

import org.example.dao.EntryDao;
import org.example.pojo.Entry;
import org.springframework.stereotype.Repository;



@Repository
public class EntryDaoImpl extends BaseDaoImpl <Entry,Integer> implements EntryDao {
}
