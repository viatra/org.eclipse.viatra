package org.eclipse.incquery.examples.bpm.tests

import org.junit.runner.RunWith
import org.eclipse.incquery.testing.core.injector.EMFPatternLanguageInjectorProvider
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import com.google.inject.Inject
import org.eclipse.incquery.testing.core.TestExecutor
import org.eclipse.incquery.testing.core.ModelLoadHelper
import org.eclipse.incquery.testing.core.SnapshotHelper
import org.junit.Test
import org.eclipse.incquery.runtime.api.IncQueryEngineManager
import org.eclipse.incquery.runtime.api.IncQueryEngineInitializationListener
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine
import static org.junit.Assert.*
import org.eclipse.incquery.runtime.api.IncQueryEngine
import org.eclipse.incquery.runtime.api.IncQueryEngineLifecycleListener
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.api.IncQueryMatcher
import org.eclipse.incquery.runtime.api.IncQueryModelUpdateListener
import org.eclipse.incquery.runtime.api.IncQueryModelUpdateListener$ChangeLevel
import org.eclipse.incquery.examples.bpm.queries.ProcessTasksMatcher
import process.ProcessFactory
import org.eclipse.incquery.examples.bpm.queries.NextActivityMatcher

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class QueryBasedFeatureTests {
  @Inject extension TestExecutor
  @Inject extension ModelLoadHelper
  @Inject extension SnapshotHelper

  @Test
  def engineInitializationListenerTest(){
    
    val rs = new ResourceSetImpl
    val manager = IncQueryEngineManager::instance
    val listener = new InitListener
    
    manager.addIncQueryEngineInitializationListener(listener)
    
    AdvancedIncQueryEngine::createUnmanagedEngine(rs)
    assertTrue(listener.engines.empty)
    
    val e2 = IncQueryEngine::on(rs)
    assertArrayEquals(newArrayList(e2), listener.engines)
    
    manager.removeIncQueryEngineInitializationListener(listener)
    val e3  = AdvancedIncQueryEngine::createUnmanagedEngine(rs)
    
    assertTrue(!listener.engines.contains(e3))
  }
  
  @Test
  def engineLifecycleListenerTest(){
    
    val resource = loadModelFromUri("org.eclipse.incquery.examples.bpm.tests/model/Simple.process")
    val engine = AdvancedIncQueryEngine::createUnmanagedEngine(resource)
    val listener = new LifycycleListener
    
    engine.addLifecycleListener(listener)
    
    val matcher = ProcessTasksMatcher::on(engine)
    assertArrayEquals(newArrayList(matcher), listener.matchers)
    
    engine.logger.fatal("Tainting in progress")
    assertTrue(listener.tainted)
    
    engine.wipe
    assertTrue(listener.wiped)

    listener.wiped = false
    engine.dispose
    assertTrue(listener.wiped)
    assertTrue(listener.disposed)
    
    engine.removeLifecycleListener(listener)
    listener.disposed = false
    engine.dispose
    assertFalse(listener.disposed)
    
  } 
  
  @Test
  def modelUpdateListenerTest(){
    
    val resource = loadModelFromUri("org.eclipse.incquery.examples.bpm.tests/model/Simple.process")
    val engine = AdvancedIncQueryEngine::createUnmanagedEngine(resource)
    val model = new ModelUpdateListener(ChangeLevel::MODEL)
    
    engine.addModelUpdateListener(model)

    val matcher = ProcessTasksMatcher::on(engine)
    model.changes.clear
    
    matcher.forOneArbitraryMatch[
      task.name = "changedName"
      val task2 = ProcessFactory::eINSTANCE.createTask// => [name = "newName"]
      task.parent.contents += task2
    ]
    /*  NOTE: the second change is actually MATCHSET,
     *  but since no listener is registered with that level,
     *  it will be considered as INDEX */
    assertArrayEquals(newArrayList(ChangeLevel::MODEL,ChangeLevel::INDEX), model.changes)
    
    val index = new ModelUpdateListener(ChangeLevel::INDEX)
    engine.addModelUpdateListener(index)
    matcher.forOneArbitraryMatch[
      task.name = "changedName2"
      val task2 = ProcessFactory::eINSTANCE.createTask// => [name = "newName"]
      task.parent.contents += task2
    ]
    /*  NOTE: the change is actually MATCHSET,
     *  but since no listener is registered with that level,
     *  it will be considered as INDEX */
    assertArrayEquals(newArrayList(ChangeLevel::INDEX), index.changes)
    index.changes.clear
    
    val matchset = new ModelUpdateListener(ChangeLevel::MATCHSET)
    engine.addModelUpdateListener(matchset)
    matcher.forOneArbitraryMatch[
      task.name = "changedName3"
      val task2 = ProcessFactory::eINSTANCE.createTask// => [name = "newName"]
      task.parent.contents += task2
    ]
    assertArrayEquals(newArrayList(ChangeLevel::MATCHSET), matchset.changes)
    assertArrayEquals(newArrayList(ChangeLevel::MATCHSET), index.changes)

    matchset.changes.clear
    // test new matcher added
    val nextMatcher = NextActivityMatcher::on(engine)
    matcher.forOneArbitraryMatch[
      val task2 = ProcessFactory::eINSTANCE.createTask// => [name = "newName"]
      task.parent.contents += task2
      task.next += task2
    ]
    assertArrayEquals(newArrayList(ChangeLevel::MATCHSET,ChangeLevel::MATCHSET), matchset.changes)
    
    // test removal
    engine.removeModelUpdateListener(model)
    model.changes.clear
    
    nextMatcher.forOneArbitraryMatch[
      next.name = "changedName4"
      next.previous.remove(act)
    ]
    engine.removeModelUpdateListener(matchset)

    index.changes.clear
    matcher.forOneArbitraryMatch[
      val task2 = ProcessFactory::eINSTANCE.createTask// => [name = "newName"]
      task.parent.contents += task2
    ]
    assertArrayEquals(newArrayList(ChangeLevel::INDEX), index.changes)
    assertTrue(model.changes.empty)
    
    //test wipe
    engine.wipe
    engine.logger.fatal("Tainting in progress")
    
    //test unsuccesful remove
    index.myLevel = ChangeLevel::MATCHSET
    val model2 = new ModelUpdateListener(ChangeLevel::MODEL)
    engine.addModelUpdateListener(model2)
    engine.removeModelUpdateListener(index)
    engine.removeModelUpdateListener(model)
    engine.removeModelUpdateListener(model2)
    
    // test all listeners removed
    val matcher2 = ProcessTasksMatcher::on(engine)
    matcher2.forOneArbitraryMatch[
      val task2 = ProcessFactory::eINSTANCE.createTask// => [name = "newName"]
      task.parent.contents += task2
    ]
    
    // test multi add
    engine.addModelUpdateListener(model)
    engine.addModelUpdateListener(model)
    engine.removeModelUpdateListener(index)
    // test dispose
    engine.dispose
    engine.removeModelUpdateListener(model)
    
  }
  
}

class InitListener implements IncQueryEngineInitializationListener{
  
  @Property val engines = newArrayList()
  
  override engineInitialized(AdvancedIncQueryEngine engine) {
      assertTrue(AdvancedIncQueryEngine::from(engine).managed)
      engines += engine
  }
  
}

class LifycycleListener implements IncQueryEngineLifecycleListener{
  
  @Property var tainted = false 
  @Property var disposed = false 
  @Property var wiped = false
  @Property var matchers = newArrayList
  
  override engineBecameTainted() {
    tainted = true
  }
  
  override engineDisposed() {
    disposed = true
  }
  
  override engineWiped() {
    wiped = true
  }
  
  override matcherInstantiated(IncQueryMatcher<? extends IPatternMatch> matcher) {
    matchers += matcher
  }
  
}

class ModelUpdateListener implements IncQueryModelUpdateListener{
  
  @Property ChangeLevel myLevel
  
  @Property val changes = newArrayList
  
  new(ChangeLevel level) {
    myLevel = level
  }
  
  override getLevel() {
    myLevel
  }
  
  override notifyChanged(ChangeLevel changeLevel) {
    assertFalse(changeLevel.compareTo(myLevel) < 0)
    changes += changeLevel
  }
  
}