package test.dmall.httpproxy.porxy;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class HttpUtils {

    public static String formatUrl(String url) {
        if(StringUtils.isNotEmpty(url)) {
            if(url.startsWith(Constants.PREFIX_FOR_HTTP)) {
                url = url.substring(Constants.PREFIX_FOR_HTTP.length());
            } else if(url.startsWith(Constants.PREFIX_FOR_HTTPS)) {
                url = url.substring(Constants.PREFIX_FOR_HTTPS.length());
            }
        }
        return url;
    }

    public static String formatUrlToUri(String url) {
        url = formatUrl(url);
        int offset = url.indexOf(Constants.SEPARATOR_FOR_SLASH, 1);
        if(offset > 0) {
            url = url.substring(offset);
        }
        return url;
    }

    public static String generateUrl(HttpServletRequest httpServletRequest) {
        String url = httpServletRequest.getRequestURL().toString();
        //针对kayak框架的特殊处理
        if(url.indexOf(Constants.DEFAULT_KENGDIE1) > -1) {
            String queryString = httpServletRequest.getQueryString();
            if(StringUtils.isNotEmpty(queryString)) {
                int offset = queryString.lastIndexOf(Constants.SEPARATOR_FOR_QUESTION_MARK);
                if(offset > 0) {
                    url += Constants.SEPARATOR_FOR_QUESTION_MARK + queryString.substring(0, offset);
                } else if(offset == 0) {
                    url += Constants.SEPARATOR_FOR_QUESTION_MARK + queryString;
                }
            }
        }
        return url;
    }

    public static String generateQueryString(HttpServletRequest httpServletRequest) {
        String queryString = httpServletRequest.getQueryString();

        //针对kayak框架的特殊处理
        if(httpServletRequest.getRequestURI().indexOf(Constants.DEFAULT_KENGDIE1) > -1) {
            if(StringUtils.isNotEmpty(queryString)) {
                int offset = queryString.lastIndexOf(Constants.SEPARATOR_FOR_QUESTION_MARK);
                if(offset > 0) {
                    queryString = queryString.substring(offset + 1);
                } else if(offset == 0) {
                    queryString = null;
                }
            }
        }
        return queryString;
    }
}
