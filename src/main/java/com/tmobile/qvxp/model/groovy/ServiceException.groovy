package com.tmobile.qvxp.model.groovy
/**
 * Service Exception for capturing Enterprise Services Functional and Business Errors on invocation.
 */
class ServiceException extends Exception {
    /**
     * This will hold the list of service codes from the backend such as RSP, TIBCO etc.
     */
    Map<String, ServiceStatusMessage> serviceStatusCodeMessageMap
    ServiceResponse serviceResponse

    ServiceException() {
        super()
    }

    /**
     * Constructor with error message from backend service.
     */
    ServiceException(String message) {
        super(message)
    }

    ServiceException(String message, Throwable cause) {
        super(message, cause)
    }

    ServiceException(Throwable cause) {
        super(cause)
    }

    ServiceException(String message, Map<String, ServiceStatusMessage> serviceStatusCodeMessageMap, Throwable rootCause) {
        super(message, rootCause)

        this.serviceStatusCodeMessageMap = serviceStatusCodeMessageMap
    }

    ServiceException(String message, Map<String, ServiceStatusMessage> serviceStatusCodeMessageMap, Throwable rootCause, ServiceResponse serviceResponse) {
        super(message, rootCause)

        this.serviceStatusCodeMessageMap = serviceStatusCodeMessageMap
        this.serviceResponse = serviceResponse
    }

    boolean hasCode(String code) {
        return serviceStatusCodeMessageMap?.containsKey(code)
    }

    Set<String> getUniqueInternationalizationMessages() {
        return ((Set<String>) serviceStatusCodeMessageMap?.collect { String k, ServiceStatusMessage v -> v.i18nMessage ? [v.message] : [] }.flatten())
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
}