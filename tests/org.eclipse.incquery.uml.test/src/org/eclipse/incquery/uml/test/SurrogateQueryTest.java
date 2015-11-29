package org.eclipse.incquery.uml.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
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
//    protected static Map<PQuery, IQuerySpecification<?>> queryToSpec = getQueryToSpecMap();

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
        
        checkQuerySpecification(querySpecification, structuralFeature, engine);
        
        checkStructuralFeatures(umlModel, engine, structuralFeature);
    
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
    private void checkStructuralFeatures(Model umlModel, AdvancedIncQueryEngine engine,
            EStructuralFeature structuralFeature) throws IncQueryException {
        @SuppressWarnings("unchecked")
        IncQueryMatcher<IPatternMatch> matcher = (IncQueryMatcher<IPatternMatch>) querySpecification.getMatcher(engine);
        IPatternMatch match = matcher.newEmptyMatch();
        
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
                    EList<?> targetValues = (EList<?>) target;
                    assertTrue("Incorrect number of matches (should be " + targetValues.size() + "): " + numberOfMatches, numberOfMatches == targetValues.size());
                } else {
                    if(target != null){
                        // check that surrogate.countMatches(source) == 1
                        assertTrue("Incorrect number of matches (should be 1): " + numberOfMatches, numberOfMatches == 1);
                        
                        // check source.eGet(feature) equals surrogate.getAllValuesOfTarget(source).iterator.next
                        Object matchTarget = matcher.getAllValues(matcher.getParameterNames().get(1), match).iterator().next();
                        assertEquals("Incorrect target", target, matchTarget);
                    } else {
                        assertTrue("Incorrect number of matches (should be 0): " + numberOfMatches, numberOfMatches == 0);
                        
                    }
                    
                }
            }
        }
    }

//    /**
//     * @return
//     * @throws IncQueryException
//     */
//    private static Map<PQuery, IQuerySpecification<?>> getQueryToSpecMap() {
//        Map<PQuery, IQuerySpecification<?>> map = Maps.newHashMap();
//        try {
//            DerivedFeatures features = DerivedFeatures.instance();
//            for (IQuerySpecification<?> spec : features.getSpecifications()) {
//                map.put(spec.getInternalQueryRepresentation(), spec);
//            }
//        } catch (IncQueryException e) {
//            fail("Could not load queries");
//        }
//        return map;
//    }

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
    private void checkQuerySpecification(IQuerySpecification<?> querySpecification, EStructuralFeature structuralFeature, IncQueryEngine engine) throws IncQueryException {
        
    
        // iterate on getAllValuesOfSource
        @SuppressWarnings("unchecked")
        IncQueryMatcher<IPatternMatch> matcher = (IncQueryMatcher<IPatternMatch>) querySpecification.getMatcher(engine);
        IPatternMatch match = matcher.newEmptyMatch();
        Set<Object> allValuesOfSource = matcher.getAllValues(matcher.getParameterNames().get(0));
        for (Object source : allValuesOfSource) {
            match.set(0, source);
            int numberOfMatches = matcher.countMatches(match);
            EObject sourceEObject = (EObject) source;
            Object target = sourceEObject.eGet(structuralFeature);
            if(structuralFeature.isMany()){
                // check source.eGet(feature) contains the same elements as surrogate.getAllValuesOfTarget(source)
                EList<?> targetValues = (EList<?>) target;
                assertTrue("Incorrect number of matches (should be " + targetValues.size() + "): " + numberOfMatches, numberOfMatches == targetValues.size());
            } else {
                if(target != null){
                    // check that surrogate.countMatches(source) == 1
                    assertTrue("Incorrect number of matches (should be 1): " + numberOfMatches, numberOfMatches == 1);
                    
                    // check source.eGet(feature) equals surrogate.getAllValuesOfTarget(source).iterator.next
                    Object matchTarget = matcher.getAllValues(matcher.getParameterNames().get(1), match).iterator().next();
                    assertEquals("Incorrect target", target, matchTarget);
                } else {
                    assertTrue("Incorrect number of matches (should be 0): " + numberOfMatches, numberOfMatches == 0);
                    
                }
                
            }
        }
    }
}
