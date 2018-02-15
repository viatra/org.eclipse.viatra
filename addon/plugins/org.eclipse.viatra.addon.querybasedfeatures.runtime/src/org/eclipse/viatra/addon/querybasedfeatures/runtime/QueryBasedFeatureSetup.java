/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.querybasedfeatures.runtime;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.SettingDelegate.Factory;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.SettingDelegate.Factory.Registry;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.addon.querybasedfeatures.runtime.handler.QueryBasedFeatures;

/**
 * A helper class to initialize query-based features in cases where the default behavior (on-demand initialization in
 * getter) is not acceptable.
 * 
 * @author Abel Hegedus
 * @since 1.3
 *
 */
public class QueryBasedFeatureSetup {

    private static final String WRONG_CLASS_MESSAGE = "Query-based feature setting delegate factory is %s but should be %s";

    /**
     * Constructor hidden for setup class
     */
    private QueryBasedFeatureSetup() {
    }

    /**
     * Initializes all query based features found in the given packages on the given notifier. The notifier cannot be
     * null and at least one EPackage must be passed when calling the method. If the delegate factory for query-based
     * features was not registered in the delegate factory registry, then it will be at the beginning of the method.
     * 
     * <p>
     * Note that the initialization of query-based features may cause the initialization of other query-based features
     * from packages that were not passed to this method. This happens if the EClass that defines such a feature has at
     * least one EObject instance contained by the notifier transitively, the feature is an EReference and the base
     * index traverses the EObject and requests the value of the feature. Such features will not be included in the
     * returned set.
     * 
     * @param rootNotifier
     *            that the query-based features are initialized on, cannot be null
     * @param ePackages
     *            to look in for query-based features, at least one is required
     * @return the set of features that are query-based and are initialized after the call (may include features that
     *         were already initialized)
     * @since 1.3
     */
    public static Set<EStructuralFeature> initializeAllQueryBasedFeatures(Notifier rootNotifier,
            EPackage... ePackages) {
        checkArgument(rootNotifier != null, "Root notifier cannot be null!");
        checkArgument(ePackages.length > 0, "EPackage list cannot be empty!");

        // get or create QBF setting delegate factory
        QueryBasedFeatureSettingDelegateFactory qbfFactory = getOrCreateFactory();

        // collect packages with QBF setting delegates
        Set<EPackage> packagesWithQBFs = filterPackagesWithQBFs(ePackages);

        // find QBF structural features
        Set<EStructuralFeature> qbfFeatures = getQBFeaturesOfPackages(packagesWithQBFs);

        // initialize all features collected
        Set<EStructuralFeature> initializedQBFFeatures = initializeFeatures(rootNotifier, qbfFactory, qbfFeatures);
        return initializedQBFFeatures;
    }

    private static QueryBasedFeatureSettingDelegateFactory getOrCreateFactory() {
        QueryBasedFeatureSettingDelegateFactory qbfFactory = null;
        Registry settingDelegateFactoryRegistry = EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE;
        Factory factory = settingDelegateFactoryRegistry.getFactory(QueryBasedFeatures.ANNOTATION_SOURCE);
        if (factory == null) {
            qbfFactory = new QueryBasedFeatureSettingDelegateFactory();
            settingDelegateFactoryRegistry.put(QueryBasedFeatures.ANNOTATION_SOURCE, qbfFactory);
        } else if (factory instanceof QueryBasedFeatureSettingDelegateFactory) {
            qbfFactory = (QueryBasedFeatureSettingDelegateFactory) factory;
        } else {
            String errorMessage = String.format(WRONG_CLASS_MESSAGE, factory.getClass().getName(),
                    QueryBasedFeatureSettingDelegateFactory.class.getName());
            throw new IllegalStateException(errorMessage);
        }
        return qbfFactory;
    }

    private static Set<EPackage> filterPackagesWithQBFs(EPackage... ePackages) {
        Set<EPackage> packagesWithQBFs = new HashSet<>();
        for (EPackage ePkg : ePackages) {
            if (hasQBFSettingDelegate(ePkg)) {
                packagesWithQBFs.add(ePkg);
            }
        }
        return packagesWithQBFs;
    }

    private static boolean hasQBFSettingDelegate(EPackage ePkg) {
        List<String> settingDelegates = EcoreUtil.getSettingDelegates(ePkg);
        boolean hasQBFSettingDelegate = false;
        if (settingDelegates.contains(QueryBasedFeatures.ANNOTATION_SOURCE)) {
            hasQBFSettingDelegate = true;
        }
        return hasQBFSettingDelegate;
    }

    private static Set<EStructuralFeature> getQBFeaturesOfPackages(Set<EPackage> packagesWithQBFs) {
        Set<EStructuralFeature> qbfFeatures = new HashSet<>();
        for (EPackage ePkg : packagesWithQBFs) {
            TreeIterator<EObject> allContents = ePkg.eAllContents();
            while (allContents.hasNext()) {
                EObject next = allContents.next();
                if (next instanceof EStructuralFeature) {
                    EStructuralFeature structuralFeature = (EStructuralFeature) next;
                    if (structuralFeature.getEAnnotation(QueryBasedFeatures.ANNOTATION_SOURCE) != null) {
                        qbfFeatures.add(structuralFeature);
                    }
                }
            }
        }
        return qbfFeatures;
    }

    private static Set<EStructuralFeature> initializeFeatures(Notifier rootNotifier,
            QueryBasedFeatureSettingDelegateFactory qbfFactory, Set<EStructuralFeature> qbfFeatures) {
        Set<EStructuralFeature> initializedQBFFeatures = new HashSet<>();
        for (EStructuralFeature eStructuralFeature : qbfFeatures) {
            qbfFactory.getSettingDelegate(eStructuralFeature).ifPresent(delegate -> {
                delegate.initializeSettingDelegate(rootNotifier);
                initializedQBFFeatures.add(eStructuralFeature);
            });
        }
        return initializedQBFFeatures;
    }

}
