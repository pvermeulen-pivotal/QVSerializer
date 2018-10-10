package com.tmobile.qvxp.test.client;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.SOAPException;

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
		OrderReferenceData orderReferenceData = new OrderReferenceData();
		OrderReferenceDataDetail orderReferenceDataDetail = new OrderReferenceDataDetail();
		orderReferenceDataDetail.code = "code";
		orderReferenceDataDetail.description = "description";
		orderReferenceDataDetail.longDescription = "longdescription";
		orderReferenceData.details.add(orderReferenceDataDetail);
		region.put("123456789",orderReferenceData);
		System.out.println("Object put: " + orderReferenceData.toString());
		OrderReferenceData d1 = (OrderReferenceData) region.get("123456789");
		System.out.println("Object get: " + d1.toString());
		
		TestDomain tdp = new TestDomain("123456789", 12, 1000L, new Date(), xmlDate, stackTrace[0], waitable,
				orderReferenceData);
		region.put(tdp.getSessionId(), tdp);
		System.out.println("Object put: " + tdp.toString());
		TestDomain tdg = (TestDomain) region.get(tdp.getSessionId());
		System.out.println("Object get: " + tdg.toString());

	}

}
