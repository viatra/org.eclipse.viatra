package org.eclipse.incquery.uml.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.context.surrogate.SurrogateQueryRegistry;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.testing.core.ModelLoadHelper;
import org.eclipse.incquery.uml.derivedfeatures.DerivedFeatures;
import org.eclipse.uml2.uml.Model;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(Parameterized.class)
public class SurrogateQueryTest {
    
    @Parameters(name = "{index}: {1}")
    public static Collection<Object[]> data() throws IncQueryException {
        DerivedFeatures features = DerivedFeatures.instance();
        Set<IQuerySpecification<?>> specifications = features.getSpecifications();
        
        TreeMap<String, IQuerySpecification<?>> fqnToQueryMap = Maps.newTreeMap();
        
        for (IQuerySpecification<?> querySpecification : specifications) {
            fqnToQueryMap.put(querySpecification.getFullyQualifiedName(), querySpecification);
        }
        
        Builder<Object[]> builder = ImmutableList.builder();
        for (Entry<String, IQuerySpecification<?>> entry : fqnToQueryMap.entrySet()) {
            builder.add(new Object[] { entry.getValue(), entry.getKey()});
        }
        return builder.build();
        
    }
    
    @Parameter
    public IQuerySpecification<?> querySpecification;
    
    @Parameter(value = 1)
    public String querySpecificationFQN;
    
    protected static Map<PQuery, EStructuralFeature> surrogateQueryToFeature = getSurrogateToQueryMap();

    private AdvancedIncQueryEngine engine;

    private Resource umlResource;
    
    @Ignore("Do not run on CI")
    @Test
    public void checkSurrogateOnUMLMetamodel() throws IncQueryException {
        
        String modelUri = "/org.eclipse.incquery.uml.test/model/UML.merged.uml";
        performSurrogateQueryValidation(modelUri);
    }
    
    /**
     * @param modelUri
     * @throws IncQueryException
     */
    private void performSurrogateQueryValidation(String modelUri) throws IncQueryException {
        ModelLoadHelper modelLoadHelper = new ModelLoadHelper();
        umlResource = modelLoadHelper.loadModelFromUri(modelUri);
        Model umlModel = (Model) umlResource.getContents().get(0);
        
        DerivedFeatures features = DerivedFeatures.instance();
        
        engine = AdvancedIncQueryEngine.createUnmanagedEngine(new EMFScope(umlResource.getResourceSet()));
        features.prepare(engine);
        
        // find surrogate EStructuralFeature
        EStructuralFeature structuralFeature = null;
        PAnnotation annotation = querySpecification.getFirstAnnotationByName("Surrogate");
        if(annotation == null){
            return;
        }
        
        PQuery query = querySpecification.getInternalQueryRepresentation();
        structuralFeature = surrogateQueryToFeature.get(query);
        assertNotNull("Could not find feature", structuralFeature);
        
        boolean incorrectValuesFound = false;
        
        incorrectValuesFound = incorrectValuesFound || checkQuerySpecification(querySpecification, structuralFeature, engine);
        
        incorrectValuesFound = incorrectValuesFound || checkStructuralFeatures(umlModel, engine, structuralFeature);
    
        assertFalse("Some values of " + querySpecificationFQN + " were incorrect, check the system output for details", incorrectValuesFound);
    }

    @After
    public void cleanUp(){
        engine.dispose();
        EList<Resource> resources = umlResource.getResourceSet().getResources();
        for (Resource resource : resources) {
            resource.unload();
        }
    }

    /**
     * @param umlModel
     * @param engine
     * @param structuralFeature
     * @throws IncQueryException
     */
    private boolean checkStructuralFeatures(Model umlModel, AdvancedIncQueryEngine engine,
            EStructuralFeature structuralFeature) throws IncQueryException {
        @SuppressWarnings("unchecked")
        IncQueryMatcher<IPatternMatch> matcher = (IncQueryMatcher<IPatternMatch>) querySpecification.getMatcher(engine);
        IPatternMatch match = matcher.newEmptyMatch();

        boolean incorrectValuesFound = false;
        
        TreeIterator<EObject> eAllContents = umlModel.eAllContents();
        // iterate on eAllContents
        while(eAllContents.hasNext()){
            EObject sourceEObject = eAllContents.next();
            // iterate on eClass.eAllStructuralFeatures
            EList<EStructuralFeature> eAllStructuralFeatures = sourceEObject.eClass().getEAllStructuralFeatures();
            if(eAllStructuralFeatures.contains(structuralFeature)){
                // check eGet versus query results
                match.set(0, sourceEObject);
                int numberOfMatches = matcher.countMatches(match);
                Object target = sourceEObject.eGet(structuralFeature);
                if(structuralFeature.isMany()){
                    // check source.eGet(feature) contains the same elements as surrogate.getAllValuesOfTarget(source)
                    final EList<?> targetValues = (EList<?>) target;
                    if(targetValues.size() != numberOfMatches){
                        incorrectValuesFound = true;
                        final Set<Object> allValues = matcher.getAllValues(match.parameterNames().get(1), match);
                        Iterable<Object> notInTargetList = Lists.newArrayList(Iterables.filter(allValues, new Predicate<Object>() {
                            @Override
                            public boolean apply(Object input) {
                                return !targetValues.contains(input);
                            }
                        }));
                        Iterable<?> notInMatches = Lists.newArrayList(Iterables.filter(targetValues, new Predicate<Object>() {
                            @Override
                            public boolean apply(Object input) {
                                return !allValues.contains(input);
                            }
                        }));
                        System.out.println("Incorrect values for:\n  Query: " + querySpecificationFQN + "\n  Source: " + sourceEObject);
                        System.out.println("-> Not in eGet():\n  " + notInTargetList);
                        System.out.println("-> Not in matches:\n  " + notInMatches);
                    }
                    // assertTrue("Incorrect number of matches (should be " + targetValues.size() + "): " + numberOfMatches, numberOfMatches == targetValues.size());
                } else {
                    if(target != null){
                        // check that surrogate.countMatches(source) == 1
                        if(numberOfMatches != 1){
                            incorrectValuesFound = true;
                            System.out.println("Incorrect values for:\n  Query: " + querySpecificationFQN + "\n  Source: " + sourceEObject);
                            System.out.println("-> eGet: " + target + "\n-> match: null");
                        }
                        // assertTrue("Incorrect number of matches (should be 1): " + numberOfMatches, numberOfMatches == 1);
                        
                        // check source.eGet(feature) equals surrogate.getAllValuesOfTarget(source).iterator.next
                        Object matchTarget = matcher.getAllValues(matcher.getParameterNames().get(1), match).iterator().next();
                        if(!matchTarget.equals(target)){
                            incorrectValuesFound = true;
                            System.out.println("Incorrect values for:\n  Query: " + querySpecificationFQN + "\n  Source: " + sourceEObject);
                            System.out.println("-> eGet: " + target + "\n-> match: " + matchTarget);
                        }
                        // assertEquals("Incorrect target", target, matchTarget);
                    } else {
                        if(numberOfMatches != 0){
                            incorrectValuesFound = true;
                            Object matchTarget = matcher.getAllValues(matcher.getParameterNames().get(1), match).iterator().next();
                            System.out.println("Incorrect values for:\n  Query: " + querySpecificationFQN + "\n  Source: " + sourceEObject);
                            System.out.println("-> eGet: null" + target + "\n-> match: " + matchTarget);
                        }
                        // assertTrue("Incorrect number of matches (should be 0): " + numberOfMatches, numberOfMatches == 0);
                    }
                    
                }
            }
        }
        
        return incorrectValuesFound;
    }

    /**
     * @return
     */
    private static Map<PQuery, EStructuralFeature> getSurrogateToQueryMap() {
        Map<PQuery, EStructuralFeature> surrogateQueryToFeature = Maps.newHashMap();
        
        Set<IInputKey> allSurrogateQueries = SurrogateQueryRegistry.instance().getAllSurrogateQueries();
        
        for (IInputKey iInputKey : allSurrogateQueries) {
            if(iInputKey instanceof EStructuralFeatureInstancesKey) {
                EStructuralFeature feature = ((EStructuralFeatureInstancesKey) iInputKey).getEmfKey();
                PQuery surrogateQuery = SurrogateQueryRegistry.instance().getSurrogateQuery(iInputKey);
                surrogateQueryToFeature.put(surrogateQuery, feature);
            }
        }
        return surrogateQueryToFeature;
    }

    /**
     * @param querySpecification
     * @param surrogateQueryToFeature 
     * @return
     * @throws IncQueryException 
     */
    private boolean checkQuerySpecification(IQuerySpecification<?> querySpecification, EStructuralFeature structuralFeature, IncQueryEngine engine) throws IncQueryException {
    
        // iterate on getAllValuesOfSource
        @SuppressWarnings("unchecked")
        IncQueryMatcher<IPatternMatch> matcher = (IncQueryMatcher<IPatternMatch>) querySpecification.getMatcher(engine);
        IPatternMatch match = matcher.newEmptyMatch();
        Set<Object> allValuesOfSource = matcher.getAllValues(matcher.getParameterNames().get(0));
        
        boolean incorrectValuesFound = false;
        
        for (Object source : allValuesOfSource) {
            match.set(0, source);
            int numberOfMatches = matcher.countMatches(match);
            EObject sourceEObject = (EObject) source;
            Object target = sourceEObject.eGet(structuralFeature);
            if(structuralFeature.isMany()){
                // check source.eGet(feature) contains the same elements as surrogate.getAllValuesOfTarget(source)
                final EList<?> targetValues = (EList<?>) target;
                if(targetValues.size() != numberOfMatches){
                    incorrectValuesFound = true;
                    final Set<Object> allValues = matcher.getAllValues(match.parameterNames().get(1), match);
                    Iterable<Object> notInTargetList = Lists.newArrayList(Iterables.filter(allValues, new Predicate<Object>() {
                        @Override
                        public boolean apply(Object input) {
                            return !targetValues.contains(input);
                        }
                    }));
                    Iterable<?> notInMatches = Lists.newArrayList(Iterables.filter(targetValues, new Predicate<Object>() {
                        @Override
                        public boolean apply(Object input) {
                            return !allValues.contains(input);
                        }
                    }));
                    System.out.println("Incorrect values for:\n  Query: " + querySpecificationFQN + "\n  Source: " + sourceEObject);
                    System.out.println("-> Not in eGet():\n  " + notInTargetList);
                    System.out.println("-> Not in matches:\n  " + notInMatches);
                }
                // assertTrue("Incorrect number of matches (should be " + targetValues.size() + "): " + numberOfMatches, numberOfMatches == targetValues.size());
            } else {
                if(target != null){
                    // check that surrogate.countMatches(source) == 1
                    if(numberOfMatches != 1){
                        incorrectValuesFound = true;
                        System.out.println("Incorrect values for:\n  Query: " + querySpecificationFQN + "\n  Source: " + sourceEObject);
                        System.out.println("-> eGet: " + target + "\n-> match: null");
                    }
                    // assertTrue("Incorrect number of matches (should be 1): " + numberOfMatches, numberOfMatches == 1);
                    
                    // check source.eGet(feature) equals surrogate.getAllValuesOfTarget(source).iterator.next
                    Object matchTarget = matcher.getAllValues(matcher.getParameterNames().get(1), match).iterator().next();
                    if(!matchTarget.equals(target)){
                        incorrectValuesFound = true;
                        System.out.println("Incorrect values for:\n  Query: " + querySpecificationFQN + "\n  Source: " + sourceEObject);
                        System.out.println("-> eGet: " + target + "\n-> match: " + matchTarget);
                    }
                    // assertEquals("Incorrect target", target, matchTarget);
                } else {
                    if(numberOfMatches != 0){
                        incorrectValuesFound = true;
                        Object matchTarget = matcher.getAllValues(matcher.getParameterNames().get(1), match).iterator().next();
                        System.out.println("Incorrect values for:\n  Query: " + querySpecificationFQN + "\n  Source: " + sourceEObject);
                        System.out.println("-> eGet: null" + target + "\n-> match: " + matchTarget);
                    }
                    // assertTrue("Incorrect number of matches (should be 0): " + numberOfMatches, numberOfMatches == 0);
                }
            }
        }
        
        return incorrectValuesFound;
    }
}
