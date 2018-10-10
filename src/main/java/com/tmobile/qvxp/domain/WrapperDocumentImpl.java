package com.tmobile.qvxp.domain;

import java.io.Serializable;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;

public class WrapperDocumentImpl implements Serializable {

	private static final long serialVersionUID = 1383903778000008292L;
	
	private com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl documentImpl;

	public WrapperDocumentImpl() {
		super();
	}

	public WrapperDocumentImpl(SOAPDocumentImpl documentImpl) {
		super();
		this.documentImpl = documentImpl;
	}

	public com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl getDocumentImpl() {
		return documentImpl;
	}

	public void setDocumentImpl(com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl documentImpl) {
		this.documentImpl = documentImpl;
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((documentImpl == null) ? 0 : documentImpl.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		WrapperDocumentImpl other = (WrapperDocumentImpl) obj;
//		if (documentImpl == null) {
//			if (other.documentImpl != null)
//				return false;
//		} else if (!documentImpl.equals(other.documentImpl))
//			return false;
//		return true;
//	}
//		
}
