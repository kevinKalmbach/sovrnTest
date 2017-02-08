package com.example.service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.config.AppProperties;
import com.example.domain.AdRequest;
import com.example.domain.Bid;
import com.example.domain.HistoryCache;
import com.example.domain.HistoryEntry;
import com.example.domain.ProviderResponse;

import rx.Observable;

@Service
public class HistoryServiceImpl implements HistoryService {
	private static final Logger LOGGER = LoggerFactory.getLogger(HistoryService.class);
	private HistoryCache cache = new HistoryCache(100);
	private long clickTimer;

	HistoryServiceImpl(AppProperties props) {
		this.clickTimer = props.getClickTimer();
		LOGGER.info("Setting clicktimer to {}", clickTimer);
	}

	private HistoryEntry getHistoryEntry(int userId, String key) {
		if (!cache.containsKey(key)) {
			HistoryEntry entry = HistoryEntry.builder().tid(key).userid(userId).build();
			LOGGER.info("new entry {} {}", key);
			cache.put(key, entry);
		}
		return cache.get(key);
	}

	private HistoryEntry getHistoryEntry(AdRequest request) {
		return getHistoryEntry(request.getUserid(), request.getTid());
	}

	// Should synchronize this (or not store it in a memory cache)
	@Override
	public void store(AdRequest request, ProviderResponse pr) {
		String key = request.getTid();
		LOGGER.info("storing a bid {}", pr);
		if (pr == null) {
			LOGGER.warn("Not storing null response");
			return;
		}
		Bid bid = Bid.builder().providerId(pr.getProviderId()).bidPrice(pr.getBidprice()).build();
		HistoryEntry entry = getHistoryEntry(request);
		entry = entry.toBuilder().bid(bid).build();
		cache.put(key, entry);
	}

	// Should synchronize this (or not store it in a memory cache)
	@Override
	public void storeWinningBid(AdRequest request, ProviderResponse pr) {
		String key = request.getTid();
		LOGGER.info("storing a winning bid {}", pr);
		if (pr == null) {
			LOGGER.warn("Not storing null winner");
			return;
		}
		HistoryEntry entry = getHistoryEntry(request);
		entry = entry.toBuilder().winningPrice(pr.getBidprice()).winningProvider(pr.getProviderId())
				.startTime(System.currentTimeMillis()).build();
		cache.put(key, entry);

	}

	// Should synchronize this (or not store it in a memory cache)
	@Override
	public Observable<String> storeClick(int userId, String tid) {
		if (tid == null) {
			LOGGER.warn("Not storing null click");
			return Observable.just(tid);
		}
		HistoryEntry entry = getHistoryEntry(userId, tid);
		entry = entry.toBuilder().clickResult("CLICK").build();
		cache.put(tid, entry);
		return Observable.just(tid);
	}

	private HistoryEntry fillInClick(HistoryEntry entry) {
		if (entry.getClickResult() != null) {
			return entry;
		}
		long now = System.currentTimeMillis();
		long delta = now - entry.getStartTime();
		// Convert seconds to millis
		String result = delta > clickTimer * 1000 ? "STALE" : "REQUEST";
		return entry.toBuilder().clickResult(result).build();
	}

	@Override
	public Observable<Collection<HistoryEntry>> getHistory() {
		return Observable.just(cache.values().stream().map(e -> fillInClick(e)).collect(Collectors.toList()));
	}

}
