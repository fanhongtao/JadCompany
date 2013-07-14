/*
 * Copyright (C) 2013 Fan Hongtao (http://fanhongtao.github.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.fanhongtao.jadcompany.utils;

/** 
 * @author Fan Hongtao &ltfanhongtao@gmail.com&gt
 * @created 2013-07-12
 */
public class StringUtils {

    public static final String CRLF = "\r\n";

    public static String stringBefore(String string, String x) {
        int idx = string.indexOf(x);
        if (idx != -1) {
            string = string.substring(0, idx);
        }
        return string;
    }

    public static String replace(String string, String search, String replace) {
        int beginIndex = 0;
        int endIndex = 0;
        StringBuilder sb = new StringBuilder();
        while ((endIndex = string.indexOf(search, beginIndex)) != -1) {
            sb.append(string.substring(beginIndex, endIndex));
            sb.append(replace);
            beginIndex = endIndex + search.length();
        }

        sb.append(string.substring(beginIndex));
        return sb.toString();
    }
}
