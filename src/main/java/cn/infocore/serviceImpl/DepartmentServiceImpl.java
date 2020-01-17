package cn.infocore.serviceImpl;

import cn.infocore.bean.Department;
import cn.infocore.mapper.DepartmentMapper;
import cn.infocore.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/16 19:04
 * @instructions
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    DepartmentMapper mapper;

    @Override
    @Cacheable(cacheNames = "dept")
    /**
     * Cacheable：将方法的结果进行缓存，以后若是使用相同的数据，不去访问数据库，
     * 直接从缓存中取。
     *
     * CacheManager管理着多个Cache，对缓存真正的CRUD操作是对Cache的，每个Cache
     * 都有自己唯一的名字，下列几个为Cache的属性：
     *     cacheNames/value：指定Cache(缓存)的名字；
     *     key：缓存数据使用的key，可以用它来指定。默认是使用参数的值；
     *          例如：传入进来的id为1，那么等同于 <1,Department>；
     *          编写SqEL： #id；参数id的值，也可以使用#root.args[0]表示。
     *     keyGenerator：key的生成器，可以自己指定生成key的id；
     *         key/keyGenerator只能存在一个。
     *     CacheManager：缓存换利器。
     *     Condition：指定符合条件的时候才缓存，例如condition=“#id>0”.
     *     unless：当指定为true则不缓存，否则则缓存。
     *     sync：是否使用异步模式。
     */
    public Department getDeptById(Integer id) {
        System.out.println("--------------------cache");
        return mapper.getDeptById(id);
    }

    @Override
    public Department updateDeptById(Department department) {
        Department dept = mapper.updateDeptById(department);
        return dept;
    }

    @Override
    public Department insertDept(Department department) {
        mapper.insertDept(department);
        return department;
    }

    @Override
    public Integer deleteById(Integer id) {
        Integer integer = mapper.deleteById(id);
        return integer;
    }
}
