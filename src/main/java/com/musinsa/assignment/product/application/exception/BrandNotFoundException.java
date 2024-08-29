package com.musinsa.assignment.product.application.exception;

import com.musinsa.assignment.common.exception.ApplicationException;
import com.musinsa.assignment.common.support.Status;

public class BrandNotFoundException extends ApplicationException {
    public BrandNotFoundException() {
        super(Status.BRAND_NOT_FOUND);
    }
}
