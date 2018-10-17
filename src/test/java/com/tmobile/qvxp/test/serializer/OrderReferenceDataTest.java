package com.tmobile.qvxp.test.serializer;

import com.tmobile.qvxp.model.java.OrderReferenceData;
import com.tmobile.qvxp.model.java.OrderReferenceDataDetail;
import com.tmobile.qvxp.test.common.TestHarness;
import org.junit.Assert;
import org.junit.Test;

public class OrderReferenceDataTest {
  private final TestHarness testHarness = new TestHarness();

  @Test
  public void testThatOrderReferenceDataSerialized() {
    Assert.assertNotNull(testHarness.clientRunner(null, getOrderReferenceData()));
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
