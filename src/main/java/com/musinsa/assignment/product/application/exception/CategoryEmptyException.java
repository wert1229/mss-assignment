package com.musinsa.assignment.product.application.exception;

import com.musinsa.assignment.common.exception.ApplicationException;
import com.musinsa.assignment.common.support.Status;

public class CategoryEmptyException extends ApplicationException {
    public CategoryEmptyException() {
        super(Status.CATEGORY_EMPTY);
    }
}
