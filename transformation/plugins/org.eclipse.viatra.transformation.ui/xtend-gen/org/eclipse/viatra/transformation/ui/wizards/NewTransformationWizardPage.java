/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.ui.wizards;

import com.google.common.base.Objects;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.viatra.transformation.ui.wizards.NewTransformationTypeSelectionWizardPage;
import org.eclipse.viatra.transformation.ui.wizards.NewTransformationWizard;
import org.eclipse.xtend.ide.wizards.AbstractNewXtendElementWizardPage;
import org.eclipse.xtend.ide.wizards.FieldInitializerUtil;
import org.eclipse.xtend.ide.wizards.Messages;
import org.eclipse.xtend.ide.wizards.XtendTypeCreatorUtil;
import org.eclipse.xtend2.lib.StringConcatenation;

/**
 * New transformation wizard page that enables the user to define the required properties of a newly created VIATRA transformation.
 * Details:
 *  - Source Folder --> Source folder where the new transformation will be created
 *  - Package --> The created transformation will be added to this package
 *  - Name --> name of the transformation class WITHOUT extension
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class NewTransformationWizardPage extends AbstractNewXtendElementWizardPage {
  private NewTransformationTypeSelectionWizardPage.TransformationType type;
  
  private boolean withDebugger;
  
  private String transformationSessionName;
  
  private boolean withLogging;
  
  public NewTransformationWizardPage() {
    super(NewTypeWizardPage.CLASS_TYPE, NewTransformationWizard.NEWPAGENAME);
    this.setDescription("Create a new empty VIATRA transformation.");
  }
  
  @Override
  public void createControl(final Composite parent) {
    Composite composite = this.createCommonControls(parent);
    this.setControl(composite);
  }
  
  @Override
  protected void doStatusUpdate() {
    IStatus[] status = { this.fContainerStatus, this.fPackageStatus, this.fTypeNameStatus };
    this.updateStatus(status);
  }
  
  @Override
  protected void init(final IStructuredSelection selection) {
    FieldInitializerUtil util = new FieldInitializerUtil();
    IJavaElement elem = util.getSelectedResource(selection);
    this.initContainerPage(elem);
    this.initTypePage(elem);
  }
  
  @Override
  protected String getElementCreationErrorMessage() {
    return Messages.ERROR_CREATING_CLASS;
  }
  
  @Override
  protected String getPackageDeclaration(final String lineSeparator) {
    String _typeName = this.getTypeName();
    IPackageFragment _packageFragment = this.getPackageFragment();
    String _superClass = this.getSuperClass();
    List<String> _superInterfaces = this.getSuperInterfaces();
    return XtendTypeCreatorUtil.createPackageDeclaration(_typeName, _packageFragment, _superClass, _superInterfaces, lineSeparator);
  }
  
  @Override
  protected String getTypeContent(final String indentation, final String lineSeparator) {
    final NewTransformationTypeSelectionWizardPage.TransformationType type = this.type;
    if (type != null) {
      switch (type) {
        case BatchTransformation:
          CharSequence _batchTransformationTemplate = this.getBatchTransformationTemplate();
          return _batchTransformationTemplate.toString();
        case EventDrivenTransformation:
          CharSequence _eDTransformationTemplate = this.getEDTransformationTemplate();
          return _eDTransformationTemplate.toString();
        default:
          CharSequence _batchTransformationTemplate_1 = this.getBatchTransformationTemplate();
          return _batchTransformationTemplate_1.toString();
      }
    } else {
      CharSequence _batchTransformationTemplate_1 = this.getBatchTransformationTemplate();
      return _batchTransformationTemplate_1.toString();
    }
  }
  
  public int createType(final NewTransformationTypeSelectionWizardPage.TransformationType type, final boolean withDebugger, final boolean withLogging, final String transformationSessionName) {
    this.type = type;
    this.withDebugger = withDebugger;
    this.withLogging = withLogging;
    this.transformationSessionName = transformationSessionName;
    return this.createType();
  }
  
  private CharSequence getBatchTransformationTemplate() {
    StringConcatenation _builder = new StringConcatenation();
    {
      if (this.withLogging) {
        _builder.append("import org.apache.log4j.Logger");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine");
    _builder.newLine();
    _builder.append("import org.eclipse.viatra.query.runtime.emf.EMFScope");
    _builder.newLine();
    _builder.append("import org.eclipse.viatra.transformation.runtime.emf.modelmanipulation.IModelManipulations");
    _builder.newLine();
    _builder.append("import org.eclipse.viatra.transformation.runtime.emf.modelmanipulation.SimpleModelManipulations");
    _builder.newLine();
    _builder.append("import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRuleFactory");
    _builder.newLine();
    _builder.append("import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRule");
    _builder.newLine();
    _builder.append("import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformation");
    _builder.newLine();
    _builder.append("import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformationStatements");
    _builder.newLine();
    {
      if (this.withDebugger) {
        _builder.append("import org.eclipse.viatra.transformation.debug.configuration.TransformationDebuggerConfiguration");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("import org.eclipse.emf.ecore.resource.Resource");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class ");
    String _typeName = this.getTypeName();
    _builder.append(_typeName, "");
    _builder.append(" {");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    {
      if (this.withLogging) {
        _builder.append("extension Logger logger = Logger.getLogger(");
        String _typeName_1 = this.getTypeName();
        _builder.append(_typeName_1, "    ");
        _builder.append(")");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("/* Transformation-related extensions */");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("extension BatchTransformation transformation");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("extension BatchTransformationStatements statements");
    _builder.newLine();
    _builder.append("    ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("/* Transformation rule-related extensions */");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("extension BatchTransformationRuleFactory = new BatchTransformationRuleFactory");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("extension IModelManipulations manipulation");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("protected ViatraQueryEngine engine");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("protected Resource resource");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("//protected BatchTransformationRule<?,?> exampleRule");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("new(Resource resource) {");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("this.resource = resource");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("// Create EMF scope and EMF IncQuery engine based on the resource");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("val scope = new EMFScope(resource)");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("engine = ViatraQueryEngine.on(scope);");
    _builder.newLine();
    _builder.append("        ");
    _builder.newLine();
    _builder.append("        ");
    {
      if (this.withLogging) {
        _builder.append("info(\"Preparing transformation rules.\")");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("        ");
    _builder.append("createTransformation");
    _builder.newLine();
    _builder.append("        ");
    {
      if (this.withLogging) {
        _builder.append("info(");
        _builder.append("\'\'\'", "        ");
        _builder.append("Prepared transformation rules");
        _builder.append("\'\'\'", "        ");
        _builder.append(")");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("public def execute() {");
    _builder.newLine();
    _builder.append("        ");
    {
      if (this.withLogging) {
        _builder.append("debug(");
        _builder.append("\'\'\'", "        ");
        _builder.append("Executing transformation on:");
        _builder.append("«", "        ");
        _builder.append("resource.URI");
        _builder.append("»", "        ");
        _builder.append("\'\'\'", "        ");
        _builder.append(")");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("//      Fire the defined rules here");
    _builder.newLine();
    _builder.append("//      exampleRule.fireAllCurrent");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("private def createTransformation() {");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("//Create VIATRA model manipulations");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("this.manipulation = new SimpleModelManipulations(engine)");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("//Create VIATRA Batch transformation");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("transformation = BatchTransformation.forEngine(engine)");
    {
      if (this.withDebugger) {
        _builder.newLineIfNotEmpty();
        _builder.append("        ");
        _builder.append(".addAdapterConfiguration(");
        _builder.newLine();
        _builder.append("        ");
        _builder.append("    ");
        _builder.append("//Create a debug adapter");
        _builder.newLine();
        _builder.append("        ");
        _builder.append("    ");
        _builder.append("//The debugger implements a classic breakpoint based functionality mapped to the field of model transformations.");
        _builder.newLine();
        _builder.append("        ");
        _builder.append("    ");
        _builder.append("//Similar to the Java debugger --> Statements == transformation rule activations.");
        _builder.newLine();
        _builder.append("        ");
        _builder.append("    ");
        _builder.append("//Breakpoints can be rendered to the individual transformation rule activations, or global conditions.");
        _builder.newLine();
        _builder.append("        ");
        _builder.append("    ");
        _builder.append("//During the execution: if a breakpoint activation is about to be fired, or the global condition is met, the execution of the transformation is halted.");
        _builder.newLine();
        _builder.append("        ");
        _builder.append("    ");
        _builder.append("//At this point, the user can specify the next course of action: step to the next activation firing, or continue the execution till the next breakpoint.");
        _builder.newLine();
        _builder.append("        ");
        _builder.append("    ");
        _builder.append("new TransformationDebuggerConfiguration(");
        {
          boolean _notEquals = (!Objects.equal(this.transformationSessionName, ""));
          if (_notEquals) {
            _builder.append("\"");
            _builder.append(this.transformationSessionName, "            ");
            _builder.append("\"");
          }
        }
        _builder.append("))");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("        ");
    _builder.append(".build");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("//Initialize batch transformation statements");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("statements = transformation.transformationStatements");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("    ");
    _builder.newLine();
    _builder.append("//  private def getExampleRule() {");
    _builder.newLine();
    _builder.append("//      if(exampleRule == null){");
    _builder.newLine();
    _builder.append("//          exampleRule = createRule.name(\"ExampleRule\").precondition(ExampleMatcher.querySpecification).action [");
    _builder.newLine();
    _builder.append("//              Do Rule Actions here");
    _builder.newLine();
    _builder.append("//          ].build");
    _builder.newLine();
    _builder.append("//      }");
    _builder.newLine();
    _builder.append("//      return exampleRule");
    _builder.newLine();
    _builder.append("//  }");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("def dispose() {");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("if (transformation != null) {");
    _builder.newLine();
    _builder.append("            ");
    _builder.append("transformation.ruleEngine.dispose");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("transformation = null");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("return");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
  
  private CharSequence getEDTransformationTemplate() {
    StringConcatenation _builder = new StringConcatenation();
    {
      if (this.withLogging) {
        _builder.append("import org.apache.log4j.Logger");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine");
    _builder.newLine();
    _builder.append("import org.eclipse.viatra.query.runtime.emf.EMFScope");
    _builder.newLine();
    {
      if (this.withDebugger) {
        _builder.append("import org.eclipse.viatra.transformation.debug.configuration.TransformationDebuggerConfiguration");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("import org.eclipse.viatra.transformation.runtime.emf.modelmanipulation.IModelManipulations");
    _builder.newLine();
    _builder.append("import org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven.EventDrivenTransformationRuleFactory");
    _builder.newLine();
    _builder.append("import org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven.EventDrivenTransformationRule");
    _builder.newLine();
    _builder.append("import org.eclipse.viatra.transformation.runtime.emf.transformation.eventdriven.EventDrivenTransformation");
    _builder.newLine();
    _builder.append("import org.eclipse.viatra.transformation.runtime.emf.modelmanipulation.SimpleModelManipulations");
    _builder.newLine();
    _builder.append("import org.eclipse.emf.ecore.resource.Resource");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class ");
    String _typeName = this.getTypeName();
    _builder.append(_typeName, "");
    _builder.append(" {");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    {
      if (this.withLogging) {
        _builder.append("extension Logger logger = Logger.getLogger(");
        String _typeName_1 = this.getTypeName();
        _builder.append(_typeName_1, "    ");
        _builder.append(")");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("/* Transformation-related extensions */");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("extension EventDrivenTransformation transformation");
    _builder.newLine();
    _builder.append("    ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("/* Transformation rule-related extensions */");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("extension EventDrivenTransformationRuleFactory = new EventDrivenTransformationRuleFactory");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("extension IModelManipulations manipulation");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("protected ViatraQueryEngine engine");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("protected Resource resource");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("//protected EventDrivenTransformationRule<?,?> exampleRule");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("new(Resource resource) {");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("this.resource = resource");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("// Create EMF scope and EMF IncQuery engine based on the resource");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("val scope = new EMFScope(resource)");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("engine = ViatraQueryEngine.on(scope);");
    _builder.newLine();
    _builder.append("        ");
    _builder.newLine();
    _builder.append("        ");
    {
      if (this.withLogging) {
        _builder.append("info(\"Preparing transformation rules.\")");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("        ");
    _builder.append("createTransformation");
    _builder.newLine();
    _builder.append("        ");
    {
      if (this.withLogging) {
        _builder.append("info(");
        _builder.append("\'\'\'", "        ");
        _builder.append("Prepared transformation rules");
        _builder.append("\'\'\'", "        ");
        _builder.append(")");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("public def execute() {");
    _builder.newLine();
    _builder.append("        ");
    {
      if (this.withLogging) {
        _builder.append("debug(");
        _builder.append("\'\'\'", "        ");
        _builder.append("Executing transformation on:");
        _builder.append("«", "        ");
        _builder.append("resource.URI");
        _builder.append("»", "        ");
        _builder.append("\'\'\'", "        ");
        _builder.append(")");
      }
    }
    _builder.append("        ");
    _builder.newLineIfNotEmpty();
    _builder.append("        ");
    _builder.append("transformation.executionSchema.startUnscheduledExecution");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("private def createTransformation() {");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("//Initialize model manipulation API");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("this.manipulation = new SimpleModelManipulations(engine)");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("//Initialize event-driven transformation");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("transformation = EventDrivenTransformation.forEngine(engine)");
    _builder.newLine();
    _builder.append("            ");
    _builder.append("//.addRule(exampleRule)");
    {
      if (this.withDebugger) {
        _builder.newLineIfNotEmpty();
        _builder.append("            ");
        _builder.append(".addAdapterConfiguration(");
        _builder.newLine();
        _builder.append("            ");
        _builder.append("    ");
        _builder.append("//Create a debug adapter");
        _builder.newLine();
        _builder.append("            ");
        _builder.append("    ");
        _builder.append("//The debugger implements a classic breakpoint based functionality mapped to the field of model transformations.");
        _builder.newLine();
        _builder.append("            ");
        _builder.append("    ");
        _builder.append("//Similar to the Java debugger --> Statements == transformation rule activations.");
        _builder.newLine();
        _builder.append("            ");
        _builder.append("    ");
        _builder.append("//Breakpoints can be rendered to the individual transformation rule activations, or global conditions.");
        _builder.newLine();
        _builder.append("            ");
        _builder.append("    ");
        _builder.append("//During the execution: if a breakpoint activation is about to be fired, or the global condition is met, the execution of the transformation is halted.");
        _builder.newLine();
        _builder.append("            ");
        _builder.append("    ");
        _builder.append("//At this point, the user can specify the next course of action: step to the next activation firing, or continue the execution till the next breakpoint.");
        _builder.newLine();
        _builder.append("            ");
        _builder.append("    ");
        _builder.append("new TransformationDebuggerConfiguration(");
        {
          boolean _notEquals = (!Objects.equal(this.transformationSessionName, ""));
          if (_notEquals) {
            _builder.append("\"");
            _builder.append(this.transformationSessionName, "                ");
            _builder.append("\"");
          }
        }
        _builder.append("))");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("            ");
    _builder.append(".build");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("    ");
    _builder.newLine();
    _builder.append("//  private def getExampleRule() {");
    _builder.newLine();
    _builder.append("//      if(exampleRule == null){");
    _builder.newLine();
    _builder.append("//        exampleRule = createRule.name(\"Host_Rule\").precondition(ExampleMatcher.querySpecification).action(");
    _builder.newLine();
    _builder.append("//            CRUDActivationStateEnum.CREATED) ");
    _builder.append("[", "");
    _builder.newLineIfNotEmpty();
    _builder.append("//            *** Actions related to match appearance ***");
    _builder.newLine();
    _builder.append("//       ].action(");
    _builder.newLine();
    _builder.append("//            CRUDActivationStateEnum.UPDATED) ");
    _builder.append("[", "");
    _builder.newLineIfNotEmpty();
    _builder.append("//            *** Actions related to match update ***");
    _builder.newLine();
    _builder.append("//        ].action(");
    _builder.newLine();
    _builder.append("//            CRUDActivationStateEnum.DELETED) ");
    _builder.append("[", "");
    _builder.newLineIfNotEmpty();
    _builder.append("//           *** Actions related to match disappearance ***");
    _builder.newLine();
    _builder.append("//        ].addLifeCycle(Lifecycles.getDefault(true, true)).build");
    _builder.newLine();
    _builder.append("//      }");
    _builder.newLine();
    _builder.append("//      return exampleRule");
    _builder.newLine();
    _builder.append("//  }");
    _builder.newLine();
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("// Dispose model transformation");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("def dispose() {");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("if (transformation != null) {");
    _builder.newLine();
    _builder.append("            ");
    _builder.append("transformation.dispose");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("transformation = null");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("return");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
}
