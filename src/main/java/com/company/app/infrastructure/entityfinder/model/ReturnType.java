package com.company.app.infrastructure.entityfinder.model;

public enum ReturnType {

    SLICE, LIST;

    public boolean match(ReturnType returnType) {
        return this.equals(returnType);
    }

}
