/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Jan Koehnlein - initial API and implementation in the Xtext project
 *   Adam Lengyel, Zoltan Ujhelyi - adaptation for requirements in VIATRA
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.sirius.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.antlr.runtime.ANTLRStringStream;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.swt.IFocusService;
import org.eclipse.viatra.query.patternlanguage.emf.parser.antlr.internal.InternalEMFPatternLanguageLexer;
import org.eclipse.viatra.query.patternlanguage.emf.sirius.SiriusVQLGraphicalEditorPlugin;
import org.eclipse.viatra.query.patternlanguage.emf.ui.util.JavaProjectClassLoaderProvider;
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.CheckConstraint;
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.FunctionEvaluationValue;
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.InterpretableExpression;
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.JavaClassReference;
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.Reference;
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.Variable;
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.VgqlPackage;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditor;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditorFactory;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditorModelAccess;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.util.LazyStringInputStream;

import com.google.inject.Injector;


/* largely inspired by old "org.eclipse.xtext.gmf.glue" from Xtext examples */
@SuppressWarnings("restriction")
public class XtextEmbeddedEditor {

	private final class CloseEditorHandler extends KeyAdapter implements VerifyKeyListener, ICompletionListener {
        
	    boolean contentAssistStopping = false;
	    
	    @Override
        public void verifyKey(VerifyEvent e) {
        	int keyCode = e.keyCode;
        	if ((e.stateMask & SWT.CTRL) != 0
        			&& ((keyCode == SWT.KEYPAD_CR) || (keyCode == SWT.CR))) {
        		e.doit = false;
        		closeEditor(true);
        	}
        	
        	/*
             * When content assist is stopped with the esc key, in the same run this verify key method is also called.
             * This means, when this method is called with the ESC key pressed, the assistSessionEnded method was
             * already called, thus the content assist overlay is always hidden. For this reason, when the assist
             * session ends, a single press of the ESC key is ignored.
             */
            if (keyCode == SWT.ESC && !contentAssistStopping) {
            	e.doit = false;
            	closeEditor(false);
            }
            contentAssistStopping = false;
        }

        @Override
        public void assistSessionStarted(ContentAssistEvent event) {
            contentAssistStopping = false;
        }

        @Override
        public void assistSessionEnded(ContentAssistEvent event) {
            contentAssistStopping = true;
        }

        @Override
        public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
            
        }
    }

    private static final int MIN_EDITOR_WIDTH = 100;

	private static final int MIN_EDITOR_HEIGHT = 20;

	private final Injector xtextInjector;

	private Point cursorLocation;

	private IGraphicalEditPart hostEditPart;

	private Decorations xtextEditorComposite;
	
	private EmbeddedEditor xtextEmbeddedEditor;

	private EmbeddedEditorModelAccess xtextPartialEditor;

	private InterpretableExpression semanticElement;

	private DDiagramElement originalSemanticElementView;

	private XtextResource virtualXtextResource;

    private final IEditorPart editor;

    private ISelectionProvider oldSelectionProvider;
	
	public XtextEmbeddedEditor(IGraphicalEditPart editPart,
			InterpretableExpression targetSemanticElement, DDiagramElement targetView, Injector xtextInjector, IEditorPart editor) {
        this.editor = editor;
        this.hostEditPart = Objects.requireNonNull(editPart);
		this.semanticElement = Objects.requireNonNull(targetSemanticElement);
		this.originalSemanticElementView = Objects.requireNonNull(targetView);
		this.xtextInjector = xtextInjector;
	}

    public void showEditor() {
        try {
            cursorLocation = ((GraphicalViewer) VGQLEditorUtil.getViewer(originalSemanticElementView)).getControl()
                    .toControl(Display.getDefault().getCursorLocation());

            // Create virtual Xtext resource for the embedded editor
            this.virtualXtextResource = createVirtualXtextResource(semanticElement);
            createXtextEditor();
            oldSelectionProvider = editor.getSite().getSelectionProvider();
            editor.getSite().setSelectionProvider(xtextEmbeddedEditor.getViewer());
        } catch (Exception e) {
            SiriusVQLGraphicalEditorPlugin.logError(e);
        } finally {
            if (hostEditPart != null) {
                hostEditPart.refresh();
            }
        }
    }

	/**
	 * Close this editor and update the underlying model
	 */
	public void closeEditor(boolean updateModel) {
		try {
	    if (xtextPartialEditor != null) {
			if (updateModel) {
			    final TransactionalEditingDomain ed = SessionManager.INSTANCE.getSession(semanticElement).getTransactionalEditingDomain();
			    final Command command = SetCommand.create(ed, semanticElement, VgqlPackage.Literals.INTERPRETABLE_EXPRESSION__EXPRESSION, xtextPartialEditor.getEditablePart());
                if (command.canExecute()) {
                    ed.getCommandStack().execute(command);
                }
			}
			
			if (xtextEditorComposite != null) {
				// The purpose of this code is to ensure that in EmbeddedEditorActions#initialize() method
				//	focusLost(FocusEvent) callback to be called before disposing the editor. Otherwise
				//	tons of "Conflicting handlers ..." message will be got when the embedded editor is opened again,
				//	because the deactivation of	the registered handlers occurs in the focusLost() callback, that
				//	is not called during disposing the editor's view. When one opens an embedded editor again, it will
				//	register its handlers again, and at this point there will be more than one registered handler for
				//	the same actions which causes the above mentioned errors.
				hostEditPart.getViewer().getControl().forceFocus();
				xtextEditorComposite.dispose();
				xtextEditorComposite = null;
			}
			xtextPartialEditor = null;
			virtualXtextResource = null;
		}
		} finally {
		    editor.getSite().setSelectionProvider(oldSelectionProvider);
		    oldSelectionProvider = null;
		}
	}

	private XtextResource createVirtualXtextResource(InterpretableExpression container) throws IOException {
	    URI uri = container.eResource().getURI().trimFileExtension().appendFileExtension("vql");
	    return createVirtualXtextResource(uri, getFullText(container));
	}
	
    private XtextResource createVirtualXtextResource(URI uri, String text) throws IOException {
        JavaProjectClassLoaderProvider provider = xtextInjector.getInstance(JavaProjectClassLoaderProvider.class);
        
        // TODO check whether this handles all cases correctly
        XtextResourceSet rs = (XtextResourceSet)xtextInjector.getInstance(IResourceSetProvider.class).get(provider.getIFile(semanticElement).getProject());
        

        IResourceFactory resourceFactory = xtextInjector.getInstance(IResourceFactory.class);
        // Create virtual resource
        XtextResource resource = (XtextResource) resourceFactory.createResource(URI.createURI(uri.toString()));
        resource.load(new LazyStringInputStream(text == null ? "" : text), null);
        rs.getResources().add(resource);

        return resource;
    }
	
    private String buildParameterDeclaration(Variable v) {
        if (!v.getTypes().isEmpty() && v.getTypes().get(0) instanceof JavaClassReference) {
            return v.getName() + " : java " + escapeTypeName(((JavaClassReference)v.getTypes().get(0)).getClassName());
        } else {
            return "";
        }
    }
    
    private String escapeTypeName(String name) {
        return Arrays.stream(name.split("\\.")).map(n -> {
            InternalEMFPatternLanguageLexer lexer = new InternalEMFPatternLanguageLexer(new ANTLRStringStream(n));
            // Escape VQL keywords, e.g. "java"
            return (lexer.nextToken().getType() == InternalEMFPatternLanguageLexer.T__19) ? "^" + n : n; 
        }).collect(Collectors.joining("."));
    }
    
    private String getPrefixText(InterpretableExpression ex) {
        StringBuilder sb = new StringBuilder();
        sb.append("pattern temporary(");
        sb.append(ex.getVariables().stream()
                .map(Reference::getExpression)
                .filter(Variable.class::isInstance)
                .map(Variable.class::cast)
                .map(this::buildParameterDeclaration)
                .collect(Collectors.joining(", ")));
        sb.append(") {\n");
        if (ex instanceof FunctionEvaluationValue) {
            sb.append(" _ == eval(");
        } else if (ex instanceof CheckConstraint) {
            sb.append("  check(");
        }
        return sb.toString();
    }
    
    private String getPostfixText(InterpretableExpression ex) {
        return ");\n }";
    }
    
    private String getEditableText(InterpretableExpression ex) {
        return ex.getExpression();
    }
    
    private String getFullText(InterpretableExpression ex) {
        return getPrefixText(ex) + '\n' +getEditableText(ex) + '\n' + getPostfixText(ex);
    }
    
	protected void createXtextEditor() {
		GraphicalViewer viewer = (GraphicalViewer) VGQLEditorUtil.getViewer(originalSemanticElementView);
		Composite parentComposite = (Composite) viewer.getControl();
		
		xtextEditorComposite = new Decorations(parentComposite, SWT.RESIZE
				| SWT.ON_TOP | SWT.BORDER | SWT.CLOSE);
		xtextEditorComposite.setLayout(new FillLayout());

		EmbeddedEditorFactory factory = new EmbeddedEditorFactory();
		xtextInjector.injectMembers(factory);
		xtextEmbeddedEditor = factory.newEditor(() -> virtualXtextResource).
		        showErrorAndWarningAnnotations().
		        
		        withParent(xtextEditorComposite);
		
        // When insertLineBreaks is set to true, the resulting editor used some kind of incorrect offset, resulting in
        // strange behavior (e.g. off-by-one deletion, content assist not working, etc.). This is most likely caused by
        // inconsistency of the getFullText() method (used to initialize the contents of the underlying Resource) and
        // the prefix/content/postfix separation (however, this is only a guess here).
        // To avoid that, line breaks are added manually.
        xtextPartialEditor = xtextEmbeddedEditor.createPartialEditor(getPrefixText(semanticElement) + "\n",
                getEditableText(semanticElement), "\n" + getPostfixText(semanticElement), false);
        
		addKeyVerifyListener();
		setEditorBounds();
		
		xtextEmbeddedEditor.getDocument().getValidationJob().schedule();
		xtextEditorComposite.setVisible(true);
		xtextEditorComposite.forceFocus();
	}

	private void addKeyVerifyListener() {
		final StyledText xtextTextWidget = xtextEmbeddedEditor.getViewer()
				.getTextWidget();
		final CloseEditorHandler listener = new CloseEditorHandler();
        xtextTextWidget.addVerifyKeyListener(listener);
        xtextEmbeddedEditor.getViewer().getContentAssistantFacade().addCompletionListener(listener);
	}

	/**
	 * This method calculates and sets the position and size of the opened
	 * 	embedded editor.
	 */
	private void setEditorBounds() {
		// mind the added newlines
		String editString = xtextPartialEditor.getEditablePart();

		int numLines = getNumLines(editString);
		int numColumns = getMaxColumns(editString);

		Font font = hostEditPart.getFigure().getFont();
		FontData fontData = font.getFontData()[0];
		int fontHeightInPixel = fontData.getHeight();

		int width = Math.max(fontHeightInPixel * (numColumns + 3), MIN_EDITOR_WIDTH);
		int height = Math.max(fontHeightInPixel * (numLines + 4), MIN_EDITOR_HEIGHT);
		
		xtextEditorComposite.setBounds(cursorLocation.x, cursorLocation.y, width + 250, height + 50);
	}
	
	private int getNumLines(String s) {
		int numLines = 1;
		for (char c : s.toCharArray()) {
			if (c == '\n') {
				++numLines;
			}
		}
		return numLines;
	}

	private int getMaxColumns(String s) {
		int maxColumns = 0;
		int currentColumns = 0;
		for (char c : s.toCharArray()) {
			if (c == '\n') {
				maxColumns = Math.max(maxColumns, currentColumns);
				currentColumns = 0;
			} else {
				++currentColumns;
			}
		}
		return Math.max(currentColumns, maxColumns);
	}

}
