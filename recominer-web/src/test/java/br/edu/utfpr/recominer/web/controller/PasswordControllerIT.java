package br.edu.utfpr.recominer.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.edu.utfpr.recominer.web.WebAppInitializer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebAppInitializer.class)
@WebIntegrationTest
public class PasswordControllerIT {

	private static final String APPLICATION_SERVICE_URL = "http://localhost:8080/projects";

	private RestTemplate rest = new TestRestTemplate();

	@Rule
	public Timeout timeout = Timeout.seconds(3);
	
	@Test
	public void testEmptyPasswordEvaluationService() throws JsonProcessingException {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		// Creating http entity object with request body and headers
		HttpEntity<String> httpEntity = new HttpEntity<String>("", requestHeaders);

		// Invoking the API
		String apiResponse = rest.postForObject(APPLICATION_SERVICE_URL, httpEntity, String.class);

		// Asserting the response of the API.
		assertNotNull(apiResponse);
		assertEquals("0", apiResponse);

	}
	
	@Test
	public void testSimplePasswordEvaluationService() throws JsonProcessingException {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		// Creating http entity object with request body and headers
		HttpEntity<String> httpEntity = new HttpEntity<String>("123456", requestHeaders);

		// Invoking the API
		String apiResponse = rest.postForObject(APPLICATION_SERVICE_URL, httpEntity, String.class);

		// Asserting the response of the API.
		assertNotNull(apiResponse);
		assertEquals("4", apiResponse);

	}

	@Test
	public void testComplexPasswordEvaluationService() throws JsonProcessingException {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		// Creating http entity object with request body and headers
		HttpEntity<String> httpEntity = new HttpEntity<String>("a!c#B1D2", requestHeaders);

		// Invoking the API
		String apiResponse = rest.postForObject(APPLICATION_SERVICE_URL, httpEntity, String.class);

		// Asserting the response of the API.
		assertNotNull(apiResponse);
		assertEquals("92", apiResponse);

	}
}
