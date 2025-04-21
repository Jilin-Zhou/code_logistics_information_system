package org.example.service.impl;

import org.example.dao.*;
import org.example.exception.ExceedException;
import org.example.pageModel.EntryDetailDto;
import org.example.pageModel.OutDetailDto;
import org.example.pageModel.OutDetailDto;
import org.example.pojo.*;
import org.example.service.OutService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;



@Service
@Transactional
public class OutServiceImpl extends BaseServiceImpl<Out, Integer> implements OutService {

    @Resource(name = "outDetailDaoImpl")
    OutDetailDao outDetailDao;

    @Resource(name = "saleDetailDaoImpl")
    SaleDetailDao saleDetailDao;

    @Resource(name = "outDaoImpl")
    OutDao outDao;

    @Resource(name = "goodsDaoImpl")
    GoodsDao goodsDao;

    @Resource(name = "outDaoImpl")
    public void setBaseDao(OutDao dao) {
        super.setBaseDao(dao);
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final AtomicInteger orderCounter = new AtomicInteger(0);

    private String generateOutOrderNumber() {
        Date now = new Date();
        String datePart = dateFormat.format(now);
        int orderNum = orderCounter.incrementAndGet();
        return "PO" + datePart + "-" + String.format("%03d", orderNum);
    }

    public boolean editOut(Out out, List<OutDetailDto> lstInserted, List<OutDetailDto> lstUpdated, List<OutDetailDto> lstDeleted){
        // 更新表头
        Out pur_in_dataBase = this.find(out.getId());
        BeanUtils.copyProperties(out, pur_in_dataBase, "outDetailSet");
        this.update(pur_in_dataBase);

        // 创建一个新的OutDetail集合
        Set<OutDetail> outDetailSet = new HashSet<>();

        // 处理删除的表细数据
        if (!lstDeleted.isEmpty()){
            for (OutDetailDto d : lstDeleted){
                OutDetail outDetail = outDetailDao.find(d.getId());
                // 把删除的采购数量加回去
                int alreadyNum = outDetail.getSaleDetail().getAlreadyNum() - outDetail.getNum();
                int Num = outDetail.getSaleDetail().getGoods().getNum()+outDetail.getNum();
                outDetail.getSaleDetail().setAlreadyNum(alreadyNum);
                outDetail.getSaleDetail().getGoods().setNum(Num);
                outDetailDao.merge(outDetail);
                outDetailDao.remove(outDetail);
            }
        }

        // 处理新增的表细数据
        if (!lstInserted.isEmpty()){
            for (OutDetailDto d : lstInserted){
                OutDetail outDetail = new OutDetail();
                outDetail.setOut(out);
                Goods goods = goodsDao.find(saleDetailDao.find(d.getId()).getGoodsPid());

                SaleDetail saleDetail = saleDetailDao.find(d.getId());
                outDetail.setSaleDetail(saleDetail);       // 设置关联
                BeanUtils.copyProperties(d, outDetail, "id", "outPid", "saleDetailPid");

                setAlreadyNum(d, outDetail, saleDetail);
                setNum(d, outDetail, goods);

                outDetailSet.add(outDetail);
            }
        }
        if (lstUpdated.size() > 0){
            for (OutDetailDto d : lstUpdated) {
                Goods goods = goodsDao.find(saleDetailDao.find(d.getId()).getGoodsPid());
                OutDetail outDetail = outDetailDao.find(d.getId());

                SaleDetail saleDetail = saleDetailDao.find(d.getSalePid());
                outDetail.setSaleDetail(saleDetail);
                BeanUtils.copyProperties(d,outDetail, "outPid","saleDetailPid");
                setNum(d, outDetail, goods);
                setAlreadyNum(d, outDetail, saleDetail);
                //outDetailDao.merge(outDetail);
                outDetailDao.persist(outDetail);
            }
        }
        pur_in_dataBase.setOutDetailSet(outDetailSet);
        return true;
    }

    public boolean addOut(Out out, List<OutDetailDto> lstInserted){
        // 更新表头
        out.setId(null);

        Set<OutDetail> outDetailSet = new HashSet<>();
        if (!lstInserted.isEmpty()){
            for (OutDetailDto d : lstInserted) {
                OutDetail outDetail = new OutDetail();
                outDetail.setOut(out);

                SaleDetail saleDetail = saleDetailDao.find(d.getId());
                Goods goods = goodsDao.find(saleDetail.getGoodsPid());
                outDetail.setSaleDetail(saleDetail);       // 设置关联
                BeanUtils.copyProperties(d, outDetail, "id", "outPid", "saleDetailPid");

                setAlreadyNum(d, outDetail, saleDetail);
                setNum(d, outDetail, goods);

                outDetailSet.add(outDetail);
            }
        }
        out.setOutDetailSet(outDetailSet);

        this.save(out);
        return true;
    }

    public boolean deleteOut(Integer outPid) {//此函数只修改库存，没有删除单据
        // 获取单据信息
        Out out = outDao.find(outPid);

        // 获取单据明细列表
        List<OutDetail> outDetails = out.getOutDetails();

        // 遍历单据明细列表，更新商品库存
        for (OutDetail d : outDetails) {
            Integer Num = d.getNum();//明细中的出库数量
            int num = d.getSaleDetail().getGoods().getNum(); // 获取当前商品库存
            Num+=num;
            d.getSaleDetail().getGoods().setNum(Num);
        }
        return true;
    }

    private void setAlreadyNum(OutDetailDto d, OutDetail outDetail, SaleDetail saleDetail) {
        // 更新采购计划明细的已采购量
        int alreadyNum = saleDetail.getAlreadyNum() != null ? saleDetail.getAlreadyNum() : 0;
        int num = d.getNum() != null ? d.getNum() : 0;
        alreadyNum += num;
        if (alreadyNum > saleDetail.getNum()) {
            throw new ExceedException("出库量已超过销售量");
        } else {
            outDetail.getSaleDetail().setAlreadyNum(alreadyNum);       // 更新采购计划明细的已采购量
        }
    }

    private void setNum(OutDetailDto d, OutDetail outDetail, Goods goods) {
        // 更新在库数量
        int Num = goods.getNum() != null ? goods.getNum() : 0;//在库数量NUM
        int num = d.getNum() != null ? d.getNum() : 0;//入库数量
        Num -= num;
        outDetail.getSaleDetail().getGoods().setNum(Num);       // 
    }
}