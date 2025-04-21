package org.example.controller;

import org.example.pageModel.Json;
import org.example.pageModel.Page;
import org.example.pageModel.Pageable;
import org.example.pojo.Yuan;
import org.example.service.YuanService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;



@RestController
@RequestMapping("yuan")
public class YuanCtrl {

    @Resource(name="yuanServiceImpl")
    YuanService yuanService;

    @RequestMapping("getYuan")
    public Page<Yuan> getYuanInfo(Pageable page, Yuan yuan){

        return yuanService.findPage(page,yuan);
    }


    @RequestMapping("${menu[1].menuList[1].url}")
    public Json edit(Yuan yuan){
        Json json = new Json();
        try {
            Yuan sc = yuanService.find(yuan.getId());
            BeanUtils.copyProperties(yuan,sc);
            yuanService.update(sc);
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
    public Json add(Yuan yuan){
        Json json = new Json();
        try {
            yuanService.save(yuan);
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
            yuanService.delete(id);
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