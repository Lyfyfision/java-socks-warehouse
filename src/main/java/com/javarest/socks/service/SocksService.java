package com.javarest.socks.service;

import com.javarest.socks.dto.CottonPercentageFilter;
import com.javarest.socks.dto.SocksRequest;
import com.javarest.socks.model.Socks;

import java.util.List;


public interface SocksService {
    void registerSocksIncome(SocksRequest socks);
    void registerSocksOutcome(SocksRequest socks);
    void updateSocksById(Long id, SocksRequest updatedSocks);
    int getSocksCount(String color, CottonPercentageFilter filter);
    List<Socks> getAllSocksSorted(String color, CottonPercentageFilter filter, String sortField, String sortDirection);
}
