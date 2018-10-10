package com.tmobile.qvxp.internal.pdx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.geode.cache.Declarable;
import org.apache.geode.pdx.FieldType;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPPart1_1Impl;

public class QVReflectionBasedAutoSerializer extends ReflectionBasedAutoSerializer
		implements PdxSerializer, Declarable {
	private final Logger log = LoggerFactory.getLogger(QVReflectionBasedAutoSerializer.class);

	public QVReflectionBasedAutoSerializer() {
		super(false, ".*,");

	}

	@Override
	public FieldType getFieldType(Field f, Class<?> clazz) {
		Throwable a;
		if (f.getType().equals(XMLGregorianCalendar.class)) {
			return FieldType.BYTE_ARRAY;
		} else if (f.getType().equals(StackTraceElement.class)) {
			return FieldType.BYTE_ARRAY;
		} else if (f.getType().equals(MimeHeaders.class)) {
			return FieldType.OBJECT;
		} else if (f.getType().equals(SOAPPart1_1Impl.class)) {
			return FieldType.OBJECT;
		} else if (f.getType().equals(com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl.class)) {
			return FieldType.BYTE_ARRAY;
		} else if (f.getType().equals(javax.xml.transform.Source.class)
				|| f.getType().equals(javax.xml.transform.dom.DOMSource.class)) {
			return FieldType.OBJECT;
		} else if (f.getType().equals(com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPPart1_1Impl.class)) {
			return FieldType.BYTE_ARRAY;
		} else {
			return super.getFieldType(f, clazz);
		}
	}

	@Override
	public boolean transformFieldValue(Field f, Class<?> clazz) {
		if (f.getType().equals(XMLGregorianCalendar.class)) {
			return true;
		} else if (f.getType().equals(StackTraceElement.class)) {
			return true;
		} else if (f.getType().equals(MimeHeaders.class)) {
			return true;
		} else if (f.getType().equals(com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl.class)) {
			return true;
		} else if (f.getType().equals(com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPPart1_1Impl.class)) {
			return true;
		} else if (f.getType().equals(Source.class) || f.getType().equals(DOMSource.class)) {
			return true;
		} else {
			return super.transformFieldValue(f, clazz);
		}
	}

	@SuppressWarnings("restriction")
	@Override
	public Object writeTransform(Field f, Class<?> clazz, Object originalValue) {
		if (f.getType().equals(StackTraceElement.class)) {
			Object stackTraceElement = null;
			if (originalValue != null) {
				StackTraceElement bi = (StackTraceElement) originalValue;
				stackTraceElement = bi.toString().getBytes();
				if (log.isDebugEnabled()) {
					log.debug("QVReflectionBasedSerializer write transformation for StackTraceElement "
							+ stackTraceElement);
				}
			}
			return stackTraceElement;
		} else if (f.getType().equals(XMLGregorianCalendar.class)) {
			Object calendar = null;
			if (originalValue != null) {
				XMLGregorianCalendar bd = (XMLGregorianCalendar) originalValue;
				calendar = bd.toString().getBytes();
				if (log.isDebugEnabled()) {
					log.debug("QVReflectionBasedSerializer write transformation for XMLGregorianCalendar " + calendar);
				}
			}
			return calendar;
		} else if (f.getType().equals(MimeHeaders.class)) {
			Map<String, String> headers = null;
			if (originalValue != null) {
				MimeHeaders hdrs = (MimeHeaders) originalValue;
				headers = new HashMap<>();
				Iterator<MimeHeader> it = hdrs.getAllHeaders();
				while (it.hasNext()) {
					MimeHeader hdr = (MimeHeader) it.next();
					headers.put(hdr.getName(), hdr.getValue());
				}
			}
			return headers;
		} else if (f.getType().equals(com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPPart1_1Impl.class)) {
			com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPPart1_1Impl impl = (SOAPPart1_1Impl) originalValue;
			byte[] objectBytes = null;
			if (originalValue != null) {
				objectBytes = objectToByteArray(originalValue);
			}
			return objectBytes;
		} else if (f.getType().equals(com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl.class)) {
			byte[] objectBytes = null;
			if (originalValue != null) {
				com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl sdi = (com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl) originalValue;
				NodeList nodeList = sdi.getChildNodes();
				if (nodeList != null && nodeList.getLength() > 0) {
					NamedNodeMap namedNodeMap = sdi.getAttributes();
					if (namedNodeMap != null && namedNodeMap.getLength() > 0) {
						objectBytes = objectToByteArray(originalValue);
					}
				}
			}
			return objectBytes;
		} else if (f.getType().equals(Source.class) || f.getType().equals(DOMSource.class)) {
			Map<String, String> domSourceMap = null;
			if (originalValue != null) {
				domSourceMap = new HashMap<>();
				if (originalValue instanceof DOMSource) {
					DOMSource domSource = (DOMSource) originalValue;
					domSourceMap.put("node", NodeToString(domSource.getNode()));
					domSourceMap.put("id", domSource.getSystemId());
				} else {
					Source source = (Source) originalValue;
					domSourceMap.put("id", source.getSystemId());
					domSourceMap.put("node", null);
				}
			}
			if (domSourceMap == null || domSourceMap.isEmpty())
				return null;
			return domSourceMap;
		} else {
			return super.writeTransform(f, clazz, originalValue);
		}
	}

	private String NodeToString(Node node) {
		if (node == null)
			return null;
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		} catch (TransformerException te) {
			te.printStackTrace();
		}
		return sw.toString();
	}

	private Node stringToNode(String strNode) throws Exception {
		if (strNode == null)
			return null;

		return DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new ByteArrayInputStream(strNode.getBytes())).getDocumentElement();
	}

	private Object byteArrayToObject(byte[] byteArray) {
		if (byteArray == null)
			return null;
		ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
		ObjectInput in = null;
		Object obj = null;
		try {
			in = new ObjectInputStream(bis);
			obj = in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
		}
		return obj;
	}

	private byte[] objectToByteArray(Object obj) {
		if (obj == null)
			return null;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] objBytes = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(obj);
			out.flush();
			objBytes = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return objBytes;
	}

	@Override
	public Object readTransform(Field f, Class<?> clazz, Object serializedValue) {
		if (f.getType().equals(StackTraceElement.class)) {
			StackTraceElement stackTraceElement = null;
			if (serializedValue != null) {
				String str = new String((byte[]) serializedValue);
				String declaringClass = str.substring(0, str.indexOf("("));
				String method = declaringClass.substring(declaringClass.lastIndexOf(".") + 1, declaringClass.length());
				declaringClass = declaringClass.substring(0, declaringClass.lastIndexOf("."));
				String file = str.substring(str.indexOf("(") + 1, str.indexOf(":"));
				String line = str.substring(str.indexOf(":") + 1, str.length() - 1);
				try {
					stackTraceElement = new StackTraceElement(declaringClass, method, file, Integer.parseInt(line));
					if (log.isDebugEnabled()) {
						log.debug("QVReflectionBasedSerializer read transformation for StackTraceElement "
								+ stackTraceElement);
					}
				} catch (Exception ex) {
					log.error("QVReflectionBasedSerializer read transformation for StackTraceElement exception: "
							+ ex.getMessage());
				}
			}
			return stackTraceElement;
		} else if (f.getType().equals(XMLGregorianCalendar.class)) {
			XMLGregorianCalendar calendar = null;
			if (serializedValue != null) {
				String str = new String((byte[]) serializedValue);
				try {
					calendar = DatatypeFactoryImpl.newInstance().newXMLGregorianCalendar(str);
					if (log.isDebugEnabled()) {
						log.debug(
								"QVReflectionBasedSerializer read transformation for XMLGregorianCalendar " + calendar);
					}
				} catch (DatatypeConfigurationException ex) {
					log.error("QVReflectionBasedSerializer read transformation for XMLGregorianCalendar exception: "
							+ ex.getMessage());
				}
			}
			return calendar;
		} else if (f.getType().equals(MimeHeaders.class)) {
			MimeHeaders mimeHeaders;
			if (serializedValue != null) {
				mimeHeaders = new MimeHeaders();
				Map<String, String> map = (Map<String, String>) serializedValue;
				map.entrySet().stream().forEach(key -> mimeHeaders.addHeader(key.getKey(), key.getValue()));
				return mimeHeaders;
			} else {
				return null;
			}
		} else if (f.getType().equals(com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl.class)) {
			com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl soapDocumentImpl = null;
			if (serializedValue != null) {
				soapDocumentImpl = (com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl) byteArrayToObject(
						(byte[]) serializedValue);
			}
			return soapDocumentImpl;
		} else if (f.getType().equals(DOMSource.class) || f.getType().equals(Source.class)) {
			DOMSource domSource = null;
			if (serializedValue != null) {
				domSource = new DOMSource();
				Map<String, String> map = (Map<String, String>) serializedValue;
				try {
					Node node = stringToNode(map.get("node"));
					String id = map.get("id");
					if (node != null) {
						domSource.setNode(node);
					}
					if (id != null && id.length() > 0) {
						domSource.setSystemId(id);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return domSource;
		} else {
			return super.readTransform(f, clazz, serializedValue);
		}
	}

};