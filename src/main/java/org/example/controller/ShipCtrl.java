package org.example.controller;

import org.example.pageModel.*;
import org.example.pojo.*;
import org.example.service.ShipDetailService;
import org.example.service.ShipService;
import org.example.util.JsonUtils;
import org.example.webSocket.ShipWebSocket;
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
@RequestMapping("/ship")
public class ShipCtrl {
    private static final String GET_LAST_SNO = "SELECT sno FROM t_ship ORDER BY id DESC LIMIT 1";

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
        String nextSerialNumber = String.format("SH%06d", nextSerialNumberValue);

        return nextSerialNumber;
    }
    @Resource(name="shipServiceImpl")
    ShipService shipService;

    @Resource(name="shipDetailServiceImpl")
    ShipDetailService shipDetailService;

    @Resource(name="shipWebSocket")
    ShipWebSocket shipWebSocket;


    @RequestMapping("/getData")
    public Page<Ship> getData(Pageable page, Ship ship, String start, String end,Integer cangPid){
        page.setOrderProperty("updateTime");
        page.setOrderDirection(Order.Direction.desc);

        List<Filter> filters = new ArrayList<>();
        if (cangPid!=null){
            Filter ft = new Filter();
            ft.setProperty("cang");
            ft.setValue(cangPid);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
        }
        addStartAndEndRestrict(start, end, filters);
        page.setFilters(filters);

        return shipService.findPage(page,ship);
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

    @RequestMapping("/getSingleShip")
    public Ship getSingleShip(Integer id){
        return shipService.find(id);
    }

    @RequestMapping("/getShipDetails")
    public Page<ShipDetail> getShipDetails(Pageable page, ShipDetail shipDetail,
                                             String  shipSName){
        if (shipSName!=null ){
            // 获取采购订单的id
            List<Filter> filters = new ArrayList<>();
            Filter ft = new Filter();
            ft.setProperty("sno");
            ft.setValue(shipSName);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
            filters.add(ft);
            Order order=new Order("sno", Order.Direction.desc);
            List <Order> lstOrder = new ArrayList<>();
            lstOrder.add(order);
            List<Ship> lstShip = shipService.findList(0,1,filters,lstOrder);
            if (lstShip.size()>0) {
                Integer shipPid = lstShip.get(0).getId();
                // end of 获取采购单的id

                filters.clear();
                ft.setProperty("ship");
                ft.setValue(shipPid);
                ft.setOperator(Filter.Operator.eq);
                filters.add(ft);
                page.setFilters(filters);
            }
        }

        return shipDetailService.findPage(page,shipDetail);
    }


    @RequestMapping("/getShipDetails/{shipPid}")
    public Page<ShipDetail> getShipDetails(Pageable page, @PathVariable("shipPid") Integer shipPid){

        if (shipPid!=null ){
            List<Filter> filters = new ArrayList<>();
            Filter ft = new Filter();
            ft.setProperty("ship");
            ft.setValue(shipPid);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
            page.setFilters(filters);
            return shipDetailService.findPage(page);
        }else
            return null;
    }

    @RequestMapping("/edit")
    public Json edit(Ship ship,
                     @RequestParam(value = "inserted", required = false) String inserted,
                     @RequestParam(value = "updated", required = false) String updated,
                     @RequestParam(value = "deleted", required = false) String deleted,Integer cangPid,Integer yuanPid)
    {
        Json json = new Json();
        List<ShipDetailDto> lstInserted = new ArrayList<>();
        List<ShipDetailDto> lstUpdated = new ArrayList<>();
        List<ShipDetailDto> lstDeleted = new ArrayList<>();


        if (!inserted.isEmpty()){
            lstInserted = JsonUtils.getListBeans(inserted, ShipDetailDto.class);
        }
        if (!updated.isEmpty()){
            lstUpdated = JsonUtils.getListBeans(updated, ShipDetailDto.class);
        }
        if (!deleted.isEmpty()){
            lstDeleted = JsonUtils.getListBeans(deleted, ShipDetailDto.class);
        }

        String errStr="";
        try {
            if (cangPid != null) {
                Cang cang = new Cang();
                cang.setId(cangPid);
                ship.setCang(cang);
            }
            if (yuanPid != null) {
                Yuan yuan = new Yuan();
                yuan.setId(yuanPid);
                ship.setYuan(yuan);
            }
            boolean hasEdit = shipService.editShip(ship, lstInserted, lstUpdated, lstDeleted);
            if (hasEdit) {
                json.setSuccess(true);
                json.setMsg("运输单更新成功！");
                shipWebSocket.sendStringMessage(WebSocketUtil.generateMsg(null, 0));
                return  json;
            }
        }catch (Exception e){
            errStr = e.getMessage();
        }
        json.setSuccess(false);
        json.setMsg("运输单更新失败！\n\n" + errStr);
        return  json;
    }

    @RequestMapping("/add")
    public Json add(Ship ship,
                    @RequestParam(value = "inserted", required = false) String inserted,
                    @RequestParam(value = "updated", required = false) String updated,Integer cangPid,Integer yuanPid){
        Json json = new Json();
        List<ShipDetailDto> lstInserted = new ArrayList<>();
        List<ShipDetailDto> lstUpdated = new ArrayList<>();

        if (!inserted.isEmpty()){
            lstInserted = JsonUtils.getListBeans(inserted, ShipDetailDto.class);
        }
        if (!updated.isEmpty()){
            lstUpdated = JsonUtils.getListBeans(updated, ShipDetailDto.class);
        }

        if (lstInserted!=null && lstUpdated!=null)
            lstInserted.addAll(lstUpdated);

        String errStr="";

        try {
            if (cangPid != null) {
                Cang cang = new Cang();
                cang.setId(cangPid);
                ship.setCang(cang);
            }
            if (yuanPid != null) {
                Yuan yuan = new Yuan();
                yuan.setId(yuanPid);
                ship.setYuan(yuan);
            }
            boolean hasAdded = shipService.addShip(ship, lstInserted);
            if (hasAdded) {
                json.setSuccess(true);
                Integer id = ship.getId();
                json.setId(id);
                json.setMsg("新增运输单成功！");
                shipWebSocket.sendStringMessage(WebSocketUtil.generateMsg(null, 0));
                return  json;
            }
        }catch (Exception e){
            errStr = e.getMessage();
            // e.printStackTrace();
        }
        json.setSuccess(false);
        json.setMsg("运输单新增失败！\n\n" + errStr);
        return json;
    }

    @RequestMapping("/del")
    public Json del(Integer id) {
        Json json = new Json();
        try {
            shipService.delete(id);
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
