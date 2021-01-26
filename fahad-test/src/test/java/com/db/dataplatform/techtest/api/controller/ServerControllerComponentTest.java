package com.db.dataplatform.techtest.api.controller;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.controller.ServerController;
import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.exception.HadoopClientException;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.db.dataplatform.techtest.Constant.URI_GETDATA;
import static com.db.dataplatform.techtest.Constant.URI_PUSHDATA;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class ServerControllerComponentTest {


  @Mock
  private Server serverMock;

  private DataEnvelope testDataEnvelope;
  private ObjectMapper objectMapper;
  private MockMvc mockMvc;
  private ServerController serverController;

  @Before
  public void setUp() throws HadoopClientException, NoSuchAlgorithmException, IOException {

    serverController = new ServerController(serverMock);
    mockMvc = standaloneSetup(serverController).build();
    objectMapper = Jackson2ObjectMapperBuilder.json()
                                              .build();

    testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();

    when(serverMock.saveDataEnvelope(any(DataEnvelope.class))).thenReturn(true);
    when(serverMock.getAllDataForBlockType(any(BlockTypeEnum.class))).thenReturn(singletonList(testDataEnvelope));
  }

  @Test
  public void testPushDataPostCallWorksAsExpected() throws Exception {

    String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

    MvcResult mvcResult = mockMvc.perform(post(URI_PUSHDATA).content(testDataEnvelopeJson)
                                                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                                 .andExpect(status().isOk())
                                 .andReturn();

    boolean checksumPass = Boolean.parseBoolean(mvcResult.getResponse()
                                                         .getContentAsString());
    assertThat(checksumPass).isTrue();
  }

  @Test
  public void testGetAllPersistedBlocksWorksAsExpected() throws Exception {

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URI_GETDATA.expand(BlockTypeEnum.BLOCKTYPEA))
                                                                .accept(MediaType.APPLICATION_JSON))
                                 .andExpect(status().isOk())
                                 .andReturn();

    List<DataEnvelope> result = objectMapper.readValue(mvcResult.getResponse()
                                                                .getContentAsString(), new TypeReference<List<DataEnvelope>>() {

    });

    assertThat(result).hasSize(1);
  }
}
