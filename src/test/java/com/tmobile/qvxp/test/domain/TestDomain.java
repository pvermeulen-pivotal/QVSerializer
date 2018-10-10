package com.tmobile.qvxp.test.domain;

import java.io.Serializable;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import com.tmobile.qvxp.domain.OrderReferenceData;
import com.tmobile.qvxp.domain.Waitable;

public class TestDomain implements Serializable {
	private static final long serialVersionUID = -4662762081845493022L;
	private String sessionId;
	private int sessionInt;
	private long sessionLong;
	private Date sessionDate;
	private XMLGregorianCalendar sessionCal;
	private StackTraceElement sessionSte;
	private Waitable waitable;
	private OrderReferenceData orderReferenceData;

	public TestDomain() {
	}

	public TestDomain(String sessionId, int sessionInt, long sessionLong, Date sessionDate,
			XMLGregorianCalendar sessionCal, StackTraceElement sessionSte, Waitable waitable,
			OrderReferenceData orderReferenceData) {
		super();
		this.sessionId = sessionId;
		this.sessionInt = sessionInt;
		this.sessionLong = sessionLong;
		this.sessionDate = sessionDate;
		this.sessionCal = sessionCal;
		this.sessionSte = sessionSte;
		this.waitable = waitable;
		this.orderReferenceData = orderReferenceData;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getSessionInt() {
		return sessionInt;
	}

	public void setSessionInt(int sessionInt) {
		this.sessionInt = sessionInt;
	}

	public long getSessionLong() {
		return sessionLong;
	}

	public void setSessionLong(long sessionLong) {
		this.sessionLong = sessionLong;
	}

	public Date getSessionDate() {
		return sessionDate;
	}

	public void setSessionDate(Date sessionDate) {
		this.sessionDate = sessionDate;
	}

	public XMLGregorianCalendar getSessionCal() {
		return sessionCal;
	}

	public void setSessionCal(XMLGregorianCalendar sessionCal) {
		this.sessionCal = sessionCal;
	}

	public StackTraceElement getSessionSte() {
		return sessionSte;
	}

	public void setSessionSte(StackTraceElement sessionSte) {
		this.sessionSte = sessionSte;
	}

	public Waitable getWaitable() {
		return waitable;
	}

	public void setWaitable(Waitable waitable) {
		this.waitable = waitable;
	}

	public OrderReferenceData getOrderReferenceData() {
		return orderReferenceData;
	}

	public void setOrderReferenceData(OrderReferenceData orderReferenceData) {
		this.orderReferenceData = orderReferenceData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orderReferenceData == null) ? 0 : orderReferenceData.hashCode());
		result = prime * result + ((sessionCal == null) ? 0 : sessionCal.hashCode());
		result = prime * result + ((sessionDate == null) ? 0 : sessionDate.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + sessionInt;
		result = prime * result + (int) (sessionLong ^ (sessionLong >>> 32));
		result = prime * result + ((sessionSte == null) ? 0 : sessionSte.hashCode());
		result = prime * result + ((waitable == null) ? 0 : waitable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestDomain other = (TestDomain) obj;
		if (orderReferenceData == null) {
			if (other.orderReferenceData != null)
				return false;
		} else if (!orderReferenceData.equals(other.orderReferenceData))
			return false;
		if (sessionCal == null) {
			if (other.sessionCal != null)
				return false;
		} else if (!sessionCal.equals(other.sessionCal))
			return false;
		if (sessionDate == null) {
			if (other.sessionDate != null)
				return false;
		} else if (!sessionDate.equals(other.sessionDate))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (sessionInt != other.sessionInt)
			return false;
		if (sessionLong != other.sessionLong)
			return false;
		if (sessionSte == null) {
			if (other.sessionSte != null)
				return false;
		} else if (!sessionSte.equals(other.sessionSte))
			return false;
		if (waitable == null) {
			if (other.waitable != null)
				return false;
		} else if (!waitable.equals(other.waitable))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TestDomain [sessionId=" + sessionId + ", sessionInt=" + sessionInt + ", sessionLong=" + sessionLong
				+ ", sessionDate=" + sessionDate + ", sessionCal=" + sessionCal + ", sessionSte=" + sessionSte
				+ ", waitable=" + waitable + ", orderReferenceData=" + orderReferenceData + "]";
	}

}
