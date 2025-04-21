package org.example.dao.impl;

import org.example.dao.LoginDao;
import org.example.pojo.Login;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class LoginDaoImpl extends BaseDaoImpl<Login,Integer> implements LoginDao {
}
