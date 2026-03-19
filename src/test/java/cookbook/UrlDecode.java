package cookbook;

import coyote.commons.UriUtil;

public class UrlDecode {
    public static void main(String[] args) {
        System.out.println(UriUtil.decodeString("https://mcas-proxyweb.mcas.ms/certificate-checker?login=false&originalUrl=https%3A%2F%2Fcardinal.service-now.com.mcas.ms%2Fgith%3Fid%3Dsc_cat_item%26sys_id%3D01aa410cc3dc5e942dc11ef4e4013144%26McasTsid%3D20893&McasCSRF=96a88d050d39fdf7ea0ce4b3edaf391efacd825457b7bd77156755da4a2cdd45"));
    }
}
