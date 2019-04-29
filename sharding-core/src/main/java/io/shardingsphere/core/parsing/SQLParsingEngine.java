/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.core.parsing;

import com.google.common.base.Optional;
import io.shardingsphere.core.constant.DatabaseType;
import io.shardingsphere.core.metadata.table.ShardingTableMetaData;
import io.shardingsphere.core.parsing.cache.ParsingResultCache;
import io.shardingsphere.core.parsing.lexer.LexerEngine;
import io.shardingsphere.core.parsing.lexer.LexerEngineFactory;
import io.shardingsphere.core.parsing.parser.sql.SQLParserFactory;
import io.shardingsphere.core.parsing.parser.sql.SQLStatement;
import io.shardingsphere.core.rule.ShardingRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.shardingsphere.core.parsing.parser.sql.SQLParser;

import static jdk.nashorn.internal.objects.NativeMath.log;

/**
 * SQL parsing engine.
 *
 * @author zhangliang
 */
@RequiredArgsConstructor
@Slf4j(topic = "developer-debug")
public final class SQLParsingEngine {
    
    private final DatabaseType dbType;
    
    private final String sql;
    
    private final ShardingRule shardingRule;
    
    private final ShardingTableMetaData shardingTableMetaData;
    
    /**
     * Parse SQL.
     * 
     * @param useCache use cache or not
     * @return parsed SQL statement
     */
    public SQLStatement parse(final boolean useCache) {
        Optional<SQLStatement> cachedSQLStatement = getSQLStatementFromCache(useCache);
        if (cachedSQLStatement.isPresent()) {
            return cachedSQLStatement.get();
        }
        LexerEngine lexerEngine = LexerEngineFactory.newInstance(dbType, sql);
        lexerEngine.nextToken();
        SQLParser sqlParser = SQLParserFactory.newInstance(dbType, lexerEngine.getCurrentToken().getType(), shardingRule, lexerEngine, shardingTableMetaData);
		SQLStatement result = sqlParser.parse();
        if (useCache) {
            ParsingResultCache.getInstance().put(sql, result);
        }
        log("SQLParsingEngine: {}", result.getClass().getSimpleName());
        return result;
    }
    
    private Optional<SQLStatement> getSQLStatementFromCache(final boolean useCache) {
        return useCache ? Optional.fromNullable(ParsingResultCache.getInstance().getSQLStatement(sql)) : Optional.<SQLStatement>absent();
    }
}
