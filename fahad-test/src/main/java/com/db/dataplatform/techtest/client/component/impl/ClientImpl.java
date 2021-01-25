package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;

import static com.db.dataplatform.techtest.Constant.URI_PUSHDATA;
import static java.util.Collections.singletonList;

/**
 * Client code does not require any test coverage
 */

@Service
@Slf4j
@RequiredArgsConstructor
@EnableRetry
public class ClientImpl implements Client {


    RestTemplate restTemplate;

    @Autowired
    public ClientImpl(final RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    @Override
    public void pushData(@Valid DataEnvelope dataEnvelope) {
        log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(singletonList(MediaType.APPLICATION_JSON));
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DataEnvelope> body = new HttpEntity<>(dataEnvelope, requestHeaders);

        restTemplate.postForLocation(URI_PUSHDATA, body);

    }

    @Override
    public List<DataEnvelope> getData(String blockType) {
        log.info("Query for data with header block type {}", blockType);
        return null;
    }

    @Override
    public boolean updateData(String blockName, String newBlockType) {
        log.info("Updating blocktype to {} for block with name {}", newBlockType, blockName);
        return true;
    }


}
