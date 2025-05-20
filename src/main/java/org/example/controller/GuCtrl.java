package org.example.controller;

import org.example.pageModel.Json;
import org.example.pageModel.Page;
import org.example.pageModel.Pageable;
import org.example.pojo.Gu;
import org.example.pojo.Login;
import org.example.service.GuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.management.Query;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;


@RestController
@RequestMapping("gu")
public class GuCtrl {

    @Resource(name="guServiceImpl")
    GuService guService;

    @RequestMapping("getGu")
    public Page<Gu> getGuInfo(Pageable page, Gu gu){
        return guService.findPage(page,gu);
    }


    @RequestMapping("${menu[1].menuList[1].url}")
    public Json edit(Gu gu){
        Json json = new Json();
        try {
            Gu sc = guService.find(gu.getId());
            BeanUtils.copyProperties(gu,sc);
            guService.update(sc);
            json.setSuccess(true);
            json.setMsg("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            json.setSuccess(true);
            json.setMsg("修改失败");
        }
        return json;
    }

    // 新增接口：通过顾客名称查询顾客ID
    @RequestMapping("getGuPidByName")
    public Json getGuPidByName(String guName) {
        Json json = new Json();
        try {
            // 创建一个Gu对象作为查询条件
            Gu condition = new Gu();
            condition.setSname(guName);

            // 使用现有服务查询符合条件的顾客
            Page<Gu> result = guService.findPage(new Pageable(), condition);

            if (result != null && result.getRows() != null && !result.getRows().isEmpty()) {
                // 找到匹配的顾客，返回第一个匹配项的ID
                Gu matchedGu = result.getRows().get(0);
                json.setObj(matchedGu.getId());
                json.setSuccess(true);
                json.setMsg("查询成功");
            } else {
                // 未找到匹配的顾客
                json.setSuccess(false);
                json.setMsg("未找到该顾客");
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.setSuccess(false);
            json.setMsg("查询失败: " + e.getMessage());
        }
        return json;
    }

    @RequestMapping("${menu[1].menuList[2].url}")
    public Json add(Gu gu){
        Json json = new Json();
        try {
            guService.save(gu);
            json.setSuccess(true);
            json.setMsg("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            json.setSuccess(true);
            json.setMsg("添加失败");
        }
        return json;
    }

    @RequestMapping("${menu[1].menuList[3].url}")
    public Json del(Integer id){
        Json json = new Json();
        try {
            guService.delete(id);
            json.setSuccess(true);
            json.setMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            json.setSuccess(true);
            json.setMsg("删除失败");
        }
        return json;
    }


}
