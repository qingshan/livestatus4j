package com.surfront.livestatus.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LivestatusResult implements Serializable {
    private List<String> headers = new ArrayList<String>();
    private Map<String, String> fieldDescs;
    private Map<String, String> fieldTypes;
    private List<List<String>> fieldValues;

    public LivestatusResult(Map<String, String> fieldTypes, Map<String, String> fieldDescs, List<String> headers, List<List<String>> fieldValues) {
        this.fieldTypes = fieldTypes;
        this.fieldDescs = fieldDescs;
        this.headers = headers;
        this.fieldValues = fieldValues;
    }

    public String[] getHeaders() {
        return headers.toArray(new String[0]);
    }

    public String getFieldType(String fieldname) {
        return fieldTypes.get(fieldname);
    }

    public String getFieldDescription(String fieldname) {
        return fieldDescs.get(fieldname);
    }

    public List<List<String>> getFieldValues() {
        return fieldValues;
    }

    public List<String> getFieldValues(String header) {
        List<String> values = new ArrayList<String>();
        int column = headers.indexOf(header);
        for (List<String> fieldValue : fieldValues) {
            values.add(fieldValue.get(column));
        }
        return values;
    }

    public List<String> getFieldValues(int index) {
        return fieldValues.get(index);
    }

    public String getFieldValue(String header, int index) {
        int column = headers.indexOf(header);
        return fieldValues.get(index).get(column);
    }

    public Object castFieldValue(String header, int index) {
        String value = getFieldValue(header, index);
        String fieldtype = fieldTypes.get(header);
        if (fieldtype.equals("int")) {
            return Integer.parseInt(value);
        } else if (fieldtype.equals("float")) {
            return Float.parseFloat(value);
        } else if (fieldtype.equals("list")) {
            List<String> res_list = new ArrayList<String>();
            String[] tokens = value.split(",");
            for (int i = 0; i < tokens.length; i++) {
                res_list.add(tokens[i]);
            }
            return res_list;
        }
        return value;
    }

    public int size() {
        return fieldValues.size();
    }
}

