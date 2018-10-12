package com.tmobile.qvxp.internal.pdx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
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
import org.w3c.dom.Document;

public class QVReflectionBasedAutoSerializer extends ReflectionBasedAutoSerializer
		implements PdxSerializer, Declarable {
	private final Logger log = LoggerFactory.getLogger(QVReflectionBasedAutoSerializer.class);

	public QVReflectionBasedAutoSerializer() {
		super(false, ".*,");
	}

	@Override
	public FieldType getFieldType(Field f, Class<?> clazz) {
		if (f.getType().equals(XMLGregorianCalendar.class)) {
			return FieldType.STRING;
		} else if (f.getType().equals(StackTraceElement.class)) {
			return FieldType.STRING;
		} else if (f.getType().equals(MimeHeaders.class)) {
			return FieldType.OBJECT;
		} else if (f.getType().equals(com.sun.xml.internal.messaging.saaj.soap.SOAPPartImpl.class)) {
			return FieldType.STRING_ARRAY;
		} else if (f.getType().equals(java.lang.Throwable.class)) {
			return FieldType.STRING;
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
		} else if (f.getType().equals(com.sun.xml.internal.messaging.saaj.soap.SOAPPartImpl.class)) {
			return true;
		} else if (f.getType().equals(java.lang.Throwable.class)) {
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
				stackTraceElement = bi.toString();
				if (log.isDebugEnabled()) {
					log.debug("QVReflectionBasedSerializer write transformation for StackTraceElement "
							+ stackTraceElement);
				}
			}
			return stackTraceElement;
		} else if (f.getType().equals(XMLGregorianCalendar.class)) {
			String calendar = null;
			if (originalValue != null) {
				XMLGregorianCalendar bd = (XMLGregorianCalendar) originalValue;
				calendar = bd.toString();
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
			if (log.isDebugEnabled()) {
				log.debug("QVReflectionBasedSerializer write transformation for MimeHeaders " + headers);
			}
			return headers;
		} else if (f.getType().equals(com.sun.xml.internal.messaging.saaj.soap.SOAPPartImpl.class)) {
			String[] object = null;
			if (originalValue != null) {
				object = new String[2];
				com.sun.xml.internal.messaging.saaj.soap.SOAPPartImpl sdi = (com.sun.xml.internal.messaging.saaj.soap.SOAPPartImpl) originalValue;
				try {
					SOAPBody body = sdi.getEnvelope().getBody();
					if (body != null) {
						Document doc = body.extractContentAsDocument();
						StringWriter sw = new StringWriter();
						TransformerFactory tf = TransformerFactory.newInstance();
						Transformer transformer = tf.newTransformer();
						transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
						transformer.setOutputProperty(OutputKeys.METHOD, "xml");
						transformer.setOutputProperty(OutputKeys.INDENT, "yes");
						transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
						transformer.transform(new DOMSource(doc), new StreamResult(sw));
						object[1] = sw.toString();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return object;
		} else if (f.getType().equals(java.lang.Throwable.class)) {
			String throwable = null;
			if (originalValue != null) {
				Throwable t = (Throwable) originalValue;
				throwable = t.toString();
			}
			return throwable;
		} else {
			return super.writeTransform(f, clazz, originalValue);
		}
	}

	@Override
	public Object readTransform(Field f, Class<?> clazz, Object serializedValue) {
		if (f.getType().equals(StackTraceElement.class)) {
			StackTraceElement stackTraceElement = null;
			if (serializedValue != null) {
				String str = (String) serializedValue;
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
				String str = (String) serializedValue;
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
		} else if (f.getType().equals(com.sun.xml.internal.messaging.saaj.soap.SOAPPartImpl.class)) {
			SOAPMessage soapMsg = null;
			MimeHeaders mHdrs = null;
			if (serializedValue != null) {
				String[] values = (String[]) serializedValue;
				if (values[1] != null) {
					try {
						soapMsg = getSoapMessageFromString(null, values[1]);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			return soapMsg;
		} else if (f.getType().equals(java.lang.Throwable.class)) {
			Throwable throwable = null;
			if (serializedValue != null) {
				String str = (String) serializedValue;
				throwable = new Throwable(str);
			}
			return throwable;
		} else {
			return super.readTransform(f, clazz, serializedValue);
		}
	}

	private SOAPMessage getSoapMessageFromString(MimeHeaders hdrs, String xml) throws SOAPException, IOException {
		MessageFactory factory = MessageFactory.newInstance();
		SOAPMessage message = factory.createMessage(hdrs,
				new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))));
		return message;
	}
}