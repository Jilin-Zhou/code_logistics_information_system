package org.example.service.impl;

import org.example.pojo.Login;
import org.example.service.LoginService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.dao.LoginDao;


import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Service
@Transactional
public class LoginServiceImpl extends BaseServiceImpl<Login,Integer> implements LoginService {
    @Resource(name="loginDaoImpl")
    public void setBaseDao(LoginDao dao){super.setBaseDao(dao);}

    @PersistenceContext // 注入 JPA 的 EntityManager
    private EntityManager entityManager;

//    @Override
//    public Login findByName(String user) {
//        // 使用 JPQL 查询
//        String jpql = "SELECT l FROM Login l WHERE l.user = :user";
//        TypedQuery<Login> query = entityManager.createQuery(jpql, Login.class);
//        query.setParameter("user", user);
//
//        // 获取单个结果（如果没有结果，返回 null）
//        List<Login> resultList = query.getResultList();
//        return resultList.isEmpty() ? null : resultList.get(0);
//    }

}
