package com.soft.book.entity;

import org.apache.ibatis.type.BigDecimalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 保留两位小数
 */
@MappedJdbcTypes(JdbcType.DECIMAL)
public class TypeHandle extends BigDecimalTypeHandler {

    @Override
    public BigDecimal getNullableResult(ResultSet rs, String columnName) throws SQLException {
        BigDecimal result;
        if ((result = rs.getBigDecimal(columnName)) == null) {
            result = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP);
        }

        return result.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        BigDecimal result;
        if ((result = rs.getBigDecimal(columnIndex)) == null) {
            result = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP);
        }

        return result.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        BigDecimal result;
        if ((result = cs.getBigDecimal(columnIndex)) == null) {
            result = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP);
        }

        return result.setScale(2, RoundingMode.HALF_UP);
    }
}
