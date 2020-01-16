package cn.infocore.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/15 22:26
 * @instructions
 */
// 使用JPA注解配置映射关系
@Entity //定义对象将会成为被JPA管理的实体，将映射到指定的数据库表
@Table(name = "jpa_user") // 用来指定和那个数据表对应，如果省略默认就是类名小写
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" }) // 当使用lombok添加get与set方法时会报错，需要使用该注解
@Data
public class User {
    @Id // 这是一个主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 指定主键为自增模式
    public Integer id;

    @Column(name = "last_name", length = 50)
    public String lastName;
    @Column(name = "first_name", length = 50)
    public String firstName;
    @Column // 省略默认列名就是属性名
    public Integer age;
}
