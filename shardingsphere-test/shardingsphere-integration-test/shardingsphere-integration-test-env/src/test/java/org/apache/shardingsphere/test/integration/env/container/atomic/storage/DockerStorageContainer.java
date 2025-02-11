/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.test.integration.env.container.atomic.storage;

import com.google.common.base.Strings;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.shardingsphere.infra.database.type.DatabaseType;
import org.apache.shardingsphere.test.integration.env.container.atomic.DockerITContainer;
import org.apache.shardingsphere.test.integration.env.container.wait.JDBCConnectionWaitStrategy;
import org.apache.shardingsphere.test.integration.env.runtime.DataSourceEnvironment;
import org.apache.shardingsphere.test.integration.env.runtime.scenario.database.DatabaseEnvironmentManager;
import org.apache.shardingsphere.test.integration.env.runtime.scenario.path.ScenarioDataPath;
import org.apache.shardingsphere.test.integration.env.runtime.scenario.path.ScenarioDataPath.Type;
import org.testcontainers.containers.BindMode;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.sql.DriverManager;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Docker storage container.
 */
public abstract class DockerStorageContainer extends DockerITContainer implements StorageContainer {
    
    private final DatabaseType databaseType;
    
    private final String scenario;
    
    @Getter
    private final Map<String, DataSource> actualDataSourceMap;
    
    @Getter
    private final Map<String, DataSource> expectedDataSourceMap;
    
    public DockerStorageContainer(final DatabaseType databaseType, final String dockerImageName, final String scenario) {
        super(databaseType.getType().toLowerCase(), dockerImageName);
        this.databaseType = databaseType;
        this.scenario = scenario;
        actualDataSourceMap = new LinkedHashMap<>();
        expectedDataSourceMap = new LinkedHashMap<>();
    }
    
    @Override
    protected void configure() {
        if (Strings.isNullOrEmpty(scenario)) {
            withClasspathResourceMapping("/env/" + databaseType.getType().toLowerCase() + "/initdb.sql", "/docker-entrypoint-initdb.d/", BindMode.READ_ONLY);
        } else {
            withClasspathResourceMapping(new ScenarioDataPath(scenario).getInitSQLResourcePath(Type.ACTUAL, databaseType), "/docker-entrypoint-initdb.d/", BindMode.READ_ONLY);
            withClasspathResourceMapping(new ScenarioDataPath(scenario).getInitSQLResourcePath(Type.EXPECTED, databaseType), "/docker-entrypoint-initdb.d/", BindMode.READ_ONLY);
        }
        withExposedPorts(getPort());
        setWaitStrategy(new JDBCConnectionWaitStrategy(
                () -> DriverManager.getConnection(getDefaultDatabaseName().isPresent()
                        ? DataSourceEnvironment.getURL(databaseType, "localhost", getFirstMappedPort(), getDefaultDatabaseName().get())
                        : DataSourceEnvironment.getURL(databaseType, "localhost", getFirstMappedPort()),
                        getRootUsername(), getRootPassword())));
    }
    
    @Override
    @SneakyThrows({IOException.class, JAXBException.class})
    protected void postStart() {
        DatabaseEnvironmentManager.getDatabaseNames(scenario).forEach(each -> actualDataSourceMap.put(each, createDataSource(each)));
        DatabaseEnvironmentManager.getExpectedDatabaseNames(scenario).forEach(each -> expectedDataSourceMap.put(each, createDataSource(each)));
    }
    
    private DataSource createDataSource(final String dataSourceName) {
        HikariDataSource result = new HikariDataSource();
        result.setDriverClassName(DataSourceEnvironment.getDriverClassName(databaseType));
        result.setJdbcUrl(DataSourceEnvironment.getURL(databaseType, getHost(), getMappedPort(getPort()), dataSourceName));
        result.setUsername(getRootUsername());
        result.setPassword(getRootPassword());
        result.setMaximumPoolSize(4);
        result.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        return result;
    }
    
    /**
     * Get JDBC URL.
     *
     * @param databaseName database name
     * @return JDBC URL
     */
    public final String getJdbcUrl(final String databaseName) {
        return DataSourceEnvironment.getURL(databaseType, getHost(), getFirstMappedPort(), databaseName);
    }
    
    /**
     * Get root username.
     *
     * @return root username
     */
    public abstract String getRootUsername();
    
    /**
     * Get root password.
     *
     * @return root password
     */
    public abstract String getRootPassword();
    
    /**
     * Get test case username.
     *
     * @return root username
     */
    public abstract String getTestCaseUsername();
    
    /**
     * Get test case password.
     *
     * @return root username
     */
    public abstract String getTestCasePassword();
    
    /**
     * Get database port.
     *
     * @return database port
     */
    public abstract int getPort();
    
    /**
     * Get default database name.
     * 
     * @return default database name
     */
    protected abstract Optional<String> getDefaultDatabaseName();
    
    @Override
    public final String getAbbreviation() {
        return databaseType.getType().toLowerCase();
    }
}
