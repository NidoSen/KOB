package com.kob.backend.controller.record;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GetRecordListController {
    @Autowired
    private GetRecordListService getRecordListService;

    @RequestMapping("/record/getlist/")
    JSONObject getList(@RequestParam Map<String, String> data) {
        System.out.println("111");
        Integer page = Integer.parseInt(data.get("page"));
        return getRecordListService.getList(page);
    }
}
