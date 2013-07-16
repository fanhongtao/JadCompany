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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/** 
 * @author Fan Hongtao &ltfanhongtao@gmail.com&gt
 * @created 2013-07-14
 */
public class SwitchFormatter implements IFormatter {

    @Override
    public boolean needFormat(List<String> selectedText) {
        if (selectedText == null || selectedText.size() < 4) {
            return false;
        }

        String secondLine = selectedText.get(1).trim();
        return secondLine.startsWith("JVM INSTR tableswitch ") || secondLine.startsWith("JVM INSTR lookupswitch ");
    }

    @Override
    public void format(VirtualEditor editor, List<String> selectedText) {
        List<String> lines = new ArrayList<String>(selectedText);
        String var = lines.remove(0).trim().replace(";", "");
        int labelIndex = -1;
        for (int i = 1, n = lines.size(); i < n; i++) {
            if (lines.get(i).trim().startsWith("goto ")) {
                labelIndex = i;
                break;
            }
        }
        if (labelIndex == -1) {
            return;
        }

        boolean hasDetail = (labelIndex < lines.size() - 1);
        String labelLine = lines.get(labelIndex);
        labelLine = labelLine.substring(labelLine.indexOf('_')).trim();
        String labels[] = labelLine.split("[ \t]+");
        Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
        for (int i = 1; i < labelIndex; i++) {
            String tmp = lines.get(i).trim();
            String caseValue = tmp.split("[ \t]+")[1];
            String label = labels[i];
            add(map, caseValue, label);
        }
        add(map, "default", labels[0]);

        StringBuilder sb = new StringBuilder();
        sb.append("switch (").append(var).append(") {").append(StringUtils.CRLF);
        Iterator<Entry<String, List<String>>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, List<String>> entry = iter.next();
            List<String> list = entry.getValue();
            for (String caseValue : list) {
                if (!caseValue.equals("default")) {
                    sb.append("case ");
                }
                sb.append(caseValue);
                if (!caseValue.endsWith(":")) {
                    sb.append(':');
                }
                sb.append(StringUtils.CRLF);
            }

            if (!hasDetail) {
                sb.append(entry.getKey()).append(StringUtils.CRLF);
            } else {
                String labelValue = entry.getKey() + ":";
                boolean find = false;
                for (int i = labelIndex + 1, n = lines.size(); i < n; i++) {
                    String line = lines.get(i).trim();
                    if (!find && line.endsWith(labelValue)) {
                        find = true;
                    } else if (find) {
                        if (line.startsWith("return;") || line.startsWith("continue;") || line.startsWith("goto ")
                                || line.startsWith("if (true)")) {
                            break;
                        }
                        if (line.matches("_L\\d{1,}:")) {
                            break;
                        }
                        sb.append(line).append(StringUtils.CRLF);
                    }
                }
            }
            sb.append("break;").append(StringUtils.CRLF);
        }
        sb.append("}").append(StringUtils.CRLF);

        editor.replaceSelectedText(sb.toString());
    }

    private void add(Map<String, List<String>> map, String caseValue, String label) {
        List<String> list = map.get(label);
        if (list == null) {
            list = new ArrayList<String>();
            map.put(label, list);
        }
        list.add(caseValue);
    }
}
