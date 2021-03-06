package com.db.dataplatform.techtest.client.component.impl;


import com.db.dataplatform.techtest.client.api.model.BlockTypeEnum;
import com.db.dataplatform.techtest.client.api.model.DataBody;
import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.api.model.DataHeader;

/**
 * Created separate TestDataHelperClient to decouple the client's Data* classes from server's
 * Ideally client should be separated in a different maven module, but due to the brevity of
 * the excercise - this may suffice.
 */
public class TestDataHelperClient {

    public static final String TEST_NAME = "Test";
    public static final String TEST_NAME_EMPTY = "";
    public static final String DUMMY_DATA = "AKCp5fU4WNWKBVvhXsbNhqk33tawri9iJUkA5o4A6YqpwvAoYjajVw8xdEw6r9796h1wEp29D";
    public static final String DUMMY_DATA_CHECKSUM = "cecfd3953783df706878aaec2c22aa70";



    public static DataEnvelope createInvalidTestDataEnvelopeApiObject() {
        DataBody dataBody = new DataBody(null, null);
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA);

        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);
        return dataEnvelope;
    }

    public static DataEnvelope createTestDataEnvelopeApiObject() {
        DataBody dataBody = new DataBody(DUMMY_DATA, DUMMY_DATA_CHECKSUM);
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA);

        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);
        return dataEnvelope;
    }

    public static DataEnvelope createTestDataEnvelopeApiObjectWithEmptyName() {
        DataBody dataBody = new DataBody(DUMMY_DATA, DUMMY_DATA_CHECKSUM);
        DataHeader dataHeader = new DataHeader(TEST_NAME_EMPTY, BlockTypeEnum.BLOCKTYPEA);

        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);
        return dataEnvelope;
    }
}
