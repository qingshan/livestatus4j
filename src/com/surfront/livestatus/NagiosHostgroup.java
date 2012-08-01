package com.surfront.livestatus;

import java.io.Serializable;
import java.util.List;

public class NagiosHostgroup implements Serializable {
    private String name;
    private List<NagiosHost> hosts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NagiosHost> getHosts() {
        return hosts;
    }

    public void setHosts(List<NagiosHost> hosts) {
        this.hosts = hosts;
    }

    public int getServiceCounts() {
        int count = 0;
        for (NagiosHost host : hosts) {
            count += host.getServicesCount();
        }
        return count;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof NagiosHostgroup)) {
            return false;
        }
        NagiosHostgroup that = (NagiosHostgroup) obj;
        return name.equals(that.name);
    }

    public String toString() {
        return name;
    }
}
