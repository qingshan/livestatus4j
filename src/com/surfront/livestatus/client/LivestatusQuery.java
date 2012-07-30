package com.surfront.livestatus.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LivestatusQuery implements Serializable {
    private String table;
    private List<String> columns = new ArrayList<String>();
    private List<String> filters = new ArrayList<String>();
    private List<String> headers = new ArrayList<String>();
    private int limit;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String[] getColumns() {
        return columns.toArray(new String[0]);
    }

    public void addColumn(String column) {
        columns.add(column);
    }

    public String[] getFilters() {
        return filters.toArray(new String[0]);
    }

    public void addFilter(String filter) {
        filters.add(filter);
    }

    public String[] getHeaders() {
        return headers.toArray(new String[0]);
    }

    public void addHeader(String header) {
        headers.add(header);
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
