package com.tmobile.qvxp.domain;

import java.io.Serializable;

import javax.xml.transform.dom.DOMSource;

public class WrapperDOMSource implements Serializable {

	private static final long serialVersionUID = 2375258054061729328L;
	
	private DOMSource domSource;
	
	public WrapperDOMSource() {}
	
	public WrapperDOMSource(DOMSource domSource) {
		this.domSource = domSource;
	}

	public DOMSource getDomSource() {
		return domSource;
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((domSource == null) ? 0 : domSource.hashCode());
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
//		WrapperDOMSource other = (WrapperDOMSource) obj;
//		if (domSource == null) {
//			if (other.domSource != null)
//				return false;
//		} else if (!domSource.equals(other.domSource))
//			return false;
//		return true;
//	}
	
}
