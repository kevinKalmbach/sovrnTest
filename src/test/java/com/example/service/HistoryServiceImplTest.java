package com.example.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Collection;

import org.junit.Test;
import org.mockito.Mockito;

import com.example.config.AppProperties;
import com.example.domain.AdRequest;
import com.example.domain.HistoryEntry;
import com.example.domain.ProviderResponse;

public class HistoryServiceImplTest {
	
	
	private AdRequest getNewAdRequest() {
		return AdRequest.builder().build();
	}
	private ProviderResponse getProviderResponse(int id) {
		return ProviderResponse.builder().providerId(id).bidprice(BigDecimal.ONE).build();
	}
	private boolean isInHstory(HistoryServiceImpl hs, String tid) {
		Collection<HistoryEntry> history = hs.getHistory().toBlocking().first();
		return history.stream().filter(e -> e.getTid().equals(tid)).findFirst().isPresent();
	}
	@Test
	public void simpleTest() {
		AppProperties props = Mockito.mock(AppProperties.class);
		Mockito.when(props.getClickTimer()).thenReturn(100);
		HistoryServiceImpl hs = new HistoryServiceImpl(props);
		
		AdRequest r = getNewAdRequest();		
		hs.store(r, getProviderResponse(1));
		assertTrue(isInHstory(hs, r.getTid()));		
	}
	@Test
	public void EvictionTest() {
		AppProperties props = Mockito.mock(AppProperties.class);
		Mockito.when(props.getClickTimer()).thenReturn(100);
		HistoryServiceImpl hs = new HistoryServiceImpl(props);
		
		// Add the fist one in
		AdRequest originalRequest = getNewAdRequest();		
		hs.store(originalRequest, getProviderResponse(1));
		assertTrue(isInHstory(hs, originalRequest.getTid()));
		
		// add 100 more things in
		for (int i=0; i < 100; i++) {
			AdRequest r = getNewAdRequest();		
			hs.store(r, getProviderResponse(1));		
		}
		
		//Now it should be gone
		assertFalse(isInHstory(hs, originalRequest.getTid()));

	}

	
}
