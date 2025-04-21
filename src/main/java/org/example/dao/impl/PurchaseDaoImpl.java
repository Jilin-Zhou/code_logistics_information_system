package org.example.dao.impl;

import org.example.dao.PurchaseDao;
import org.example.pojo.Purchase;
import org.springframework.stereotype.Repository;



@Repository
public class PurchaseDaoImpl extends BaseDaoImpl <Purchase,Integer> implements PurchaseDao {
}
