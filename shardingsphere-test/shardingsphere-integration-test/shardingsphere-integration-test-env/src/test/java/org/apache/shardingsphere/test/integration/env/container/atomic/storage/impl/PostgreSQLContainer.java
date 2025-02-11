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

package org.apache.shardingsphere.test.integration.env.container.atomic.storage.impl;

import org.apache.shardingsphere.infra.database.type.DatabaseTypeFactory;
import org.apache.shardingsphere.test.integration.env.container.atomic.storage.DockerStorageContainer;
import org.testcontainers.containers.BindMode;

import java.util.Optional;

/**
 * PostgreSQL container.
 */
public final class PostgreSQLContainer extends DockerStorageContainer {
    
    public PostgreSQLContainer(final String scenario) {
        super(DatabaseTypeFactory.getInstance("PostgreSQL"), "postgres:12-alpine", scenario);
    }
    
    public PostgreSQLContainer(final String scenario, final String dockerImageName) {
        super(DatabaseTypeFactory.getInstance("PostgreSQL"), dockerImageName, scenario);
    }
    
    @Override
    protected void configure() {
        withCommand("--max_connections=600", "--wal_level=logical");
        addEnv("POSTGRES_USER", getRootUsername());
        addEnv("POSTGRES_PASSWORD", getRootPassword());
        withClasspathResourceMapping("/env/postgresql/postgresql.conf", "/etc/postgresql/postgresql.conf", BindMode.READ_ONLY);
        super.configure();
    }
    
    @Override
    public String getRootUsername() {
        return "root";
    }
    
    @Override
    public String getRootPassword() {
        return "root";
    }
    
    @Override
    public String getTestCaseUsername() {
        return "scaling";
    }
    
    @Override
    public String getTestCasePassword() {
        return "root";
    }
    
    @Override
    public int getPort() {
        return 5432;
    }
    
    @Override
    protected Optional<String> getDefaultDatabaseName() {
        return Optional.of("postgres");
    }
}
