/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.xcore.resource;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.xcore.XPackage;
import org.eclipse.emf.ecore.xcore.resource.XcoreModelAssociator;
import org.eclipse.incquery.xcore.mappings.IncQueryXcoreMapper;
import org.eclipse.incquery.xcore.util.IncQueryXcoreEcoreBuilder;
import org.eclipse.incquery.xcore.util.IncQueryXcoreGenModelBuilder;
import org.eclipse.xtext.resource.DerivedStateAwareResource;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class IncQueryXcoreModelAssociator extends XcoreModelAssociator {

    @Inject
    private IncQueryXcoreGenModelBuilder genModelBuilder;

    @Inject
    private Provider<IncQueryXcoreEcoreBuilder> xcoreEcoreBuilderProvider;

    @Inject
    private IncQueryXcoreMapper mapper;

    public IncQueryXcoreModelAssociator() {
        super();
        super.mapper = this.mapper;
        super.genModelBuilder = this.genModelBuilder;
    }
    
    public void installDerivedState(DerivedStateAwareResource resource, boolean preLinkingPhase)
    {
      if (resource.getParseResult() != null && resource.getParseResult().getRootASTElement() instanceof XPackage)
      {
        XPackage model = (XPackage)resource.getParseResult().getRootASTElement();
        IncQueryXcoreEcoreBuilder xcoreEcoreBuilder = xcoreEcoreBuilderProvider.get();
        EPackage ePackage = xcoreEcoreBuilder.getEPackage(model);
        resource.getContents().add(ePackage);
        GenModel genModel = genModelBuilder.getGenModel(model);
        genModel.setCanGenerate(true);
        genModelInitializer.initialize(genModel, true);
        if (!preLinkingPhase)
        {
          xcoreEcoreBuilder.link();
          genModelBuilder.initializeUsedGenPackages(genModel);

          // If the model has edit support, it's important to determine if we have a dependencies on Ecore's generated item providers...
          //
          if (genModel.hasEditSupport())
          {
            for (GenPackage genPackage : genModel.getUsedGenPackages())
            {
              // If we find a GenPackage for Ecore itself...
              //
              if (EcorePackage.eNS_URI.equals(genPackage.getNSURI()))
              {
                boolean needsEcoreEditSupport = false;
                EPackage ecorePackage = genPackage.getEcorePackage();

                // Consider all the class of the package...
                LOOP:
                for (EClassifier eClassifier : ePackage.getEClassifiers())
                {
                  if (eClassifier instanceof EClass)
                  {
                    EClass eClass = (EClass)eClassifier;
                    
                    // If one of the super types is from the Ecore package and isn't EObject, we need Ecore edit support.
                    //
                    for (EClass eSuperType : eClass.getEAllSuperTypes())
                    {
                      if (eSuperType.getEPackage() == ecorePackage && !"EObject".equals(eSuperType.getName()))
                      {
                        needsEcoreEditSupport = true;
                        break LOOP;
                      }
                    }
                    // If one of the reference types is from the Ecore package and isn't EObject, we need Ecore edit support.
                    //
                    for (EReference eReference : eClass.getEAllReferences())
                    {
                      EClass eReferenceType = eReference.getEReferenceType();
                      if (eReferenceType != null && eReferenceType.getEPackage() == ecorePackage && !"EObject".equals(eReferenceType.getName()))
                      {
                        needsEcoreEditSupport = true;
                        break LOOP;
                      }
                    }
                  }
                }
                // Modify the Ecore package's GenPackage's model to indicate whether Ecore provides edit support.
                // Do this without producing notifications to avoid Ecore Tools transactional editing domain complaining that there is a model modification without a write transaction.
                //
                GenModel ecoreGenModel = genPackage.getGenModel();
                ecoreGenModel.eSetDeliver(false);
                ecoreGenModel.setEditDirectory(needsEcoreEditSupport ? "/org.eclipse.emf.edit.ecore/src" : "");
                ecoreGenModel.eSetDeliver(true);
                break;
              }
            }
          }
        }
        resource.getContents().addAll(jvmInferrer.inferElements(genModel));
        if (!preLinkingPhase)
        {
          xcoreEcoreBuilder.linkInstanceTypes();
          jvmInferrer.inferDeepStructure(genModel);
        }
        resource.getCache().clear(resource);
      }
    }


}
