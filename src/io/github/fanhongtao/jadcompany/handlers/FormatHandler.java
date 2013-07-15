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

import io.github.fanhongtao.jadcompany.format.IFormatter;
import io.github.fanhongtao.jadcompany.format.IntentFormatter;
import io.github.fanhongtao.jadcompany.format.StringBuilderFormatter;
import io.github.fanhongtao.jadcompany.format.SwitchFormatter;
import io.github.fanhongtao.jadcompany.ui.VirtualEditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

public class FormatHandler extends AbstractHandler {

    private List<IFormatter> formatterList;

    public FormatHandler() {
        formatterList = new ArrayList<IFormatter>();
        formatterList.add(new StringBuilderFormatter());
        formatterList.add(new SwitchFormatter());
        formatterList.add(new IntentFormatter());
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        IEditorPart editorPart = window.getActivePage().getActiveEditor();
        if (editorPart != null && (editorPart instanceof ITextEditor)) {
            VirtualEditor editor = new VirtualEditor((ITextEditor) editorPart);
            List<String> selectedText = editor.getSelectedTextInList();

            for (IFormatter formatter : formatterList) {
                if (formatter.needFormat(selectedText)) {
                    formatter.format(editor, selectedText);
                    break;
                }
            }
        }

        return null;
    }
}
