package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
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

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.springframework.util.StringUtils.hasText;

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
  @Transactional
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
  public List<DataEnvelope> getAllDataForBlockType(final BlockTypeEnum blockType) {

    if (blockType != null) {
      return dataBodyServiceImpl.getDataByBlockType(blockType)
                                .stream()
                                .map(e -> {
                                  DataBody dataBody = modelMapper.map(e, DataBody.class);
                                  DataHeader dataHeader = modelMapper.map(e.getDataHeaderEntity(), DataHeader.class);
                                  return new DataEnvelope(dataHeader, dataBody);
                                })

                                .collect(Collectors.toList());
    }
    return emptyList();
  }

  @Override
  @Transactional
  public boolean updateBlockTypeForBlockName(final String blockName, final String newBlockType) {

    Optional<BlockTypeEnum> newBlockTypeEnum = parseBlockTypeString(newBlockType);

    if (!hasText(blockName) || !newBlockTypeEnum.isPresent()) throw new IllegalArgumentException("invalid blockname and/or blocktype");

    Optional<DataBodyEntity> dataByBlockName = dataBodyServiceImpl.getDataByBlockName(blockName);

    log.info("updating blockname: {} with the new blockType: {}", blockName, newBlockType);

    return dataByBlockName.map(e -> {
      // update the blockType
      e.getDataHeaderEntity().setBlockType(newBlockTypeEnum.get());
      dataBodyServiceImpl.saveDataBody(e);
      return true;
    }).orElse(false);

  }

  private Optional<BlockTypeEnum> parseBlockTypeString(final String blockType) {
      if (hasText(blockType))
        return Optional.ofNullable(BlockTypeEnum.valueOf(blockType));

    return Optional.empty();
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
