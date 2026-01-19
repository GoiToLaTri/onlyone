package com.zwind.identityservice.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class AppException extends RuntimeException {
    private final AppError appError;

    public AppException(AppError appError, Object ...args) {
        super(formatMessage(appError.getMessage(), args));
        this.appError = appError;
    }

    private static String formatMessage(String template, Object... args) {
        if (args != null && args.length > 0)
            return String.format(template, args);

        return template.replace("%s", "").replaceAll("\\s{2,}", " ").trim();
    }
}
