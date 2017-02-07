package com.example.service;

import java.util.Collection;

import com.example.domain.AdRequest;
import com.example.domain.HistoryEntry;
import com.example.domain.ProviderResponse;

import rx.Observable;

public interface HistoryService {
	void store(AdRequest request, ProviderResponse pr);
	void storeWinningBid(AdRequest request ,ProviderResponse pr);
	Observable<String> storeClick(int userId, String tid);
    Observable<Collection<HistoryEntry>> getHistory();
}
