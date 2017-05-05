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
package org.eclipse.viatra.transformation.ui.wizards

import org.eclipse.core.runtime.IStatus
import org.eclipse.jdt.core.IJavaElement
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.swt.widgets.Composite
import org.eclipse.viatra.transformation.ui.wizards.NewTransformationTypeSelectionWizardPage.TransformationType
import org.eclipse.xtend.ide.wizards.AbstractNewXtendElementWizardPage
import org.eclipse.xtend.ide.wizards.FieldInitializerUtil
import org.eclipse.xtend.ide.wizards.Messages
import org.eclipse.xtend.ide.wizards.XtendTypeCreatorUtil

/**
 * New transformation wizard page that enables the user to define the required properties of a newly created VIATRA transformation.
 * Details:
 *  - Source Folder --> Source folder where the new transformation will be created
 *  - Package --> The created transformation will be added to this package
 *  - Name --> name of the transformation class WITHOUT extension
 *  
 * @author Peter Lunk
 *
 */
class NewTransformationWizardPage extends AbstractNewXtendElementWizardPage {
    var TransformationType type
    var boolean withDebugger
    var String transformationSessionName
    var boolean withLogging
    
    new() {
        super(CLASS_TYPE, NewTransformationWizard.NEWPAGENAME)
        this.setDescription("Create a new empty VIATRA transformation.")
    }

    override void createControl(Composite parent) {
        var Composite composite = createCommonControls(parent)
        setControl(composite)
    }

    override protected void doStatusUpdate() {
        var IStatus[] status = #[fContainerStatus, fPackageStatus, fTypeNameStatus]
        updateStatus(status)
    }

    override protected void init(IStructuredSelection selection) {
        var FieldInitializerUtil util = new FieldInitializerUtil()
        var IJavaElement elem = util.getSelectedResource(selection)
        initContainerPage(elem)
        initTypePage(elem)
    }

    override protected String getElementCreationErrorMessage() {
        return Messages.ERROR_CREATING_CLASS
    }

    override protected String getPackageDeclaration(String lineSeparator) {
        return XtendTypeCreatorUtil.createPackageDeclaration(getTypeName(), getPackageFragment(), getSuperClass(),
            getSuperInterfaces(), lineSeparator)
    }

    override protected String getTypeContent(String indentation, String lineSeparator) {
        switch (type) {
            case BatchTransformation: {
                return getBatchTransformationTemplate().toString
            }
            case EventDrivenTransformation: {
                return getEDTransformationTemplate().toString
            }
            default: {
                return getBatchTransformationTemplate().toString
            }
        }
    }
    
    def int createType(TransformationType type, boolean withDebugger, boolean withLogging, String transformationSessionName) {
        this.type = type
        this.withDebugger = withDebugger
        this.withLogging = withLogging
        this.transformationSessionName = transformationSessionName
        return createType
    }

    private def getBatchTransformationTemplate() {
        '''
            «IF withLogging»import org.apache.log4j.Logger«ENDIF»
            import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
            import org.eclipse.viatra.query.runtime.emf.EMFScope
            import org.eclipse.viatra.transformation.runtime.emf.modelmanipulation.IModelManipulations
            import org.eclipse.viatra.transformation.runtime.emf.modelmanipulation.SimpleModelManipulations
            import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRuleFactory
            import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRule
            import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformation
            import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformationStatements
            «IF withDebugger»import org.eclipse.viatra.transformation.debug.configuration.TransformationDebuggerConfiguration«ENDIF»
            import org.eclipse.emf.ecore.resource.Resource
            
            class «typeName» {
                «IF withLogging»extension Logger logger = Logger.getLogger(«typeName»)«ENDIF»
            
                /* Transformation-related extensions */
                extension BatchTransformation transformation
                extension BatchTransformationStatements statements
                
                /* Transformation rule-related extensions */
                extension BatchTransformationRuleFactory = new BatchTransformationRuleFactory
                extension IModelManipulations manipulation
            
                protected ViatraQueryEngine engine
                protected Resource resource
                //protected BatchTransformationRule<?,?> exampleRule
            
                new(Resource resource) {
                    this.resource = resource
                    // Create EMF scope and EMF IncQuery engine based on the resource
                    val scope = new EMFScope(resource)
                    engine = ViatraQueryEngine.on(scope);
                    
                    «IF withLogging»info("Preparing transformation rules.")«ENDIF»
                    createTransformation
                    «IF withLogging»info(«"'''"»Prepared transformation rules«"'''"»)«ENDIF»
            
                }
            
                public def execute() {
                    «IF withLogging»debug(«"'''"»Executing transformation on:«"«"»resource.URI«"»"»«"'''"»)«ENDIF»
            //      Fire the defined rules here
            //      exampleRule.fireAllCurrent
                }
            
                private def createTransformation() {
                    //Create VIATRA model manipulations
                    this.manipulation = new SimpleModelManipulations(engine)
                    //Create VIATRA Batch transformation
                    transformation = BatchTransformation.forEngine(engine)«IF withDebugger»
                    .addAdapterConfiguration(
                        //Create a debug adapter
                        //The debugger implements a classic breakpoint based functionality mapped to the field of model transformations.
                        //Similar to the Java debugger --> Statements == transformation rule activations.
                        //Breakpoints can be rendered to the individual transformation rule activations, or global conditions.
                        //During the execution: if a breakpoint activation is about to be fired, or the global condition is met, the execution of the transformation is halted.
                        //At this point, the user can specify the next course of action: step to the next activation firing, or continue the execution till the next breakpoint.
                        new TransformationDebuggerConfiguration(«IF transformationSessionName!=""»"«transformationSessionName»"«ENDIF»))«ENDIF»
                    .build
                    //Initialize batch transformation statements
                    statements = transformation.transformationStatements
                }
                
            //  private def getExampleRule() {
            //      if(exampleRule == null){
            //          exampleRule = createRule.name("ExampleRule").precondition(ExampleMatcher.querySpecification).action [
            //              Do Rule Actions here
            //          ].build
            //      }
            //      return exampleRule
            //  }
            
                def dispose() {
                    if (transformation != null) {
                        transformation.ruleEngine.dispose
                    }
                    transformation = null
                    return
                }
            }
        '''
    }
    
    private def getEDTransformationTemplate() {
        '''
            «IF withLogging»import org.apache.log4j.Logger«ENDIF»
            import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
            import org.eclipse.viatra.query.runtime.emf.EMFScope
            «IF withDebugger»import org.eclipse.viatra.transformation.debug.configuration.TransformationDebuggerConfiguration«ENDIF»
            import org.eclipse.viatra.transformation.runtime.emf.modelmanipulation.IModelManipulations
            import org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven.EventDrivenTransformationRuleFactory
            import org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven.EventDrivenTransformationRule
            import org.eclipse.viatra.transformation.runtime.emf.transformation.eventdriven.EventDrivenTransformation
            import org.eclipse.viatra.transformation.runtime.emf.modelmanipulation.SimpleModelManipulations
            import org.eclipse.emf.ecore.resource.Resource
            
            class «typeName» {
                «IF withLogging»extension Logger logger = Logger.getLogger(«typeName»)«ENDIF»
            
                /* Transformation-related extensions */
                extension EventDrivenTransformation transformation
                
                /* Transformation rule-related extensions */
                extension EventDrivenTransformationRuleFactory = new EventDrivenTransformationRuleFactory
                extension IModelManipulations manipulation
            
                protected ViatraQueryEngine engine
                protected Resource resource
                //protected EventDrivenTransformationRule<?,?> exampleRule
            
                new(Resource resource) {
                    this.resource = resource
                    // Create EMF scope and EMF IncQuery engine based on the resource
                    val scope = new EMFScope(resource)
                    engine = ViatraQueryEngine.on(scope);
                    
                    «IF withLogging»info("Preparing transformation rules.")«ENDIF»
                    createTransformation
                    «IF withLogging»info(«"'''"»Prepared transformation rules«"'''"»)«ENDIF»
            
                }
            
                public def execute() {
                    «IF withLogging»debug(«"'''"»Executing transformation on:«"«"»resource.URI«"»"»«"'''"»)«ENDIF»        
                    transformation.executionSchema.startUnscheduledExecution
                }
            
                private def createTransformation() {
                    //Initialize model manipulation API
                    this.manipulation = new SimpleModelManipulations(engine)
                    //Initialize event-driven transformation
                    transformation = EventDrivenTransformation.forEngine(engine)
                        //.addRule(exampleRule)«IF withDebugger»
                        .addAdapterConfiguration(
                            //Create a debug adapter
                            //The debugger implements a classic breakpoint based functionality mapped to the field of model transformations.
                            //Similar to the Java debugger --> Statements == transformation rule activations.
                            //Breakpoints can be rendered to the individual transformation rule activations, or global conditions.
                            //During the execution: if a breakpoint activation is about to be fired, or the global condition is met, the execution of the transformation is halted.
                            //At this point, the user can specify the next course of action: step to the next activation firing, or continue the execution till the next breakpoint.
                            new TransformationDebuggerConfiguration(«IF transformationSessionName!=""»"«transformationSessionName»"«ENDIF»))«ENDIF»
                        .build
                }
                
            //  private def getExampleRule() {
            //      if(exampleRule == null){
            //        exampleRule = createRule.name("Host_Rule").precondition(ExampleMatcher.querySpecification).action(
            //            CRUDActivationStateEnum.CREATED) «"["»
            //            *** Actions related to match appearance ***
            //       ].action(
            //            CRUDActivationStateEnum.UPDATED) «"["»
            //            *** Actions related to match update ***
            //        ].action(
            //            CRUDActivationStateEnum.DELETED) «"["»
            //           *** Actions related to match disappearance ***
            //        ].addLifeCycle(Lifecycles.getDefault(true, true)).build
            //      }
            //      return exampleRule
            //  }
            
            
                // Dispose model transformation
                def dispose() {
                    if (transformation != null) {
                        transformation.dispose
                    }
                    transformation = null
                    return
                }
            }
        '''
    }

}
