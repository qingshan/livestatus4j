package com.surfront.livestatus;

import java.io.Serializable;

public class NagiosService implements Serializable {
    private String name;
    private String status;
    private String info;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof NagiosService)) {
            return false;
        }
        NagiosService that = (NagiosService) obj;
        return name.equals(that.name);
    }

    public String toString() {
        return name;
    }
}
