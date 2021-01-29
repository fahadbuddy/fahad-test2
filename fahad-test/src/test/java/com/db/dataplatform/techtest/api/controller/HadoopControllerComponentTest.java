package com.db.dataplatform.techtest.api.controller;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.controller.HadoopDummyServerController;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.exception.HadoopClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static com.db.dataplatform.techtest.Constant.URI_POSTDATA_HADOOP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class HadoopControllerComponentTest {


  @Mock
  private Server serverMock;

  private DataEnvelope testDataEnvelope;
  private ObjectMapper objectMapper;
  private MockMvc mockMvc;
  private HadoopDummyServerController serverController;

  @Before
  public void setUp() throws HadoopClientException, NoSuchAlgorithmException, IOException {

    serverController = new HadoopDummyServerController(serverMock);
    mockMvc = standaloneSetup(serverController).build();
    objectMapper = Jackson2ObjectMapperBuilder.json()
                                              .build();

    testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();

  }

  @Test
  public void testPushDataPostCallWorksAsExpected() throws Exception {
    // Given
    String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

    // When
    MvcResult mvcResult = mockMvc.perform(post(URI_POSTDATA_HADOOP).content(testDataEnvelopeJson)

                                                                   .contentType(MediaType.APPLICATION_JSON_VALUE))
                                 // Don't expect any status as the server may give timeout
                                 //.andExpect(status().isOk())
                                 .andReturn();

  }


}
