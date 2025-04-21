package org.example.controller;

import org.example.pageModel.*;
import org.example.pojo.Goods;
import org.example.service.GoodsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;



@RestController
//@RequestMapping("/goods")
@RequestMapping("${menu[0].url}")
public class GoodsCtrl {

    @Resource(name="goodsServiceImpl")
    GoodsService goodsService;

    //@RequestMapping("/getGoods")
    @RequestMapping("${menu[0].menuList[0].url}")
    public Page<Goods> getGoodsInfo(Pageable page, Goods goods, Integer goodsTypePid, Integer yxqq, Integer yxqz,String start, String end ){
        page.setOrderProperty("createTime");
        page.setOrderDirection(Order.Direction.desc);

        List<Filter> filters = new ArrayList<>();

        PurchasePlanCtrl.addStartAndEndRestrict(start, end, filters);
        if (yxqq!=null ){
            Filter ft = new Filter();
            ft.setProperty("size");
            ft.setValue(yxqq);
            ft.setOperator(Filter.Operator.ge);
            filters.add(ft);
        }
        if(goodsTypePid!=null){
            Filter ft = new Filter();
            ft.setProperty("goodsType");
            ft.setValue(goodsTypePid);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
        }
        page.setFilters(filters);
        return goodsService.findPage(page,goods);
    }

    //@RequestMapping("/edit")


    @RequestMapping("${menu[0].menuList[1].url}")
    public Json edit(Goods goods, Integer goodsTypePid){
        Json json = new Json();
        boolean hasEdit = goodsService.edit(goods,goodsTypePid);

        if (hasEdit){
            json.setSuccess(true);
            json.setMsg("修改成功");
        }else{
            json.setSuccess(false);
            json.setMsg("修改失败");
        }
        return json;

    }

    @RequestMapping("/add")
    public Json add(Goods goods,Integer goodsTypePid){
        Json json = new Json();
        boolean hasEdit = goodsService.add(goods,goodsTypePid);
        if (hasEdit){
            json.setSuccess(true);
            json.setMsg("修改成功");
        }else{
            json.setSuccess(false);
            json.setMsg("修改失败");
        }
        return json;
    }

    @RequestMapping("/del")
    public Json del(Integer id){
        Json json = new Json();
        try {
            goodsService.delete(id);
            json.setSuccess(true);
            json.setMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            json.setSuccess(true);
            json.setMsg("删除失败");
        }
        return json;
    }

    @RequestMapping("/getGoodsList")
    public List<Goods> getGoodsList(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) Integer goodsTypePid) {
        Pageable pageable = new Pageable();
        pageable.setPage(page);
        pageable.setRows(size);
        pageable.setOrderProperty("createTime");
        pageable.setOrderDirection(Order.Direction.desc);
        List<Filter> filters = new ArrayList<>();
        if(goodsTypePid!=null){
            Filter ft = new Filter();
            ft.setProperty("goodsType");
            ft.setValue(goodsTypePid);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
        }
        pageable.setFilters(filters);
        Page<Goods> goodsPage = goodsService.findPage(pageable, new Goods());
        return goodsPage.getRows(); // 只返回图书列表
    }

}
