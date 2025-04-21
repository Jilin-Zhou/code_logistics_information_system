package org.example.service.impl;

import org.example.dao.*;
import org.example.exception.ExceedException;
import org.example.pageModel.EntryDetailDto;
import org.example.pageModel.PurchaseDetailDto;
import org.example.pojo.*;
import org.example.service.EntryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.Transient;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;



@Service
@Transactional
public class EntryServiceImpl extends BaseServiceImpl<Entry, Integer> implements EntryService {

    @Resource(name = "entryDetailDaoImpl")
    EntryDetailDao entryDetailDao;

    @Resource(name = "purchaseDetailDaoImpl")
    PurchaseDetailDao purchaseDetailDao;

    @Resource(name = "goodsDaoImpl")
    GoodsDao goodsDao;

    @Resource(name = "entryDaoImpl")
    public void setBaseDao(EntryDao dao) {
        super.setBaseDao(dao);
    }

    @Resource(name = "entryDaoImpl")
    EntryDao entryDao;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final AtomicInteger orderCounter = new AtomicInteger(0);

    private String generateEntryOrderNumber() {
        Date now = new Date();
        String datePart = dateFormat.format(now);
        int orderNum = orderCounter.incrementAndGet();
        return "PO" + datePart + "-" + String.format("%03d", orderNum);
    }

    public boolean editEntry(Entry entry, List<EntryDetailDto> lstInserted, List<EntryDetailDto> lstUpdated, List<EntryDetailDto> lstDeleted){
        // 更新表头
        Entry pur_in_dataBase = this.find(entry.getId());
        BeanUtils.copyProperties(entry, pur_in_dataBase, "entryDetailSet");
        this.update(pur_in_dataBase);

        // 创建一个新的PurchaseDetail集合
        Set<EntryDetail> entryDetailSet = new HashSet<>();

        // 处理删除的表细数据
        if (!lstDeleted.isEmpty()){
            for (EntryDetailDto d : lstDeleted){

                EntryDetail entryDetail = entryDetailDao.find(d.getId());

                // 把删除的采购数量加回去
                int alreadyNum = entryDetail.getPurchaseDetail().getAlreadyNum() - entryDetail.getNum();
                int Num = entryDetail.getPurchaseDetail().getPurchasePlanDetail().getGoods().getNum() - entryDetail.getNum();
                entryDetail.getPurchaseDetail().setAlreadyNum(alreadyNum);
                entryDetail.getPurchaseDetail().getPurchasePlanDetail().getGoods().setNum(Num);
                entryDetailDao.merge(entryDetail);
                entryDetailDao.remove(entryDetail);
            }
        }

        // 处理新增的表细数据
        if (!lstInserted.isEmpty()){
            for (EntryDetailDto d : lstInserted){
                EntryDetail entryDetail = new EntryDetail();
                entryDetail.setEntry(entry);

                PurchaseDetail purchaseDetail = purchaseDetailDao.find(d.getId());
                Goods goods = goodsDao.find(purchaseDetailDao.find(d.getId()).getPurchasePlanDetail().getGoodsPid());
                entryDetail.setPurchaseDetail(purchaseDetail);       // 设置关联
                BeanUtils.copyProperties(d, entryDetail, "id", "entryPid", "purchaseDetailPid");

                setAlreadyNum(d, entryDetail, purchaseDetail);
                setNum(d, entryDetail, goods);

                entryDetailSet.add(entryDetail);
            }
        }
        if (lstUpdated.size() > 0){
            for (EntryDetailDto d : lstUpdated) {
                Goods goods = goodsDao.find(purchaseDetailDao.find(d.getId()).getPurchasePlanDetail().getGoodsPid());
                EntryDetail entryDetail = entryDetailDao.find(d.getId());

                PurchaseDetail purchaseDetail = purchaseDetailDao.find(d.getPurchasePid());
                entryDetail.setPurchaseDetail(purchaseDetail);
                BeanUtils.copyProperties(d,entryDetail, "entryPid","purchaseDetailPid");
                setNum(d, entryDetail, goods);
                setAlreadyNum(d, entryDetail, purchaseDetail);
                //entryDetailDao.merge(entryDetail);
                entryDetailDao.persist(entryDetail);
            }
        }

        pur_in_dataBase.setEntryDetailSet(entryDetailSet);
        return true;
    }

    public boolean addEntry(Entry entry, List<EntryDetailDto> lstInserted){
        // 更新表头
        entry.setId(null);

        Set<EntryDetail> entryDetailSet = new HashSet<>();
        if (!lstInserted.isEmpty()){
            for (EntryDetailDto d : lstInserted) {
                EntryDetail entryDetail = new EntryDetail();
                entryDetail.setEntry(entry);

                PurchaseDetail purchaseDetail = purchaseDetailDao.find(d.getId());
                Goods goods = goodsDao.find(purchaseDetail.getPurchasePlanDetail().getGoodsPid());
                entryDetail.setPurchaseDetail(purchaseDetail);       // 设置关联
                BeanUtils.copyProperties(d, entryDetail, "id", "entryPid", "purchaseDetailPid");

                setAlreadyNum(d, entryDetail, purchaseDetail);
                setNum(d, entryDetail, goods);

                entryDetailSet.add(entryDetail);
            }
        }
        entry.setEntryDetailSet(entryDetailSet);

        this.save(entry);
        return true;
    }


    public boolean deleteEntry(Integer entryPid) {//此函数只修改库存，没有删除单据
        // 获取单据信息
        Entry entry = entryDao.find(entryPid);

        // 获取单据明细列表
        List<EntryDetail> entryDetails = entry.getEntryDetails();

        // 遍历单据明细列表，更新商品库存
        for (EntryDetail d : entryDetails) {
            Integer Num = d.getNum();//明细中的入库数量
            int num = d.getPurchaseDetail().getPurchasePlanDetail().getGoods().getNum(); // 获取当前商品库存
            Num-=num;
            d.getPurchaseDetail().getPurchasePlanDetail().getGoods().setNum(Num);
        }
        return true;
    }

    private void setAlreadyNum(EntryDetailDto d, EntryDetail entryDetail, PurchaseDetail purchaseDetail) {
        // 更新已入库量
        int alreadyNum = purchaseDetail.getAlreadyNum() != null ? purchaseDetail.getAlreadyNum() : 0;
        int num = d.getNum() != null ? d.getNum() : 0;
        alreadyNum += num;
        if (alreadyNum > purchaseDetail.getNum()) {
            throw new ExceedException("入库量已超过采购量");
        } else {
            entryDetail.getPurchaseDetail().setAlreadyNum(alreadyNum);       // 更新采购计划明细的已采购量
        }
    }

    private void setNum(EntryDetailDto d, EntryDetail entryDetail, Goods goods) {
        // 更新在库数量
        int Num = goods.getNum() != null ? goods.getNum() : 0;//在库数量NUM
        int num = d.getNum() != null ? d.getNum() : 0;//入库数量
        Num += num;
        entryDetail.getPurchaseDetail().getPurchasePlanDetail().getGoods().setNum(Num);       // 更新采购计划明细的已采购量
    }

}
