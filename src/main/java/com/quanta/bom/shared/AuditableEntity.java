package com.quanta.bom.shared;

import java.time.LocalDateTime;

public abstract class AuditableEntity {

    public LocalDateTime createdAt;
    public Integer createdBy;
    public LocalDateTime lastUpdate;
    public Integer lastModifiedBy;
    public String recordState = RecordState.ACTIVE.getValue();
}
