package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.db.dataplatform.techtest.Constant.URI_GETDATA;
import static com.db.dataplatform.techtest.Constant.URI_PUSHDATA;
import static java.util.Arrays.asList;
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

        HttpEntity<DataEnvelope> body = new HttpEntity<>(dataEnvelope, createHttpHeaders());

        restTemplate.postForLocation(URI_PUSHDATA, body);

    }

    @Override
    public Optional<List<DataEnvelope>> getData(String blockType) {
        log.info("Query for data with header block type {}", blockType);

        try {
            ResponseEntity<DataEnvelope[]> responseEntity = restTemplate.getForEntity(URI_GETDATA.expand(blockType),
                                                                                      DataEnvelope[].class);

            if (responseEntity.hasBody()) {
                return Optional.of(asList(responseEntity.getBody()));
            }

        } catch (Exception e) {
             log.error("Exception occurred when querying for header block type {}", blockType);
        }
        return Optional.empty();
    }

    @Override
    public boolean updateData(String blockName, String newBlockType) {
        log.info("Updating blocktype to {} for block with name {}", newBlockType, blockName);
        return true;
    }

    private HttpHeaders createHttpHeaders() {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(singletonList(MediaType.APPLICATION_JSON));
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        return requestHeaders;
    }


}
