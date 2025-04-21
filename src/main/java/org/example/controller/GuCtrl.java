package org.example.controller;

import org.example.pageModel.Json;
import org.example.pageModel.Page;
import org.example.pageModel.Pageable;
import org.example.pojo.Gu;
import org.example.service.GuService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;



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