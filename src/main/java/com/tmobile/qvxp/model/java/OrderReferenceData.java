package com.tmobile.qvxp.model.java;

import java.util.ArrayList;
import java.util.List;

public class OrderReferenceData {
  public String referenceDataName;
  public List<OrderReferenceDataDetail> details = new ArrayList<>();

  @Override
  public String toString() {
    return "OrderReferenceData [referenceDataName=" + referenceDataName + ", details=" + details + "]";
  }


}