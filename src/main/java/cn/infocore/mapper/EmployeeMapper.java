package cn.infocore.mapper;

import cn.infocore.bean.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/15 17:18
 * @instructions
 */
@Mapper
public interface EmployeeMapper {
    Employee findEmployeeById(Integer id);
}
