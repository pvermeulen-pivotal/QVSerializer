package com.tmobile.qvxp.model.groovy

class ServiceStatusMessage implements Serializable {

    private final static long serialVersionUID = 1L

    String message
    boolean i18nMessage

    boolean getI18nMessage() {
        return i18nMessage
    }

    void setI18nMessage(boolean i18nMessage) {
        this.i18nMessage = i18nMessage
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ServiceStatusMessage that = (ServiceStatusMessage) o

        if (i18nMessage != that.i18nMessage) return false
        if (message != that.message) return false

        return true
    }


    int hashCode() {
        int result
        result = message ? message.hashCode() : 0
        result = 31 * result + (i18nMessage ? 1 : 0)
        return result
    }

    public ServiceStatusMessage() {
    }

    public ServiceStatusMessage(String message, boolean i18nMessage) {
        this.message = message
        this.i18nMessage = i18nMessage
    }

    public String toString() {
        return ("message: " + message + " i18n?: " + i18nMessage)
    }
}

