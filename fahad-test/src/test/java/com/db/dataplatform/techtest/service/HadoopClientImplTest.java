package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.client.config.RestTemplateConfiguration;
import com.db.dataplatform.techtest.server.component.HadoopClient;
import com.db.dataplatform.techtest.server.component.impl.HadoopClientImpl;
import com.db.dataplatform.techtest.server.exception.HadoopClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static com.db.dataplatform.techtest.Constant.URI_POSTDATA_HADOOP;
import static com.db.dataplatform.techtest.client.component.impl.TestDataHelperClient.createTestDataEnvelopeApiObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RestTemplateConfiguration.class, HadoopClientImpl.class})
class HadoopClientImplTest {

  @Autowired
  HadoopClient clientImpl;
  @Autowired
  private RestTemplate restTemplate;
  private ObjectMapper mapper;
  private MockRestServiceServer mockServer;

  @BeforeEach
  void setup() {

    mockServer = MockRestServiceServer.bindTo(restTemplate)
                                      .build();
    mapper = new ObjectMapper();
  }

  @Test
  void canPushDataToServer() throws JsonProcessingException, HadoopClientException {

    // Given
    String expectedUri = URI_POSTDATA_HADOOP;

    String jsonPayload = mapper.writeValueAsString(createTestDataEnvelopeApiObject());

    mockServer.expect(once(), requestTo(expectedUri))
              .andExpect(method(HttpMethod.POST))
              .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                                                   .body(jsonPayload));

    // When
    boolean status = clientImpl.persistToDataLake(jsonPayload);

    // Then
    mockServer.verify();
    assertThat(status).isTrue();
  }

  @Test
  void retriesIfServerUnavailable() throws JsonProcessingException, HadoopClientException {

    // Given
    String expectedUri = URI_POSTDATA_HADOOP;

    String jsonPayload = mapper.writeValueAsString(createTestDataEnvelopeApiObject());

    // first time server is unavailable
    mockServer.expect(once(), requestTo(expectedUri))
              .andExpect(method(HttpMethod.POST))
              .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

    // second try succeeds
    mockServer.expect(once(), requestTo(expectedUri))
              .andExpect(method(HttpMethod.POST))
              .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                                                   .body(jsonPayload));

    // When
    boolean status = clientImpl.persistToDataLake(jsonPayload);

    // Then
    mockServer.verify();
    assertThat(status).isTrue();
  }

  @Test
  void whenServiceDown() throws JsonProcessingException {

    // Given
    String expectedUri = URI_POSTDATA_HADOOP;

    String jsonPayload = mapper.writeValueAsString(createTestDataEnvelopeApiObject());

    // first time server is unavailable
    mockServer.expect(times(3), requestTo(expectedUri))
              .andExpect(method(HttpMethod.POST))
              .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));


    // When
    assertThrows(HadoopClientException.class, () ->clientImpl.persistToDataLake(jsonPayload));

  }

}