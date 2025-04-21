package org.example.dao.impl;

import org.example.dao.ShipDao;
import org.example.pojo.Ship;
import org.springframework.stereotype.Repository;



@Repository
public class ShipDaoImpl extends BaseDaoImpl <Ship,Integer> implements ShipDao {
}