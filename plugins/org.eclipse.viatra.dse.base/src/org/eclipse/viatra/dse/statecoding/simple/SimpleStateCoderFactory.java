package org.eclipse.viatra.dse.statecoding.simple;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.dse.statecode.IStateCoder;
import org.eclipse.viatra.dse.statecode.IStateCoderFactory;
import org.eclipse.viatra.dse.util.EMFHelper;
import org.eclipse.viatra.dse.util.EMFHelper.MetaModelElements;

public class SimpleStateCoderFactory implements IStateCoderFactory {

    private MetaModelElements metaModelElements;

    public SimpleStateCoderFactory(Collection<EPackage> metaModelPackages) {
        metaModelElements = EMFHelper.getAllMetaModelElements(new HashSet<EPackage>(metaModelPackages));
    }

    @Override
    public IStateCoder createStateCoder() {
        return new SimpleStateCoder(metaModelElements);
    }

}
