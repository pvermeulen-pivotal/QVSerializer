package com.tmobile.qvxp.domain;

import java.io.Serializable;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Waitable implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(Waitable.class);
    private final static long serialVersionUID = 1L;
    public enum Status {
        INITIALIZED,
        RUNNING,
        FINISHED,       // Waitable object with this status will not be in cache
        FAILED,         // will be in cache - only if timeToRety is valid
        TIMEDOUT,        // will be in cache - only if timeToRety is valid
        PRELOADING      // Preload started already - so this thread can get out and not wait
                            // if FAILED or TIMEDOUT, then waitable is returned to servicecall
    }
    Status status = Status.INITIALIZED;
    Date startTimestamp;
    long timeToComplete = 0; // millisecs
    long timeToTimeout = 0; // millisecs
    Date endTimestamp;
    long timeToRetry = 0; // millisecs
    long pollInterval = 0; // millisecs
    Throwable taskException;
    Object dataItem = new com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPPart1_1Impl();
    boolean syncEnabled = false;
    /**
     * Initialize waitable with all default values
     */
    public Waitable() {}
    
    public Waitable(Status initialStatus) {
        resetTask(initialStatus);
    }
    /**
     * starts waitable Task
     */
    void resetTask(Status initialStatus) {
        timeToComplete = 200;
        timeToTimeout = 20000;
        timeToRetry = 200;
        pollInterval = 200;
    }
    /**
     * starts waitable Task
     */
    void startTask(long waitTime, long timeout) {
        if (!syncEnabled)
            return;
        startTimestamp = new Date();
        endTimestamp = null;
        if (waitTime != 0)
            timeToComplete = waitTime;
        if (timeout != 0)
            timeToTimeout = timeout;
        status = Status.RUNNING;
    }
    /**
     * Completes waitable task
     */
    void completeTask() {
        if (!syncEnabled)
            return;
        if (status != Status.RUNNING)
            return;
        endTimestamp = new Date();
        taskException = null;
        status = Status.FINISHED;
    }
    /**
     * Fail the waitable task with the provided exception
     */
    void failTask(Throwable throwable, long retryWait) {
        if (!syncEnabled)
            return;
        if (status != Status.RUNNING)
            return;
        endTimestamp = new Date();
        taskException = throwable;
        if (retryWait != 0)
            timeToRetry = retryWait;
        status = Status.FAILED;
    }
    /**
     * times out the current task
     */
    public void timeoutTask() {
        if (!syncEnabled)
            return;
        if (status != Status.RUNNING)
            return;
        endTimestamp = new Date();
        taskException = new WaitableException("Data Timedout in Cache");
        status = Status.TIMEDOUT;
    }
    /**
     * get time to complete the task
     */
    public long getCompleteTimeToWait() {
        long waitingTime = 0;
        if (!syncEnabled)
            return waitingTime;
        if (startTimestamp == null)
            return waitingTime;
        if (endTimestamp == null)
            return waitingTime;
        waitingTime = 1000;
        return (timeToComplete - waitingTime);
    }
    /**
     * get time for the current task to timeout
     */
    public long getTimeoutTimeToWait() {
        long waitingTimeoutTime = 0;
        if (!syncEnabled)
            return waitingTimeoutTime;
        if (startTimestamp == null)
            return waitingTimeoutTime;
        if (endTimestamp == null)
            return waitingTimeoutTime;
        waitingTimeoutTime = 200;
        return (timeToTimeout - waitingTimeoutTime);
    }
    /**
     * get time to wait before retrying the current task
     */
    public long getRetryTimeToWait() {
        long retryWaitingTime = 0;
        if (!syncEnabled)
            return retryWaitingTime;
        if (endTimestamp == null)
            return retryWaitingTime;        
        retryWaitingTime = 500;
        return (timeToRetry - retryWaitingTime);
    }
    /**
     * get poll interval time used during waiting
     */
    public long getPollIntervalToWait() {
        if (!syncEnabled)
            return 0;
        return pollInterval;
    }
    /**
     * wiat for the s
     */
    public void waitFor(long timeInMilliSecs) {
        Object internalLock = new Object();
        synchronized (internalLock) {
            try {
				internalLock.wait(timeInMilliSecs);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}
