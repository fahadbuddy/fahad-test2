package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.component.impl.ServerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataEnvelopeApiObject;
import static com.db.dataplatform.techtest.TestDataHelper.createTestInvalidCheckSumDataEnvelopeApiObject;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServerServiceTests {

    @Mock
    private DataBodyService dataBodyServiceImplMock;

    private ModelMapper modelMapper;

    private DataBodyEntity expectedDataBodyEntity;
    private DataEnvelope expectedDataEnvelope;

    private Server server;

    @Before
    public void setup() {
        ServerMapperConfiguration serverMapperConfiguration = new ServerMapperConfiguration();
        modelMapper = serverMapperConfiguration.createModelMapperBean();

        expectedDataEnvelope = createTestDataEnvelopeApiObject();
        expectedDataBodyEntity = modelMapper.map(expectedDataEnvelope.getDataBody(), DataBodyEntity.class);
        expectedDataBodyEntity.setDataHeaderEntity(modelMapper.map(expectedDataEnvelope.getDataHeader(), DataHeaderEntity.class));

        server = new ServerImpl(dataBodyServiceImplMock, modelMapper);
    }

    @Test
    public void shouldSaveDataEnvelopeAsExpected() throws NoSuchAlgorithmException, IOException {
        // Given/When
        boolean success = server.saveDataEnvelope(expectedDataEnvelope);

        // Then
        assertThat(success).isTrue();
        verify(dataBodyServiceImplMock, times(1)).saveDataBody(eq(expectedDataBodyEntity));
    }

    @Test
    public void shouldNotSaveDataEnvelopeIfCheckSumNotMatched() throws NoSuchAlgorithmException, IOException {
        // Given/When
        boolean success = server.saveDataEnvelope(createTestInvalidCheckSumDataEnvelopeApiObject());

        // Then
        assertThat(success).isFalse();
        verify(dataBodyServiceImplMock, times(0)).saveDataBody(any());
    }

    @Test
    public void shouldGetAllDataEnvelopeWhereBlockTypeMatches() throws NoSuchAlgorithmException, IOException {
        // Given
        when(dataBodyServiceImplMock.getDataByBlockType(eq(BlockTypeEnum.BLOCKTYPEA))).thenReturn(asList(expectedDataBodyEntity));

        // When
        List<DataEnvelope> result = server.getAllDataForBlockType(BlockTypeEnum.BLOCKTYPEA);

        // Then
        assertThat(result).hasSize(1);
        verify(dataBodyServiceImplMock, times(1)).getDataByBlockType(any());
    }


}
