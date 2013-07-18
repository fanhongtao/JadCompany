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
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
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
     * @throws IOException 
     */
    public List<String> getSelectedTextInList() throws IOException {
        List<String> list = new ArrayList<String>();
        String selectedText = getSelectedText();
        if (selectedText != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
                    selectedText.getBytes())));
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
        }
        return list;
    }

    public void replaceSelectedText(String replaceText) throws BadLocationException {
        TextSelection selection = (TextSelection) getSelection();
        AbstractTextEditor textEditor = (AbstractTextEditor) editor;
        IDocument document = textEditor.getDocumentProvider().getDocument(editor.getEditorInput());
        document.replace(selection.getOffset(), selection.getLength(), replaceText);
    }

    public IDocument getCurrentDocument() {
        IDocumentProvider dp = editor.getDocumentProvider();
        return dp.getDocument(editor.getEditorInput());
    }

    public void selectCurrentFile() {
        IDocument doc = getCurrentDocument();
        TextSelection newSelection = new TextSelection(doc, 0, doc.getLength());
        ISelectionProvider selectionProvider = editor.getSelectionProvider();
        selectionProvider.setSelection(newSelection);
    }

    public void selectCurrentLine() throws BadLocationException {
        ITextSelection oldSelection = getSelection();
        IDocument doc = getCurrentDocument();
        IRegion line = doc.getLineInformation(oldSelection.getStartLine());

        TextSelection newSelection = new TextSelection(doc, line.getOffset(), line.getLength());
        ISelectionProvider selectionProvider = editor.getSelectionProvider();
        selectionProvider.setSelection(newSelection);
    }

    /**
     * Select current line if nothing is selected
     * @throws BadLocationException
     */
    public void selectCurrentLineIfNoSelection() throws BadLocationException {
        ITextSelection selection = getSelection();
        if (selection.getLength() == 0) {
            selectCurrentLine();
        }
    }
}
