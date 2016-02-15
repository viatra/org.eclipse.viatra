package org.eclipse.viatra.migrator;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.osgi.framework.Version;

@SuppressWarnings("all")
public abstract class JavaProjectMigratorData {
  protected final static VersionRange stable = new VersionRange(new Version(1, 2, 0), true, new Version(2, 0, 0), false);
  
  protected final static VersionRange incubation = new VersionRange(new Version(0, 12, 0), true, new Version(1, 0, 0), false);
  
  protected final static LinkedHashMap<String, String> bundleRenames = CollectionLiterals.<String, String>newLinkedHashMap(
    Pair.<String, String>of("org.eclipse.incquery.databinding.runtime", "org.eclipse.viatra.addon.databinding.runtime"), 
    Pair.<String, String>of("org.eclipseincquery.facet.browser", "org.eclipse.viatra.integration.modisco"), 
    Pair.<String, String>of("org.eclipseincquery.patternlanguage", "org.eclipse.viatra.query.patternlanguage"), 
    Pair.<String, String>of("org.eclipseincquery.patternlanguage.emf.tests", "org.eclipse.viatra.query.patternlanguage.emf.tests"), 
    Pair.<String, String>of("org.eclipseincquery.patternlanguage.emf.ui", "org.eclipse.viatra.query.patternlanguage.emf.ui"), 
    Pair.<String, String>of("org.eclipseincquery.patternlanguage.generator", "org.eclipse.viatra.query.patternlanguage.generator"), 
    Pair.<String, String>of("org.eclipseincquery.patternlanguage.tests", "org.eclipse.viatra.query.patternlanguage.tests"), 
    Pair.<String, String>of("org.eclipseincquery.patternlanguage.ui", "org.eclipse.viatra.query.patternlanguage.ui"), 
    Pair.<String, String>of("org.eclipseincquery.querybasedfeatures.runtime", "org.eclipse.viatra.addon.querybasedfeatures.runtime"), 
    Pair.<String, String>of("org.eclipseincquery.querybasedfeatures.tooling", "org.eclipse.viatra.addon.querybasedfeatures.tooling"), 
    Pair.<String, String>of("org.eclipse.incquery.runtime", "org.eclipse.viatra.query.runtime"), 
    Pair.<String, String>of("org.eclipseincquery.runtime.base", "org,eclipse.viatra.query.runtime.base"), 
    Pair.<String, String>of("org.eclipse.incquery.runtime.base.itc", "org.eclipseviatra.query.runtime.base.itc"), 
    Pair.<String, String>of("org.eclipse.incquery.runtime.base.itc.tests", "org.eclipseviatra.query.runtime.base.itc.tests"), 
    Pair.<String, String>of("org.eclipse.incquery.runtime.evm", "org.eclipseviatra.transformation.evm"), 
    Pair.<String, String>of("org.eclipse.incquery.runtime.evm.transactions", "org.eclipseviatra.transformation.evm.transactions"), 
    Pair.<String, String>of("org.eclipseincquery.runtime.gmf", "org.eclipse.viatra.integration.gmf"), 
    Pair.<String, String>of("org.eclipseincquery.runtime.graphiti", "org.eclipse.viatra.integration.graphiti"), 
    Pair.<String, String>of("org.eclipse.incquery.runtime.localsearch", "org.eclipse.viatra.query.runtime.localsearch"), 
    Pair.<String, String>of("org.eclipse.incquery.runtime.matchers", "org.eclipse.viatra.query.runtime.matchers"), 
    Pair.<String, String>of("org.eclipse.incquery.runtime.rete", "org.eclipse.viatra.query.runtime.rete"), 
    Pair.<String, String>of("org.eclipse.incquery.runtime.rete.recipes", "org.eclipse.viatra.query.runtime.rete.recipes"), 
    Pair.<String, String>of("org.eclipse.incquery.runtime.tests", "org.eclipse.viatra.query.runtime.tests"), 
    Pair.<String, String>of("org.eclipse.incquery.snapshot", "org.eclipse.viatra.query.testing.snapshot"), 
    Pair.<String, String>of("org.eclipse.incquery.snapshot.edit", "org.eclipse.viatra.query.testing.snapshot.edit"), 
    Pair.<String, String>of("org.eclipse.incquery.snapshot.editor", "org.eclipse.viatra.query.testing.snapshot.editor"), 
    Pair.<String, String>of("org.eclipse.incquery.testing.core", "org.eclipse.viatra.query.testing.core"), 
    Pair.<String, String>of("org.eclipse.incquery.testing.queries", "org.eclipse.viatra.query.testing.queries"), 
    Pair.<String, String>of("org.eclipse.incquery.testing.ui", "org.eclipse.viatra.query.testing.ui"), 
    Pair.<String, String>of("org.eclipse.incquery.tooling.core", "org.eclipse.viatra.query.tooling.core"), 
    Pair.<String, String>of("org.eclipse.incquery.tooling.debug", "org.eclipse.viatra.query.tooling.debug"), 
    Pair.<String, String>of("org.eclipse.incquery.tooling.generator.model", "org.eclipse.viatra.query.tooling.generator.model"), 
    Pair.<String, String>of("org.eclipse.incquery.tooling.generator.model.ui", "org.eclipse.viatra.query.tooling.generator.model.ui"), 
    Pair.<String, String>of("org.eclipse.incquery.tooling.localsearch.ui", "org.eclipse.viatra.query.tooling.localsearch.ui"), 
    Pair.<String, String>of("org.eclipse.incquery.tooling.ui", "org.eclipse.viatra.query.tooling.ui"), 
    Pair.<String, String>of("org.eclipse.incquery.tooling.ui.retevis", "org.eclipse.viatra.query.tooling.ui.retevis"), 
    Pair.<String, String>of("org.eclipse.incquery.uml", "org.eclipse.viatra.integration.uml"), 
    Pair.<String, String>of("org.eclipse.incquery.uml.test", "org.eclipse.viatra.integration.uml.test"), 
    Pair.<String, String>of("org.eclipse.incquery.validation.core", "org.eclipse.viatra.addon.validation.core"), 
    Pair.<String, String>of("org.eclipse.incquery.validation.runtime", "org.eclipse.viatra.addon.validation.runtime"), 
    Pair.<String, String>of("org.eclipse.incquery.validation.runtime.ui", "org.eclipse.viatra.addon.validation.runtime.ui"), 
    Pair.<String, String>of("org.eclipse.incquery.validation.tooling", "org.eclipse.viatra.addon.validation.tooling"), 
    Pair.<String, String>of("org.eclipse.incquery.viewers.runtime", "org.eclipse.viatra.addon.viewers.runtime"), 
    Pair.<String, String>of("org.eclipse.incquery.viewers.runtime.zest", "org.eclipse.viatra.addon.viewers.runtime.zest"), 
    Pair.<String, String>of("org.eclipse.incquery.viewers.tooling.ui", "org.eclipse.viatra.addon.viewers.tooling"), 
    Pair.<String, String>of("org.eclipse.incquery.viewers.tooling.ui.zest", "org.eclipse.viatra.addon.viewers.tooling.zest"), 
    Pair.<String, String>of("org.eclipse.incquery.viewmodel", "org.eclipse.viatra.transformation.views"), 
    Pair.<String, String>of("org.eclipse.incquery.xcore", "org.eclipse.viatra.integration.xcore"), 
    Pair.<String, String>of("org.eclipse.incquery.xcore.model", "org.eclipse.viatra.integration.xcore.model"), 
    Pair.<String, String>of("org.eclipse.incquery.xcore.modeleditor", "org.eclipse.viatra.integration.xcore.modeleditor"), 
    Pair.<String, String>of("org.eclipse.incquery.xcore.ui", "org.eclipse.viatra.integration.xcore.ui"), 
    Pair.<String, String>of("org.eclipse.viatra.emf.mwe2integration", "org.eclipse.viatra.integration.mwe2"), 
    Pair.<String, String>of("org.eclipse.viatra.emf.mwe2integration.debug", "org.eclipse.viatra.integration.mwe2.debug"), 
    Pair.<String, String>of("org.eclipse.viatra.emf.mwe2integration.test", "org.eclipse.viatra.integration.mwe2.test"), 
    Pair.<String, String>of("org.eclipse.viatra.emf.runtime", "org.eclipse.viatra.transformation.runtime.emf"), 
    Pair.<String, String>of("org.eclipse.viatra.emf.runtime.debug", "org.eclipse.viatra.transformation.debug"), 
    Pair.<String, String>of("org.eclipse.viatra.emf.runtime.debug.ui", "org.eclipse.viatra.transformation.debug.ui"), 
    Pair.<String, String>of("org.eclipse.viatra.emf.runtime.tracer", "org.eclipse.viatra.transformation.tracer"));
  
  protected final static LinkedHashMap<String, VersionRange> bundleVersions = CollectionLiterals.<String, VersionRange>newLinkedHashMap(
    Pair.<String, VersionRange>of("org.eclipse.viatra.addon.databinding.runtime", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.integration.modisco", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.patternlanguage", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.patternlanguage.emf.tests", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.patternlanguage.emf.ui", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.patternlanguage.generator", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.patternlanguage.tests", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.patternlanguage.ui", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.addon.querybasedfeatures.runtime", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.addon.querybasedfeatures.tooling", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.runtime", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org,eclipse.viatra.query.runtime.base", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipseviatra.query.runtime.base.itc", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipseviatra.query.runtime.base.itc.tests", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipseviatra.transformation.evm", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipseviatra.transformation.evm.transactions", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.integration.gmf", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.integration.graphiti", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.runtime.localsearch", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.runtime.matchers", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.runtime.rete", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.runtime.rete.recipes", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.runtime.tests", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.testing.snapshot", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.testing.snapshot.edit", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.testing.snapshot.editor", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.testing.core", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.testing.queries", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.testing.ui", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.tooling.core", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.tooling.debug", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.tooling.generator.model", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.tooling.generator.model.ui", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.tooling.localsearch.ui", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.tooling.ui", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.query.tooling.ui.retevis", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.integration.uml", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.integration.uml.test", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.addon.validation.core", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.addon.validation.runtime", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.addon.validation.runtime.ui", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.addon.validation.tooling", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.addon.viewers.runtime", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.addon.viewers.runtime.zest", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.addon.viewers.tooling", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.addon.viewers.tooling.zest", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.transformation.views", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.integration.xcore", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.integration.xcore.model", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.integration.xcore.modeleditor", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.integration.xcore.ui", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.integration.mwe2", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.integration.mwe2.debug", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.integration.mwe2.test", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.transformation.runtime.emf", JavaProjectMigratorData.stable), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.transformation.debug", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.transformation.debug.ui", JavaProjectMigratorData.incubation), 
    Pair.<String, VersionRange>of("org.eclipse.viatra.transformation.tracer", JavaProjectMigratorData.incubation));
  
  /**
   * Package or Type renames. Shall be fully qualified.
   * 
   * Package names shall be end with '.' while type names shall be not. List shall be ordered so for every entry key
   * there is no prefix is present in the preceding entry keys. Bundle renames are added automatically
   */
  protected final static Map<String, String> qualifiedNameRenames = JavaProjectMigratorData.initQualifiedRenames();
  
  public static Map<String, String> initQualifiedRenames() {
    Pair<String, String> _mappedTo = Pair.<String, String>of("org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine", "org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine");
    Pair<String, String> _mappedTo_1 = Pair.<String, String>of("org.eclipse.incquery.runtime.api.IncQueryEngine", "org.eclipse.viatra.query.runtime.api.ViatraQueryEngine");
    Pair<String, String> _mappedTo_2 = Pair.<String, String>of("org.eclipse.incquery.runtime.api.IncQueryMatcher", "org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher");
    Pair<String, String> _mappedTo_3 = Pair.<String, String>of("org.eclipse.incquery.runtime.api.impl.BasePatternGroup", "org.eclipse.viatra.query.runtime.api.impl.BaseQueryGroup");
    Pair<String, String> _mappedTo_4 = Pair.<String, String>of("org.eclipse.incquery.runtime.api.PackageBasedPatternGroup", "org.eclipse.viatra.query.runtime.api.PackageBasedQueryGroup");
    Pair<String, String> _mappedTo_5 = Pair.<String, String>of("org.eclipse.incquery.runtime.api.GenericPatternGroup", "org.eclipse.viatra.query.runtime.api.GenericQueryGroup");
    Pair<String, String> _mappedTo_6 = Pair.<String, String>of("org.eclipse.incquery.runtime.api.IncQueryEngineManager", "org.eclipse.viatra.query.runtime.api.ViatraQueryEngineManager");
    Pair<String, String> _mappedTo_7 = Pair.<String, String>of("org.eclipse.incquery.runtime.api.IncQueryEngineLifecycleListener", "org.eclipse.viatra.query.runtime.api.ViatraQueryEngineLifecycleListener");
    Pair<String, String> _mappedTo_8 = Pair.<String, String>of("org.eclipse.incquery.runtime.api.IncQueryModelUpdateListener", "org.eclipse.viatra.query.runtime.api.ViatraQueryModelUpdateListener");
    Pair<String, String> _mappedTo_9 = Pair.<String, String>of("org.eclipse.incquery.runtime.api.IncQueryEngineInitializationListener", "org.eclipse.viatra.query.runtime.api.ViatraQueryEngineInitializationListener");
    Pair<String, String> _mappedTo_10 = Pair.<String, String>of("org.eclipse.incquery.runtime.internal.apiimpl.IncQueryEngineImpl", "org.eclipse.viatra.query.runtime.internal.apiimpl.ViatraQueryEngineImpl");
    Pair<String, String> _mappedTo_11 = Pair.<String, String>of("org.eclipse.viatra.emf.runtime.update.IQEngineUpdateCompleteProvider", "org.eclipse.viatra.transformation.evm.update.QueryEngineUpdateCompleteProvider");
    final LinkedHashMap<String, String> result = CollectionLiterals.<String, String>newLinkedHashMap(_mappedTo, _mappedTo_1, _mappedTo_2, _mappedTo_3, _mappedTo_4, _mappedTo_5, _mappedTo_6, _mappedTo_7, _mappedTo_8, _mappedTo_9, _mappedTo_10, _mappedTo_11);
    Set<Map.Entry<String, String>> _entrySet = JavaProjectMigratorData.bundleRenames.entrySet();
    List<Map.Entry<String, String>> _sortWith = IterableExtensions.<Map.Entry<String, String>>sortWith(_entrySet, new Comparator<Map.Entry<String, String>>() {
      @Override
      public int compare(final Map.Entry<String, String> o1, final Map.Entry<String, String> o2) {
        String _key = o2.getKey();
        int _length = _key.length();
        String _key_1 = o1.getKey();
        int _length_1 = _key_1.length();
        return Integer.compare(_length, _length_1);
      }
    });
    for (final Map.Entry<String, String> entry : _sortWith) {
      String _key = entry.getKey();
      String _plus = (_key + ".");
      String _value = entry.getValue();
      String _plus_1 = (_value + ".");
      result.put(_plus, _plus_1);
    }
    return result;
  }
}
