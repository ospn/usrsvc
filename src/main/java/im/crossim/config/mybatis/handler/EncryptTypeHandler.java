package im.crossim.config.mybatis.handler;

import im.crossim.UsrSvcApplication;
import im.crossim.crypto.service.CryptoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class EncryptTypeHandler implements TypeHandler<String> {

    private CryptoService getCryptoService() {
        return UsrSvcApplication.ac.getBean(CryptoService.class);
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        CryptoService cryptoService = getCryptoService();

        try {
            String encrypted = cryptoService.encrypt(parameter);
            ps.setString(i, encrypted);
        } catch (Exception ex) {
            log.error(
                    String.format(
                            "加密字段时发生异常：%s",
                            parameter
                    ),
                    ex
            );

            throw ex;
        }
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        CryptoService cryptoService = getCryptoService();

        String result = rs.getString(columnName);
        try {
            return cryptoService.decrypt(result);
        } catch (Exception ex) {
            log.error(
                    String.format(
                            "解密字段时发生异常：%s",
                            result
                    ),
                    ex
            );

            throw ex;
        }
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        CryptoService cryptoService = getCryptoService();

        String result = rs.getString(columnIndex);
        try {
            return cryptoService.decrypt(result);
        } catch (Exception ex) {
            log.error(
                    String.format(
                            "解密字段时发生异常：%s",
                            result
                    ),
                    ex
            );

            throw ex;
        }
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        CryptoService cryptoService = getCryptoService();

        String result = cs.getString(columnIndex);
        try {
            return cryptoService.decrypt(result);
        } catch (Exception ex) {
            log.error(
                    String.format(
                            "解密字段时发生异常：%s",
                            result
                    ),
                    ex
            );

            throw ex;
        }
    }

}
