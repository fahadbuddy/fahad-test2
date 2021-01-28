package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;

import static com.db.dataplatform.techtest.Constant.URI_GETDATA;
import static com.db.dataplatform.techtest.Constant.URI_PATCHDATA;
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
    @Retryable
    public void pushData(@Valid DataEnvelope dataEnvelope) {
        log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);

        HttpEntity<DataEnvelope> body = new HttpEntity<>(dataEnvelope, createHttpHeaders());

        ResponseEntity<Void> exchange = null;
        try {
            exchange = restTemplate.exchange(URI_PUSHDATA, HttpMethod.POST,
                                             body, Void.class);

            if (exchange.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully pushed data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);
            }
        }
        catch (Exception e) {
            log.error("Error occurred when pushing data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA, e);
            throw e;
        }

    }

    @Override
    public List<DataEnvelope> getData(String blockType) {
        log.info("Query for data with header block type {}", blockType);

        ResponseEntity<DataEnvelope[]> exchange = null;
        try {
            exchange = restTemplate.exchange(URI_GETDATA.expand(blockType), HttpMethod.GET, new HttpEntity(createHttpHeaders()), DataEnvelope[].class);

            if (exchange.getStatusCode().is2xxSuccessful() && exchange.hasBody()) {
                log.info("Succesfully queried for data with header block type {}", blockType);
                return asList(exchange.getBody());
            }
        }
        catch (Exception e) {
            log.info("Got exception when querying for data with header block type {}", blockType);
            throw e;
        }
        return null;
    }

    @Override
    public boolean updateData(String blockName, String newBlockType) {
        log.info("Updating blockName {} to blocktype {}", blockName, newBlockType);

        ResponseEntity<Boolean> exchange = null;
        try {
            exchange = restTemplate.exchange(URI_PATCHDATA.expand(blockName, newBlockType), HttpMethod.PATCH,
                                             new HttpEntity(createHttpHeaders()), Boolean.class);

            if (exchange.getStatusCode().is2xxSuccessful() && exchange.getBody()) {
                log.info("Successfully updated blockName {} to blocktype {}", blockName, newBlockType);
                return true;
            }
        }
        catch (Exception e) {
            log.error("Exception occurred when updating blockname {}", blockName, e);
        }

        return false;
    }

    private HttpHeaders createHttpHeaders() {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(singletonList(MediaType.APPLICATION_JSON));
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        return requestHeaders;
    }


}
