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
package io.github.fanhongtao.jadcompany.ui;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

/** 
 * @author Fan Hongtao &ltfanhongtao@gmail.com&gt
 * @created 2013-07-11
 */
public class VirtualEditor {
    private ITextEditor editor;

    public VirtualEditor(ITextEditor editor) {
        super();
        this.editor = editor;
    }

    public ITextSelection getSelection() {
        ISelectionProvider selectionProvider = editor.getSelectionProvider();
        if (selectionProvider == null) {
            return null;
        }

        ISelection selection = selectionProvider.getSelection();
        return (ITextSelection) selection;
    }

    public String getSelectedText() {
        ITextSelection selection = getSelection();
        if (selection == null) {
            return null;
        }
        String selectedText = selection.getText();
        if (selectedText != null && selectedText.length() > 0) {
            return selectedText;
        }
        return null;
    }

    /**
     * Get selected contents of current editor.
     * @return A list of strings. One for each line.
     */
    public List<String> getSelectionTextInList() {
        List<String> list = new ArrayList<String>();
        String selectedText = getSelectedText();
        if (selectedText != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
                        selectedText.getBytes())));
                String line = reader.readLine();
                while (line != null) {
                    list.add(line);
                    line = reader.readLine();
                }
            } catch (IOException ex) {
            }
        }
        return list;
    }

    public void replaceSelectedText(String replaceText) {
        TextSelection selection = (TextSelection) getSelection();
        AbstractTextEditor textEditor = (AbstractTextEditor) editor;
        IDocument document = textEditor.getDocumentProvider().getDocument(editor.getEditorInput());
        try {
            document.replace(selection.getOffset(), selection.getLength(), replaceText);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
