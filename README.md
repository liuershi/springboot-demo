# springboot学习

## 六、Springboot与数据访问

### 1.JDBC与数据源

#### 1.1、pom依赖于数据库配置

``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

数据库配置，以mysql为例：

```yml
spring:
  datasource:
    username: root
    password: 123456
    url: jdbc:mysql://192.168.13.163:3306/springbootMybatis
    driver-class-name: com.mysql.jdbc.Driver
```

springboot2.0默认使用的数据源为：

```java
com.zaxxer.hikari.HikariDataSource
```

数据源的配置都在DataSourceProperties中；

通过数据源获取的连接：

```java
HikariProxyConnection@3353485 wrapping com.mysql.jdbc.JDBC4Connection@4b939e
```

自动配置数据源类路径：org.springframework.boot.autoconfigure.jdbc

#### 1.2、默认数据源

springboot2.0默认数据源为HikariDataSource，也支持org.apache.tomcat.jdbc.pool.DataSource和org.apache.commons.dbcp2.BasicDataSource；

#### 1.3、支持自定义数据源

```java
/**
 * Generic DataSource configuration.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(DataSource.class)
@ConditionalOnProperty(name = "spring.datasource.type")
static class Generic {
    @Bean
    DataSource dataSource(DataSourceProperties properties) {
        // 使用DataSourceBuilder创建数据源，利用反射响应的type创建数据源，最后bind()绑定相关属性
        return properties.initializeDataSourceBuilder().build();
    }
}
```

#### 1.4、支持启动执行sql

他是通过DataSourceInitializerInvoker去实现的；

org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker：它的作用是执行sql中的create和insert语句

```yml
规则：
	schema-*.sql：执行create命令
	data-*.sql：执行insert命令
```

springboot2.0执行sql时需要设置initialization-mode属性，否则会报错：

```yml
spring:
  datasource:
	initialization-mode: always
```

同时也可以自定义sql名，不必符合规范：

```yml
spring:
  datasource:
    schema:
      - classpath:Person.sql
```

<img src="D:\Program Files (x86)\WXWork\zhangwei\sortware\photo\1579069522(1).jpg" alt="1579069522(1)" style="zoom:100%;" />

#### 1.5、操作数据库，提供JdbcTemplate

通过依赖注入，写个简单的例子：

```java
@RestController()
public class DataSourceQueryController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/query")
    public Map<String, Object> query() {
        List<Map<String, Object>> list =
                jdbcTemplate.queryForList("select * from Persons");
        return list.get(0);
    }
}
```

#### 1.6、指定自定义数据源（Druid）：

```xml
<!-- 引入druid数据源 -->
<!-- https://mvnrepository.com/artifact/com.alibaba/druid -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.21</version>
</dependency>
<!-- 使用druid配置需要的starter -->
<!-- https://mvnrepository.com/artifact/com.alibaba/druid-spring-boot-starter -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.10</version>
</dependency>
```



```yml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
```

#### 1.7、配置serverlet与filter登录druid后台

配置druid

```yml
spring:
  datasource:
    #基本属性设置
    username: root
    password: 123456
    url: jdbc:mysql://192.168.13.163:3306/springbootMybatis
    driver-class-name: com.mysql.jdbc.Driver
#    initialization-mode: always
#        schema:
#          - classpath:Person.sql
    ###################以下为druid增加的配置##################
    type: com.alibaba.druid.pool.DruidDataSource
    # 下面为连接池的补充设置，应用到上面所有数据源中
    # 初始化大小，最小，最大
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      # 配置获取连接等待超时的时间
      max-wait: 60000
      # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
      time-between-eviction-runs-millis: 60000
      # 配置一个连接在池中最小生存的时间，单位是毫秒
      min-evictable-idle-time-millis: 300000
      validationQuery: SELECT 1 FROM DUAL
      testWhileIdle: true
      testOnBorrow: false
      testOnReturn: false
      # 打开PSCache，并且指定每个连接上PSCache的大小
      poolPreparedStatements: true
      maxPoolPreparedStatementPerConnectionSize: 20
      # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙，此处是filter修改的地方
      filters: stat, wall, log4j
      useGlobalDataSourceStat: true
      connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
```



配置servlet和filter：

```java
@Configuration
public class DruidConfig {

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource config() {
        return new DruidDataSource();
    }

    // 配置Druid监控
    // 1.配置Servlet
    @Bean
    public ServletRegistrationBean statViewServlet(){
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");

        Map<String, String> initParams = new HashMap<>();
        initParams.put("loginUsername","admin");
        initParams.put("loginPassword","root");
        // 添加IP白名单，默认为全部IP
        initParams.put("allow","192.168.23.95");
        // 添加IP黑名单，当白名单和黑名单重复时，黑名单优先级更高
        initParams.put("deny","192.168.23.94");
        bean.setInitParameters(initParams);
        return bean;
    }

    // 2.配置过滤器
    @Bean
    public FilterRegistrationBean webStatFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());

        Map<String, String> initParams = new HashMap<>();
        // 排除拦截的文件
        initParams.put("exclusions","/druid/*"); 
        bean.setInitParameters(initParams);
        bean.setUrlPatterns(Arrays.asList("/*"));
        return bean;
    }
}
```

### 2、整合mybatis

### 1）注解方式

#### 1.1、引入pom依赖

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.3.2</version>
</dependency>
```

#### 1.2、建表

```sql
-- auto-generated definition
create table Persons
(
    PersonID  int auto_increment
        primary key,
    LastName  varchar(255) null,
    FirstName varchar(255) null,
    Address   varchar(255) null,
    City      varchar(255) null,
    Age       int          not null
);
```

#### 1.3、编写bean

```java
@Data
public class Persons {
    private Integer personId;
    private String lastName;
    private String firstName;
    private String address;
    private String city;
    private Integer age;
}
```

#### 1.4、编写Mapper

```java
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
```

#### 1.5、编写controller

```java
@RestController
public class PersonsController {

    @Autowired
    PersonsMapper personsMapper;

    @GetMapping("/person/{age}")
    public Persons getByAge(@PathVariable("age") Integer age) {
        Persons person = personsMapper.getPersonByAge(age);
        return person;
    }

    @GetMapping("/person")
    public Persons insertPerson(Persons persons) {
        personsMapper.insertPerson(persons);
        return persons;
    }

    @GetMapping("/updatePerson")
    public String updatePersonByAge(String city, Integer age){
        personsMapper.updatePersonByAge(city, age);
        return city;
    }

    @GetMapping("/delPerson/{age}")
    public Integer delPerson(@PathVariable("age") Integer age){
        personsMapper.delPersonByAge(age);
        return age;
    }
}
```

*注意：由于创建表时使用的主键自增，所以在insert时不需要传入主键，但insert完毕返回当前插入的数据时不会返回当前主键，通过设置注解@options的属性返回当前数据的主键*

#### 1.6、mybatis开启驼峰命名法（非必须）

产生原因：定义表结构时使用的是驼峰命名法，例如PersonID，而在使用mybatis时无法使用person_id与其等同，而springboot也是支持驼峰命名法的，可以手动开启；

![1579075717(1)](D:\Program Files (x86)\WXWork\zhangwei\sortware\photo\1579075717(1).jpg)

解决方法：在配置类中开启驼峰命名规则

```java
 // 开启驼峰命名法：PersonID <--> person_id
@Bean
public ConfigurationCustomizer configurationCustomizer() {
    return configuration -> {
        configuration.setMapUnderscoreToCamelCase(true);
    };
}
```

或者在yml中添加配置：

```yml
mybatis:
  configuration:
    map-underscore-to-camel-case: true
```

<u>**但自己两种方式都未生效，未找到原因**</u>

------

问题：mapper文件夹中mapper接口过多时，每个mapper文件都需要加@Mapper注解，导致过于麻烦且耦合度较高

解决：在启动类或者配置类中加注解，将指定文件夹下的mapper接口全部扫描到容器中，例如：

```java
@MapperScan(value = "cn.infocore.mapper")
```

### 2）配置文件

#### 2.1、引入pom依赖



```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.3.2</version>
</dependency>
```

#### 2.2、建表

```sql
-- auto-generated definition
create table employee
(
    id       int auto_increment
        primary key,
    name     varchar(200) null,
    age      int          null,
    position varchar(200) null
)
    charset = utf8;
```

#### 2.3、编写bean

```java
@Data
public class Employee {
    private Integer id;
    private String name;
    private Integer age;
    private String position;
}
```

#### 2.4、mybatis配置文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

</configuration>
```

#### 2.5、mapper映射文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="cn.infocore.mapper.EmployeeMapper">
    <select id="findEmployeeById" resultType="cn.infocore.bean.Employee">
        select * from employee where id = #{id}
    </select>
</mapper>
```

#### 2.6、yml配置mybatis配置和mapper映射文件

```yml
mybatis:
  config-location: classpath:mybatis/mybatis-config.xml
  mapper-locations: classpath:mybatis/mapper/*.xml
```

文件路径如图：

![1579081850(1)](D:\Program Files (x86)\WXWork\zhangwei\sortware\photo\1579081850(1).jpg)

#### 2.7、mapper接口编写

```java
@Mapper
public interface EmployeeMapper {
    Employee findEmployeeById(Integer id);
}
```

#### 2.8、编写controller

```java
@RestController
public class EmployeeController {

    @Autowired
    EmployeeMapper employeeMapper;

    @GetMapping("/employee/{id}")
    public Employee findById(@PathVariable("id") Integer id) {
        Employee employee = employeeMapper.findEmployeeById(id);
        return employee;
    }
}
```

### 3、整合JPA

它也叫Java Persistence API，中文名为java持久层api，spring底层是通过spring data来进行数据处理的，Spring Data为我们提供使用统一的API来对数据访问层进行操作，而它的主要模块就包括[Spring Data JPA](https://spring.io/projects/spring-data-jpa)，[Spring Data JDBC](https://spring.io/projects/spring-data-jdbc)，[Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)，[Spring Data Redis](https://spring.io/projects/spring-data-redis)等等。

而spring data JPA属于ORM（Object Relational Mapping），也是描述实体类与表的关系。

![1579095947(1)](D:\Program Files (x86)\WXWork\zhangwei\sortware\photo\1579095947(1).jpg)

#### 1.1、添加JPA依赖

```XML
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>
```

#### 1.2、编写实体类（Entity）

定义实体类和数据表的映射关系：

```java
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
```

#### 1.3、编写Dao接口操作实体类对应的数据表（Repository）

```java
public interface UserRepository extends JpaRepository<User, Integer> {
    // 继承的JpaRepository的第一个泛型为我们需要操作的实体类，第二个泛型为操作实体类的主键类型
}
```

#### 1.4、设置JPA的配置文件

```yml
###########################JPA###########################
spring:
  jpa:
    hibernate:
      # 更新或者创建数据表
      ddl-auto: update
    # 控制台显示sql
    show-sql: true
```

#### 1.5、编写controller

```java
@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/user/{id}")
    public User getById(@PathVariable("id") Integer id){
        User user = userRepository.getOne(id);
        return user;
    }

    @GetMapping("/user")
    public User insertUser(User user) {
        User save = userRepository.save(user);
        return save;
    }
}
```

## 七、springboot启动配置原理

## 八、自定义starters

starter：

​	1、这个场景需要使用到的依赖是什么？

​	2、如何编写自动配置

```java
@Configuration // 指定这个类是一个配置类
@ConditionalOnxxx // 指定条件下自动配置类生效
@AutoConfigureAfter // 指定自动配置类的顺序
@Bean // 给容器中添加组件

@ConfigurationProperties结合相关的xxxProperties类来绑定相关的属性
@EnableConfigurationProperties // 让xxxProperties生效并且加入到容器中，使用者就可以自动装配了
    
自动配置类要能加载的话：
需要将需要加载的自动配置类，放在META-INF/spring.factories中
```

![](D:\Program Files (x86)\WXWork\zhangwei\sortware\photo\1579145512(1).jpg)

![](D:\Program Files (x86)\WXWork\zhangwei\sortware\photo\1579145562(1).png)

​	3、模式

启动器只用来导入依赖，我们需要专门写一个自动配置模块，启动器来依赖自动配置模块，他人使用时引入启动器依赖就可以使用了。

命名规则：

​	1、官方启动器：

​		spring-boot-starter-web：spring-boot-starter-模块

​	2、自定义启动器：

​		mybatis-spring-boot-starter：模块-spring-boot-starter

例子：

#### 1.1、新建工程

首先，新建一个空工程，然后建两个子模块，一个子模块为maven，该模块为启动器模块，第二个模块为spring initialezer，里面进行各种配置与定义。

#### 1.2、starter引入依赖

在启动器的pom中引入自动配置模块的依赖

```xml
<!-- 引入infocore-spring-boot-autoconfiguration依赖 -->
<dependency>
    <groupId>cn.infocore</groupId>
    <artifactId>infocore-spring-boot-autoconfiguration</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

 #### 1.3、定义properties

```java
@Data
@ConfigurationProperties(prefix = "infocore.streamer") // 定义使用name和message属性需要以infocore.streamer为前缀
public class HelloProperties {
    private String name;
    private String message;
}
```

 #### 1.4、编写autoconfiguration

```java
@Configuration // 声明这是一个配置类
@ConditionalOnWebApplication // 当为web应用时该配置类才生效
@EnableConfigurationProperties(HelloProperties.class)
public class HelloAutoConfiguration {

    @Autowired
    HelloProperties helloProperties;

    @Bean
    public HelloService getHelloService() {
        HelloService service = new HelloService();
        service.setHelloProperties(helloProperties);
        return service;
    }
}
```

#### 1.5、编写service

```java
@RestController
@Data
public class HelloService {

    @Autowired
    HelloProperties helloProperties;

    // 引入starter后可以根据service调用到该方法
    public String sayHello() {
        return helloProperties.getName() + "：" + helloProperties.getMessage();
    }
}
```

#### 1.6、编写spring.factories

路径为：resources/META-INF/spring.factories

```spring.factories
# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
cn.infocore.autoconfiguration.HelloAutoConfiguration # 我们自己定义的autoconfiguration的路径
```

#### 1.7、build到maven仓库

![](D:\Program Files (x86)\WXWork\zhangwei\sortware\photo\1579161113(1).jpg)

先install自动配置模块，因为启动器模块是依赖自动配置模块的。

#### 1.8、测试

首先，在测试工程pom中引入启动器模块的依赖：

```xml
<!-- 引入自定义starter -->
<dependency>
    <groupId>org.example</groupId>
    <artifactId>infocore-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

然后，编写controller

```java
@RestController
public class HelloController {

    @Autowired
    HelloService helloService;

    @GetMapping("/say")
    public String say() {
        return helloService.sayHello();
    }
}
```

最后，在配置文件中定义我们自定义的properties中的属性

```properties
infocore.streamer.name=jack
infocore.streamer.message=hello
```

最后访问controller看到效果。

## 九、springboot-cache

### 1、cache的作用

随着时间的积累，应用的使用用户不断增加，数据规模也越来越大，往往数据库查询操作会成为影响用户使用体验的瓶颈，此时使用缓存往往是解决这一问题非常好的手段之一。Spring 3开始提供了强大的基于注解的缓存支持，可以通过注解配置方式低侵入的给原有Spring应用增加缓存功能，提高数据访问性能。在Spring Boot中对于缓存的支持，提供了一系列的自动化配置，使我们可以非常方便的使用缓存。

#### 1）JSR107：

Java Caching定义了5个核心接口，分别是CachingProvider, CacheManager, Cache, Entry 和 Expiry。

|      接口       | 作用                                                         |
| :-------------: | ------------------------------------------------------------ |
| CachingProvider | 定义了创建、配置、获取、管理和控制多个CacheManager。一个应用可 以在运行期访问多个CachingProvider。 |
|  CacheManager   | 定义了创建、配置、获取、管理和控制多个唯一命名的Cache，这些Cache 存在于CacheManager的上下文中。一个CacheManager仅被一个CachingProvider所拥有。 |
|      Cache      | 是一个类似Map的数据结构并临时存储以Key为索引的值。一个Cache仅被一个 CacheManager所拥有。 |
|      Entry      | 是一个存储在Cache中的key-value对。                           |
|     Expiry      | 每一个存储在Cache中的条目有一个定义的有效期。一旦超过这个时间，条目为过期 的状态。一旦过期，条目将不可访问、更新和删除。缓存有效期可以通过ExpiryPolicy设置。 |

#### 2）Spring缓存抽象：

Spring从3.1开始定义了org.springframework.cache.Cache 和org.springframework.cache.CacheManager接口来统一不同的缓存技术； 并支持使用JCache（JSR-107）注解简化我们开发。

Cache接口为缓存的组件规范定义，包含缓存的各种操作集合。
Cache接口下Spring提供了各种xxxCache的实现；如RedisCache，EhCacheCache , ConcurrentMapCache。

其中几个重要的概念：

| Cache          | 缓存接口，定义缓存操作。实现有RedisCache、EhCacheCache、ConcurrentMapCache等等。 |
| -------------- | ------------------------------------------------------------ |
| CacheManager   | 缓存管理器，管理各种缓存（Cache）组件。                      |
| @Cacheable     | 主要针对方法配置，可以对方法的请求参数对结果进行缓存。       |
| @CacheEvict    | 清空缓存（例如删除操作时清空缓存中的数据）。                 |
| @CachePut      | 保证方法被调用，又希望结果被缓存。（一般由于缓存更新）       |
| @EnableCaching | 开启基于注解的缓存。                                         |
| keyGennerator  | 缓存数据时key生成策略                                        |
| serialize      | 缓存数据时value序列化策略                                    |

### 2、使用spring缓存抽象

#### 1.1、准备环境

环境准备同整合mybatis一致，就使用之前的环境，唯一区别是操作新的数据表。

建表：department.sql

```sql
-- auto-generated definition
create table department
(
    id    int auto_increment comment '主键'
        primary key,
    name  varchar(100) not null comment '姓名',
    age   int          not null comment '年龄',
    dt_id int          not null comment '部门ID'
);
```

#### 1.2、编写bean

```java
@Data
public class Department {
    private Integer id;
    private String name;
    private Integer age;
    private Integer dt_id;
}
```

#### 1.3、编写mapper

```java
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
```

#### 1.4、编写service以及service的实现

service：

```java
@Service
public interface DepartmentService {

    Department getDeptById(Integer id);

    Department updateDeptById(Department department);

    Department insertDept(Department department);

    Integer deleteById(Integer id);
}
```

service的实现：

```java
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
```

#### 1.5、编写controller

```java
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
```



