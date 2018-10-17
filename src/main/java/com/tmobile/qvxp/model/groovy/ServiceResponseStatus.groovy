package com.tmobile.qvxp.model.groovy;

import groovy.transform.CompileStatic

@CompileStatic
class ServiceResponseStatus {
    public static final String SUCCESS_STATUSCODE = "100";

    boolean succeeded = false

    LinkedHashMap<String, ServiceStatusMessage> serviceStatusCodeMessageMap = new LinkedHashMap<String, ServiceStatusMessage>()

    void addMessage(String code, String message, boolean i18message = false) {
        if (!serviceStatusCodeMessageMap.containsKey(code)) {
            serviceStatusCodeMessageMap[code] = new ServiceStatusMessage(message, i18message)
        }
    }

    void setPrimaryMessage(String code, String message, boolean i18message = false) {
        if (serviceStatusCodeMessageMap.size() == 0) {
            addMessage(code, message, i18message)
            return
        }

        LinkedHashMap<String, ServiceStatusMessage> newMap = new LinkedHashMap<String, ServiceStatusMessage>()
        newMap[code] = new ServiceStatusMessage(message, i18message)

        serviceStatusCodeMessageMap.each { String key, ServiceStatusMessage value ->
            if (code != key) {
                newMap.put(key, value)
            }
        }

        serviceStatusCodeMessageMap = newMap
    }

    boolean hasCode(String code) {
        boolean result = false;
        serviceStatusCodeMessageMap.each { String k, ServiceStatusMessage v ->
            if (!result) {
                result = (k == code)
            }
        }
        return result
    }

    boolean hasErrors() {
        return (!succeeded)
    }

    String getPrimaryStatusCode() {
        return serviceStatusCodeMessageMap?.take(1)?.keySet()?.first()
    }

    ServiceStatusMessage getPrimaryStatusMessage() {
        String primaryCode = getPrimaryStatusCode()
        return getMessageForCode(primaryCode)
    }

    ServiceStatusMessage getMessageForCode(String code) {
        return serviceStatusCodeMessageMap[code]
    }

    boolean wasSuccessful() {
        (succeeded && hasCode(SUCCESS_STATUSCODE))
    }
}
