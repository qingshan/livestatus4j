package com.surfront.livestatus.util;

public class NagiosHelper {

    public static String getStateName(int state) {
        switch (state) {
            case 0:
                return "OK";
            case 1:
                return "WARNING";
            case 2:
                return "CRITICAL";
            case 3:
                return "UNKNOWN";
            case 4:
                return "DEPENDENT";
            default:
                return "UNKNOWN";
        }
    }

    public static String getShortStateName(int state) {
        switch (state) {
            case 0:
                return "OK";
            case 1:
                return "WARN";
            case 2:
                return "CRIT";
            case 3:
                return "UNKN";
            case 4:
                return "DEP";
            default:
                return "UNKN";
        }
    }

    public static String getHostStateName(int state) {
        switch (state) {
            case 0:
                return "UP";
            case 1:
                return "DOWN";
            case 2:
                return "UNREACH";
            default:
                return "UNKNOWN";
        }
    }
}
