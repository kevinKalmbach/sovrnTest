package com.example.dal;

import com.example.domain.Partner;
import com.example.domain.PartnerRequest;
import com.example.domain.PartnerResponse;

import rx.Observable;

public interface CallToPartner {
	Observable<PartnerResponse> callpartner(Partner p, PartnerRequest request);
}
