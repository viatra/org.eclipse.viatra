package org.eclipse.viatra.examples.bpm.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.examples.bpm.tests.queries.NextActivityMatcher
import org.eclipse.viatra.examples.bpm.tests.queries.ProcessTasksMatcher
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.ViatraQueryModelUpdateListener.ChangeLevel
import org.eclipse.viatra.query.testing.core.ModelLoadHelper
import org.eclipse.viatra.query.testing.core.injector.EMFPatternLanguageInjectorProvider
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra.examples.bpm.process.ProcessFactory

import static org.junit.Assert.*
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineManager
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryModelUpdateListener
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineLifecycleListener
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineInitializationListener
import org.eclipse.xtend.lib.annotations.Accessors

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class RuntimeListenerTest {
  @Inject extension ModelLoadHelper

  @Test
  def engineInitializationListenerTest(){
    
    val rs = new ResourceSetImpl
    val manager = ViatraQueryEngineManager::instance
    val listener = new InitListener
    
    manager.addQueryEngineInitializationListener(listener)
    
    AdvancedViatraQueryEngine::createUnmanagedEngine(new EMFScope(rs))
    assertTrue(listener.engines.empty)
    
    val e2 = ViatraQueryEngine::on(new EMFScope(rs))
    assertArrayEquals(newArrayList(e2), listener.engines)
    
    manager.removeQueryEngineInitializationListener(listener)
    val e3  = AdvancedViatraQueryEngine::createUnmanagedEngine(new EMFScope(rs))
    
    assertTrue(!listener.engines.contains(e3))
  }
  
  @Test
  def engineLifecycleListenerTest(){
    
    val resource = loadModelFromUri("org.eclipse.viatra.examples.bpm.tests/model/Simple.process")
    val engine = AdvancedViatraQueryEngine::createUnmanagedEngine(new EMFScope(resource))
    val listener = new LifycycleListener
    
    engine.addLifecycleListener(listener)
    
    val matcher = ProcessTasksMatcher::on(engine)
    assertArrayEquals(newArrayList(matcher), listener.matchers)
    
    //Cannot externally taint the engine; turning check off 
    //engine.logger.fatal("Tainting in progress")
    //assertTrue(listener.tainted)
    
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
    
    val resource = loadModelFromUri("org.eclipse.viatra.examples.bpm.tests/model/Simple.process")
    val engine = AdvancedViatraQueryEngine::createUnmanagedEngine(new EMFScope(resource))
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

class InitListener implements ViatraQueryEngineInitializationListener{
  
  @Property val engines = newArrayList()
  
  override engineInitialized(AdvancedViatraQueryEngine engine) {
      assertTrue(AdvancedViatraQueryEngine::from(engine).managed)
      engines += engine
  }
  
}

class LifycycleListener implements ViatraQueryEngineLifecycleListener{
  
//  @Property
	var tainted = false 
//  @Property 
  	var disposed = false 
//  @Property
	var wiped = false
//  @Property
	var matchers = newArrayList
  
  def isTainted() {
  	tainted
  }
  
  def setTainted(boolean tainted) {
  	this.tainted = tainted
  }
  
  def isDisposed() {
  	disposed
  }
  
  def setDisposed(boolean disposed) {
  	this.disposed = disposed
  }
  
  def isWiped() {
  	wiped
  }
  
  def setWiped(boolean wiped) {
  	this.wiped = wiped
  }
  
  def getMatchers() {
  	matchers
  }
  
  override engineBecameTainted(String description, Throwable t) {
    tainted = true
  }
  
  override engineDisposed() {
    disposed = true
  }
  
  override engineWiped() {
    wiped = true
  }
  
  override matcherInstantiated(ViatraQueryMatcher<? extends IPatternMatch> matcher) {
    matchers += matcher
  }
  
}

class ModelUpdateListener implements ViatraQueryModelUpdateListener{
  
  @Accessors ChangeLevel myLevel
  
  @Accessors val changes = newArrayList
  
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