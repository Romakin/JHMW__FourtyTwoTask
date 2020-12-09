package ru.netology.task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private static final String INDEX_FILE_NAME = "index.html";
    private String protocol;
    private String method;
    private String path;
    private String fullUrl;
    private Map<String, String> headers;
    private Map<String, String> queryParameters;
    private byte[] body;
    private BufferedReader in;

    public Request(BufferedReader in) {
        this.in = in;
        queryParameters = new HashMap<>();
        headers = new HashMap<>();
    }

    public String getProtocol() {
        return protocol;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public String getHeaders(String headerName) {
        return headers.get(headerName);
    }

    public String getQueryParameters(String parName) {
        return queryParameters.get(parName);
    }

    public boolean parse() throws IOException {
        String firstLine = in.readLine();
        String[] components = firstLine.split("[\\s\\t]");
        method = components[0];
        fullUrl = components[1];
        protocol = components[2];
        if (setHeaders()) return false;
        setPath();
        return true;
    }

    private boolean setHeaders() throws IOException {
        while (true) {
            String line = in.readLine();
            if (line.length() == 0) break;
            int ind = line.indexOf(":");
            if (ind == -1) return true;
            headers.put(line.substring(0, ind), line.substring(ind + 1));
        }
        return false;
    }

    private void setPath() {
        int ind = fullUrl.indexOf("?");
        if (ind == -1) {
            path = fullUrl;
        } else {
            path = fullUrl.substring(0, ind);
            parseQueryParameters(fullUrl.substring(ind + 1));
        }
        if ("/".equals(path)) path += INDEX_FILE_NAME;
    }

    public void parseQueryParameters(String queryStr) {
        for (String par : queryStr.split("&"))  {
            int ind = par.indexOf('=');
            if (ind > -1)
                queryParameters.put(par.substring(0, ind),
                        par.substring(ind + 1));
            else
                queryParameters.put(par, null);
        }
    }

}
