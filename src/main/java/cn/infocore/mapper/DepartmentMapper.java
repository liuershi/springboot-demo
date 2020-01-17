package cn.infocore.mapper;

import cn.infocore.bean.Department;
import org.apache.ibatis.annotations.*;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/16 18:54
 * @instructions
 */
@Mapper
public interface DepartmentMapper {

    @Select("SELECT* FROM department where id=#{id}")
    Department getDeptById(Integer id);

    @Update("UPDATE department set dt_id = #{dt_id} where id=#{id}")
    Department updateDeptById(Department department);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO department VALUES (#{id},#{name},#{age},#{dt_id})")
    Integer insertDept(Department department);

    @Delete("DELETE FROM department WHERE id=#{id}")
    Integer deleteById(Integer id);
}
