import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 主要是用来生成 mysql 数据库的代码，如果要生成别的数据库的代码需要改下面的几点
 * 1、getDataTableName() 方法里面的 sql
 * 2、配置文件的 jdbcConnection 标签
 *
 * @date 2018-09-01
 */
public class CodeGenTest {

    // 数据库名称
    private final String TABLE_NAME = "你的数据库名称";
    // 配置文件的名称
    private final String XML_NAME = "generatorConfig.xml";
    // 是否生成整个数据库的代码，还是生成指定表的代码
    private final boolean FLAG = true;

    /**
     * 直接运行
     */
    @Test
    public void test() {
        /**
         * true 表示生成整个数据库的代码
         * false 表示生成配置文件中指定表的代码
         */
        try {
            this.buildCode(FLAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 生成代码
     */
    private void buildCode(boolean flag) throws Exception {

        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        //.../source/sys/target/classes
        URL classUrl = CodeGenTest.class.getProtectionDomain().getCodeSource().getLocation();
        File configFile = new File(classUrl.getPath() + XML_NAME);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);

        if (flag) {

            this.initTableConfigurations(config);

        }

        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }

    /**
     * 初始化整个数据库的表
     */
    private void initTableConfigurations(Configuration config) throws Exception {

        List<TableConfiguration> tableConfigurations = config.getContexts().get(0).getTableConfigurations();
        tableConfigurations.clear();

        String modelType = "CONDITIONAL";
        ModelType defaultModelType = ModelType.getModelType(modelType);
        Context context = new Context(defaultModelType);

        List<String> tableNames = this.getDataTableName();

        for (int i = 0; tableNames.size() > i; i++) {
            String tblName = tableNames.get(i);
            tblName.trim();
            TableConfiguration tableConfiguration = new TableConfiguration(context);
            tableConfiguration.setTableName(tblName);
            tableConfiguration.setDomainObjectName(this.underlineToCamel(tblName));
            tableConfigurations.add(tableConfiguration);
        }
    }

    /**
     * 初始化数据库配置信息
     */
    private JdbcConnection initJdbcConnection() throws Exception {

        //1.创建一个xml解析器对象
        SAXReader reader = new SAXReader();

        //2.读取xml文档，返回Document对象
        URL classUrl = CodeGenTest.class.getProtectionDomain().getCodeSource().getLocation();
        Document document = reader.read(new File(classUrl.getPath() + XML_NAME));

        // 获取数据库信息
        Element element = document.getRootElement()
                .element("context")
                .element("jdbcConnection");

        return new JdbcConnection(element.attributeValue("driverClass"),
                element.attributeValue("connectionURL"),
                element.attributeValue("userId"),
                element.attributeValue("password"));
    }

    /**
     * 查询数据表的名字
     */
    private List<String> getDataTableName() throws Exception {

        JdbcConnection jdbcConnection = this.initJdbcConnection();

        Connection conn = null;
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + TABLE_NAME + "' GROUP BY TABLE_NAME";

        List<String> list = new ArrayList<String>();
        Class.forName(jdbcConnection.getDriverClass());
        conn = DriverManager.getConnection(jdbcConnection.getConnectionURL(),
                jdbcConnection.getUserId(), jdbcConnection.getPassword());
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            String tblName = rs.getString("TABLE_NAME");
            list.add(tblName);
        }

        return list;
    }


    /**
     * 下划线格式字符串转换为驼峰格式字符串（但首字母大写）
     */
    private String underlineToCamel(String param) {
        if (StringUtils.isBlank(param)) {
            return "";
        }

        int len = param.length();
        StringBuilder sb = new StringBuilder(len);

        sb.append(Character.toUpperCase(param.charAt(0)));

        for (int i = 1; i < len; i++) {
            char c = param.charAt(i);
            if (c == '_') {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}

/**
 * 数据库配置类
 */
class JdbcConnection {

    private String driverClass;
    private String connectionURL;
    private String userId;
    private String password;

    public JdbcConnection() {
    }

    public JdbcConnection(String driverClass, String connectionURL, String userId, String password) {
        this.driverClass = driverClass;
        this.connectionURL = connectionURL;
        this.userId = userId;
        this.password = password;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}