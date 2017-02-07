package com.example.service;

import java.util.Arrays;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.dal.CallToProvider;
import com.example.dal.CallToProviderImpl;
import com.example.dal.ProviderDal;
import com.example.domain.AdRequest;
import com.example.domain.AdResponse;
import com.example.domain.Provider;
import com.example.domain.ProviderRequest;
import com.example.domain.ProviderResponse;

import rx.Observable;
import rx.schedulers.Schedulers;

@Service
public class AdServiceImpl implements AdService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CallToProviderImpl.class);

	private final ProviderDal providerDal;
	private final CallToProvider callProvider;
	private final HistoryService historyService;

	public AdServiceImpl(ProviderDal pd, CallToProvider call, HistoryService historyService) {
		this.providerDal = pd;
		this.callProvider = call;
		this.historyService = historyService;
	}

	private ProviderResponse pickHighestAndStoreHistory(Object a[], AdRequest request) {
		LOGGER.info("going to get highest");
		ProviderResponse highest = Arrays.stream(a).filter(Objects::nonNull).map(o -> (ProviderResponse) o).
				peek(pr -> historyService.store(request,pr)).
				reduce(null,
				(h, pr) -> h == null || h.getBidprice().compareTo(pr.getBidprice()) == -1 ? pr : h);
		LOGGER.info("wow, we chose {}", highest);
		historyService.storeWinningBid(request, highest);
		return highest;

	}

	private AdResponse mapToAdResponse(AdRequest request, ProviderResponse pr) {
		return AdResponse.builder().tid(request.getTid()).html(pr.getAdhtml()).build();

	}

	@Override
	public Observable<AdResponse> getAd(AdRequest request) {
		ProviderRequest pr = ProviderRequest.builder().width(request.getWidth()).height(request.getHeight())
				.domain(request.getDomain()).userip(request.getUserip()).useragent(request.getUserAgent()).build();

		Observable<Provider> providers = providerDal.getProviders(request).doOnNext(p -> LOGGER.info("got a provider {}", p));
		Observable<Observable<ProviderResponse>> responses = providers.map(p -> callProvider.callProvider(p, pr)).doOnNext(pp -> LOGGER.info("got a provider response {}", pp));
		return Observable.zip(responses, a -> pickHighestAndStoreHistory(a,request)).filter(Objects::nonNull).map(l -> mapToAdResponse(request, l))
				.observeOn(Schedulers.io());
	}

}
