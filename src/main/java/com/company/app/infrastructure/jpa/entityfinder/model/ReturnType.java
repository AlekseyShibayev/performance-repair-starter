package com.company.app.infrastructure.jpa.entityfinder.model;

public enum ReturnType {

    SLICE, LIST;

    public boolean match(ReturnType returnType) {
        return this.equals(returnType);
    }

}
