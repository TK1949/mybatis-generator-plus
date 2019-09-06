# mybatis generator plus

#### 项目介绍
mybatis自动生成代码升级版

#### 软件架构
以最少的配置，简单快速的生成数据库代码，用于和mybatis整合使用


#### 安装教程

1. 需要更加不同的项目修改配置文件 jdbcConnection 节点的信息
2. 在 CodeGenTest.java 类中需要根据不同的项目修改数据库的名称

#### 使用说明

1. 在 CodeGenTest.java 类中修改 test 方法可以达到生成整个数据库的代码还是指定数据表的代码

   ```java
   @Test
   
   public void test() {
       /**
       * true 表示生成整个数据库的代码
       * false 表示生成配置文件中指定表的代码
       */
       try {
           this.buildCode(true);
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
   
   ```

   

#### 参与贡献

1. 作者：LeungZengJian
2. 时间：2018-09-01
