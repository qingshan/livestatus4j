package com.surfront.livestatus.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LivestatusClient {
    private final String host;
    private final int port;

    public LivestatusClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public LivestatusResult query(LivestatusQuery query) throws IOException {
        Map<String, String> fieldDescs = new LinkedHashMap<String, String>();
        Map<String, String> fieldTypes = new LinkedHashMap<String, String>();
        List<List<String>> fieldValues = new ArrayList<List<String>>();
        String response = query("columns", null, new String[]{"table = " + query.getTable()}, null);
        BufferedReader in = null;
        try {
            in = new BufferedReader(new StringReader(response));
            // Skip column headers
            String line = in.readLine();
            while ((line = in.readLine()) != null) {
                fieldDescs.put(line.split(";")[1], line.split(";")[0]);
                fieldTypes.put(line.split(";")[1], line.split(";")[3]);
            }
            in.close();

            response = query(query.getTable(), query.getColumns(), query.getFilters(), new String[]{"ColumnHeaders: on", "ResponseHeader: fixed16", "OutputFormat: csv"}, query.getLimit());
            in = new BufferedReader(new StringReader(response));
            String responseHeader = in.readLine();
            if (!responseHeader.startsWith("200")) {
                throw new IOException("Livestatus response: \n" + responseHeader + " \n" + in.readLine());
            }
            int response_size = Integer.parseInt(responseHeader.split(" ")[responseHeader.split(" ").length - 1]);
            if (response_size > 1024 * 1024 * 1024) {
                throw new IOException("Livestatus answer exceeds 1 GB. Aborting..");
            }
            while ((line = in.readLine()) != null) {
                List<String> values = new ArrayList<String>();
                for (String field : line.split(";")) {
                    values.add(field);
                }
                fieldValues.add(values);
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        List<String> headers = fieldValues.remove(0);
        return new LivestatusResult(fieldTypes, fieldDescs, headers, fieldValues);
    }

    public String query(String table, String[] columns, String[] filters, String[] headers) throws IOException {
        return query(table, columns, filters, headers, 0);
    }

    public String query(String table, String[] columns, String[] filters, String[] headers, int limit) throws IOException {
        String request = toRequest(table, columns, filters, headers, limit);
        return query(request);
    }

    public String query(String request) throws IOException {
        Socket socket = new Socket(host, port);
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(request);
            return read(in);
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            socket.close();
        }
    }

    private static String read(BufferedReader in) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }

    private String toRequest(String table, String[] columns, String[] filters, String[] headers, int limit) {
        StringBuilder query = new StringBuilder();
        query.append("GET ").append(table).append("\n");
        if (columns != null) {
            query.append("Columns: ");
            for (String column : columns) {
                query.append(column).append(" ");
            }
            String temp = query.toString();
            query = new StringBuilder(temp.trim());
            query.append("\n");
        }

        if (filters != null) {
            for (String filter : filters) {
                query.append("Filter: ").append(filter).append("\n");
            }
        }
        if (headers != null) {
            for (String header : headers) {
                query.append(header).append("\n");
            }
        }
        if (limit != 0) {
            query.append("Limit: ").append(limit).append("\n");
        }
        query.append("\n");
        return query.toString();
    }
}
