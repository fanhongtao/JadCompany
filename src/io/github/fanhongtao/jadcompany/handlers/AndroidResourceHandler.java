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
package io.github.fanhongtao.jadcompany.handlers;

import io.github.fanhongtao.jadcompany.ui.VirtualEditor;
import io.github.fanhongtao.jadcompany.utils.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Replace number(s) in current file to Android's resource id. <br>
 * <br>
 * <b>Before replace:</b><br>
 * <code> textView = (TextView)findViewById(0x7f0c0001);</code><br>
 * <br>
 * <b>After replace:</b><br>
 * <code> textView = (TextView)findViewById(R.id.password);</code><br>
 * @author Fan Hongtao &ltfanhongtao@gmail.com&gt
 * @created 2013-07-18
 */
public class AndroidResourceHandler extends AbstractHandler {

    public AndroidResourceHandler() {
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        IEditorPart editorPart = window.getActivePage().getActiveEditor();

        try {
            if (editorPart != null && (editorPart instanceof ITextEditor)) {
                VirtualEditor editor = new VirtualEditor((ITextEditor) editorPart);
                editor.selectCurrentFile();
                List<String> selectedText = editor.getSelectedTextInList();
                replaceResource(editor, selectedText);
            }
        } catch (Exception e) {
            throw new ExecutionException("Failed to replace Android.", e);
        }

        return null;
    }

    private void replaceResource(VirtualEditor editor, List<String> selectedText) throws Exception {
        File file = new File("D:/public.xml");
        if (!file.exists()) {
            return;
        }

        // Read resource from XML
        Map<String, String> map = new HashMap<String, String>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        Element element = document.getDocumentElement();
        NodeList nodeList = element.getElementsByTagName("public");
        if ((nodeList == null) || (nodeList.getLength() == 0)) {
            return;
        }

        for (int i = 0, n = nodeList.getLength(); i < n; i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) node;
                String type = e.getAttribute("type");
                String name = e.getAttribute("name");
                String id = e.getAttribute("id");
                int idDec = Integer.parseInt(id.substring(2), 16);
                // System.out.println("type: " + type + ", name: " + name + ", id: " + id);

                String value = "R." + type + "." + name;
                map.put(id, value);
                map.put("" + idDec, value);
            }
        }

        // change
        StringBuilder sb = new StringBuilder();
        for (String line : selectedText) {
            Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> entry = iterator.next();
                String id = entry.getKey();
                String res = entry.getValue();
                // System.out.println("id: " + id + ", res: " + res);
                line = StringUtils.replace(line, id, res);
            }
            sb.append(line).append(StringUtils.CRLF);
        }

        editor.replaceSelectedText(sb.toString());
    }
}
