package com.tmobile.qvxp.internal.pdx;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPPartImpl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPPart1_1Impl;
import org.apache.geode.cache.Declarable;
import org.apache.geode.pdx.FieldType;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class QVReflectionBasedAutoSerializer extends ReflectionBasedAutoSerializer
        implements PdxSerializer, Declarable {
  private final Logger log = LoggerFactory.getLogger(QVReflectionBasedAutoSerializer.class);

  public QVReflectionBasedAutoSerializer() {
    super(false, ".*");
  }

  private boolean isSpecialField(Field f) {
    return isXMLGregorianCalendar(f) || isStackTraceElement(f) || isMimeHeaders(f) || isSoapFault(f) || isSoap1Part1(f) || isSoapPart(f) || isThrowable(f) || isSoapDocument(f);
  }

  @Override
  public FieldType getFieldType(Field f, Class<?> clazz) {
    if (isXMLGregorianCalendar(f)) {
      return FieldType.STRING;
    }
    if (isStackTraceElement(f)) {
      return FieldType.STRING;
    }
    if (isMimeHeaders(f)) {
      return FieldType.OBJECT;
    }
    if (isSoapDocument(f)) {
      return FieldType.STRING_ARRAY;
    }
    if (isSoap1Part1(f)) {
      return FieldType.STRING_ARRAY;
    }
    if (isSoapPart(f)) {
      return FieldType.STRING_ARRAY;
    }
    if (isSoapFault(f)) {
      return FieldType.STRING;
    }
    return super.getFieldType(f, clazz);
  }

  @Override
  public boolean transformFieldValue(Field f, Class<?> clazz) {
    return isSpecialField(f) || super.transformFieldValue(f, clazz);
  }

  @SuppressWarnings("restriction")
  @Override
  public Object writeTransform(Field f, Class<?> clazz, Object originalValue) {
    if (originalValue == null) {
      return null;
    }
    if (isSoapDocument(f)) {
      return writeSoapDocument(originalValue);
    }
    if (isStackTraceElement(f)) {
      return writeStackTraceElement(originalValue);
    }
    if (isXMLGregorianCalendar(f)) {
      return writeXMLGregorianCalendar(originalValue);
    }
    if (isMimeHeaders(f)) {
      return writeMimeHeaders(originalValue);
    }
    if (isSoap1Part1(f)) {
      return writeSoap1Part1(originalValue);
    }
    if (isSoapPart(f)) {
      return writeSoapPart(originalValue);
    }
    if (isThrowable(f)) {
      if (originalValue.getClass().equals(SOAPFaultException.class)) {
        return writeSoapFault(originalValue);
      }
      Throwable t = (Throwable) originalValue;
      if (t.getCause() == null) {
        t.initCause(new Throwable());
      }
      return super.writeTransform(f, clazz, t);
    }
    if (log.isDebugEnabled()) {
      log.debug("QVReflectionBasedSerializer writeTransform : using ReflectionBasedAutoSerializer" +
              " " + f.getType());
    }
    return super.writeTransform(f, clazz, originalValue);
  }

  @Override
  public Object readTransform(Field f, Class<?> clazz, Object serializedValue) {
    if (serializedValue == null) {
      return null;
    }
    if (isStackTraceElement(f)) {
      return readStackTraceElement(serializedValue);
    }
    if (isXMLGregorianCalendar(f)) {
      return readXMLGregorianCalendar(serializedValue);
    }
    if (isMimeHeaders(f)) {
      return readMimeHeaders(serializedValue);
    }
    if (isThrowable(f)) {
      if (isSoapFault(f) || serializedValue.toString().contains("<S" +
              ":Fault")) {
        return readSoapFault(serializedValue);
      }
    } else {
      return super.readTransform(f, clazz, serializedValue);
    }
    if (isSoapPart(f)) {
      return readSoapPart(serializedValue);
    }
    if (log.isDebugEnabled()) {
      log.debug("QVReflectionBasedSerializer readTransform : using ReflectionBasedAutoSerializer " + f.getType());
    }
    return super.readTransform(f, clazz, serializedValue);
  }

  private Object readSoapPart(Object serializedValue) {
    SOAPMessage soapMsg = null;
    MimeHeaders mHdrs = null;
    if (serializedValue != null) {
      String[] values = (String[]) serializedValue;
      if (values[1] != null) {
        try {
          soapMsg = getSoapMessageFromString(null, values[1]);
        } catch (Exception ex) {
          if (log.isDebugEnabled()) {
            log.debug("QVReflectionBasedSerializer read transformation for SoapPart exception" + ex);
          }
          ex.printStackTrace();
        }
      }
    }
    return soapMsg;
  }

  private Object readSoapFault(Object serializedValue) {
    SOAPMessage soapMsg;
    SOAPFault soapFault = null;
    SOAPFaultException soapFaultException = null;
    if (serializedValue != null) {
      String value = (String) serializedValue;
      try {
        soapMsg = getSoapMessageFromString(null, value);
        soapFault = soapMsg.getSOAPPart().getEnvelope().getBody().getFault();
        soapFaultException = new SOAPFaultException(soapFault);
      } catch (Exception ex) {
        if (log.isDebugEnabled()) {
          log.debug("QVReflectionBasedSerializer read transformation for SoapFault exception" + ex);
        }
        ex.printStackTrace();
      }
    }
    return soapFaultException;
  }

  private Object readMimeHeaders(Object serializedValue) {
    MimeHeaders mimeHeaders;
    if (serializedValue != null) {
      mimeHeaders = new MimeHeaders();
      Map<String, String> map = (Map<String, String>) serializedValue;
      map.entrySet().stream().forEach(key -> mimeHeaders.addHeader(key.getKey(), key.getValue()));
      return mimeHeaders;
    } else {
      return null;
    }
  }

  private Object readXMLGregorianCalendar(Object serializedValue) {
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
  }

  private Object readStackTraceElement(Object serializedValue) {
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
  }

  private boolean isStackTraceElement(Field f) {
    return f.getType().equals(StackTraceElement.class);
  }

  private boolean isSoapFault(Field f) {
    return f.getType().equals(SOAPFaultException.class);
  }

  private boolean isSoapDocument(Field f) {
    return f.getType().equals(SOAPDocumentImpl.class);
  }

  private boolean isThrowable(Field f) {
    return f.getType().equals(Throwable.class);
  }

  private boolean isSoapPart(Field f) {
    return f.getType().equals(com.sun.xml.internal.messaging.saaj.soap.SOAPPartImpl.class);
  }

  private boolean isSoap1Part1(Field f) {
    return f.getType().equals(SOAPPart1_1Impl.class);
  }

  private boolean isMimeHeaders(Field f) {
    return f.getType().equals(MimeHeaders.class);
  }

  private boolean isXMLGregorianCalendar(Field f) {
    return f.getType().equals(XMLGregorianCalendar.class);
  }

  private Object writeSoapPart(Object originalValue) {
    String[] object = null;
    if (originalValue != null) {
      object = new String[2];
      SOAPPartImpl soapPart =
              (SOAPPartImpl) originalValue;
      handleSOAPPartTransform(object, soapPart);
    }
    return object;
  }

  private Object writeSoap1Part1(Object originalValue) {
    String[] object = null;
    if (originalValue != null) {
      object = new String[2];
      SOAPPart1_1Impl sdi =
              (SOAPPart1_1Impl) originalValue;
      handleSOAPPartTransform(object, sdi);
    }
    return object;
  }

  /*Does not seem to be able to  get envelope for SOAPDocument*/
  private Object writeSoapDocument(Object originalValue) {
    String[] object = null;
    if (originalValue != null) {
      SOAPDocumentImpl soapDocument = (SOAPDocumentImpl) originalValue;
      try {
        SOAPPartImpl soapPart = soapDocument.getSOAPPart();
        if (soapPart != null) {
          SOAPBody body = soapPart.getEnvelope().getBody();
          if (body != null) {
            Document doc = body.extractContentAsDocument();
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(sw));
            System.out.println(sw.toString());
            object[1] = sw.toString();
          }
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return object;
  }

  private Object writeSoapFault(Object originalValue) {
    System.out.println("USING SOAPFAULT FOR SERIALIZATION");
    String document = null;
    if (originalValue != null) {
      SOAPFault soapFault = ((SOAPFaultException) originalValue).getFault();
      try {
        Document doc = soapFault.getOwnerDocument();
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(new DOMSource(doc),
                new StreamResult(sw));
        document = sw.toString();
        System.out.println(document);
      } catch (Exception e) {
        if (log.isDebugEnabled()) {
          log.debug("QVReflectionBasedSerializer read transformation for SoapFault exception" + e);
        }
        e.printStackTrace();
      }
    }
    return document;
  }

  private Object writeMimeHeaders(Object originalValue) {
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
  }

  private Object writeXMLGregorianCalendar(Object originalValue) {
    String calendar = null;
    if (originalValue != null) {
      XMLGregorianCalendar bd = (XMLGregorianCalendar) originalValue;
      calendar = bd.toString();
      if (log.isDebugEnabled()) {
        log.debug("QVReflectionBasedSerializer write transformation for XMLGregorianCalendar " + calendar);
      }
    }
    return calendar;
  }

  private Object writeStackTraceElement(Object originalValue) {
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
  }

  private SOAPMessage getSoapMessageFromString(MimeHeaders hdrs, String xml) throws
          SOAPException, IOException {
    MessageFactory factory = MessageFactory.newInstance();
    return factory.createMessage(hdrs,
            new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))));
  }

  private void handleSOAPPartTransform(String[] object, SOAPPartImpl soapPart) {
    try {
      if (soapPart.getSOAPPart() != null) {
        SOAPBody body = soapPart.getEnvelope().getBody();
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
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}