package org.example.service;

import org.example.pageModel.OutDetailDto;
import org.example.pojo.Out;

import java.util.List;



public interface OutService extends BaseService<Out, Integer>{

    public boolean editOut(Out out, List<OutDetailDto> lstInserted, List<OutDetailDto> lstUpdated,
                                List<OutDetailDto> lstDeleted);

    public boolean addOut(Out out, List<OutDetailDto> lstInserted);

    public boolean deleteOut(Integer outPid);
}