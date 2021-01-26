package com.db.dataplatform.techtest.integrationtests;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import com.db.dataplatform.techtest.server.persistence.repository.DataStoreRepository;
import com.db.dataplatform.techtest.server.service.impl.DataBodyServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.util.List;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataBodyEntity;
import static com.db.dataplatform.techtest.TestDataHelper.createTestDataHeaderEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This Integration test is necessary to validate that the JPA queries created dynamically work as expected.
 * Mocking out a dynamic repo method introduces issues at runtime as we can't reliably know whether
 * the query will work as intended.
 */
@DataJpaTest
@EnableJpaRepositories(basePackages = "com.db.dataplatform.techtest.server.persistence.repository")
@EntityScan(basePackages = "com.db.dataplatform.techtest.server.persistence.model")
@ContextConfiguration(classes = TestConfiguration.class)
class DataBodyServiceImplIntegrationTest {

  @Autowired
  DataStoreRepository dataStoreRepository;

  @Autowired
  DataHeaderRepository dataHeaderRepository;

  @Test
  void canFilterDataBodyEntityByBlockTypeEnum() {
    // Given
    DataHeaderEntity testDataHeaderEntity = createTestDataHeaderEntity(Instant.now(), BlockTypeEnum.BLOCKTYPEA,
                                                                       TestDataHelper.TEST_NAME);
    final DataBodyEntity expectedDataBodyEntity = createTestDataBodyEntity(testDataHeaderEntity);

    DataHeaderEntity testDataHeaderEntity2 = createTestDataHeaderEntity(Instant.now(), BlockTypeEnum.BLOCKTYPEB,
                                                                        TestDataHelper.TEST_NAME_2);
    final DataBodyEntity expectedDataBodyEntity2 = createTestDataBodyEntity(testDataHeaderEntity2);

    // When
    dataStoreRepository.save(expectedDataBodyEntity);
    dataStoreRepository.save(expectedDataBodyEntity2);

    // Then
    assertThat(dataStoreRepository.findAll()).hasSize(2);
    assertThat(dataHeaderRepository.findAll()).hasSize(2);


    // When query bby BlockTypeA
    DataBodyServiceImpl service = new DataBodyServiceImpl(dataStoreRepository);

    List<DataBodyEntity> dataByBlockTypeA = service.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA);

    assertThat(dataByBlockTypeA).hasSize(1);
    assertThat(dataByBlockTypeA).containsOnly(expectedDataBodyEntity);
  }


}

// this is required to prevent spring doing auto scanning all
// classes. Only required classes are scanned by the context configuration above.
@Configuration
class TestConfiguration{

}