package cn.infocore.mapper;

import cn.infocore.bean.Persons;
import org.apache.ibatis.annotations.*;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/15 14:40
 * @instructions 使用注解方法操作数据库
 */
// 指定这是操作数据库的mapper
@Mapper
public interface PersonsMapper {

    @Select("select * from Persons where age=#{age}")
    public Persons getPersonByAge(Integer age);

    @Delete("delete from Persons where age=#{age}")
    public int delPersonByAge(Integer age);

    // 由于使用的主键自增，所以通过指定options的属性返回自增的主键
    @Options(useGeneratedKeys = true, keyProperty = "personId")
    @Insert("insert into Persons values (#{personId},#{lastName},#{firstName},#{address},#{city},#{age})")
    public int insertPerson(Persons persons);

    @Update("update Persons set city = #{city} where age=#{age}")
    public int updatePersonByAge(String city, Integer age);
}
