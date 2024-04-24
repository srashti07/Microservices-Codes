package com.bej.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT,reason = "Customer Already Exists")
public class CustomerAlreadyExistsException extends  Exception{
}
