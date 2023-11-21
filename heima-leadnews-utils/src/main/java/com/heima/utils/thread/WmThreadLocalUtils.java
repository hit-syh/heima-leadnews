package com.heima.utils.thread;

import com.heima.model.wemedia.pojos.WmUser;

public class WmThreadLocalUtils {
    private final static ThreadLocal<WmUser> AP_USER_THREAD_LOCAL =new ThreadLocal<>();

    //存入
    public static void setUser(WmUser wmUser)
    {
        AP_USER_THREAD_LOCAL.set(wmUser);
    }
    //清理
    public static WmUser getUser()
    {
        return  AP_USER_THREAD_LOCAL.get();
    }
    //清理
    public static void  clear()
    {
        AP_USER_THREAD_LOCAL.remove();
    }
}
