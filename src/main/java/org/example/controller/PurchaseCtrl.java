package org.example.controller;

import org.example.pageModel.*;
import org.example.pojo.*;
import org.example.service.PurchaseDetailService;
import org.example.service.PurchaseService;
import org.example.util.JsonUtils;
import org.example.webSocket.PurchaseWebSocket;
import org.example.webSocket.WebSocketUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/purchase")
public class PurchaseCtrl {
    private static final String GET_LAST_SNO = "SELECT sno FROM t_purchase ORDER BY id DESC LIMIT 1";

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
        String nextSerialNumber = String.format("PC%06d", nextSerialNumberValue);

        return nextSerialNumber;
    }
    @Resource(name="purchaseServiceImpl")
    PurchaseService purchaseService;

    @Resource(name="purchaseDetailServiceImpl")
    PurchaseDetailService purchaseDetailService;

    @Resource(name="purchaseWebSocket")
    PurchaseWebSocket purchaseWebSocket;


    @RequestMapping("/getData")
    public Page<Purchase> getData(Pageable page, Purchase purchase, String start, String end,Integer gongPid,Integer cangPid,Integer yuanPid){
        page.setOrderProperty("updateTime");
        page.setOrderDirection(Order.Direction.desc);

        List<Filter> filters = new ArrayList<>();
        if (gongPid!=null){
            Filter ft = new Filter();
            ft.setProperty("gong");
            ft.setValue(gongPid);
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

        return purchaseService.findPage(page,purchase);
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

    @RequestMapping("/getSinglePurchase")
    public Purchase getSinglePurchase(Integer id){
        return purchaseService.find(id);
    }


    @RequestMapping("/getPurchaseDetails/{purchasePid}")
    public Page<PurchaseDetail> getPurchaseDetails(Pageable page, @PathVariable("purchasePid") Integer purchasePid){

        if (purchasePid!=null ){
            List<Filter> filters = new ArrayList<>();
            Filter ft = new Filter();
            ft.setProperty("purchase");
            ft.setValue(purchasePid);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
            page.setFilters(filters);
            return purchaseDetailService.findPage(page);
        }else
            return null;
    }
    @RequestMapping("/getPurchaseDetails")
    public Page<PurchaseDetail> getPurchaseDetails(Pageable page, PurchaseDetail purchaseDetail,
                                                           String  purchaseSName){
        if (purchaseSName!=null ){
            // 获取采购订单的id
            List<Filter> filters = new ArrayList<>();
            Filter ft = new Filter();
            ft.setProperty("sno");
            ft.setValue(purchaseSName);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
            filters.add(ft);
            Order order=new Order("sno", Order.Direction.desc);
            List <Order> lstOrder = new ArrayList<>();
            lstOrder.add(order);
            List<Purchase> lstPurchase = purchaseService.findList(0,1,filters,lstOrder);
            if (lstPurchase.size()>0) {
                Integer purchasePid = lstPurchase.get(0).getId();
                // end of 获取采购单的id

                filters.clear();
                ft.setProperty("purchase");
                ft.setValue(purchasePid);
                ft.setOperator(Filter.Operator.eq);
                filters.add(ft);
                page.setFilters(filters);
            }
        }

        return purchaseDetailService.findPage(page,purchaseDetail);
    }

    @RequestMapping("/edit")
    public Json edit(Purchase purchase,
                     @RequestParam(value = "inserted", required = false) String inserted,
                     @RequestParam(value = "updated", required = false) String updated,
                     @RequestParam(value = "deleted", required = false) String deleted,Integer cangPid,Integer yuanPid,Integer gongPid)
    {
        Json json = new Json();
        List<PurchaseDetailDto> lstInserted = new ArrayList<>();
        List<PurchaseDetailDto> lstUpdated = new ArrayList<>();
        List<PurchaseDetailDto> lstDeleted = new ArrayList<>();


        if (!inserted.isEmpty()){
            lstInserted = JsonUtils.getListBeans(inserted, PurchaseDetailDto.class);
        }
        if (!updated.isEmpty()){
            lstUpdated = JsonUtils.getListBeans(updated, PurchaseDetailDto.class);
        }
        if (!deleted.isEmpty()){
            lstDeleted = JsonUtils.getListBeans(deleted, PurchaseDetailDto.class);
        }

        String errStr="";
        try {
            if (cangPid != null) {
                Cang cang = new Cang();
                cang.setId(cangPid);
                purchase.setCang(cang);
            }
            if (yuanPid != null) {
                Yuan yuan = new Yuan();
                yuan.setId(yuanPid);
                purchase.setYuan(yuan);
            }
            if (gongPid != null) {
                Gong gong = new Gong();
                gong.setId(gongPid);
                purchase.setGong(gong);
            }
            boolean hasEdit = purchaseService.editPurchase(purchase, lstInserted, lstUpdated, lstDeleted);
            if (hasEdit) {
                json.setSuccess(true);
                json.setMsg("采购计划单更新成功！");
                purchaseWebSocket.sendStringMessage(WebSocketUtil.generateMsg(null, 0));
                return  json;
            }
        }catch (Exception e){
            errStr = e.getMessage();
        }
        json.setSuccess(false);
        json.setMsg("采购计划单更新失败！\n\n" + errStr);
        return  json;
    }

    @RequestMapping("/add")
    public Json add(Purchase purchase,
                    @RequestParam(value = "inserted", required = false) String inserted,
                    @RequestParam(value = "updated", required = false) String updated,Integer cangPid,Integer yuanPid,Integer gongPid)
    {
        Json json = new Json();
        List<PurchaseDetailDto> lstInserted = new ArrayList<>();
        List<PurchaseDetailDto> lstUpdated = new ArrayList<>();

        if (!inserted.isEmpty()){
            lstInserted = JsonUtils.getListBeans(inserted, PurchaseDetailDto.class);
        }
        if (!updated.isEmpty()){
            lstUpdated = JsonUtils.getListBeans(updated, PurchaseDetailDto.class);
        }

        if (lstInserted!=null && lstUpdated!=null)
            lstInserted.addAll(lstUpdated);

        String errStr="";

        try {
            if (cangPid != null) {
                Cang cang = new Cang();
                cang.setId(cangPid);
                purchase.setCang(cang);
            }
            if (yuanPid != null) {
                Yuan yuan = new Yuan();
                yuan.setId(yuanPid);
                purchase.setYuan(yuan);
            }
            if (gongPid != null) {
                Gong gong = new Gong();
                gong.setId(gongPid);
                purchase.setGong(gong);
            }
            boolean hasAdded = purchaseService.addPurchase(purchase, lstInserted);
            if (hasAdded) {
                json.setSuccess(true);
                Integer id = purchase.getId();
                json.setId(id);
                json.setMsg("新增采购单成功！");
                purchaseWebSocket.sendStringMessage(WebSocketUtil.generateMsg(null, 0));
                return  json;
            }
        }catch (Exception e){
            errStr = e.getMessage();
           // e.printStackTrace();
        }
        json.setSuccess(false);
        json.setMsg("采购单新增失败！\n\n" + errStr);
        return json;
    }

    @RequestMapping("/del")
    public Json del(Integer id) {
        Json json = new Json();
        try {
            purchaseService.delete(id);
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
