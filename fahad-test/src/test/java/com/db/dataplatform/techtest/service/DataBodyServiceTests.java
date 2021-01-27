package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataStoreRepository;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.service.impl.DataBodyServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.db.dataplatform.techtest.TestDataHelper.TEST_NAME;
import static com.db.dataplatform.techtest.TestDataHelper.createTestDataBodyEntity;
import static com.db.dataplatform.techtest.TestDataHelper.createTestDataHeaderEntity;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataBodyServiceTests {

    public static final String TEST_NAME_NO_RESULT = "TestNoResult";

    @Mock
    private DataStoreRepository dataStoreRepositoryMock;

    private DataBodyService dataBodyService;
    private DataBodyEntity expectedDataBodyEntity;

    @Before
    public void setup() {
        DataHeaderEntity testDataHeaderEntity = createTestDataHeaderEntity(Instant.now(), BlockTypeEnum.BLOCKTYPEA,
                                                                           TestDataHelper.TEST_NAME);
        expectedDataBodyEntity = createTestDataBodyEntity(testDataHeaderEntity);

        dataBodyService = new DataBodyServiceImpl(dataStoreRepositoryMock);
    }

    @Test
    public void shouldSaveDataBodyEntityAsExpected(){
        // Given / When
        dataBodyService.saveDataBody(expectedDataBodyEntity);

        // Then
        verify(dataStoreRepositoryMock, times(1))
                .save(eq(expectedDataBodyEntity));
    }

    @Test
    public void canGetAllDataBodyEntitiesForAGivenBlockType(){
        // Given
        when(dataStoreRepositoryMock.findByDataHeaderEntityBlockType(any())).thenReturn(asList(expectedDataBodyEntity));

        // When
        dataBodyService.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA);

        // Then
        verify(dataStoreRepositoryMock, times(1)).findByDataHeaderEntityBlockType(BlockTypeEnum.BLOCKTYPEA);
    }

    @Test
    public void canGetDataBodyEntityForAGivenBlockName(){
        // Given
        when(dataStoreRepositoryMock.findByDataHeaderEntityName(any())).thenReturn(Optional.of(expectedDataBodyEntity));

        // When
        dataBodyService.getDataByBlockName(TEST_NAME);

        // Then
        verify(dataStoreRepositoryMock, times(1)).findByDataHeaderEntityName(TEST_NAME);
    }

}
