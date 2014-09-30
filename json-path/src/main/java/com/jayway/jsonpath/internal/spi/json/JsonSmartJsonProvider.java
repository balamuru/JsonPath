/*
 * Copyright 2011 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jayway.jsonpath.internal.spi.json;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.spi.json.Mode;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.mapper.AMapper;
import net.minidev.json.mapper.DefaultMapperOrdered;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class JsonSmartJsonProvider extends AbstractJsonProvider {

    private final Mode mode;

    private final static AMapper<?> orderedMapper = DefaultMapperOrdered.DEFAULT;

    public JsonSmartJsonProvider() {
        this(Mode.SLACK);
    }

    public JsonSmartJsonProvider(Mode mode) {
        this.mode = mode;
    }

    public Object createArray() {
        return orderedMapper.createArray();
    }

    public Object createMap() {
        return orderedMapper.createObject();
    }

    public Object parse(String json) {
        try {
            return createParser().parse(json, orderedMapper);
        } catch (ParseException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {
        try {
            return createParser().parse(new InputStreamReader(jsonStream, charset), orderedMapper);
        } catch (ParseException e) {
            throw new InvalidJsonException(e);
        } catch (UnsupportedEncodingException e) {
            throw new JsonPathException(e);
        }
    }

    @Override
    public String toJson(Object obj) {

        if (obj instanceof Map) {
            return JSONObject.toJSONString((Map<String, ?>) obj);
        } else if (obj instanceof List) {
            return JSONArray.toJSONString((List<?>) obj);
        } else {
            throw new UnsupportedOperationException(obj.getClass().getName() + " can not be converted to JSON");
        }
    }

    private JSONParser createParser() {
        return new JSONParser(mode.intValue());
    }
}
