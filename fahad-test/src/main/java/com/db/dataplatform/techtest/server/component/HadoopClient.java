package com.db.dataplatform.techtest.server.component;

public interface HadoopClient {
  boolean persistToDataLake(String payload);
}
