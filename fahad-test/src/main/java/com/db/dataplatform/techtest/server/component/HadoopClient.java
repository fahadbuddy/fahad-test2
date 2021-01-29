package com.db.dataplatform.techtest.server.component;

import com.db.dataplatform.techtest.server.exception.HadoopClientException;

public interface HadoopClient {
  boolean persistToDataLake(String payload) throws HadoopClientException;
}
