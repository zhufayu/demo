package test.dmall.httpproxy.porxy;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final int MULTIPLE = 5;
    public static final String SUFFIX = ".lightning.dmall.com";

    public static final String SEPARATOR_FOR_QUESTION_MARK = "?";
    public static final String ZK_NOTICE_TYPE = "type";
    public static final String ZK_NOTICE_DOMAIN = "domain";
    public static final String ZK_NOTICE_NODE = "node";
    public static final String ZK_NOTICE_GROUP = "group";
    public static final String ZK_ROOT_DOMAIN = "root" + SUFFIX;
    public static final String ZK_ROOT_OUTER_PREFIX = "outer.";
    public static final String ZK_ROOT_INNER_PREFIX = "inner.";

    //请求前缀
    public static final String PREFIX_FOR_HTTP = "http:/";
    public static final String PREFIX_FOR_HTTPS = "https:/";
    public static final String NGINX_PROXY_PASS = "http://";
    public static final String NGINX_UPSTREAM = "lightning";


    public static final String SEPARATOR_FOR_SLASH = "/";
    public static final String DEFAULT_KENGDIE1 = "kayak";

    //动作类型
    public static final int ACTION_DEL = 0;
    public static final int ACTION_ADD = 1;
    public static final int ACTION_COVER = 2;

    //Admiral类型
    //按照精确匹配->前缀匹配->后缀匹配->正则匹配的顺序进行处理,每层内部使用最大化匹配
    public static final String MATCH_LIST_SUFFIX = "List";
    public static final String MATCH_MAP_SUFFIX = "Map";

    //精确匹配,对应ngnix的=
    public static final String MATCH_EXACT = "exact";
    public static final String MATCH_EXACTLIST = MATCH_EXACT + MATCH_LIST_SUFFIX;
    public static final String MATCH_EXACTMAP = MATCH_EXACT + MATCH_MAP_SUFFIX;
    //前缀匹配,对应ngnix的^~
    public static final String MATCH_PREFIX = "prefix";
    public static final String MATCH_PREFIXLIST = MATCH_PREFIX + MATCH_LIST_SUFFIX;
    public static final String MATCH_PREFIXMAP = MATCH_PREFIX + MATCH_MAP_SUFFIX;
    //后缀匹配,对应nginx的~$
    public static final String MATCH_SUFFIX = "suffix";
    public static final String MATCH_SUFFIXLIST = MATCH_SUFFIX + MATCH_LIST_SUFFIX;
    public static final String MATCH_SUFFIXMAP = MATCH_SUFFIX + MATCH_MAP_SUFFIX;
    //正则表达式匹配,影响性能,最好不用
    public static final String MATCH_REGEX = "regex";
    public static final String MATCH_REGEXLIST = MATCH_REGEX + MATCH_LIST_SUFFIX;
    public static final String MATCH_REGEXMAP = MATCH_REGEX + MATCH_MAP_SUFFIX;

    //AB测试的分段策略类型
    public static final String AB_EXPERIMENT_DETAIL = "ab.experiment.detail";

    public static final List<String> AB_TYPE_PARS = Arrays.asList(AB_EXPERIMENT_DETAIL);
}
