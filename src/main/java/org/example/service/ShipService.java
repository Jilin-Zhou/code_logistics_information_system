package org.example.service;

import org.example.pageModel.ShipDetailDto;
import org.example.pojo.Ship;

import java.util.List;



public interface ShipService extends BaseService<Ship, Integer>{

    public boolean editShip(Ship ship, List<ShipDetailDto> lstInserted, List<ShipDetailDto> lstUpdated,
                             List<ShipDetailDto> lstDeleted);

    public boolean addShip(Ship ship, List<ShipDetailDto> lstInserted);
}