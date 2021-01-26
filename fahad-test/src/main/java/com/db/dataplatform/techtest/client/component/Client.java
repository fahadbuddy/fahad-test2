package com.db.dataplatform.techtest.client.component;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

public interface Client {
    @Retryable
    void pushData(DataEnvelope dataEnvelope) throws JsonProcessingException;
    // Use Optional on return values, to avoid doing null checks.
    Optional<List<DataEnvelope>> getData(String blockType);
    boolean updateData(String blockName, String newBlockType) throws UnsupportedEncodingException;
}
