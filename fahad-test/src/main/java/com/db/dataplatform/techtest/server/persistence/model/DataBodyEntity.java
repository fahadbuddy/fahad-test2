package com.db.dataplatform.techtest.server.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "DATA_STORE")
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DataBodyEntity {

    @Id
    @SequenceGenerator(name = "dataStoreSequenceGenerator", sequenceName = "SEQ_DATA_STORE", allocationSize = 1)
    @GeneratedValue(generator = "dataStoreSequenceGenerator")
    @Column(name = "DATA_STORE_ID")
    private Long dataStoreId;

    @EqualsAndHashCode.Include
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "DATA_HEADER_ID")
    private DataHeaderEntity dataHeaderEntity;

    @EqualsAndHashCode.Include
    @Column(name = "DATA_BODY")
    private String dataBody;


    @Column(name = "CREATED_TIMESTAMP")
    private Instant createdTimestamp;

    @PrePersist
    public void setTimestamps() {
        if (createdTimestamp == null) {
            createdTimestamp = Instant.now();
        }
    }
}
