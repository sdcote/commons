package cookbook;

import coyote.commons.NetUtil;

public class NetUtilExamples {
    public static void main(String[] args) {
        System.out.println(NetUtil.getQualifiedHostName("206.80.116.92"));
        System.out.println(NetUtil.getQualifiedHostName("147.206.237.200"));
        System.out.println(NetUtil.getQualifiedHostName("143.98.190.1"));
        System.out.println(NetUtil.getQualifiedHostName("205.254.54.103"));
    }

}
