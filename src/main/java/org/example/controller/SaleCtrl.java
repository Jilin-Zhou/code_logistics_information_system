package org.example.controller;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.pageModel.*;
import org.example.pojo.*;
import org.example.service.SaleDetailService;
import org.example.service.SaleService;
import org.example.util.JsonUtils;
import org.example.webSocket.SaleWebSocket;
import org.example.webSocket.WebSocketUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;


@RestController
@RequestMapping("/sale")
public class SaleCtrl {
    private static final String GET_LAST_SNO = "SELECT sno FROM t_sale ORDER BY id DESC LIMIT 1";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @RequestMapping("/getSerial")
    public String getSerial(){
        String lastSerialNumber;
        int lastSerialNumberValue = 0;
        try{
            lastSerialNumber = jdbcTemplate.queryForObject(GET_LAST_SNO,String.class);
        }catch(Exception e){
            lastSerialNumber = null;
        }
        if (lastSerialNumber != null && !lastSerialNumber.isEmpty()) {
            lastSerialNumberValue = Integer.parseInt(lastSerialNumber.substring(2));
        }else {
            lastSerialNumberValue = 0;
        }

        int nextSerialNumberValue = lastSerialNumberValue + 1;
        String nextSerialNumber = String.format("SA%06d", nextSerialNumberValue);

        return nextSerialNumber;
    }

//    @Autowired
//    private EntityManager entityManager;
//    @RequestMapping("/cusadd")
//    @Transactional  // 添加 @Transactional 注解
//    public Json cusAdd(@RequestBody SaleAddRequest request) {
//        Json json = new Json();
//        try {
//            // 1. 创建并设置 Sale 对象
//            Sale sale = new Sale();
//            sale.setSno(request.getSno());
//            sale.setPhone(request.getPhone());
//            sale.setAddress(request.getAddress());
//            sale.setAddition(request.getAddition());
//            sale.setTotal(Float.valueOf(request.getTotal()));
//            sale.setCreateTime(String.valueOf(new Date()));
//            // 2. 持久化 Sale 对象
//            entityManager.persist(sale);  // 使用 entityManager.persist()
//            entityManager.flush(); // 确保 sale 获得 ID
//            // 3. 处理 SaleDetail 列表
//            if (request.getItems() != null && !request.getItems().isEmpty()) {
//                for (SaleDetailDto detailDto : request.getItems()) {
//                    // 创建 SaleDetail 对象
//                    SaleDetail saleDetail = new SaleDetail();
//                    saleDetail.setNum(detailDto.getNum());
//                    saleDetail.setPrice(detailDto.getPrice());
//                    saleDetail.setAlreadyNum(0); // 设置初始已完成数量为0
//                    saleDetail.setIsClose(0); // 设置初始状态为未完结
//                    // 获取对应的 Goods 对象
//                    Goods goods = entityManager.find(Goods.class, detailDto.getGoodsPid());
//                    if (goods == null) {
//                        throw new Exception("商品ID " + detailDto.getGoodsPid() + " 不存在！");
//                    }
//                    // 设置关联关系
//                    saleDetail.setGoods(goods);
//                    saleDetail.setSale(sale);
//                    // 持久化 SaleDetail 对象
//                    entityManager.persist(saleDetail);  // 使用 entityManager.persist()
//                }
//            }
//            // 4. 设置响应结果
//            json.setSuccess(true);
//            json.setMsg("新增销售单成功");
//            json.setId(sale.getId());
//        } catch (Exception e) {
//            json.setSuccess(false);
//            json.setMsg("新增销售单失败: " + e.getMessage());
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//        return json;
//    }
//
//    // 修改 SaleAddRequest 类，增加必要的字段
//    @Data
//    public static class SaleAddRequest {
//        @Getter
//        @Setter
//        private String sno;
//        private String username;
//        private String name;
//        private String phone;
//        private String address;
//        private String addition;
//        private String total;
//        private List<SaleDetailDto> items;
//    }



    @Resource(name="saleServiceImpl")
    SaleService saleService;

    @Resource(name="saleDetailServiceImpl")
    SaleDetailService saleDetailService;

    @Resource(name="saleWebSocket")
    SaleWebSocket saleWebSocket;

    @RequestMapping("/getData")
    public Page<Sale> getData(Pageable page, Sale sale, String start, String end,Integer cangPid,Integer yuanPid, Integer guPid){
        page.setOrderProperty("createTime");
        page.setOrderDirection(Order.Direction.desc);

        List<Filter> filters = new ArrayList<>();
        if(guPid!=null){
            Filter ft = new Filter();
            ft.setProperty("gu");
            ft.setValue(guPid);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);

        }
        if (cangPid!=null){
            Filter ft = new Filter();
            ft.setProperty("cang");
            ft.setValue(cangPid);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
        }
        if (yuanPid!=null){
            Filter ft = new Filter();
            ft.setProperty("yuan");
            ft.setValue(yuanPid);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
        }

        addStartAndEndRestrict(start, end, filters);
        page.setFilters(filters);

        return saleService.findPage(page,sale);
    }

    static void addStartAndEndRestrict(String start, String end, List<Filter> filters) {
        if (start!=null && !start.isEmpty()){
            Filter ft = new Filter();
            ft.setProperty("createTime");
            ft.setValue(start);
            ft.setOperator(Filter.Operator.ge);
            filters.add(ft);
        }
        if (end!=null && !end.isEmpty()){
            Filter ft = new Filter();
            ft.setProperty("createTime");
            ft.setValue(end);
            ft.setOperator(Filter.Operator.le);
            filters.add(ft);
        }
    }


    @RequestMapping("/getSingleSale")
    public Sale getSingleSale(Integer id){
        return saleService.find(id);
    }

    /**
     * 通过销售单的ID来查询表细
     */
    @RequestMapping("/getSaleDetails/{salePid}")
    public Page<SaleDetail> getSaleDetails(Pageable page,SaleDetail saleDetail,
                                                           @PathVariable(value = "salePid",required = false) Integer salePid){
        saleDetail.setIsClose(0);
        if (salePid!=null ){
            List<Filter> filters = new ArrayList<>();
            Filter ft = new Filter();
            ft.setProperty("sale");
            ft.setValue(salePid);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
            page.setFilters(filters);
            return saleDetailService.findPage(page,saleDetail);
        }else
            return null;
    }

    /**
     * 通过销售单的 sname（订单号，唯一键）来获取表细
     */
    @RequestMapping("/getSaleDetails")
    public Page<SaleDetail> getSaleDetails(Pageable page,SaleDetail saleDetail,
                                                           String  saleSName){
        if (saleSName!=null ){
            // 获取采购订单的id
            List<Filter> filters = new ArrayList<>();
            Filter ft = new Filter();
            ft.setProperty("sno");
            ft.setValue(saleSName);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
            filters.add(ft);
            Order order=new Order("sno", Order.Direction.desc);
            List <Order> lstOrder = new ArrayList<>();
            lstOrder.add(order);
            List<Sale> lstSale = saleService.findList(0,1,filters,lstOrder);
            if (lstSale.size()>0) {
                Integer salePid = lstSale.get(0).getId();
                // end of 获取采购订单的id

                filters.clear();
                ft.setProperty("sale");
                ft.setValue(salePid);
                ft.setOperator(Filter.Operator.eq);
                filters.add(ft);
                page.setFilters(filters);
            }
        }

        return saleDetailService.findPage(page,saleDetail);
    }

    @RequestMapping("/edit")
    public Json edit(Sale sale,
                     @RequestParam(value = "inserted", required = false) String inserted,
                     @RequestParam(value = "updated", required = false) String updated,
                     @RequestParam(value = "deleted", required = false) String deleted,
                     Integer cangPid,Integer yuanPid, Integer guPid)
    {
        Json json = new Json();
        List<SaleDetailDto> lstInserted = new ArrayList<>();
        List<SaleDetailDto> lstUpdated = new ArrayList<>();
        List<SaleDetailDto> lstDeleted = new ArrayList<>();


        if (!inserted.isEmpty()){
            lstInserted = JsonUtils.getListBeans(inserted, SaleDetailDto.class);
        }
        if (!updated.isEmpty()){
            lstUpdated = JsonUtils.getListBeans(updated, SaleDetailDto.class);
        }
        if (!deleted.isEmpty()){
            lstDeleted = JsonUtils.getListBeans(deleted, SaleDetailDto.class);
        }

        String errStr="";
        try {
            if(guPid != null){
                Gu gu = new Gu();
                gu.setId(guPid);
                sale.setGu(gu);
            }
            if (cangPid != null) {
                Cang cang = new Cang();
                cang.setId(cangPid);
                sale.setCang(cang);
            }
            if (yuanPid != null) {
                Yuan yuan = new Yuan();
                yuan.setId(yuanPid);
                sale.setYuan(yuan);
            }
            boolean hasEdit = saleService.editSale(sale, lstInserted, lstUpdated, lstDeleted);
            if (hasEdit) {
                json.setSuccess(true);
                json.setMsg("销售单更新成功！");
                //  WebSocketUtil.sendSaleStringMsg(WebSocketUtil.generateMsg(null, 0));
                return  json;
            }
        }catch (Exception e){
            errStr = e.getMessage();
        }
        json.setSuccess(false);
        json.setMsg("销售单更新失败！\n\n" + errStr);
        return  json;
    }


    @RequestMapping("/add")
    public Json add(Sale sale,
                    @RequestParam(value = "inserted", required = false) String inserted,
                    @RequestParam(value = "updated", required = false) String updated,
                    Integer cangPid,Integer yuanPid, Integer guPid)
    {
        Json json = new Json();
        List<SaleDetailDto> lstInserted = new ArrayList<>();
        List<SaleDetailDto> lstUpdated = new ArrayList<>();

        if (!inserted.isEmpty()){
            lstInserted = JsonUtils.getListBeans(inserted, SaleDetailDto.class);
        }
        if (!updated.isEmpty()){
            lstUpdated = JsonUtils.getListBeans(updated, SaleDetailDto.class);
        }

        if (lstInserted!=null && lstUpdated!=null)
            lstInserted.addAll(lstUpdated);

        String errStr="";

        try {
            if (guPid != null){
                Gu gu = new Gu();
                gu.setId(guPid);
                sale.setGu(gu);
            }
            if (cangPid != null) {
                Cang cang = new Cang();
                cang.setId(cangPid);
                sale.setCang(cang);
            }
            if (yuanPid != null) {
                Yuan yuan = new Yuan();
                yuan.setId(yuanPid);
                sale.setYuan(yuan);
            }
            boolean hasAdded = saleService.addSale(sale, lstInserted);
            if (hasAdded) {
                json.setSuccess(true);
                Integer id = sale.getId();
                json.setId(id);
                json.setMsg("新增销售单成功！");
                saleWebSocket.sendStringMessage(WebSocketUtil.generateMsg(null, 0));
                return  json;
            }
        }catch (Exception e){
            errStr = e.getMessage();
            e.printStackTrace();
        }
        json.setSuccess(false);
        json.setMsg("销售单更新失败！\n\n" + errStr);
        return json;
    }

    @RequestMapping("/del")
    public Json del(Integer id) {
        Json json = new Json();
        try {
            saleService.delete(id);
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