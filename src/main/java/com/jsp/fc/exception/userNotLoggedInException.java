package com.jsp.fc.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class userNotLoggedInException extends RuntimeException {
     private String message;
}
