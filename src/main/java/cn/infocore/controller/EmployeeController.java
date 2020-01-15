package cn.infocore.controller;

import cn.infocore.bean.Employee;
import cn.infocore.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/15 17:25
 * @instructions
 */
@RestController
public class EmployeeController {

    @Autowired
    EmployeeMapper employeeMapper;

    @GetMapping("/employee/{id}")
    public Employee findById(@PathVariable("id") Integer id) {
        Employee employee = employeeMapper.findEmployeeById(id);
        return employee;
    }
}
