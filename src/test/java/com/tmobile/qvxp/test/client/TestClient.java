package com.tmobile.qvxp.test.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import com.tmobile.qvxp.domain.OrderReferenceData;
import com.tmobile.qvxp.domain.OrderReferenceDataDetail;
import com.tmobile.qvxp.domain.Waitable;
import com.tmobile.qvxp.domain.Waitable.Status;
import com.tmobile.qvxp.internal.pdx.QVReflectionBasedAutoSerializer;
import com.tmobile.qvxp.test.domain.TestDomain;

public class TestClient {

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

	private static SOAPMessage makeSoapMsg() {
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

	public static void main(String[] args) throws DatatypeConfigurationException, SOAPException {
		ClientCacheFactory ccf = new ClientCacheFactory();
		ccf.addPoolLocator("localhost", 10334);
		ccf.setPdxReadSerialized(false);
		ccf.setPdxSerializer(new QVReflectionBasedAutoSerializer());
		ClientCache cache = ccf.create();

		javax.xml.soap.MimeHeaders mh;
		ClientRegionFactory crf = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
		Region<String, Object> region = crf.create("mpos2");

		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar xmlDate = DatatypeFactoryImpl.newInstance().newXMLGregorianCalendar(c);
		StackTraceElement[] stackTrace = new RuntimeException("Runtime Exception").getStackTrace();
		Waitable waitable = new Waitable(Status.INITIALIZED);
		SOAPMessage sm = makeSoapMsg();
		waitable.dataItem = makeSoapMsg();
		OrderReferenceData orderReferenceData = new OrderReferenceData();
		OrderReferenceDataDetail orderReferenceDataDetail = new OrderReferenceDataDetail();
		orderReferenceDataDetail.code = "code";
		orderReferenceDataDetail.description = "description";
		orderReferenceDataDetail.longDescription = "longdescription";
		orderReferenceData.details.add(orderReferenceDataDetail);

		TestDomain tdp = new TestDomain("123456789", 12, 1000L, new Date(), xmlDate, stackTrace[0], waitable,
				orderReferenceData);
		region.put(tdp.getSessionId(), tdp);
		System.out.println("Object put: " + tdp.toString());
		TestDomain tdg = (TestDomain) region.get(tdp.getSessionId());
		System.out.println("Object get: " + tdg.toString());

	}

}
