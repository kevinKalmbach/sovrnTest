package com.example.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import com.example.domain.AdRequest;
import com.example.domain.Provider;
import com.example.domain.ProviderResponse;

import rx.Emitter;
import rx.Emitter.BackpressureMode;
import rx.Observable;

@Repository
public class ProviderDalImpl implements ProviderDal {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderDalImpl.class);
	private final String statement = "select url,provider_name,provider_id from provider where provider_id in (select provider_id from provider_size_assoc where ad_size_id IN (select ad_size_id from ad_size where width=? and height=?)) and provider_id in (select provider_id from user_provider_assoc where user_id=?)";
	private JdbcTemplate template;
	private Validator validator;

	public ProviderDalImpl(DataSource dataSource, Validator validator) {
		template = new JdbcTemplate(dataSource);
		this.validator = validator;
	}

	private Provider createFromRow(ResultSet rs) throws SQLException, ConstraintViolationException {
		Provider p =  Provider.builder().url(rs.getString(1)).providerId(rs.getInt(3)).build();
		 Set<ConstraintViolation<Provider>> violations = validator.validate(p);
		 if (!violations.isEmpty())
		 {
			 LOGGER.info("constraint violations");
			 throw new ConstraintViolationException(violations);
		 }
		return p;

	}

	private void selectFromDb(Emitter<Provider> e, AdRequest request) {
		PreparedStatementSetter pss = ps -> {
			ps.setInt(1, request.getHeight());
			ps.setInt(2, request.getWidth());
			ps.setInt(3, request.getUserid());
		};
		RowCallbackHandler rch = rs -> {
			try {
				e.onNext(createFromRow(rs));
			} catch (Throwable t) {
				e.onError(t);
			}
		};
		template.query(statement, pss, rch);
		e.onCompleted();
	}

	@Override
	public Observable<Provider> getProviders(AdRequest request) {
		return Observable.fromEmitter(emitter -> selectFromDb(emitter, request), BackpressureMode.BUFFER);

	}

}
