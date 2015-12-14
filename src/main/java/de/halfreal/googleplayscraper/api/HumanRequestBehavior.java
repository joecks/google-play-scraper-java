package de.halfreal.googleplayscraper.api;

public class HumanRequestBehavior extends RequestBehavior {

    private final long m_timeMultiplier;
    private final long m_humanBaseTime;


    public HumanRequestBehavior() {
        this(3000, 3000);
    }

    public HumanRequestBehavior(long timeMultiplier, long humanBaseTime) {
        m_timeMultiplier = timeMultiplier;
        m_humanBaseTime = humanBaseTime;
    }

    @Override
    public void awaitContinue() throws InterruptedException {
        Thread.sleep(nextSleepTime());
    }

    protected long nextSleepTime() {
        return m_humanBaseTime + (long) (Math.random() * m_timeMultiplier);
    }
}
