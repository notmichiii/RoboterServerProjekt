package com.server.data;

import com.server.jooq.Tables;
import com.server.jooq.tables.Basis;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.List;

public class DatabaseManager {
    private final DSLContext dsl;

    public DatabaseManager(String address, String dbName, String username, String password) {
        this("jdbc:mariadb://" + address + "/" + dbName, username, password);
    }

    public DatabaseManager(String url, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        HikariDataSource ds = new HikariDataSource(config);
        dsl = DSL.using(ds, SQLDialect.MARIADB);
    }

    public DatabaseManager(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<Basis> getBase() {
        return dsl.selectFrom(Tables.BASIS).fetchInto(Basis.class);
    }
}
