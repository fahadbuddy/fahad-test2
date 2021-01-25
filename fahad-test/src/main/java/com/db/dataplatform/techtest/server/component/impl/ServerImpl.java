package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.util.MD5Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

  private final DataBodyService dataBodyServiceImpl;
  private final ModelMapper modelMapper;

  /**
   * @param envelope
   *
   * @return true if there is a match with the client provided checksum.
   */
  @Override
  public boolean saveDataEnvelope(DataEnvelope envelope) {

    String toCheck = MD5Utils.createCheckSum(envelope.getDataBody()
                                                     .getDataBody())
                             .orElse("");

    boolean isValidChecksum = toCheck.equals(envelope.getDataBody()
                                                     .getChecksum());
    if (!isValidChecksum) return false;

    // Save to persistence.
    persist(envelope);

    log.info("Data persisted successfully, data name: {}", envelope.getDataHeader()
                                                                   .getName());
    return true;
  }

  @Override
  public List<DataBody> getAllDataForBlockType(final BlockTypeEnum blockType) {
    if (blockType != null){
      return dataBodyServiceImpl.getDataByBlockType(blockType).stream().map(e -> modelMapper.map(e, DataBody.class)).collect(
              Collectors.toList());
    }
    return emptyList();
  }

  private void persist(DataEnvelope envelope) {

    log.info("Persisting data with attribute name: {}", envelope.getDataHeader()
                                                                .getName());
    DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);

    DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
    dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);

    saveData(dataBodyEntity);
  }

  private void saveData(DataBodyEntity dataBodyEntity) {

    dataBodyServiceImpl.saveDataBody(dataBodyEntity);
  }

}
