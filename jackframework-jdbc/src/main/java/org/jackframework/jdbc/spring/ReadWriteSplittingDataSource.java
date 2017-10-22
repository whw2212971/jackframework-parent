package org.jackframework.jdbc.spring;

import org.jackframework.common.CaptainTools;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class ReadWriteSplittingDataSource implements DataSource, InitializingBean {

    protected DataSource readDataSource;

    protected DataSource writeDataSource;

    protected ReadWriteSplittingTransactionManager transactionManager;

    @Override
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getDataSource().getConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getDataSource().isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getDataSource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        getDataSource().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        getDataSource().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getDataSource().getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return getDataSource().getParentLogger();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CaptainTools.assertNotNull(readDataSource, "The property 'readDataSource' is required.");
        CaptainTools.assertNotNull(writeDataSource, "The property 'writeDataSource' is required.");
        CaptainTools.assertNotNull(transactionManager, "The property 'transactionManager' is required.");
    }

    protected DataSource getDataSource() {
        return transactionManager.hasWritableTransaction() ? writeDataSource : readDataSource;
    }

    public DataSource getReadDataSource() {
        return readDataSource;
    }

    public void setReadDataSource(DataSource readDataSource) {
        this.readDataSource = readDataSource;
    }

    public DataSource getWriteDataSource() {
        return writeDataSource;
    }

    public void setWriteDataSource(DataSource writeDataSource) {
        this.writeDataSource = writeDataSource;
    }

    public ReadWriteSplittingTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(ReadWriteSplittingTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

}
