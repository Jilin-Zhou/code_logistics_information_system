package org.example.service.impl;

import org.example.dao.*;
import org.example.exception.ExceedException;
import org.example.pageModel.ShipDetailDto;
import org.example.pojo.*;
import org.example.service.ShipService;
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
public class ShipServiceImpl extends BaseServiceImpl<Ship, Integer> implements ShipService {

    @Resource(name = "shipDetailDaoImpl")
    ShipDetailDao shipDetailDao;

    @Resource(name = "outDetailDaoImpl")
    OutDetailDao outDetailDao;

    @Resource(name = "shipDaoImpl")
    public void setBaseDao(ShipDao dao) {
        super.setBaseDao(dao);
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final AtomicInteger orderCounter = new AtomicInteger(0);

    private String generateShipOrderNumber() {
        Date now = new Date();
        String datePart = dateFormat.format(now);
        int orderNum = orderCounter.incrementAndGet();
        return "PO" + datePart + "-" + String.format("%03d", orderNum);
    }

    public boolean editShip(Ship ship, List<ShipDetailDto> lstInserted, List<ShipDetailDto> lstUpdated, List<ShipDetailDto> lstDeleted){
        // 更新表头
        Ship pur_in_dataBase = this.find(ship.getId());
        BeanUtils.copyProperties(ship, pur_in_dataBase, "shipDetailSet");
        this.update(pur_in_dataBase);

        // 创建一个新的OutDetail集合
        Set<ShipDetail> shipDetailSet = new HashSet<>();

        // 处理删除的表细数据
        if (!lstDeleted.isEmpty()){
            for (ShipDetailDto d : lstDeleted){
                ShipDetail shipDetail = shipDetailDao.find(d.getId());
                // 把删除的采购数量加回去
                int alreadyNum = shipDetail.getOutDetail().getAlreadyNum() - shipDetail.getNum();
                shipDetail.getOutDetail().setAlreadyNum(alreadyNum);
                shipDetailDao.merge(shipDetail);
                shipDetailDao.remove(shipDetail);
            }
        }

        // 处理新增的表细数据
        if (!lstInserted.isEmpty()){
            for (ShipDetailDto d : lstInserted){
                ShipDetail shipDetail = new ShipDetail();
                shipDetail.setShip(ship);

                OutDetail outDetail = outDetailDao.find(d.getId());
                shipDetail.setOutDetail(outDetail);       // 设置关联
                BeanUtils.copyProperties(d, shipDetail, "id", "shipPid", "outDetailPid");

                setAlreadyNum(d, shipDetail, outDetail);

                shipDetailSet.add(shipDetail);
            }
        }

        pur_in_dataBase.setShipDetailSet(shipDetailSet);
        return true;
    }

    public boolean addShip(Ship ship, List<ShipDetailDto> lstInserted){

        // 更新表头
        ship.setId(null);

        Set<ShipDetail> shipDetailSet = new HashSet<>();
        if (!lstInserted.isEmpty()){
            for (ShipDetailDto d : lstInserted) {
                ShipDetail shipDetail = new ShipDetail();
                shipDetail.setShip(ship);

                OutDetail outDetail = outDetailDao.find(d.getId());
                shipDetail.setOutDetail(outDetail);       // 设置关联
                BeanUtils.copyProperties(d, shipDetail, "id", "shipPid", "outDetailPid");

                setAlreadyNum(d, shipDetail, outDetail);

                shipDetailSet.add(shipDetail);
            }
        }
        ship.setShipDetailSet(shipDetailSet);

        this.save(ship);
        return true;
    }

    private void setAlreadyNum(ShipDetailDto d, ShipDetail shipDetail, OutDetail outDetail) {
        // 更新采购计划明细的已采购量
        int alreadyNum = outDetail.getAlreadyNum() != null ? outDetail.getAlreadyNum() : 0;
        int num = d.getNum() != null ? d.getNum() : 0;
        alreadyNum += num;
        if (alreadyNum > outDetail.getNum()) {
            throw new ExceedException("运输量已超过出库量");
        } else {
            shipDetail.getOutDetail().setAlreadyNum(alreadyNum);       // 更新采购计划明细的已采购量
        }
    }
}
