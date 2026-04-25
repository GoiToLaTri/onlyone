package com.zwind.identityservice.validation;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.zwind.common_lib.exception.HttpError;
import com.zwind.common_lib.exception.HttpException;
import com.zwind.identityservice.enums.SessionStatus;
import com.zwind.identityservice.modules.authentication.dto.SessionStage;

import io.grpc.Metadata;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionValidator {
    public void validateRest(SessionStage session, HttpServletRequest request) {
        if(session.getStatus() != SessionStatus.ACTIVE)
            throw new HttpException(HttpError.UNAUTHENTICATED);

        SessionStage.DeviceDetails details = SessionStage.DeviceDetails.builder()
                .platform(request.getHeader("Sec-CH-UA-Platform"))
                .platformVersion(request.getHeader("Sec-CH-UA-Platform-Version"))
                .architecture(request.getHeader("Sec-CH-UA-Arch"))
                .build();

        boolean matchingDevice = matchingDevice(details, session);
        if(!matchingDevice)
            throw new HttpException(HttpError.UNAUTHENTICATED);
    }

    public void validateGrpc(SessionStage session, Metadata metadata) {
        if(session.getStatus() != SessionStatus.ACTIVE)
            throw new HttpException(HttpError.UNAUTHENTICATED);

        SessionStage.DeviceDetails details = SessionStage.DeviceDetails.builder()
                .platform(metadata.get(Metadata.Key.of("ua-platform", Metadata.ASCII_STRING_MARSHALLER)))
                .platformVersion(metadata.get(Metadata.Key.of("ua-platform-version",Metadata.ASCII_STRING_MARSHALLER)))
                .architecture(metadata.get(Metadata.Key.of("ua-arch",Metadata.ASCII_STRING_MARSHALLER)))
                .build();

        boolean matchingDevice = matchingDevice(details, session);
        if(!matchingDevice)
            throw new HttpException(HttpError.UNAUTHENTICATED);
    }

    private boolean matchingDevice(SessionStage.DeviceDetails deviceDetails,
                                   SessionStage sessionStage){
        SessionStage.DeviceDetails details = sessionStage.getDeviceDetails();
        int confidenceScore = 100;

        String platform = cleanHeader(deviceDetails.getPlatform());
        String platformVersion = cleanHeader(deviceDetails.getPlatformVersion());
        String architecture = cleanHeader(deviceDetails.getArchitecture());

        if (platform == null || platform.equalsIgnoreCase("unknown"))
            confidenceScore -= 20;
        else if (!platform.equals(details.getPlatform()))
            confidenceScore -= 50;

        if(!Objects.equals(platformVersion, details.getPlatformVersion()))
            confidenceScore -= 10;

        if(!Objects.equals(architecture, details.getArchitecture()))
            confidenceScore -= 40;

        return confidenceScore >= 70;
    }



    private String cleanHeader(String value) {
        if (value == null) return null;
        return value.replace("\"", "").trim();
    }
}
