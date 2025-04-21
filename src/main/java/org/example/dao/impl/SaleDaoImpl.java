package org.example.dao.impl;

import org.example.dao.SaleDao;
import org.example.pojo.Sale;
import org.springframework.stereotype.Repository;

@Repository
public class SaleDaoImpl extends BaseDaoImpl <Sale,Integer> implements SaleDao {
}
