package org.example.service;

import org.example.pageModel.EntryDetailDto;
import org.example.pojo.Entry;

import java.util.List;



public interface EntryService extends BaseService<Entry, Integer>{

    public boolean editEntry(Entry entry, List<EntryDetailDto> lstInserted, List<EntryDetailDto> lstUpdated,
                                List<EntryDetailDto> lstDeleted);

    public boolean addEntry(Entry entry, List<EntryDetailDto> lstInserted);

    public boolean deleteEntry(Integer entryPid);
}
