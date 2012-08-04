package com.surfront.livestatus;

import com.surfront.livestatus.client.LivestatusClient;
import com.surfront.livestatus.client.LivestatusQuery;
import com.surfront.livestatus.client.LivestatusResult;
import com.surfront.livestatus.util.NagiosHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class NagiosLivestatus {
    private LivestatusClient client;

    public NagiosLivestatus() {
        this("127.0.0.1", 6557);
    }

    public NagiosLivestatus(String host, int port) {
        this.client = new LivestatusClient(host, port);
    }

    public Set<String> getHostgroupNames() throws IOException {
        LivestatusQuery query = new LivestatusQuery();
        query.setTable("hostgroups");
        query.addColumn("name");
        LivestatusResult result = client.query(query);
        return new TreeSet<String>(result.getFieldValues("name"));
    }

    public Set<String> getHostNames(String hostgroup) throws IOException {
        LivestatusQuery query = new LivestatusQuery();
        query.setTable("hostgroups");
        query.addColumn("members");
        query.addFilter("name = " + hostgroup);
        LivestatusResult result = client.query(query);
        if (result.size() == 0) {
            return new TreeSet<String>();
        }
        return new TreeSet((List<String>) result.castFieldValue("members", 0));
    }

    public NagiosHostgroup getHostgroup(String hostgroup) throws IOException {
        return toHostgroup(hostgroup, getHosts(hostgroup));
    }

    public List<NagiosHost> getHosts(String hostgroup) throws IOException {
        LivestatusQuery query = new LivestatusQuery();
        query.setTable("hosts");
        query.addColumn("name");
        query.addColumn("groups");
        query.addColumn("state");
        query.addFilter("groups >= " + hostgroup);
        LivestatusResult result = client.query(query);
        List<NagiosHost> hosts = new ArrayList<NagiosHost>();
        int size = result.size();
        for (int i = 0; i < size; i++) {
            hosts.add(toNagiosHost(result, i));
        }
        for (NagiosHost host : hosts) {
            for (NagiosService service : getServices(hostgroup, host.getName())) {
                host.addService(service);
            }
        }
        return hosts;
    }

    public NagiosHost getHost(String hostgroup, String host) throws IOException {
        LivestatusQuery query = new LivestatusQuery();
        query.setTable("hosts");
        query.addColumn("name");
        query.addColumn("groups");
        query.addColumn("state");
        query.addColumn("plugin_output");
        query.addFilter("name = " + host);
        query.addFilter("groups >= " + hostgroup);
        LivestatusResult result = client.query(query);
        if (result.size() == 0) {
            throw new IOException("No such host: " + host);
        }
        NagiosHost nhost = toNagiosHost(result, 0);
        for (NagiosService service : getServices(hostgroup, host)) {
            nhost.addService(service);
        }
        return nhost;
    }

    public List<NagiosService> getServices(String hostgroup, String host) throws IOException {
        LivestatusQuery query = new LivestatusQuery();
        query.setTable("services");
        query.addColumn("host_name");
        query.addColumn("description");
        query.addColumn("state");
        query.addColumn("plugin_output");
        query.addColumn("perf_data");
        query.addFilter("host_groups >= " + hostgroup);
        query.addFilter("host_name = " + host);
        LivestatusResult result = client.query(query);
        List<NagiosService> services = new ArrayList<NagiosService>();
        int size = result.size();
        for (int i = 0; i < size; i++) {
            services.add(toNagiosService(result, i));
        }
        return services;
    }

    public List<NagiosSummary> getSummaries() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("GET services\n");
        sb.append("Stats: state = 0\n");
        sb.append("Stats: state = 1\n");
        sb.append("Stats: state = 2\n");
        sb.append("Stats: state = 3\n");
        sb.append("Columns: host_name\n");
        sb.append("\n");
        String request = sb.toString();
        String response = client.query(request);
        List<NagiosSummary> summaries = new ArrayList<NagiosSummary>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new StringReader(response));
            String line;
            while ((line = in.readLine()) != null) {
                List<String> values = new ArrayList<String>();
                for( String field : line.split(";") ){
                    values.add(field);
                }
                String name = values.get(0);
                NagiosSummary summary = new NagiosSummary();
                summary.setName(name);
                summary.addOkServiceCount(Integer.parseInt(values.get(1)));
                summary.addWarningServiceCount(Integer.parseInt(values.get(2)));
                summary.addCriticalServiceCount(Integer.parseInt(values.get(3)));
                summary.addUnknownServiceCount(Integer.parseInt(values.get(4)));
                summaries.add(summary);
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return summaries;
    }

    private NagiosHostgroup toHostgroup(String name, List<NagiosHost> hosts) {
        NagiosHostgroup hostgroup = new NagiosHostgroup();
        hostgroup.setName(name);
        hostgroup.setHosts(hosts);
        return hostgroup;
    }

    private NagiosHost toNagiosHost(LivestatusResult result, int index) {
        NagiosHost host = new NagiosHost();
        String name = result.getFieldValue("name", index);
        host.setName(name);
        host.setAddress(name.contains("/") ? name.substring(name.indexOf('/') + 1) : name);
        host.setStatus(NagiosHelper.getHostStateName((Integer) result.castFieldValue("state", index)));
        return host;
    }

    private NagiosService toNagiosService(LivestatusResult result, int index) {
        NagiosService service = new NagiosService();
        String name = result.getFieldValue("description", index);
        service.setName(name);
        service.setStatus(NagiosHelper.getStateName((Integer) result.castFieldValue("state", index)));
        service.setInfo(result.getFieldValue("plugin_output", index));
        return service;
    }
}
