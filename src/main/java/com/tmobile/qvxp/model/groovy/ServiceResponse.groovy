package com.tmobile.qvxp.model.groovy;

class ServiceResponse {
    ServiceResponseStatus serviceStatus
    def response

    boolean hasErrors() {
        if (serviceStatus != null && !serviceStatus.hasErrors()) {
            return false;
        }

        return true;
    }

    boolean success() {
        (serviceStatus && serviceStatus.wasSuccessful())
    }
}
