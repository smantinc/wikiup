package org.wikiup.servlet.exception;

import javax.servlet.http.HttpServletResponse;

import org.wikiup.core.util.StringUtil;

public class MissingRequestParameterException extends WikiupServletException {
    static final long serialVersionUID = 1527956842575948520L;

    public MissingRequestParameterException(String message) {
        super(HttpServletResponse.SC_BAD_REQUEST, StringUtil.format("Parameter Missing: {0}", message));
    }
}
