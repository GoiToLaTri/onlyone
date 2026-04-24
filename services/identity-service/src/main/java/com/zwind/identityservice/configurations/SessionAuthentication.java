package com.zwind.identityservice.configurations;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionAuthentication extends AbstractAuthenticationToken {
    SessionPrincipal sessionPrincipal;

    public SessionAuthentication(
            SessionPrincipal principal,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(authorities);
        this.sessionPrincipal = principal;
        setAuthenticated(true);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public @Nullable Object getCredentials() {
        return null;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return sessionPrincipal;
    }

    @Override
    @NonNull
    public String getName() {
        return sessionPrincipal.getAccountId();
    }
}
