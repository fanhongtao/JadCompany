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
 * @author Fan Hongtao &ltfanhongtao@gmail.com&gt
 * @created 2013-07-14
 */
public class StringBuilderFormatter implements IFormatter {

    @Override
    public boolean needFormat(List<String> selectedText) {
        String firstLine = selectedText.get(0);
        return (firstLine.trim().contains("(new StringBuilder("));
    }

    @Override
    public void format(VirtualEditor editor, List<String> selectedText) throws Exception {
        StringBuilder sb = new StringBuilder(selectedText.get(0));
        for (int i = 1, n = selectedText.size(); i < n; i++) {
            sb.append(selectedText.get(i).trim());
        }
        String text = sb.toString();

        // For strings like:
        //   (new StringBuilder()).append(bd.hN().gf()).append(c1.li()).toString();
        text = StringUtils.replace(text, "(new StringBuilder()).append(", "");
        text = StringUtils.replace(text, ").append(", " + ");
        text = StringUtils.replace(text, ").toString()", "");

        // For strings like:
        //    (new StringBuilder("onNotifyChange ")).append(s1).toString();
        String x = "(new StringBuilder(";
        int index = text.indexOf(x);
        if (index != -1) {
            String tmp = text.substring(0, index);
            text = tmp + text.substring(index + x.length()).replaceFirst("\"\\)", "\"");
        }

        editor.replaceSelectedText(text);
    }

}
