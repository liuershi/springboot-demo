package cn.infocore.mapper;

import cn.infocore.bean.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/15 17:18
 * @instructions 使用sql映射文件方式操作数据库
 */
@Mapper
public interface EmployeeMapper {
    Employee findEmployeeById(Integer id);
}
