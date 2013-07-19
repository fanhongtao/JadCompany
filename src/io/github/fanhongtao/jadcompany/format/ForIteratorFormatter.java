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
package io.github.fanhongtao.jadcompany.format;

import io.github.fanhongtao.jadcompany.ui.VirtualEditor;
import io.github.fanhongtao.jadcompany.utils.StringUtils;

import java.util.List;

/**
 * Format for-iterator to iterator-while. <br>
 * With or without the definition statement.
 * <br>
 * <b>Before format:</b>
 * <pre>
 *  String s1;
 *  for(Iterator iterator = list.iterator(); iterator.hasNext(); foo(s1))
 *      s1 = (String)iterator.next();
 * </pre>
 * <b>After format:</b>
 * <pre>
 *  Iterator iterator = list.iterator();
 *  while(iterator.hasNext()) {
 *      String s1 = (String)iterator.next();
 *      foo(s1);
 *  }
 * </pre>
 * @author Fan Hongtao &ltfanhongtao@gmail.com&gt
 * @created 2013-07-19
 */
public class ForIteratorFormatter implements IFormatter {

    @Override
    public boolean needFormat(List<String> selectedText) {
        if (selectedText == null || (selectedText.size() != 2 && selectedText.size() != 3)) {
            return false;
        }

        int indexFor = (selectedText.size() == 2) ? 0 : 1;
        String lineFor = selectedText.get(indexFor).trim();
        return lineFor.startsWith("for(Iterator ") && (lineFor.split(";").length == 3);
    }

    @Override
    public void format(VirtualEditor editor, List<String> selectedText) throws Exception {
        int indexFor = (selectedText.size() == 2) ? 0 : 1;
        String lineFor = selectedText.get(indexFor).trim();
        String as[] = lineFor.split(";");
        String prefix = "    ";

        StringBuilder sb = new StringBuilder();
        sb.append(as[0].substring(4)).append(";").append(StringUtils.CRLF);
        sb.append("while (").append(as[1]).append(") {").append(StringUtils.CRLF);

        sb.append(prefix);
        if (indexFor == 1) {
            String lineDefine = selectedText.get(0).trim();
            String type = lineDefine.split("[ \t]+")[0];
            sb.append(type).append(" ");
        }
        sb.append(selectedText.get(indexFor + 1).trim()).append(StringUtils.CRLF);

        sb.append(prefix).append(as[2].substring(0, as[2].length() - 1)).append(";").append(StringUtils.CRLF);
        sb.append("}").append(StringUtils.CRLF);

        editor.replaceSelectedText(sb.toString());
    }

}
