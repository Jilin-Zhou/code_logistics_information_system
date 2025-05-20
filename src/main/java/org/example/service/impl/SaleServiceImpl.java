package org.example.service.impl;

import org.example.dao.GoodsDao;
import org.example.dao.SaleDao;
import org.example.dao.SaleDetailDao;
import org.example.pageModel.SaleDetailDto;
import org.example.pojo.Goods;
import org.example.pojo.Sale;
import org.example.pojo.SaleDetail;
import org.example.service.SaleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class SaleServiceImpl extends BaseServiceImpl<Sale, Integer> implements SaleService {

    @Resource(name = "saleDetailDaoImpl")
    SaleDetailDao saleDetailDao;

    @Resource(name = "goodsDaoImpl")
    GoodsDao goodsDao;

    @Resource(name = "saleDaoImpl")
    public void setBaseDao(SaleDao dao) {
        super.setBaseDao(dao);
    }

    public boolean editSale(Sale sale, List<SaleDetailDto> lstInserted, List<SaleDetailDto> lstUpdated,
                                    List<SaleDetailDto> lstDeleted){
        //更新表头
        Sale pur_plan_in_dataBase = this.find(sale.getId());
        BeanUtils.copyProperties(sale,pur_plan_in_dataBase,"saleDetailSet");
        this.update(pur_plan_in_dataBase);

        //新删除的表细数据
        if (lstDeleted.size()>0){
            for (SaleDetailDto d : lstDeleted){
                saleDetailDao.remove(saleDetailDao.find(d.getId()));
            }
        }

        //新增的表细数据
        if (lstInserted.size() > 0){
            for (SaleDetailDto d : lstInserted) {
                SaleDetail saleDetail = new SaleDetail();
                saleDetail.setSale(pur_plan_in_dataBase);
                Goods goods = goodsDao.find(d.getGoodsPid());
                saleDetail.setGoods(goods);
                BeanUtils.copyProperties(d,saleDetail, "salePid","goodsPid");
                saleDetailDao.persist(saleDetail);
            }

        }

        //更新的表细数据
        if (lstUpdated.size() > 0){
            for (SaleDetailDto d : lstUpdated) {
                SaleDetail saleDetail = saleDetailDao.find(d.getId());
                Goods goods = goodsDao.find(d.getGoodsPid());
                saleDetail.setGoods(goods);
                BeanUtils.copyProperties(d,saleDetail, "salePid","goodsPid");
                saleDetailDao.persist(saleDetail);
            }
        }

        return true;
    }

    public boolean addSale(Sale sale, List<SaleDetailDto> lstInserted){
        //更新表头
        sale.setId(null);

        Set<SaleDetail> saleDetailSet = new HashSet<>();
        if (lstInserted.size() > 0){
            for (SaleDetailDto d : lstInserted) {
                SaleDetail saleDetail = new SaleDetail();
                Goods goods = goodsDao.find(d.getGoodsPid());
                saleDetail.setGoods(goods);
                saleDetail.setSale(sale);
                saleDetail.setAlreadyNum(0);
                BeanUtils.copyProperties(d, saleDetail, "salePid", "goodsPid");
                saleDetailSet.add(saleDetail);
            }
        }
        sale.setSaleDetailSet(saleDetailSet);

        this.save(sale);
        return true;
    }
}
