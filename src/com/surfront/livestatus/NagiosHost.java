package com.surfront.livestatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NagiosHost implements Serializable {
    private String name;
    private String address;
    private String status;
    private List<NagiosService> services = new ArrayList<NagiosService>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getServicesCount() {
        return services.size();
    }

    public List<NagiosService> getServices() {
        return services;
    }

    public void setServices(List<NagiosService> services) {
        this.services = services;
    }

    public void addService(NagiosService service) {
        services.add(service);
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof NagiosHost)) {
            return false;
        }
        NagiosHost that = (NagiosHost) obj;
        return name.equals(that.name);
    }

    public String toString() {
        return name;
    }
}
