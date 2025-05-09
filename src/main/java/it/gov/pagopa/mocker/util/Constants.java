package it.gov.pagopa.mocker.util;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Constants {

    public static final String APPLICATION_JSON = "application/json";

    public static final String APPLICATION_XML = "application/xml";

    public static final String TEXT_XML = "text/xml";

    public static final String HEADER_CONTENTTYPE = "content-type";

    public static final String HEADER_CONTENTLENGTH = "content-length";

    public static final String STRING_CONTENT_KEY = "content";

    public static final String HEADER_SOAPACTION = "soapaction";

    public static final String APACHE_DISALLOW_DOCTYPE_DECL_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";

    public static final String REGEX_NORMALIZE_FIELDNAME = ".+:";

    public static final String REGEX_XML_TRIM = ">\\s*<";

    public static final String MOCKER_PATH_ROOT = "/mocker";

    public static final String REGEX_XML_TRIM_REPLACEMENT = "><";

    public static final String EMPTY_STRING = "";

    public static final String SCRIPTEXECUTOR_FUNCTION_SUFFIX = "__execute";

    public static final Set<String> NOT_CACHEABLE_HEADERS_COMMON = Set.of(
            "authorization", "age", "cache-control", "etag",
            "expires", "if-modified-since", "if-none-match",
            "last-modified", "request-context", "request-id", "user-agent"
    );

    public static final Set<String> NOT_CACHEABLE_HEADERS_CLOUDPROVIDER = Set.of(
            "traceparent", "x-appgw-trace-id", "x-client-ip",
            "x-real-ip", "x-request-id"
    );

    public static final Set<String> NOT_CACHEABLE_HEADERS_CUSTOM = Set.of(
            "x-cache-exclude-headers",
            // client-related headers: Postman
            "postman-token",
            // client-related headers: Google Chrome
            "sec-ch-ua", "sec-ch-ua-mobile", "sec-ch-ua-platform",
            "sec-fetch-dest", "sec-fetch-mode", "sec-fetch-site"
    );

    public static final Set<String> NOT_CACHEABLE_HEADERS;
    static {
        Set<String> set = new HashSet<>();
        set.addAll(NOT_CACHEABLE_HEADERS_CLOUDPROVIDER);
        set.addAll(NOT_CACHEABLE_HEADERS_COMMON);
        set.addAll(NOT_CACHEABLE_HEADERS_CUSTOM);
        NOT_CACHEABLE_HEADERS = Collections.unmodifiableSet(set);
    }

    public static final String WHITESPACE = " ";


    private Constants() {}
}
