package org.example.controller;

import org.example.pageModel.Json;
import org.example.pageModel.Page;
import org.example.pageModel.Pageable;
import org.example.pojo.Cang;
import org.example.service.CangService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;



@RestController
@RequestMapping("cang")
public class CangCtrl {

    @Resource(name="cangServiceImpl")
    CangService cangService;

    @RequestMapping("getCang")
    public Page<Cang> getCangInfo(Pageable page, Cang cang){

        return cangService.findPage(page,cang);
    }


    @RequestMapping("${menu[1].menuList[1].url}")
    public Json edit(Cang cang){
        Json json = new Json();
        try {
            Cang sc = cangService.find(cang.getId());
            BeanUtils.copyProperties(cang,sc);
            cangService.update(sc);
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
    public Json add(Cang cang){
        Json json = new Json();
        try {
            cangService.save(cang);
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
            cangService.delete(id);
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