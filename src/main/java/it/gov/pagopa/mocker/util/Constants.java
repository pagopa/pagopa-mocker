package it.gov.pagopa.mocker.util;

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

    public static final String APACHE_DISALLOW_DOCTYPE_DECL_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";

    public static final String REGEX_NORMALIZE_FIELDNAME = ".+:";

    public static final String REGEX_XML_TRIM = ">\\s*<";

    public static final String MOCKER_PATH_ROOT = "/mocker";

    public static final String REGEX_XML_TRIM_REPLACEMENT = "><";

    public static final String EMPTY_STRING = "";

    public static final Set<String> NOT_CACHEABLE_HEADERS = Set.of(
            "authorization", "age", "etag",
            "expires", "if-modified-since", "if-none-match",
            "last-modified", "user-agent"
    );

    public static final String WHITESPACE = " ";


    private Constants() {}
}
