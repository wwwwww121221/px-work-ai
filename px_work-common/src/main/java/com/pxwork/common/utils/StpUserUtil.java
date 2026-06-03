package com.pxwork.common.utils;

import cn.dev33.satoken.stp.StpLogic;

/**
 * Sa-Token 学员端认证工具类。
 */
public class StpUserUtil {

    /**
     * 账号类型标识。
     */
    public static final String TYPE = "user";

    /**
     * 底层 StpLogic 对象。
     */
    public static final StpLogic stpLogic = new StpLogic(TYPE);

    private StpUserUtil() {
    }

    public static StpLogic getStpLogic() {
        return stpLogic;
    }

    public static boolean isLogin() {
        return stpLogic.isLogin();
    }

    public static void checkLogin() {
        stpLogic.checkLogin();
    }

    public static void login(Object id) {
        stpLogic.login(id);
    }

    public static void logout() {
        stpLogic.logout();
    }

    public static long getLoginIdAsLong() {
        return stpLogic.getLoginIdAsLong();
    }

    public static long getLoginIdAsLong(long defaultValue) {
        return stpLogic.isLogin() ? stpLogic.getLoginIdAsLong() : defaultValue;
    }

    public static String getTokenValue() {
        return stpLogic.getTokenValue();
    }
}
