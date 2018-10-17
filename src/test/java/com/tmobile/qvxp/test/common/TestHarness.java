package com.tmobile.qvxp.test.common;

import com.tmobile.qvxp.internal.pdx.QVReflectionBasedAutoSerializer;
import com.tmobile.qvxp.model.java.OrderReferenceData;
import com.tmobile.qvxp.model.java.Waitable;
import com.tmobile.qvxp.test.domain.TestDomain;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TestHarness {

  public TestDomain clientRunner(Waitable waitable, OrderReferenceData orderReferenceData) {
    TestDomain tdg = null;
    try {
      ClientCache cache = getClientCache();
      Region<String, Object> region = getRegion(cache);
      TestDomain tdp = getTestDomain(waitable, orderReferenceData);
      region.put(tdp.getSessionId(), tdp);
      System.out.println("GemFire put: " + tdp.toString());
      tdg = (TestDomain) region.get(tdp.getSessionId());
      System.out.println("GemFire get: " + tdg.toString());
      cache.close();
    } catch (Exception e) {
      System.out.println(e + e.getCause().toString());
    }
    return tdg;
  }

  private TestDomain getTestDomain(Waitable waitable, OrderReferenceData orderReferenceData) throws DatatypeConfigurationException {
    GregorianCalendar c = new GregorianCalendar();
    c.setTime(new Date());
    XMLGregorianCalendar xmlDate = DatatypeFactoryImpl.newInstance().newXMLGregorianCalendar(c);
    StackTraceElement[] stackTrace = new RuntimeException("Runtime Exception").getStackTrace();
    return new TestDomain("123456789", 12, 1000L, new Date(), xmlDate, stackTrace[0], waitable,
            orderReferenceData);
  }

  private  Region<String, Object> getRegion(ClientCache cache) {
    ClientRegionFactory crf = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
    return (Region<String, Object>) crf.create("test");
  }

  private  ClientCache getClientCache() {
    ClientCacheFactory ccf = new ClientCacheFactory();
    ccf.addPoolLocator("localhost", 10334);
    ccf.setPdxReadSerialized(false);
    ccf.setPdxSerializer(new QVReflectionBasedAutoSerializer());
    return ccf.create();
  }


}
