package org.jackframework.service.component;

import com.alibaba.fastjson.JSONObject;

public class FastjsonResultWrapper implements ResultWrapper {

    @Override
    public Object wrapResult(HttpProcessContext processContext, Object result) {
        JSONObject wrapResult = new JSONObject();
        wrapResult.put("success", true);
        if (result != null) {
            wrapResult.put("result", result);
        }
        return wrapResult;
    }

}
