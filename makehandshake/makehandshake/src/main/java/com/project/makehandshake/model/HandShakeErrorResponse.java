package com.project.makehandshake.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public class HandShakeErrorResponse extends Throwable {
    String responseMessage;
}
