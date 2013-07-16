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

import java.util.List;

/**
 * Format code for Android Intent.<br>
 * <br>
 * new Intent(this, com/foo/Class1); --> new Intent(this, Class1.class); <br>
 * intent.setClass(this, com/foo/Class1);  --> intent.setClass(this, Class1.class); <br>
 * (new Intent()).setClass(this, com/foo/Class1);  --> (new Intent()).setClass(this, Class1.class); <br>
 * 
 * @author Fan Hongtao &ltfanhongtao@gmail.com&gt
 * @created 2013-07-16
 */
public class IntentFormatter implements IFormatter {

    private static final String NEW_INTENT = "new Intent(";
    private static final String SET_CLASS = ".setClass(";

    @Override
    public boolean needFormat(List<String> selectedText) {
        if (selectedText == null || selectedText.size() != 1) { // only support one line
            return false;
        }

        String firstLine = selectedText.get(0);
        return (firstLine.contains(NEW_INTENT) || firstLine.contains(SET_CLASS));
    }

    @Override
    public void format(VirtualEditor editor, List<String> selectedText) throws Exception {
        String firstLine = selectedText.get(0);

        int index1 = -1;
        if (firstLine.contains(SET_CLASS)) {
            index1 = firstLine.indexOf(SET_CLASS);
            index1 = firstLine.indexOf(",", index1);
        } else if (firstLine.contains(NEW_INTENT)) {
            index1 = firstLine.indexOf(NEW_INTENT);
            index1 = firstLine.indexOf(",", index1);
        }
        if (index1 == -1) {
            return;
        }

        int index2 = firstLine.indexOf(")", index1);
        if (index2 == -1) {
            return;
        }

        String tmp = firstLine.substring(index1 + 1, index2);
        int index3 = tmp.lastIndexOf("/");
        if (index3 == -1) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(firstLine.subSequence(0, index1 + 1));
        sb.append(" ");
        sb.append(tmp.substring(index3 + 1));
        sb.append(".class");
        sb.append(firstLine.substring(index2));

        editor.replaceSelectedText(sb.toString());
    }
}
