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

package org.apache.shardingsphere.encrypt.merge.dql.fixture;

import org.apache.shardingsphere.encrypt.merge.dal.show.EncryptShowColumnsMergedResult;
import org.apache.shardingsphere.encrypt.rule.EncryptRule;
import org.apache.shardingsphere.infra.binder.statement.SQLStatementContext;

public final class EncryptColumnsMergedResultFixture extends EncryptShowColumnsMergedResult {
    
    public EncryptColumnsMergedResultFixture(final SQLStatementContext sqlStatementContext, final EncryptRule encryptRule) {
        super(sqlStatementContext, encryptRule);
    }
    
    @Override
    public boolean nextValue() {
        return false;
    }
    
    @Override
    public Object getOriginalValue(final int columnIndex, final Class<?> type) {
        return null;
    }
    
    @Override
    public boolean wasNull() {
        return false;
    }
}
