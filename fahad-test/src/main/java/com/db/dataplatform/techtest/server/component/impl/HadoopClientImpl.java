package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.server.component.HadoopClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.db.dataplatform.techtest.Constant.URI_POSTDATA_HADOOP;
import static java.util.Collections.singletonList;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableRetry
public class HadoopClientImpl implements HadoopClient {

  RestTemplate restTemplate;

  @Autowired
  public HadoopClientImpl(final RestTemplate restTemplate) {

    this.restTemplate = restTemplate;
  }


  @Override
  @Retryable(maxAttempts = 3 )
  public boolean persistToDataLake(final String payload) {

    log.info("Pushing payload {} to {}", payload, URI_POSTDATA_HADOOP);

    HttpEntity<String> body = new HttpEntity<>(payload, createHttpHeaders());

    ResponseEntity<Void> exchange = null;
    try {
      exchange = restTemplate.exchange(URI_POSTDATA_HADOOP, HttpMethod.POST,
                                       body, Void.class);

      if (exchange.getStatusCode().is2xxSuccessful()) {
        log.info("Successfully posted data to {}", URI_POSTDATA_HADOOP);
        return true;
      }
    }
    catch (Exception e) {
      log.error("Error occurred when pushing data {} to {}", payload,  URI_POSTDATA_HADOOP, e);
      throw e;
    }

    return false;
  }

  private HttpHeaders createHttpHeaders() {

    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.setAccept(singletonList(MediaType.APPLICATION_JSON));
    requestHeaders.setContentType(MediaType.APPLICATION_JSON);
    return requestHeaders;
  }
}
