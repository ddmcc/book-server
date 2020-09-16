package com.soft.book.config.filter;

import com.google.common.collect.Lists;

import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.StatementExecuteType;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.fastjson.JSONObject;
import com.soft.book.utils.MapHelper;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DruidLogFilter extends FilterEventAdapter {

    private static final String TBL_PRE = "T_ONE_";
    private static final List<String> EXCLUDE_TABLE = Lists.newArrayList("T_ONE_EXPORT_COLUMN");
    private static ThreadLocal threadLocal = new ThreadLocal<>();
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SQLUtils.FormatOption statementSqlFormatOption = new SQLUtils.FormatOption(false, false);

    public DruidLogFilter() {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {
        interceptorBefore(statement, sql);
        super.statementExecuteUpdateBefore(statement, sql);
    }

    @Override
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {

        super.statementExecuteUpdateAfter(statement, sql, updateCount);
    }

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        interceptorBefore(statement, sql);
        super.statementExecuteBefore(statement, sql);
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {

        super.statementExecuteAfter(statement, sql, result);
    }

    @Override
    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {
        threadLocal.remove();
        super.statement_executeErrorAfter(statement, sql, error);
    }


    private void interceptorBefore(StatementProxy statementProxy, String sql) {
        StatementExecuteType executeType = statementProxy.getLastExecuteType();
        if (!(executeType == StatementExecuteType.Execute || executeType == StatementExecuteType.ExecuteUpdate)) {
            return;
        }

        OperationEnum type = OperationEnum.getOperationType(sql);
        if (type == null) {
            return;
        }

        List<String> tables;
        try {
            String executeSql = getFinalExecuteSql(statementProxy, sql);
            Statement statement = CCJSqlParserUtil.parse(executeSql);
            tables = getTables(statement);
            List<Map<String, Object>> parameters = parameterMapping(statement, statementProxy, tables.get(0));
        } catch (JSQLParserException e) {
            log.error("解析SQL出错：", e);
            return;
        }

        if (tables.isEmpty()) {
            log.warn("无法获取操作库表...{}", sql);
            return;
        }

        Set<String> tableSet  = tables.stream()
            .filter(e -> e.startsWith(TBL_PRE) || EXCLUDE_TABLE.contains(e))
            .collect(Collectors.toSet());

        if (tableSet.isEmpty()) {
            return;
        }


    }

    private Optional<String> getPriKey(String tableName) {
        String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.`KEY_COLUMN_USAGE` WHERE table_name=? AND constraint_name='PRIMARY'";
        List<Map<String, Object>> rst = jdbcTemplate.queryForList(sql, tableName);
        if (rst.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of((String) rst.get(0).get("COLUMN_NAME"));
        }
    }

    /**
     * 从where条件获取主键值
     * @param expression    where条件
     * @param primaryKey    主键名
     * @return 主键值
     */
    private Optional<Object> getPrimaryKeyValue(Expression expression, String primaryKey) {
        if (expression instanceof AndExpression) {
            AndExpression andExpression = (AndExpression) expression;
            Object value = getPrimaryKeyValue(andExpression.getLeftExpression(), primaryKey);
            if (value == null) {
                return getPrimaryKeyValue(andExpression.getRightExpression(), primaryKey);
            } else {
                return Optional.of(value);
            }
        } else if (expression instanceof OrExpression) {
            OrExpression orExpression = (OrExpression) expression;
            Object value = getPrimaryKeyValue(orExpression.getLeftExpression(), primaryKey);
            if (value == null) {
                return getPrimaryKeyValue(orExpression.getRightExpression(), primaryKey);
            } else {
                return Optional.of(value);
            }
        } else if (expression instanceof EqualsTo) {
            EqualsTo equalsTo = (EqualsTo) expression;
            Column column = (Column) equalsTo.getLeftExpression();
            if (column.getColumnName().equals(primaryKey)) {
                return Optional.of(equalsTo.getRightExpression().toString());
            } else {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }


    /**
     * 获取最后执行的sql，参数已解析之后的
     */
    private String getFinalExecuteSql(StatementProxy statement, String sql) {
        int parametersSize = statement.getParametersSize();
        if (parametersSize == 0) {
            return sql;
        }

        List<Object> parameters = new ArrayList<>(parametersSize);
        for (int i = 0; i < parametersSize; ++i) {
            JdbcParameter jdbcParam = statement.getParameter(i);
            parameters.add(jdbcParam != null ? jdbcParam.getValue() : null);
        }
        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        return SQLUtils.format(sql, dbType, parameters, this.statementSqlFormatOption);
    }


    private List<String> getTables(Statement statement) {
        try {
            if (statement instanceof Insert) {
                String name = ((Insert) statement).getTable().getName();
                return Lists.newArrayList(name);
            }

            if (statement instanceof Update) {
                return ((Update) statement).getTables().stream().map(Table::getName).collect(Collectors.toList());
            }

            if (statement instanceof Delete) {
                return ((Delete) statement).getTables().stream().map(Table::getName).collect(Collectors.toList());
            }

            return Lists.newArrayList();
        } catch (Exception e) {
            log.error("获取操作库表错误..{}", JSONObject.toJSONString(statement), e);
        }

        return Lists.newArrayList();
    }

    private List<Map<String, Object>> parameterMapping(Statement statement,
                                                       StatementProxy statementProxy,
                                                       String table) {

        List<Column> columns = Lists.newArrayList();
        if (statement instanceof Insert) {
            columns = ((Insert) statement).getColumns();
        }

        if (statement instanceof Update) {
            columns = ((Update) statement).getColumns();
        }

        if (statement instanceof Delete) {
            Expression expression = ((Delete) statement).getWhere();
            Optional<Object> optionalXmId = getPrimaryKeyValue(expression, "XM_ID");
            if (optionalXmId == null || !optionalXmId.isPresent()) {
                Optional<String> op = getPriKey(table);
                String column;
                if (!op.isPresent() || StringUtils.isBlank(column = op.get())) {
                    return Lists.newArrayList();
                }

                Optional<Object> optional = getPrimaryKeyValue(expression, column);
                if (optional == null || !optional.isPresent()) {
                    return Lists.newArrayList();
                }

                return Lists.newArrayList(MapHelper.ofLinkedMap("XM_ID",
                    queryProjectId(statementProxy, table, column, (String) optional.get())));
            }

            return Lists.newArrayList(MapHelper.ofLinkedMap("XM_ID", optionalXmId.get()));
        }

        List<Map<String, Object>> result = Lists.newArrayList();
        for (int i = 0; i < columns.size(); i++) {
            result.add(MapHelper.ofLinkedMap(columns.get(i).getColumnName(), statementProxy.getParameter(i).getValue()));
        }

        return result;
    }


    private String queryProjectId(StatementProxy statementProxy, String table, String primary, String value) {
        String sql = "SELECT XM_ID FROM " + table + " WHERE " + primary + " = ?";
        List<Map<String, Object>> values = Lists.newArrayList();
        try {
            values = JdbcUtils.executeQuery(statementProxy.getConnection(), sql, Lists.newArrayList(value));
        } catch (SQLException e) {
            log.warn("查询项目id出错..{}", sql);
        }

        return values.isEmpty() ? "" : String.valueOf(values.get(0).get("XM_ID"));
    }


    /**
     * 目前支持 INSERT/DELETE/UPDATE 的SQL操作记录，到时候哪个不需要就删除掉哪个吧
     */
    enum OperationEnum {
        /**
         * 删除
         */
        DELETE("DELETE"),
        /**
         * 新增
         */
        INSERT("INSERT"),
        /**
         * 更新
         */
        UPDATE("UPDATE");

        private String value;


        OperationEnum(String value) {
            this.value = value;
        }

        public static OperationEnum getOperationType(String sql) {
            OperationEnum[] enums = OperationEnum.values();
            for (OperationEnum anEnum : enums) {
                if (StringUtils.startsWithIgnoreCase(sql, anEnum.value)) {
                    return anEnum;
                }
            }
            return null;
        }
    }
}
