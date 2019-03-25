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

package io.shardingsphere.spi.parsing;

import io.shardingsphere.spi.NewInstanceServiceLoader;

import java.util.Collection;

/**
 * Parsing hook for SPI.
 *
 * @author zhangliang
 */
public final class SPIParsingHook implements ParsingHook {
    
    private static final NewInstanceServiceLoader<ParsingHook> SERVICE_LOADER = NewInstanceServiceLoader.load(ParsingHook.class);
    
    private final Collection<ParsingHook> parsingHooks = SERVICE_LOADER.newServiceInstances();
    
    @Override
    public void start(final String sql) {
        for (ParsingHook each : parsingHooks) {
            each.start(sql);
        }
    }
    
    @Override
    public void finishSuccess() {
        for (ParsingHook each : parsingHooks) {
            each.finishSuccess();
        }
    }
    
    @Override
    public void finishFailure(final Exception cause) {
        for (ParsingHook each : parsingHooks) {
            each.finishFailure(cause);
        }
    }
}
