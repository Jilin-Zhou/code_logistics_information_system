package org.example.controller;

import org.example.pageModel.Json;
import org.example.pageModel.Page;
import org.example.pageModel.Pageable;
import org.example.pojo.Gong;
import org.example.service.GongService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;



@RestController
@RequestMapping("gong")
public class GongCtrl {

    @Resource(name="gongServiceImpl")
    GongService gongService;

    @RequestMapping("getGong")
    public Page<Gong> getGongInfo(Pageable page, Gong gong){

        return gongService.findPage(page,gong);
    }


    @RequestMapping("${menu[1].menuList[1].url}")
    public Json edit(Gong gong){
        Json json = new Json();
        try {
            Gong sc = gongService.find(gong.getId());
            BeanUtils.copyProperties(gong,sc);
            gongService.update(sc);
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
    public Json add(Gong gong){
        Json json = new Json();
        try {
            gongService.save(gong);
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
            gongService.delete(id);
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