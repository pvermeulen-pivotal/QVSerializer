package com.tmobile.qvxp.test.serializer;

import com.tmobile.qvxp.model.java.OrderReferenceData;
import com.tmobile.qvxp.model.java.OrderReferenceDataDetail;
import com.tmobile.qvxp.test.common.TestHarness;
import com.tmobile.qvxp.test.domain.TestDomain;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class OrderReferenceDataTest {
  private final TestHarness testHarness = new TestHarness();

  @Test
  public void testThatOrderReferenceDataSerialized() {
    TestDomain domain = testHarness.clientRunner(null, getOrderReferenceData());
    OrderReferenceData orderReferenceData = domain.getOrderReferenceData();
    List<OrderReferenceDataDetail> detail = orderReferenceData.details;
    OrderReferenceDataDetail orderReferenceDataDetail = detail.get(0);
    Assert.assertNotNull(domain);
    Assert.assertEquals("code", orderReferenceDataDetail.code);
    Assert.assertEquals("description", orderReferenceDataDetail.description);
    Assert.assertEquals("longdescription", orderReferenceDataDetail.longDescription);


  }

  private static OrderReferenceData getOrderReferenceData() {
    OrderReferenceData orderReferenceData = new OrderReferenceData();
    OrderReferenceDataDetail orderReferenceDataDetail = new OrderReferenceDataDetail();
    orderReferenceDataDetail.code = "code";
    orderReferenceDataDetail.description = "description";
    orderReferenceDataDetail.longDescription = "longdescription";
    orderReferenceData.details.add(orderReferenceDataDetail);
    return orderReferenceData;
  }
}
