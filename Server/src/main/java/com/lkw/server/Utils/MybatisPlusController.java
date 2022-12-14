package com.lkw.server.Utils;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lkw.server.entity.File;
import com.lkw.server.entity.User;
import com.lkw.server.mapper.FileMapper;
import com.lkw.server.mapper.UserMapper;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MybatisPlusController {
    //单例模式
    static MybatisPlusController controller=new MybatisPlusController();
    private static SqlSessionFactory sqlSessionFactory = controller.initSqlSessionFactory();
    private ArrayList<String> users=new ArrayList<>();
    private MybatisPlusController(){

    }
    public static MybatisPlusController getController(){
        return controller;
    }
    public void RemoveUser(String name){
        users.remove(name);
    }
    public String loginOrSignUp(String mode,String name,String password) {
        //初始化
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            //创建mapper对象
            UserMapper mapper = session.getMapper(UserMapper.class);
            if(mode.equals("login")) {
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("name", name);
                User user = mapper.selectOne(queryWrapper);
                if (user != null) {//未找到,尝试注册
                    if (user.getPassword().equals(password)) {//找到
                        if (users.contains(user.getName())){
                            return "false_logged";
                        }else{
                            users.add(user.getName());
                            user.setOnlineTime(new Date());
                            mapper.updateById(user);
                            return "true";
                        }
                    } else
                        return "false";
                }else
                    return "false";
            } else if (mode.equals("signUp")) {
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("name",name);
                if (mapper.exists(queryWrapper)) {
                    return "false_signUp";
                }else {
                    User user1 = new User(null, name, password, new Date(), new Date());
                    mapper.insert(user1);
                    return "true_signUp";
                }

            }else return "false";
        }
    }
    public void fileAdd(String fileName,String username){
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            //创建mapper对象
            FileMapper mapper = session.getMapper(FileMapper.class);
            File file=new File(fileName,0L,username,new Date());
            mapper.insert(file);
        }
    }

    public void fileUpdate(String fileName) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            //创建mapper对象
            FileMapper mapper = session.getMapper(FileMapper.class);
            //查询出对象
            QueryWrapper<File> fileQueryWrapper = new QueryWrapper<>();
            fileQueryWrapper.eq("file_name", fileName);
            File file = mapper.selectOne(fileQueryWrapper);
            //对象赋值
            file.setDownloadCount(file.getDownloadCount() + 1L);
            System.out.println(file.getDownloadCount());

            //更新对象
            mapper.updateById(file);
        }
    }

    //工厂方法
    public  SqlSessionFactory initSqlSessionFactory() {
        DataSource dataSource = dataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("Production", transactionFactory, dataSource);
        MybatisConfiguration configuration = new MybatisConfiguration(environment);
        //
        configuration.addMapper(UserMapper.class);
        configuration.addMapper(FileMapper.class);
        configuration.setLogImpl(StdOutImpl.class);
        return new MybatisSqlSessionFactoryBuilder().build(configuration);
    }

    //连接进行
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://49.234.25.18:3306/course?serverTimezone=GMT%2B8");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        return dataSource;
    }
}
