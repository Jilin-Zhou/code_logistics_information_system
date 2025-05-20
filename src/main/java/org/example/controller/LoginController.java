package org.example.controller;

import org.example.pageModel.Json;
import org.example.pojo.Gu;
import org.example.pojo.Login;
import org.example.service.LoginService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.activation.DataSource;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.annotation.Resource;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/in_out_api")
public class LoginController {
    @Autowired
    LoginService loginService;
    JdbcTemplate jdbcTemplate;

    // 注册
    @PostMapping("/register")
    public Json register(@RequestBody Login login) {
        Json json = new Json();
        try{
            loginService.save(login);
            json.setSuccess(true);
            json.setMsg("注册成功");
        }catch (Exception e){
            e.printStackTrace();
            json.setSuccess(false);
            json.setMsg("注册失败");
        }
        return json;
    }


    @Autowired
    private EntityManager entityManager;
    @PostMapping("/login")
    public Json login(@RequestBody Login user) {
        Json json = new Json();

        String query = "SELECT l FROM Login l WHERE l.user = :user";
        TypedQuery<Login> typedQuery = entityManager.createQuery(query, Login.class);
        typedQuery.setParameter("user", user.getUser());

        try {
            Login dbUser = typedQuery.getSingleResult();

            if (dbUser != null && dbUser.getPassword().equals(user.getPassword())) {
                json.setSuccess(true);
                json.setMsg("登录成功");
            } else {
                json.setSuccess(false);
                json.setMsg("用户名或密码错误");
            }
        } catch (Exception e) {
            json.setSuccess(false);
            json.setMsg("用户名或密码错误");
        }

        return json;
    }


    @Transactional
    @PostMapping("/add-customer")
    public Json addCustomer(@RequestBody Gu customerInfo) {
        Json json = new Json();
        try {
            // 创建顾客实体对象
            Gu customer = new Gu();
            customer.setSname(customerInfo.getSname());
            customer.setAddress(customerInfo.getAddress());
            customer.setPhone(customerInfo.getPhone());
            customer.setAddition(customerInfo.getAddition());

            // 使用EntityManager持久化对象
            entityManager.persist(customer);
            entityManager.flush(); // 立即刷新到数据库

            json.setSuccess(true);
            json.setMsg("顾客信息添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            json.setSuccess(false);
            json.setMsg("顾客信息添加失败: " + e.getMessage());
        }
        return json;
    }




    // 修改信息
    @PutMapping("/update")
    public Json edit(Login user) {
        Json json = new Json();
        try{
            Login sc = loginService.find(user.getId());
            BeanUtils.copyProperties(user, sc);
            loginService.update(sc);
            json.setSuccess(true);
            json.setMsg("修改成功");
        }catch (Exception e){
            e.printStackTrace();
            json.setSuccess(true);
            json.setMsg("修改失败");
        }
        return json;
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    public Json del(Integer id){
        Json json = new Json();
        try{
            loginService.delete(id);
            json.setSuccess(true);
            json.setMsg("删除成功");
        }catch (Exception e){
            e.printStackTrace();
            json.setSuccess(true);
            json.setMsg("删除失败");
        }
        return json;
    }
}




