package cn.infocore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/14 20:15
 * @instructions
 */
@RestController()
public class DataSourceQueryController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/query")
    public Map<String, Object> query() {
        List<Map<String, Object>> list =
                jdbcTemplate.queryForList("select * from Persons");
        return list.get(0);
    }
}
