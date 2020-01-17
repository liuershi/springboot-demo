package cn.infocore.controller;

import cn.infocore.bean.Department;
import cn.infocore.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/16 19:06
 * @instructions
 */
@RestController
public class DepartmentController {

    @Autowired
    DepartmentService service;

    @GetMapping("/dept/{id}")
    public Department getDeptById(@PathVariable("id") Integer id){
        Department dept = service.getDeptById(id);
        return dept;
    }

    @GetMapping("/dept")
    public Department insertDept(Department department) {
        service.insertDept(department);
        return department;
    }

    @GetMapping("dept/update")
    public Department updateDept(Department department){
        Department dept = service.updateDeptById(department);
        return dept;
    }

    @GetMapping("/dept/del/{id}")
    public String deleteDeptById(@PathVariable("id") Integer id) {
        Integer integer = service.deleteById(id);
        if (integer>0){
            return "yes";
        }
        return "no";
    }
}
