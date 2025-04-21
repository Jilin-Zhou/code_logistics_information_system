package org.example.controller;

import org.example.pageModel.*;
import org.example.pojo.*;
import org.example.service.EntryDetailService;
import org.example.service.EntryService;
import org.example.util.JsonUtils;
import org.example.webSocket.EntryWebSocket;
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
@RequestMapping("/entry")
public class EntryCtrl {
    private static final String GET_LAST_SNO = "SELECT sno FROM t_entry ORDER BY id DESC LIMIT 1";

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
        String nextSerialNumber = String.format("EN%06d", nextSerialNumberValue);

        return nextSerialNumber;
    }
    @Resource(name="entryServiceImpl")
    EntryService entryService;

    @Resource(name="entryDetailServiceImpl")
    EntryDetailService entryDetailService;

    @Resource(name="entryWebSocket")
    EntryWebSocket entryWebSocket;


    @RequestMapping("/getData")
    public Page<Entry> getData(Pageable page, Entry entry, String start, String end){
        page.setOrderProperty("updateTime");
        page.setOrderDirection(Order.Direction.desc);

        List<Filter> filters = new ArrayList<>();

        addStartAndEndRestrict(start, end, filters);
        page.setFilters(filters);

        return entryService.findPage(page,entry);
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

    @RequestMapping("/getSingleEntry")
    public Entry getSingleEntry(Integer id){
        return entryService.find(id);
    }


    @RequestMapping("/getEntryDetails/{entryPid}")
    public Page<EntryDetail> getEntryDetails(Pageable page, @PathVariable("entryPid") Integer entryPid){

        if (entryPid!=null ){
            List<Filter> filters = new ArrayList<>();
            Filter ft = new Filter();
            ft.setProperty("entry");
            ft.setValue(entryPid);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
            page.setFilters(filters);
            return entryDetailService.findPage(page);
        }else
            return null;
    }

    /**
     * 通过采购计划单的 sname（订单号，唯一键）来获取表细
     */

    @RequestMapping("/getEntryDetails")
    public Page<EntryDetail> getEntryDetails(Pageable page, EntryDetail entryDetail,
                                                   String  entrySName){
        if (entrySName!=null ){
            // 获取采购订单的id
            List<Filter> filters = new ArrayList<>();
            Filter ft = new Filter();
            ft.setProperty("sno");
            ft.setValue(entrySName);
            ft.setOperator(Filter.Operator.eq);
            filters.add(ft);
            filters.add(ft);
            Order order=new Order("sno", Order.Direction.desc);
            List <Order> lstOrder = new ArrayList<>();
            lstOrder.add(order);
            List<Entry> lstEntry = entryService.findList(0,1,filters,lstOrder);
            if (lstEntry.size()>0) {
                Integer entryPid = lstEntry.get(0).getId();
                // end of 获取采购单的id

                filters.clear();
                ft.setProperty("entry");
                ft.setValue(entryPid);
                ft.setOperator(Filter.Operator.eq);
                filters.add(ft);
                page.setFilters(filters);
            }
        }

        return entryDetailService.findPage(page,entryDetail);
    }

    @RequestMapping("/edit")
    public Json edit(Entry entry,
                     @RequestParam(value = "inserted", required = false) String inserted,
                     @RequestParam(value = "updated", required = false) String updated,
                     @RequestParam(value = "deleted", required = false) String deleted,Integer cangPid,Integer yuanPid)
    {
        Json json = new Json();
        List<EntryDetailDto> lstInserted = new ArrayList<>();
        List<EntryDetailDto> lstUpdated = new ArrayList<>();
        List<EntryDetailDto> lstDeleted = new ArrayList<>();


        if (!inserted.isEmpty()){
            lstInserted = JsonUtils.getListBeans(inserted, EntryDetailDto.class);
        }
        if (!updated.isEmpty()){
            lstUpdated = JsonUtils.getListBeans(updated, EntryDetailDto.class);
        }
        if (!deleted.isEmpty()){
            lstDeleted = JsonUtils.getListBeans(deleted, EntryDetailDto.class);
        }

        String errStr="";
        try {
            if (cangPid != null) {
                Cang cang = new Cang();
                cang.setId(cangPid);
                entry.setCang(cang);
            }
            if (yuanPid != null) {
                Yuan yuan = new Yuan();
                yuan.setId(yuanPid);
                entry.setYuan(yuan);
            }
            boolean hasEdit = entryService.editEntry(entry, lstInserted, lstUpdated, lstDeleted);
            if (hasEdit) {
                json.setSuccess(true);
                json.setMsg("入库单更新成功！");
                entryWebSocket.sendStringMessage(WebSocketUtil.generateMsg(null, 0));
                return  json;
            }
        }catch (Exception e){
            errStr = e.getMessage();
        }
        json.setSuccess(false);
        json.setMsg("入库单更新失败！\n\n" + errStr);
        return  json;
    }

    @RequestMapping("/add")
    public Json add(Entry entry,
                    @RequestParam(value = "inserted", required = false) String inserted,
                    @RequestParam(value = "updated", required = false) String updated,Integer cangPid,Integer yuanPid){
        Json json = new Json();
        List<EntryDetailDto> lstInserted = new ArrayList<>();
        List<EntryDetailDto> lstUpdated = new ArrayList<>();

        if (!inserted.isEmpty()){
            lstInserted = JsonUtils.getListBeans(inserted, EntryDetailDto.class);
        }
        if (!updated.isEmpty()){
            lstUpdated = JsonUtils.getListBeans(updated, EntryDetailDto.class);
        }

        if (lstInserted!=null && lstUpdated!=null)
            lstInserted.addAll(lstUpdated);

        String errStr="";

        try {
            if (cangPid != null) {
                Cang cang = new Cang();
                cang.setId(cangPid);
                entry.setCang(cang);
            }
            if (yuanPid != null) {
                Yuan yuan = new Yuan();
                yuan.setId(yuanPid);
                entry.setYuan(yuan);
            }
            boolean hasAdded = entryService.addEntry(entry, lstInserted);
            if (hasAdded) {
                json.setSuccess(true);
                Integer id = entry.getId();
                json.setId(id);
                json.setMsg("新增入库单成功！");
                entryWebSocket.sendStringMessage(WebSocketUtil.generateMsg(null, 0));
                return  json;
            }
        }catch (Exception e){
            errStr = e.getMessage();
            // e.printStackTrace();
        }
        json.setSuccess(false);
        json.setMsg("入库单新增失败！\n\n" + errStr);
        return json;
    }

    @RequestMapping("/del")
    public Json del(Integer id) {
        Json json = new Json();
        try {
            entryService.deleteEntry(id);
            entryService.delete(id);
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