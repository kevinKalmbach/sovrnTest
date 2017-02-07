package com.example.service;

import java.util.Collection;

import com.example.domain.AdRequest;
import com.example.domain.HistoryEntry;
import com.example.domain.PartnerResponse;

import rx.Observable;

public interface HistoryService {
	void store(AdRequest request, PartnerResponse pr);
	void storeWinningBid(AdRequest request ,PartnerResponse pr);
	Observable<String> storeClick(int userId, String tid);
    Observable<Collection<HistoryEntry>> getHistory();
}
