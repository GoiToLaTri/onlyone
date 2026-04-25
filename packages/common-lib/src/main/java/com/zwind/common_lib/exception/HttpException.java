package com.zwind.common_lib.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class HttpException extends RuntimeException {
    private final HttpError httpError;

    public HttpException(HttpError httpError, Object ...args) {
        super(formatMessage(httpError.getMessage(), args));
        this.httpError = httpError;
    }

    private static String formatMessage(String template, Object... args) {
        if (args != null && args.length > 0)
            return String.format(template, args);

        return template.replace("%s", "").replaceAll("\\s{2,}", " ").trim();
    }
}
