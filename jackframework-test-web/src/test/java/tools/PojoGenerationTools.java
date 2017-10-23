package tools;

import com.alibaba.druid.pool.DruidDataSource;
import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;
import org.jackframework.common.CaptainTools;
import org.jackframework.common.CharsWriter;
import org.jackframework.jdbc.core.CommonDaoConfig;
import org.jackframework.jdbc.jdbc.DataAccessChannelFactory;
import org.jackframework.jdbc.orm.Column;
import org.jackframework.jdbc.orm.Table;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class PojoGenerationTools {

    protected static final String PROPERTIES    = "application.properties";
    protected static final String POJO_PATH     = "D:\\Projects\\jackframework-parent\\jackframework-test-service\\src\\main\\java\\org\\jackframework\\testservice\\pojo\\";
    protected static final String PACKAGE_NAME  = "org.jackframework.testservice.pojo";
    protected static final String TEMPLATE_PATH = "PojoTemplate.jetx";

    protected static final Map<Integer, Class> SQL_TYPE_MAP = new HashMap<Integer, Class>() {
        {
            put(Types.BIT, Boolean.class);
            put(Types.TINYINT, Boolean.class);
            put(Types.SMALLINT, Integer.class);
            put(Types.INTEGER, Integer.class);
            put(Types.BIGINT, Long.class);
            put(Types.FLOAT, Float.class);
            put(Types.REAL, Float.class);
            put(Types.DOUBLE, Double.class);
            put(Types.NUMERIC, BigDecimal.class);
            put(Types.DECIMAL, BigDecimal.class);
            put(Types.CHAR, String.class);
            put(Types.VARCHAR, String.class);
            put(Types.LONGVARCHAR, String.class);
            put(Types.DATE, Date.class);
            put(Types.TIME, Date.class);
            put(Types.TIMESTAMP, Date.class);
            put(Types.BOOLEAN, Boolean.class);
            put(Types.NCHAR, String.class);
            put(Types.NVARCHAR, String.class);
            put(Types.LONGNVARCHAR, String.class);
        }
    };

    protected static final Logger LOGGER = LoggerFactory.getLogger(PojoGenerationTools.class);

    @Test
    public void generatePojo() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(CaptainTools.loadResourceAsStream(PROPERTIES));

        String jdbcUrl  = properties.getProperty("dataSource.url");
        String user     = properties.getProperty("dataSource.user");
        String password = properties.getProperty("dataSource.password");

        CommonDaoConfig commonDaoConfig = new CommonDaoConfig();
        DataSource      dataSource      = createDataSource(jdbcUrl, user, password);

        commonDaoConfig.setDataSource(dataSource);

        DataAccessChannelFactory dataAccessChannelFactory = new DataAccessChannelFactory(commonDaoConfig);

        Connection       connection       = dataSource.getConnection();
        DatabaseMetaData databaseMetaData = connection.getMetaData();

        ResultSet resultSet = databaseMetaData.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"});

        JetEngine   jetEngine   = JetEngine.create();
        JetTemplate jetTemplate = jetEngine.createTemplate(CaptainTools.loadResourceAsString(TEMPLATE_PATH));

        while (resultSet.next()) {
            String          tableName = resultSet.getString("TABLE_NAME");
            Table           table     = dataAccessChannelFactory.createTable(tableName);
            PojoInfo        pojoInfo  = new PojoInfo();
            List<FieldInfo> fields    = new ArrayList<FieldInfo>();
            Set<String>     imports   = new LinkedHashSet<String>();

            String pojoName = tableToClass(table.getTableName());

            pojoInfo.setPackageName(PACKAGE_NAME);
            pojoInfo.setImports(imports);
            pojoInfo.setFields(fields);
            pojoInfo.setPojoName(pojoName);

            int count = table.getColumnsCount();
            for (int i = 0; i < count; i++) {
                Column    column    = table.getColumn(i);
                FieldInfo fieldInfo = new FieldInfo();

                String fieldName = columnToField(column.getColumnName());
                Class  dataType  = SQL_TYPE_MAP.get(column.getSqlType());

                if (dataType == null) {
                    LOGGER.warn("The sql type is not suppored: {}",
                            column.getSqlType());
                    continue;
                }

                fieldInfo.setFieldName(fieldName);
                fieldInfo.setFieldType(dataType.getSimpleName());
                fieldInfo.setGetterName(CaptainTools.getGetterName(fieldName));
                fieldInfo.setSetterName(CaptainTools.getSetterName(fieldName));

                fields.add(fieldInfo);

                String typeName = dataType.getName();
                if (!typeName.startsWith("java.lang")) {
                    imports.add(typeName);
                }
            }

            File                file    = new File(POJO_PATH, pojoName + ".java");
            FileWriter          writer  = new FileWriter(file);
            Map<String, Object> context = new HashMap<String, Object>();

            context.put("pojoInfo", pojoInfo);

            jetTemplate.render(context, writer);

            writer.flush();
            writer.close();

            LOGGER.info("Generated: {}", file.getAbsolutePath());
        }
    }

    public static DataSource createDataSource(String jdbcUrl, String username, String password) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.init();
        return dataSource;
    }

    public static String tableToClass(String name) {
        CharsWriter cbuf   = new CharsWriter();
        int         length = name.length();
        if (0 < length) {
            cbuf.write(Character.toUpperCase(name.charAt(0)));
            write(1, length, name, cbuf);
        }
        return cbuf.closeThen().toString();
    }

    public static String columnToField(String name) {
        CharsWriter cbuf   = new CharsWriter();
        int         length = name.length();
        if (0 < length) {
            cbuf.write(Character.toLowerCase(name.charAt(0)));
            write(1, length, name, cbuf);
        }
        return cbuf.closeThen().toString();
    }

    public static void write(int off, int length, String name, CharsWriter cbuf) {
        while (off < length) {
            int codePoint = name.charAt(off++);
            if (codePoint == '_') {
                if (off < length) {
                    cbuf.write(Character.toUpperCase(name.charAt(off++)));
                    continue;
                }
                cbuf.write('_');
            } else {
                cbuf.write(Character.toLowerCase(codePoint));
            }
        }
    }

    public static class PojoInfo {

        protected String packageName;

        protected Set<String> imports;

        protected String pojoName;

        protected List<FieldInfo> fields;

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public Set<String> getImports() {
            return imports;
        }

        public void setImports(Set<String> imports) {
            this.imports = imports;
        }

        public String getPojoName() {
            return pojoName;
        }

        public void setPojoName(String pojoName) {
            this.pojoName = pojoName;
        }

        public List<FieldInfo> getFields() {
            return fields;
        }

        public void setFields(List<FieldInfo> fields) {
            this.fields = fields;
        }

    }

    public static class FieldInfo {

        protected String fieldName;

        protected String fieldType;

        protected String getterName;

        protected String setterName;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldType() {
            return fieldType;
        }

        public void setFieldType(String fieldType) {
            this.fieldType = fieldType;
        }

        public String getGetterName() {
            return getterName;
        }

        public void setGetterName(String getterName) {
            this.getterName = getterName;
        }

        public String getSetterName() {
            return setterName;
        }

        public void setSetterName(String setterName) {
            this.setterName = setterName;
        }

    }

}
