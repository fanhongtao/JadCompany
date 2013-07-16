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

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CommentHandler extends AbstractHandler {
    /**
     * The constructor.
     */
    public CommentHandler() {
    }

    /**
     * the command has been executed, so extract extract the needed information
     * from the application context.
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        IEditorPart editorPart = window.getActivePage().getActiveEditor();
        try {
            if (editorPart != null && (editorPart instanceof ITextEditor)) {
                VirtualEditor editor = new VirtualEditor((ITextEditor) editorPart);
                editor.selectCurrentLineIfNoSelection();
                List<String> lines = editor.getSelectedTextInList();

                StringBuilder sb = new StringBuilder();
                for (int index = 0; index < lines.size(); index++) {
                    if (index != 0) {
                        sb.append(StringUtils.CRLF);
                    }

                    String line = lines.get(index);
                    sb.append(line);

                    String tmp = line.trim();
                    if (tmp.isEmpty()) {
                        continue;
                    }

                    tmp = StringUtils.stringBefore(tmp, " = ");
                    tmp = StringUtils.stringBefore(tmp, " extends ");
                    tmp = StringUtils.stringBefore(tmp, " implements ");
                    tmp = StringUtils.stringBefore(tmp, " {");
                    tmp = StringUtils.stringBefore(tmp, "(");
                    tmp = StringUtils.stringBefore(tmp, ";");

                    String tokens[] = tmp.split("\\b");
                    sb.append(" // ").append(tokens[tokens.length - 1]);
                }

                editor.replaceSelectedText(sb.toString());
            }
        } catch (Exception e) {
            throw new ExecutionException("Failed to comment ID(s)", e);
        }

        return null;
    }
}
