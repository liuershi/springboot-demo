package cn.infocore.bean;

import lombok.Data;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/15 14:37
 * @instructions
 */
@Data
public class Persons {
    private Integer personId;
    private String lastName;
    private String firstName;
    private String address;
    private String city;
    private Integer age;
}
