package org.example.controller;

import org.example.pageModel.*;
import org.example.pojo.*;
import org.example.service.OutDetailService;
import org.example.service.OutService;
import org.example.util.JsonUtils;
import org.example.webSocket.OutWebSocket;
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
@RequestMapping("/out")
public class OutCtrl {
    private static final String GET_LAST_SNO = "SELECT sno FROM t_out ORDER BY id DESC LIMIT 1";

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
        String nextSerialNumber = String.format("OU%06d", nextSerialNumberValue);

        return nextSerialNumber;
    }
    @Resource(name="outServiceImpl")
    OutService outService;

    @Resource(name="outDetailServiceImpl")
    OutDetailService outDetailService;

    @Resource(name="outWebSocket")
    OutWebSocket outWebSocket;


    @RequestMapping("/getData")
    public Page<Out> getData(Pageable page, Out out, String start, String end,Integer cangPid,Integer yuanPid){
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
        if (yuanPid!=null){
            Filter ft = new Filter();
            ft.setProperty("yuan");
            ft.setValue(yuanPid);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
        }
        addStartAndEndRestrict(start, end, filters);
        page.setFilters(filters);

        return outService.findPage(page,out);
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

    @RequestMapping("/getSingleOut")
    public Out getSingleOut(Integer id){
        return outService.find(id);
    }


    @RequestMapping("/getOutDetails/{outPid}")
    public Page<OutDetail> getOutDetails(Pageable page, @PathVariable("outPid") Integer outPid){

        if (outPid!=null ){
            List<Filter> filters = new ArrayList<>();
            Filter ft = new Filter();
            ft.setProperty("out");
            ft.setValue(outPid);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
            page.setFilters(filters);
            return outDetailService.findPage(page);
        }else
            return null;
    }
    @RequestMapping("/getOutDetails")
    public Page<OutDetail> getOutDetails(Pageable page, OutDetail outDetail,
                                                   String  outSName){
        if (outSName!=null ){
            // 获取出库订单的id
            List<Filter> filters = new ArrayList<>();
            Filter ft = new Filter();
            ft.setProperty("sno");
            ft.setValue(outSName);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
            filters.add(ft);
            Order order=new Order("sno", Order.Direction.desc);
            List <Order> lstOrder = new ArrayList<>();
            lstOrder.add(order);
            List<Out> lstOut = outService.findList(0,1,filters,lstOrder);
            if (lstOut.size()>0) {
                Integer outPid = lstOut.get(0).getId();
                // end of 获取出库单的id

                filters.clear();
                ft.setProperty("out");
                ft.setValue(outPid);
                ft.setOperator(Filter.Operator.eq);
                filters.add(ft);
                page.setFilters(filters);
            }
        }

        return outDetailService.findPage(page,outDetail);
    }

    @RequestMapping("/edit")
    public Json edit(Out out,
                     @RequestParam(value = "inserted", required = false) String inserted,
                     @RequestParam(value = "updated", required = false) String updated,
                     @RequestParam(value = "deleted", required = false) String deleted,Integer cangPid,Integer yuanPid)
    {
        Json json = new Json();
        List<OutDetailDto> lstInserted = new ArrayList<>();
        List<OutDetailDto> lstUpdated = new ArrayList<>();
        List<OutDetailDto> lstDeleted = new ArrayList<>();


        if (!inserted.isEmpty()){
            lstInserted = JsonUtils.getListBeans(inserted, OutDetailDto.class);
        }
        if (!updated.isEmpty()){
            lstUpdated = JsonUtils.getListBeans(updated, OutDetailDto.class);
        }
        if (!deleted.isEmpty()){
            lstDeleted = JsonUtils.getListBeans(deleted, OutDetailDto.class);
        }

        String errStr="";
        try {
            if (cangPid != null) {
                Cang cang = new Cang();
                cang.setId(cangPid);
                out.setCang(cang);
            }
            if (yuanPid != null) {
                Yuan yuan = new Yuan();
                yuan.setId(yuanPid);
                out.setYuan(yuan);
            }
            boolean hasEdit = outService.editOut(out, lstInserted, lstUpdated, lstDeleted);
            if (hasEdit) {
                json.setSuccess(true);
                json.setMsg("出库单更新成功！");
                outWebSocket.sendStringMessage(WebSocketUtil.generateMsg(null, 0));
                return  json;
            }
        }catch (Exception e){
            errStr = e.getMessage();
        }
        json.setSuccess(false);
        json.setMsg("出库单更新失败！\n\n" + errStr);
        return  json;
    }

    @RequestMapping("/add")
    public Json add(Out out,
                    @RequestParam(value = "inserted", required = false) String inserted,
                    @RequestParam(value = "updated", required = false) String updated,Integer cangPid,Integer yuanPid)
    {
        Json json = new Json();
        List<OutDetailDto> lstInserted = new ArrayList<>();
        List<OutDetailDto> lstUpdated = new ArrayList<>();

        if (!inserted.isEmpty()){
            lstInserted = JsonUtils.getListBeans(inserted, OutDetailDto.class);
        }
        if (!updated.isEmpty()){
            lstUpdated = JsonUtils.getListBeans(updated, OutDetailDto.class);
        }

        if (lstInserted!=null && lstUpdated!=null)
            lstInserted.addAll(lstUpdated);

        String errStr="";

        try {
            if (cangPid != null) {
                Cang cang = new Cang();
                cang.setId(cangPid);
                out.setCang(cang);
            }
            if (yuanPid != null) {
                Yuan yuan = new Yuan();
                yuan.setId(yuanPid);
                out.setYuan(yuan);
            }
            boolean hasAdded = outService.addOut(out, lstInserted);
            if (hasAdded) {
                json.setSuccess(true);
                Integer id = out.getId();
                json.setId(id);
                json.setMsg("新增出库单成功！");
                outWebSocket.sendStringMessage(WebSocketUtil.generateMsg(null, 0));
                return  json;
            }
        }catch (Exception e){
            errStr = e.getMessage();
            // e.printStackTrace();
        }
        json.setSuccess(false);
        json.setMsg("出库单新增失败！\n\n" + errStr);
        return json;
    }

    @RequestMapping("/del")
    public Json del(Integer id) {
        Json json = new Json();
        try {
            outService.deleteOut(id);
            outService.delete(id);
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