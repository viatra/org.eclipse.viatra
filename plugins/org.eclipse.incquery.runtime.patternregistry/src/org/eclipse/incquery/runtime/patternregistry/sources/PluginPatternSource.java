package org.eclipse.incquery.runtime.patternregistry.sources;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.incquery.runtime.IExtensions;

public class PluginPatternSource {

    // private static Map<Pattern, IMatcherFactory<?>> collectGeneratedMatcherFactories() {
    // Map<Pattern, IMatcherFactory<?>> factories = new HashMap<Pattern, IMatcherFactory<?>>();
    // for (IMatcherFactory<?> factory : MatcherFactoryRegistry.getContributedMatcherFactories()) {
    // Pattern pattern = factory.getPattern();
    // Boolean annotationValue = getValueOfQueryExplorerAnnotation(pattern);
    // if (annotationValue != null && annotationValue) {
    // factories.put(pattern, factory);
    // }
    // }
    // return factories;
    // }
    //
    // private static Boolean getValueOfQueryExplorerAnnotation(Pattern pattern) {
    // Annotation annotation = CorePatternLanguageHelper.getFirstAnnotationByName(pattern,
    // IExtensions.QUERY_EXPLORER_ANNOTATION);
    // if (annotation == null) {
    // return null;
    // } else {
    // for (AnnotationParameter ap : annotation.getParameters()) {
    // if (ap.getName().equalsIgnoreCase("display")) {
    // return Boolean.valueOf(((BoolValueImpl) ap.getValue()).isValue());
    // }
    // }
    // return Boolean.TRUE;
    // }
    // }

    public static void initializeRegisteredPatterns() {
        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
        if (extensionRegistry != null) {
            IExtensionPoint extensionPoint = extensionRegistry
                    .getExtensionPoint(IExtensions.MATCHERFACTORY_EXTENSION_POINT_ID);
            if (extensionPoint != null) {
                for (IExtension extension : extensionPoint.getExtensions()) {
                    for (IConfigurationElement configurationElement : extension.getConfigurationElements()) {
                        if (configurationElement.getName().equals("matcher")) {

                        }
                    }
                }
            }
        }
    }

    // private static void prepareMatcherFactory(Map<String, IMatcherFactory<?>> factories, Set<String> duplicates,
    // IConfigurationElement el) {
    // try {
    // String id = el.getAttribute("id");
    // @SuppressWarnings("unchecked")
    // IMatcherFactoryProvider<IMatcherFactory<IncQueryMatcher<IPatternMatch>>> provider =
    // (IMatcherFactoryProvider<IMatcherFactory<IncQueryMatcher<IPatternMatch>>>) el
    // .createExecutableExtension("factoryProvider");
    // IMatcherFactory<IncQueryMatcher<IPatternMatch>> matcherFactory = provider.get();
    // String fullyQualifiedName = matcherFactory.getPatternFullyQualifiedName();
    // if (id.equals(fullyQualifiedName)) {
    // if (factories.containsKey(fullyQualifiedName)) {
    // duplicates.add(fullyQualifiedName);
    // } else {
    // factories.put(fullyQualifiedName, matcherFactory);
    // }
    // } else {
    // throw new UnsupportedOperationException("Id attribute value " + id
    // + " does not equal pattern FQN of factory " + fullyQualifiedName + " in plugin.xml of "
    // + el.getDeclaringExtension().getUniqueIdentifier());
    // }
    // } catch (Exception e) {
    // IncQueryEngine.getDefaultLogger().error(
    // "[MatcherFactoryRegistry] Exception during matcher factory registry initialization "
    // + e.getMessage(), e);
    // }
    // }

}
