package com.tmobile.qvxp.test.serializer;

import com.sun.xml.internal.messaging.saaj.soap.MessageFactoryImpl;
import com.sun.xml.internal.messaging.saaj.soap.MessageImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Message1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPPart1_1Impl;
import com.tmobile.qvxp.model.groovy.ServiceException;
import com.tmobile.qvxp.model.groovy.ServiceResponse;
import com.tmobile.qvxp.model.groovy.ServiceStatusMessage;
import com.tmobile.qvxp.model.java.Waitable;
import com.tmobile.qvxp.model.java.WaitableException;
import com.tmobile.qvxp.test.common.TestHarness;
import com.tmobile.qvxp.test.domain.TestDomain;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class WaitableTest {

  private final TestHarness testHarness = new TestHarness();
  private Waitable waitable;

  @Test
  public void testThatDefaultWaitableIsSerialized() {
    waitable = getWaitable(new WaitableException());
    Assert.assertNotNull(testHarness.clientRunner(waitable,null));
  }

  @Test
  public void testNullCase() {
    waitable = getWaitable(null);
    Assert.assertNotNull(testHarness.clientRunner(waitable,null));
  }

  @Test
  public void testThatNullWaitableIsSerialized() {
    waitable = getWaitable(new WaitableException(null));
    Assert.assertNotNull(testHarness.clientRunner(waitable,null));
  }

  @Test
  public void testThatDefaultServiceExceptionSerialized() {
    waitable = getWaitable(new ServiceException());
    Assert.assertNotNull(testHarness.clientRunner(waitable,null));
  }

  @Test
  public void testThatWaitableWithMessageSerialized() {
    waitable = getWaitable(getWaitableException());
    Assert.assertNotNull(testHarness.clientRunner(waitable,null));
  }

  @Test
  public void testThatServiceExceptionWithMessageSerialized() {
    waitable = getWaitable(getServiceException());
    Assert.assertNotNull(testHarness.clientRunner(waitable,null));
  }

  @Test
  public void testThatServiceExceptionWithCauseAndMessageSerialized() {
    waitable = getWaitable(getServiceExceptionWithCause());
    Assert.assertNotNull(testHarness.clientRunner(waitable, null));
  }

  @Test
  public void testThatServiceExceptionCauseOnlySerialized() {
    waitable = getWaitable(getServiceExceptionCauseOnly());
    Assert.assertNotNull(testHarness.clientRunner(waitable, null));
  }

  @Test
  public void testThatServiceExceptionNullCauseOnlySerialized() {
    waitable = getWaitable(new ServiceException(new Throwable()));
    Assert.assertNotNull(testHarness.clientRunner(waitable, null));
  }

  @Test
  public void testThatServiceExceptionWithStatusCodeSerialized() {
    waitable = getWaitable(getServiceExceptionWithStatusCode());
    Assert.assertNotNull(testHarness.clientRunner(waitable, null));
  }

  @Test
  public void testThatServiceExceptionWithStatusAndResponseSerialized() {
    waitable = getWaitable(getServiceExceptionWithStatusCodeAndResponse());
    Assert.assertNotNull(testHarness.clientRunner(waitable, null));
  }

  /*StackOverFlow Without Throwable*/
  private  WaitableException getWaitableException() {
    return new WaitableException("something happened");
  }

  /*StackOverFlow Without Throwable*/
  private  ServiceException getServiceException() {
    return new ServiceException("Meta 1 The LISA VSE service can't return valid" +
            " response, please check your parameter values");
  }

  /*passes without throwable*/
  private  ServiceException getServiceExceptionWithCause() {
    return new ServiceException("Meta 1 The LISA VSE service can't return valid" +
            " response, please check your parameter values", new Throwable("test cause"));
  }

  /*passes without throwable*/
  private  ServiceException getServiceExceptionCauseOnly() {
    return new ServiceException(new Throwable("test cause"));
  }

  /*passes without throwable*/
  private  ServiceException getServiceExceptionWithStatusCode() {
    Map<String, ServiceStatusMessage> serviceStatusCodeMessageMap = new HashMap<>();
    ServiceStatusMessage serviceStatusMessage = new ServiceStatusMessage("message", true);
    serviceStatusCodeMessageMap.put("testString", serviceStatusMessage);
    return new ServiceException("test message", serviceStatusCodeMessageMap, new Throwable("root " +
            "cause"));
  }

  /*passes without throwable*/
  private  ServiceException getServiceExceptionWithStatusCodeAndResponse() {
    Map<String, ServiceStatusMessage> serviceStatusCodeMessageMap = new HashMap<>();
    ServiceStatusMessage serviceStatusMessage = new ServiceStatusMessage("message", true);
    serviceStatusCodeMessageMap.put("testString", serviceStatusMessage);
    return new ServiceException("test message", serviceStatusCodeMessageMap, new Throwable("root " +
            "cause"), new ServiceResponse());
  }

  private  Waitable getWaitable(Throwable throwable) {
    Waitable waitable = new Waitable(Waitable.Status.FAILED);
    MessageImpl message = new Message1_1Impl(makeSoapMsg());
    waitable.dataItem = new SOAPPart1_1Impl(message);
    waitable.taskException = throwable;
    return waitable;
  }

  private  SOAPMessage makeSoapMsg() {
    InputStream is = new ByteArrayInputStream(s1.getBytes());
    try {
      MimeHeaders mHdrs = new MimeHeaders();
      mHdrs.addHeader("Content-Type", "text/xml");
      return MessageFactory.newInstance().createMessage(mHdrs, is);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SOAPException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }


  private static String s1 = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
          + " <soap:Header>\r\n"
          + "         <header name=\"header1\" value=\"1-header\" />\r\n"
          + "         <header name=\"header2\" value=\"2-header\" />\r\n"
          + " </soap:Header>\r\n"
          + "	<soap:Body>\r\n" + "		<getBasicAccountDetailsRequest\r\n"
          + "			xmlns=\"http://retail.tmobile.com/sdo\">\r\n" + "			<header>\r\n"
          + "				<partnerId>ACUI</partnerId>\r\n" + "				<requestId>1307865749</requestId>\r\n"
          + "				<partnerSessionId>5AECB23F342D5C41B01888EF0CDDCFE5\r\n"
          + "				</partnerSessionId>\r\n"
          + "				<partnerTransactionId>26966182420729118</partnerTransactionId>\r\n"
          + "				<partnerTimestamp>2018-10-11T16:11:22.556Z</partnerTimestamp>\r\n"
          + "				<application>ACUI</application>\r\n"
          + "				<applicationUserId>vkuperm</applicationUserId>\r\n"
          + "				<channel>CARE</channel>\r\n"
          + "				<targetSystemUserId>1992</targetSystemUserId>\r\n"
          + "				<storeId>451</storeId>\r\n" + "				<dealerCode>0000002</dealerCode>\r\n"
          + "			</header>\r\n" + "			<ban>444444440</ban>\r\n"
          + "			<retrieveAdditionalBillingInfo>true</retrieveAdditionalBillingInfo>\r\n"
          + "			<retrievePaperlessBillDetails>true</retrievePaperlessBillDetails>\r\n"
          + "			<includeBusinessFamilyDiscount>true</includeBusinessFamilyDiscount>\r\n"
          + "			<returnUnSimEligibility>true</returnUnSimEligibility>\r\n"
          + "			<includeCrossStackableIndicator>true</includeCrossStackableIndicator>\r\n"
          + "			<includeUpfrontPaymentEligibility>true\r\n" + "			</includeUpfrontPaymentEligibility>\r\n"
          + "		</getBasicAccountDetailsRequest>\r\n" + "	</soap:Body>\r\n" + "</soap:Envelope>\r\n";

  private static String s2 = "<S:Envelope\r\n" + "	xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
          + "	<S:Body>\r\n" + "		<S:Fault xmlns:ns3=\"http://www.w3.org/2003/05/soap-envelope\">\r\n"
          + "			<faultcode>S:Server</faultcode>\r\n"
          + "			<faultstring>Meta 1 The LISA VSE service can't return valid response, please check your parameter values</faultstring>\r\n"
          + "			<detail>\r\n" + "				<ns2:exception class=\"java.lang.Exception\"\r\n"
          + "					xmlns:ns2=\"http://jax-ws.dev.java.net/\">\r\n"
          + "					<message>(getBasicAccountDetails)The LISA VSE service can't return\r\n"
          + "						valid response, please check your parameter values.</message>\r\n"
          + "					<ns2:stackTrace></ns2:stackTrace>\r\n" + "				</ns2:exception>\r\n"
          + "			</detail>\r\n" + "		</S:Fault>\r\n" + "	</S:Body>\r\n" + "</S:Envelope>";


}
