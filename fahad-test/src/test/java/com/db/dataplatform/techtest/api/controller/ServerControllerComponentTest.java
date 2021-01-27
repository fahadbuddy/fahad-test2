package com.db.dataplatform.techtest.api.controller;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.controller.ServerController;
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
import static com.db.dataplatform.techtest.Constant.URI_PATCHDATA;
import static com.db.dataplatform.techtest.Constant.URI_PUSHDATA;
import static com.db.dataplatform.techtest.TestDataHelper.TEST_NAME;
import static com.db.dataplatform.techtest.server.persistence.BlockTypeEnum.BLOCKTYPEB;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    when(serverMock.updateBlockTypeForBlockName(eq(TEST_NAME), eq(BLOCKTYPEB.name()))).thenReturn(Boolean.TRUE);
  }

  @Test
  public void testPushDataPostCallWorksAsExpected() throws Exception {
    // Given
    String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

    // When
    MvcResult mvcResult = mockMvc.perform(post(URI_PUSHDATA).content(testDataEnvelopeJson)
                                                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                                 .andExpect(status().isOk())
                                 .andReturn();

    // Then
    boolean checksumPass = Boolean.parseBoolean(mvcResult.getResponse()
                                                         .getContentAsString());
    assertThat(checksumPass).isTrue();
  }

  @Test
  public void testGetAllPersistedBlocksWorksAsExpected() throws Exception {
    // Given
    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URI_GETDATA.expand(BlockTypeEnum.BLOCKTYPEA))
                                                                .accept(MediaType.APPLICATION_JSON))
                                 .andExpect(status().isOk())
                                 .andReturn();

    // When
    List<DataEnvelope> result = objectMapper.readValue(mvcResult.getResponse()
                                                                .getContentAsString(), new TypeReference<List<DataEnvelope>>() {

    });

    // Then
    assertThat(result).hasSize(1);
  }

  @Test
  public void testCanUpdateBlockTypeViaBlockName() throws Exception {

    // Given
    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(URI_PATCHDATA.expand(TEST_NAME, BLOCKTYPEB))
                                                                .accept(MediaType.APPLICATION_JSON))
                                 .andExpect(status().isOk())
                                 .andReturn();

    // When
    Boolean result = objectMapper.readValue(mvcResult.getResponse()
                                                     .getContentAsString(), Boolean.class);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isTrue();
  }

}
