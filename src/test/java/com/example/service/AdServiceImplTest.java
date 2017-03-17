package com.example.service;
import com.example.dal.CallToProvider;
import com.example.dal.ProviderDal;
import com.example.domain.AdRequest;
import com.example.domain.AdResponse;
import com.example.domain.Provider;
import com.example.domain.ProviderResponse;
import com.google.common.primitives.Ints;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AdServiceImplTest {
	@Mock
	private ProviderDal pd;
	@Mock
	private HistoryService historyService;
	@Mock
	private CallToProvider providerCalls;

	@InjectMocks
	AdServiceImpl adService;

	private class ProviderMatches extends BaseMatcher<Provider> {
		int id;

		ProviderMatches(int id) {
			this.id = id;
		}

		@Override
		public boolean matches(Object item) {
			if (!(item instanceof Provider)) {
				return false;
			}
			int otherId = ((Provider) item).getProviderId();
			return id == otherId;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("whatever");
		}

	}

	@Before
	public void setupProviderCalls() {
		ProviderResponse r1 = ProviderResponse.builder().providerId(1).bidprice(new BigDecimal("1")).adhtml("h1").build();
		ProviderResponse r2 = ProviderResponse.builder().providerId(2).bidprice(new BigDecimal("2")).adhtml("h2").build();
		ProviderResponse r3 = ProviderResponse.builder().providerId(3).bidprice(new BigDecimal("3")).adhtml("h3").build();
		// A null response means the provider is'y there, slow, non-200, invalid response, etc
		ProviderResponse r4 = null;

		Mockito.when(providerCalls.callProvider(Mockito.argThat(new ProviderMatches(1)), Mockito.any()))
				.thenReturn(Observable.just(r1));
		Mockito.when(providerCalls.callProvider(Mockito.argThat(new ProviderMatches(2)), Mockito.any()))
				.thenReturn(Observable.just(r2));
		Mockito.when(providerCalls.callProvider(Mockito.argThat(new ProviderMatches(3)), Mockito.any()))
				.thenReturn(Observable.just(r3));
		Mockito.when(providerCalls.callProvider(Mockito.argThat(new ProviderMatches(4)), Mockito.any()))
				.thenReturn(Observable.just(r4));

	}
	public void setupProviderDal(int ... desiredProviders) {
		List<Provider> ret = Ints.asList(desiredProviders).stream().map(i -> Provider.builder().providerId(i).url("http://").build()).collect(Collectors.toList());		
		Mockito.when(pd.getProvidersObs(Mockito.any())).thenReturn(Observable.from(ret));
		Mockito.when(pd.getProviders(Mockito.any())).thenReturn(ret);


	}

	@Test
	public void test1() {
		AdRequest request = AdRequest.builder().build();
		setupProviderDal(1,2,3);
		AdResponse response = adService.getAd(request).toBlocking().first();
		// Make sure the history service was called
		Mockito.verify(historyService, Mockito.times(3)).store(Mockito.any(),Mockito.any());
		Mockito.verify(historyService).storeWinningBid(Mockito.any(),Mockito.any());

		assertEquals("h3", response.getHtml());
	}
	@Test(expected=NoSuchElementException.class)
	public void testNoProviders() {
		AdRequest request = AdRequest.builder().build();
		setupProviderDal();
		adService.getAd(request).toBlocking().first();

	}
	@Test(expected=NoSuchElementException.class)
	public void testOnlyBadProviders() {
		AdRequest request = AdRequest.builder().build();
		setupProviderDal(4,4,4);
		// Make sure the history service was never called
		Mockito.verify(historyService, Mockito.never()).store(Mockito.any(),Mockito.any());
		Mockito.verify(historyService, Mockito.never()).storeWinningBid(Mockito.any(),Mockito.any());
		adService.getAd(request).toBlocking().first();
	}
	@Test
	public void testOneBadProvider() {
		AdRequest request = AdRequest.builder().build();
		setupProviderDal(1,3,4,2);
		AdResponse response = adService.getAd(request).toBlocking().first();

		// Make sure the history service was called	
		Mockito.verify(historyService, Mockito.times(3)).store(Mockito.any(),Mockito.any());
		Mockito.verify(historyService).storeWinningBid(Mockito.any(),Mockito.any());		
		assertEquals("h3", response.getHtml());
	}
}
