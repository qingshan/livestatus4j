package com.surfront.livestatus;

import java.io.Serializable;

public class NagiosSummary implements Serializable {
    private String name;
    private int okServiceCount;
    private int warningServiceCount;
    private int criticalServiceCount;
    private int unknownServiceCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalServiceCount() {
        return okServiceCount + warningServiceCount + criticalServiceCount + unknownServiceCount;
    }

    public int getOkServiceCount() {
        return okServiceCount;
    }

    public void addOkServiceCount(int okServiceCount) {
        this.okServiceCount += okServiceCount;
    }

    public int getWarningServiceCount() {
        return warningServiceCount;
    }

    public void addWarningServiceCount(int warningServiceCount) {
        this.warningServiceCount += warningServiceCount;
    }

    public int getCriticalServiceCount() {
        return criticalServiceCount;
    }

    public void addCriticalServiceCount(int criticalServiceCount) {
        this.criticalServiceCount += criticalServiceCount;
    }

    public int getUnknownServiceCount() {
        return unknownServiceCount;
    }

    public void addUnknownServiceCount(int unknownServiceCount) {
        this.unknownServiceCount += unknownServiceCount;
    }
}
