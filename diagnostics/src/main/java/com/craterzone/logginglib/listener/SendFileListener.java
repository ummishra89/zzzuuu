package com.craterzone.logginglib.listener;

import com.craterzone.logginglib.model.DiagnosticResponse;

/**
 * Created by craterzone on 4/9/16.
 */
public interface SendFileListener {
    void onSendFileSuccess(DiagnosticResponse response);
    void onSendFileError(DiagnosticResponse response);
}
