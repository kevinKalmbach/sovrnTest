package com.example.service;

import java.util.Arrays;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.dal.CallToPartner;
import com.example.dal.CallToPartnerImpl;
import com.example.dal.PartnerDal;
import com.example.domain.AdRequest;
import com.example.domain.AdResponse;
import com.example.domain.Partner;
import com.example.domain.PartnerRequest;
import com.example.domain.PartnerResponse;

import rx.Observable;
import rx.schedulers.Schedulers;

@Service
public class AdServiceImpl implements AdService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CallToPartnerImpl.class);

	private final PartnerDal partnerDal;
	private final CallToPartner callPartner;
	private final HistoryService historyService;

	public AdServiceImpl(PartnerDal pd, CallToPartner call, HistoryService historyService) {
		this.partnerDal = pd;
		this.callPartner = call;
		this.historyService = historyService;
	}

	private PartnerResponse pickHighestAndStoreHistory(Object a[], AdRequest request) {
		LOGGER.info("going to get highest");
		PartnerResponse highest = Arrays.stream(a).filter(Objects::nonNull).map(o -> (PartnerResponse) o).
				peek(pr -> historyService.store(request,pr)).
				reduce(null,
				(h, pr) -> h == null || h.getBidprice().compareTo(pr.getBidprice()) == -1 ? pr : h);
		LOGGER.info("wow, we chose {}", highest);
		historyService.storeWinningBid(request, highest);
		return highest;

	}

	private AdResponse mapToAdResponse(AdRequest request, PartnerResponse pr) {
		return AdResponse.builder().tid(request.getTid()).html(pr.getAdhtml()).build();

	}

	@Override
	public Observable<AdResponse> getAd(AdRequest request) {
		PartnerRequest pr = PartnerRequest.builder().width(request.getWidth()).height(request.getHeight())
				.domain(request.getDomain()).userip(request.getUserip()).useragent(request.getUserAgent()).build();

		Observable<Partner> partners = partnerDal.getPartners(request).doOnNext(p -> LOGGER.info("got a partner {}", p));
		Observable<Observable<PartnerResponse>> responses = partners.map(p -> callPartner.callpartner(p, pr)).doOnNext(pp -> LOGGER.info("got a partner response {}", pp));
		return Observable.zip(responses, a -> pickHighestAndStoreHistory(a,request)).filter(Objects::nonNull).map(l -> mapToAdResponse(request, l))
				.observeOn(Schedulers.io());
	}

}
