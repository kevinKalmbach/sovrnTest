package com.example.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.example.domain.AdResponse;
import com.example.service.AdService;

import rx.Observable;

//@RunWith(SpringRunner.class)
//@WebMvcTest(AdController.class)
//@AutoConfigureMockMvc(secure = false)
@RunWith(MockitoJUnitRunner.class)
public class AdControllerTest {
	private MockMvc mvc;

	@Mock
	private AdService service;
	@InjectMocks
	private AdController controller;

	private AdResponse RESPONSE = AdResponse.builder().tid("1111").html("h1").build();

	@Before
	public void setup() throws Exception {
		LocalValidatorFactoryBean v = new LocalValidatorFactoryBean();
		v.setProviderClass(HibernateValidator.class);
		v.afterPropertiesSet();
	v.afterPropertiesSet();
		mvc = MockMvcBuilders.standaloneSetup(controller).setValidator(v).build();

	}

	private ResultActions doAsync(String url) throws Exception {
		MvcResult mvcResult = mvc.perform(get(url).header("Accept", "*/*;*")).andExpect(status().isOk())
				.andExpect(request().asyncStarted()).andReturn();

		return mvc.perform(asyncDispatch(mvcResult));
	}

	@Test
	public void happyPathTest() throws Exception {
		Mockito.when(service.getAd(Mockito.any())).thenReturn(Observable.just(RESPONSE));

		doAsync("/ad?width=100&height=100&userid=111&url=http://whatever").andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("html").value("h1"));
	}

	@Test
	@Ignore
	public void checkBadInput() throws Exception {
		String url = "/ad?width=-1&height=100&userid=111&url=http://whatever";
		mvc.perform(get(url).header("Accept", "*/*;*")).andExpect(status().is(400));
		// missing
		url = "/ad?height=100&userid=111&url=http://whatever";
		mvc.perform(get(url).header("Accept", "*/*;*")).andExpect(status().is(400));
		url = "/ad?width=100&height=100&userid=-1&url=http://whatever";
		mvc.perform(get(url).header("Accept", "*/*;*")).andExpect(status().is(400));
		// TODO: more to do here

	}

	@Test
	public void testNotFound() throws Exception {
		Mockito.when(service.getAd(Mockito.any())).thenReturn(Observable.empty());

		doAsync("/ad?width=100&height=100&userid=111&url=http://whatever").andExpect(status().isNotFound());
	}

}
