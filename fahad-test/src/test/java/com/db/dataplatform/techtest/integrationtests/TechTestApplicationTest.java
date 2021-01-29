package com.db.dataplatform.techtest.integrationtests;

import com.db.dataplatform.techtest.TechTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(TechTestApplication.class)
public class TechTestApplicationTest {

  @Test
  void testContextLoads() {

  }
}
