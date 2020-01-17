package cn.infocore.service;

import cn.infocore.bean.Department;
import org.springframework.stereotype.Service;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/16 19:02
 * @instructions
 */
@Service
public interface DepartmentService {

    Department getDeptById(Integer id);

    Department updateDeptById(Department department);

    Department insertDept(Department department);

    Integer deleteById(Integer id);
}
