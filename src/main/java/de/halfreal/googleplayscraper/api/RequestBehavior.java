package de.halfreal.googleplayscraper.api;

public class RequestBehavior {

    public enum UserAgentMode {
        DESKTOP, ALL
    }

    private final UserAgentMode m_userAgentMode;

    public RequestBehavior(UserAgentMode userAgentMode) {
        m_userAgentMode = userAgentMode;
    }

    public RequestBehavior() {
        this(UserAgentMode.DESKTOP);
    }

    public void awaitContinue() throws InterruptedException {
    }

    public String userAgent() {
        switch (m_userAgentMode) {
            case DESKTOP:
                return UserAgents.randomDesktop();
            case ALL:
                return UserAgents.randomAll();
            default:
                throw new RuntimeException("Missing not handled enum key "+ m_userAgentMode);
        }
    }
}
