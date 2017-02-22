/*
 * Copyright 2015 eWise Systems, Inc.
 * v0.0.2
 */
(function () {
    var engine = {
        setResult: function(result) {
            android_engine.setResult(result);
        },
        done: function() {
            android_engine.done();
        },
        doneNoClose: function() {
            android_engine.doneNoClose();
        },
        getPlatform: function() {
            return android_engine.getPlatform();
        },
        goal: function() {
            return android_engine.goal();
        },
        getState: function() {
            return android_engine.getState();
        },
        setState: function(state) {
            android_engine.setState(state);
        },
        setStatus: function(status) {
            android_engine.setStatus(status);
        },
        getCredentials: function() {
            return JSON.parse(android_engine.getCredentials());
        },
        setErrorType: function(errorType) {
            android_engine.setErrorType(errorType);
        },
        setErrorMessage: function(errorMsg) {
            android_engine.setErrorMessage(errorMsg);
        },
        setVerifyMessage: function(msg) {
            android_engine.setVerifyMessage(msg);
        },
        getOtpKey: function() {
            return android_engine.getOtpKey();
        },
        setTransferPrompts: function(transferPromptsJsonString) {
            android_engine.setTransferPrompts(transferPromptsJsonString);
        },
        getTransferPrompts: function() {
            return JSON.parse(android_engine.getTransferPrompts());
        },
        clearTransferPrompts: function() {
            return android_engine.clearTransferPrompts();
        },
        setLoginUrls: function(loginUrls) {
            android_engine.setLoginUrls(loginUrls);
        }
    };
    var dataVault = {
        saveAccount: function(jsAccount) {
            android_dataVault.saveAccount(JSON.stringify(jsAccount));
        },
        saveTransactions: function(acctNumber, transactions) {
            android_dataVault.saveTransactions(acctNumber, JSON.stringify(transactions));
        },
        getArrayLength: function(arrLength) {
            return android_dataVault.getArrayLength(arrLength);
        }
    };

    window.engine = engine;
    window.dataVault = dataVault;
}());
