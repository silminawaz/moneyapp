/* 
 * Copyright 2015 eWise Systems, Inc.
 */
(function () {
    var eWise = {};

    var getParamNames = function (func) {
        var STRIP_COMMENTS = /((\/\/.*$)|(\/\*[\s\S]*?\*\/))/mg;
        var ARGUMENT_NAMES = /([^\s,]+)/g;
        var fnStr = func.toString().replace(STRIP_COMMENTS, '');
        var result = fnStr.slice(fnStr.indexOf('(') + 1, fnStr.indexOf(')')).match(ARGUMENT_NAMES);
        if (result === null)
            result = [];
        return result;
    };

    eWise.$ = jQuery.noConflict(true);
    eWise._ = _.noConflict();

    eWise.highlight = function (elements, color) {
        if (!color) {
            color = 'green';
        }

        elements.wrap('<div class="highlighter"></div>');
        eWise.$('.highlighter').css('border', '5px solid ' + color);
    };

    eWise.currentPage = function (pages) {
        var page;
        if (pages.some(function (p) {
            if (p.isCurrentPage()) {
                page = p;
                eWise.acaEngineJSLogger.info('found me at ' + p.name);
                return true;
            }
            eWise.acaEngineJSLogger.info('not at ' + p.name);
            return false;
        })) {
            return page;
        } else {
            eWise.acaEngineJSLogger.info('unkown page');
            return null;
        }
    };

    eWise.getState = function () {
        var state = eWise.engine.getState();
        if (state) {
            return JSON.parse(state);
        } else {
            return state;
        }
    };

    eWise.setState = function (state) {
        eWise.engine.setState(JSON.stringify(state));
    };

    eWise.getPayload = function() {
        var payload = eWise.engine.getPayload();
        if (payload) {
            return JSON.parse(payload);
        } else {
            return payload;
        }
    };

    eWise.aca = function (acaFactory) {
        if (typeof acaFactory !== 'function') {
            eWise.acaEngineJSLogger.error('Illegal argument. eWise.aca expects a function.');
            throw 'Illegal argument. eWise.aca expects a function.';
        }

        var params = getParamNames(acaFactory);
        var args = [];
        for (var i = 0; i < params.length; i++) {
            if (eWise.hasOwnProperty(params[i])) {
                args[i] = eWise[params[i]];
            } else {
                args[i] = undefined;
            }
        }

        var aca = acaFactory.apply(undefined, args);
        var goal = eWise.engine.goal();
        if (typeof aca[goal] !== 'function') {
            eWise.acaEngineJSLogger.error('ACA does not support goal: ' + goal);
            eWise.engine.done();
        } else {
            eWise.acaEngineJSLogger.info('Executing goal: ' + goal);
            aca[goal].call(undefined);
        }
    };

    window.eWise = eWise;
}());
