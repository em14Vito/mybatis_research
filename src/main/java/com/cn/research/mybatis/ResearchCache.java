/**
 * Copyright ${license.git.copyrightYears} the original author or authors.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cn.research.mybatis;

import com.alibaba.fastjson.JSON;

import com.cn.research.mybatis.generator.BusDO;
import com.cn.research.mybatis.generator.busDOMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * 测试mapper的映射方法
 *
 * @author zhoudong
 * @version 1.0.0 2018/5/7 16:17
 * @see [String]
 * @see {URL}
 * @see [Class name#method name]
 */
public class ResearchCache {

  private static SqlSessionFactory sqlSessionFactory;

  public ResearchCache(){
    String resource = "mybatis-config.xml";
    InputStream inputStream = null;

    // Resources路径： 从resources 下包为根目录;
    // 这里需要注意一点:需要将mybatis-config.xml文件夹 mark 为 Resources( Idea)
    try {
      inputStream = Resources.getResourceAsStream(resource);
    } catch (IOException e) {
      e.printStackTrace();
    }

    sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
  }


  public static void main(String[] args) {

    ResearchCache researchCache = new ResearchCache();

    /**
     * 结论: cache在一次sqlSession中(应该是一个事务中),是不会被插进cache里的(hashMap实现,所以不是线程安全)；
     * 只有一个sqlSession Close之后，才会将查询的数据插入cache里；
     */
    researchCache.testCacheWithSignleSqlSession();
    researchCache.testCacheWithOwnSqlSession();

    //TODO 缓存查询: sqlSession 1:先查询,再更新(更新上次查询的数据); sqlSession 2: 单查询(条件跟1一样);
  }


  /**
   * 1. 可用来追踪mapper映射的源码;
   * 2. 可用来追踪cache缓存的源码:
   *    - 两次相同的查询，分别生成各种查询需要的sqlSession; in other words, sqlSession不共享
   */
  public void testCacheWithOwnSqlSession(){

    SqlSession sqlSession = null;

    /************    测试Mapping   ******************/
    //1. sqlSession方式执行sql：
    try {
      sqlSession = sqlSessionFactory.openSession();
      BusDO busDO1 =
              sqlSession.selectOne(
                      "com.cn.research.mybatis.generator.busDOMapper.selectByPrimaryKey", 1);
      System.out.println("sqlSession.selectOne result :" + busDO1.getBusName());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      sqlSession.close();
    }

    //2. 接口形式执行sql, 这里使用了cache
    try {
      sqlSession = sqlSessionFactory.openSession();
      busDOMapper dao = sqlSession.getMapper(busDOMapper.class);
      BusDO busDO = new BusDO();
      busDO.setBusName("asdasd");
      busDO = dao.selectByPrimaryKey(1);
      System.out.println("success" + busDO.getBusName());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      sqlSession.close();
    }

  }

  /**
   * 1. 研究cache的使用，查询 => 更新 => 查询  对应的cache变化:
   */
  public void testCacheUpdateUsage(){
   SqlSession sqlSession = null;

    sqlSession = sqlSessionFactory.openSession();
    busDOMapper dao = sqlSession.getMapper(busDOMapper.class);

    /************    测试Cache   ******************/

    //select
    BusDO busDO = dao.selectByPrimaryKey(1);
    System.out.println("第一次查询: busDO is " + JSON.toJSONString(busDO));

    //update
//    busDO.setBusName("583路123");
//    dao.updateByPrimaryKeySelective(busDO);

    //select
    BusDO busDO2 = dao.selectByPrimaryKey(1);
    System.out.println("第二次查询: busDO is " + JSON.toJSONString(busDO2));



  }


  /**
   * 1. cache研究：
   * 两次相同的查询，共享一个SqlSession
   */
  public void testCacheWithSignleSqlSession(){

    SqlSession sqlSession = null;

    /************    测试Mapping   ******************/
    //1. sqlSession方式执行sql：
    try {
      sqlSession = sqlSessionFactory.openSession();
      BusDO busDO1 =
              sqlSession.selectOne(
                      "com.cn.research.mybatis.generator.busDOMapper.selectByPrimaryKey", 1);
      System.out.println("sqlSession.selectOne result :" + busDO1.getBusName());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
//      sqlSession.close();
    }

    //2. 接口形式执行sql, 这里使用了cache
    try {
//      sqlSession = sqlSessionFactory.openSession();
      busDOMapper dao = sqlSession.getMapper(busDOMapper.class);
      BusDO busDO = new BusDO();
      busDO.setBusName("asdasd");
      busDO = dao.selectByPrimaryKey(2);
      System.out.println("success" + busDO.getBusName());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      sqlSession.close();
    }

  }

}
