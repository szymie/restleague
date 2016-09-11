package org.tiwpr.szymie.resources;

import org.tiwpr.szymie.models.Error;

import java.util.Optional;

public class SaveResult {

    private Error error;
    private Integer entityId;

    public SaveResult(Error error) {
        this.error = error;
    }

    public SaveResult(Integer entityId) {
        this.entityId = entityId;
    }

    public Optional<Error> getError() {
        return Optional.ofNullable(error);
    }

    public Optional<Integer> getEntityId() {
        return Optional.ofNullable(entityId);
    }
}
