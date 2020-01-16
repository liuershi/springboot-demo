package cn.infocore.repository;

import cn.infocore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author wei.zhang@infocore.cn
 * @date 2020/1/15 22:47
 * @instructions
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    // 继承的JpaRepository的第一个泛型为我们需要操作的实体类，第二个泛型为操作实体类的主键类型
}
