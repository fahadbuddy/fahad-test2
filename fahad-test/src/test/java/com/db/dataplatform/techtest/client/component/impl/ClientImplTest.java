package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.BlockTypeEnum;
import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import com.db.dataplatform.techtest.client.config.RestTemplateConfiguration;
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

import java.util.List;

import static com.db.dataplatform.techtest.Constant.URI_GETDATA;
import static com.db.dataplatform.techtest.Constant.URI_PUSHDATA;
import static com.db.dataplatform.techtest.client.component.impl.TestDataHelperClient.createInvalidTestDataEnvelopeApiObject;
import static com.db.dataplatform.techtest.client.component.impl.TestDataHelperClient.createTestDataEnvelopeApiObject;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RestTemplateConfiguration.class, ClientImpl.class})
class ClientImplTest {

  @Autowired
  Client clientImpl;
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
  void canPushDataToServer() throws JsonProcessingException {

    // Given
    String expectedUri = URI_PUSHDATA;

    DataEnvelope expectedBody = createTestDataEnvelopeApiObject();

    mockServer.expect(once(), requestTo(expectedUri))
              .andExpect(method(HttpMethod.POST))
              .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                                                   .body(mapper.writeValueAsString(expectedBody)));

    // When
    clientImpl.pushData(expectedBody);

    // Then
    mockServer.verify();
  }

  @Test
  void whenInvalidDataEnvelopeThrowsException() throws Exception {

    // Given
    String expectedUri = URI_PUSHDATA;

    DataEnvelope expectedBody = createInvalidTestDataEnvelopeApiObject();

    // When/Then
    assertThrows(AssertionError.class, () -> clientImpl.pushData(expectedBody));
  }

  @Test
  void retriesIfServerUnavailable() throws JsonProcessingException {

    // Given
    String expectedUri = URI_PUSHDATA;

    DataEnvelope expectedBody = createTestDataEnvelopeApiObject();

    // first time server is unavailable
    mockServer.expect(once(), requestTo(expectedUri))
              .andExpect(method(HttpMethod.POST))
              .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

    // second try succeeds
    mockServer.expect(once(), requestTo(expectedUri))
              .andExpect(method(HttpMethod.POST))
              .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                                                   .body(mapper.writeValueAsString(expectedBody)));

    // When
    clientImpl.pushData(expectedBody);

    // Then
    mockServer.verify();
  }

  @Test
  void canGetDataFromServer() {

    String expectedUri = "/hotels/42";
    String body = "{ \"id\" : \"42\", \"name\" : \"Holiday Inn\"}";
    mockServer.expect(once(), requestTo(expectedUri))
              .andExpect(method(HttpMethod.GET))
              .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

  }

  @Test
  void canGetDataByBlockTypeFromServer() throws JsonProcessingException {

    // Given

    DataEnvelope expectedBody = createTestDataEnvelopeApiObject();
    String expectedJsonDataEnvelopeList = mapper.writeValueAsString(asList(expectedBody));

    mockServer.expect(once(), requestTo(URI_GETDATA.expand(BlockTypeEnum.BLOCKTYPEA)))
              .andExpect(method(HttpMethod.GET))
              .andRespond(withSuccess(expectedJsonDataEnvelopeList, MediaType.APPLICATION_JSON).contentType(
                      MediaType.APPLICATION_JSON));

    // When
    List<DataEnvelope> actual = clientImpl.getData(BlockTypeEnum.BLOCKTYPEA.name());


    // Then
    mockServer.verify();
    assertThat(actual).containsOnly(expectedBody);
  }

}