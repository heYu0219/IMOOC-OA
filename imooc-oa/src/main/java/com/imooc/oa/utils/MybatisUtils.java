package com.imooc.oa.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.Reader;
import java.util.function.Function;

public class MybatisUtils {
    private static SqlSessionFactory sqlSessionFactory=null;
    static {
        Reader reader=null;
        try {
            reader= Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory=new SqlSessionFactoryBuilder().build(reader);
        }catch (Exception e){
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * 执行select查询sql
     * @param func 要执行查询语句的代码块
     * @return 查询结果
     */
    public static Object executeQuery(Function<SqlSession,Object> func){
        //将打开连接和关闭连接封装
        SqlSession sqlSession=sqlSessionFactory.openSession();
        try {
            Object obj=func.apply(sqlSession);
            return obj;
        }finally {
            sqlSession.close();
        }

    }

    /**
     * 执行增删改写操作sql
     * @param func 要执行的写操作代码块
     * @return 写操作返回的结果
     */
    public static Object executeUpdate(Function<SqlSession,Object> func){
        //autoCommit:打开手动提交事务，实现事务控制
        SqlSession sqlSession=sqlSessionFactory.openSession(false);
        try {
            Object obj=func.apply(sqlSession);
            sqlSession.commit();//事务控制
            return obj;
        }catch (RuntimeException e){
            sqlSession.rollback();//更新异常时回滚事务
            throw e;
        }
        finally {
            sqlSession.close();
        }

    }
}
