/*******************************************************************************
 * Copyright (c) 2010-2016, Gyorgy Gerencser, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gyorgy Gerencser - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.querybyexample.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.ISourceProviderService;
import org.eclipse.viatra.addon.querybyexample.ui.handlers.QBEViewMainSourceProvider;
import org.eclipse.viatra.addon.querybyexample.ui.ui.QBEView;

public class QBEViewUtils {

    private static IFile linkedFile;
    private static Set<String> reservedWords;
    public static final String RESERVED_WORD_VAR_PREFIX = "^";
    public static final String PLUGIN_ID = "org.eclipse.viatra.addon.querybyexample.ui";

    static {
        reservedWords = new HashSet<String>();
        reservedWords.add("as");
        reservedWords.add("boolean");
        reservedWords.add("byte");
        reservedWords.add("case");
        reservedWords.add("catch");
        reservedWords.add("char");
        reservedWords.add("check");
        reservedWords.add("count");
        reservedWords.add("default");
        reservedWords.add("do");
        reservedWords.add("double");
        reservedWords.add("else");
        reservedWords.add("eval");
        reservedWords.add("extends");
        reservedWords.add("false");
        reservedWords.add("finally");
        reservedWords.add("find");
        reservedWords.add("float");
        reservedWords.add("for");
        reservedWords.add("if");
        reservedWords.add("import");
        reservedWords.add("instanceof");
        reservedWords.add("int");
        reservedWords.add("long");
        reservedWords.add("neg");
        reservedWords.add("new");
        reservedWords.add("null");
        reservedWords.add("or");
        reservedWords.add("package");
        reservedWords.add("pattern");
        reservedWords.add("private");
        reservedWords.add("return");
        reservedWords.add("short");
        reservedWords.add("static");
        reservedWords.add("super");
        reservedWords.add("switch");
        reservedWords.add("synchronized");
        reservedWords.add("this");
        reservedWords.add("throw");
        reservedWords.add("true");
        reservedWords.add("try");
        reservedWords.add("val");
        reservedWords.add("var");
        reservedWords.add("void");
        reservedWords.add("while");
    }

    public static IFile getLinkedFile() {
        return linkedFile;
    }

    public static void setLinkedFile(IFile linkedFile) {
        QBEViewUtils.linkedFile = linkedFile;
    }

    public static QBEViewMainSourceProvider getMainSourceProvider(ExecutionEvent event) {
        ISourceProviderService sourceProviderService = (ISourceProviderService) HandlerUtil
                .getActiveWorkbenchWindow(event).getService(ISourceProviderService.class);
        return (QBEViewMainSourceProvider) sourceProviderService
                .getSourceProvider(QBEViewMainSourceProvider.QBEVIEW_MAIN_SOURCE_PROVIDER_PROVIDED_SOURCE_NAME);
    }

    public static QBEView getQBEView(ExecutionEvent event) {
        IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
        IWorkbenchPage page = workbenchWindow.getActivePage();
        return (QBEView) page.getActivePart();
    }

    public static boolean validatePropertyName(String property) {
        return property.matches("[\\^]?[a-zA-Z0-9_]+");
    }

    public static boolean validateQueryPackageName(String property) {
        return property.matches("[\\^]?[a-zA-Z0-9_\\.]+");
    }

    public static boolean checkVariableNameIsReserved(String variableName) {
        return reservedWords.contains(variableName);
    }
}
