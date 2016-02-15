package org.eclipse.viatra.examples.bpm.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.addon.querybasedfeatures.runtime.QueryBasedFeatureHelper
import org.eclipse.viatra.addon.querybasedfeatures.runtime.QueryBasedFeatureKind
import org.eclipse.viatra.examples.bpm.operation.Checklist
import org.eclipse.viatra.examples.bpm.operation.OperationPackage
import org.eclipse.viatra.examples.bpm.process.Task
import org.eclipse.viatra.examples.bpm.system.Job
import org.eclipse.viatra.examples.bpm.system.System
import org.eclipse.viatra.examples.bpm.system.Data
import org.eclipse.viatra.examples.bpm.process.Process
import org.eclipse.viatra.examples.bpm.tests.queries.JobTasksMatcher
import org.eclipse.viatra.examples.bpm.tests.queries.ProcessTasksMatcher
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.extensibility.QuerySpecificationRegistry
import org.eclipse.viatra.query.testing.core.ModelLoadHelper
import org.eclipse.viatra.query.testing.core.TestExecutor
import org.eclipse.viatra.query.testing.core.injector.EMFPatternLanguageInjectorProvider
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class QueryBasedFeatureTest {
 
  @Inject extension TestExecutor
  @Inject extension ModelLoadHelper

  @Before
  def prepareQueries(){
    
    assertNotNull(QuerySpecificationRegistry::getQuerySpecification("org.eclipse.viatra.examples.bpm.queries.system.JobTaskCorrespondence"))
    assertNotNull(QuerySpecificationRegistry::getQuerySpecification("org.eclipse.viatra.examples.bpm.queries.system.DataTaskReadCorrespondence"))
    assertNotNull(QuerySpecificationRegistry::getQuerySpecification("org.eclipse.viatra.examples.bpm.queries.system.DataTaskWriteCorrespondence"))
    assertNotNull(QuerySpecificationRegistry::getQuerySpecification("org.eclipse.viatra.examples.bpm.queries.system.JobInfoCorrespondence"))
    assertNotNull(QuerySpecificationRegistry::getQuerySpecification("org.eclipse.viatra.examples.bpm.queries.operation.ChecklistEntryJobCorrespondence"))
    assertNotNull(QuerySpecificationRegistry::getQuerySpecification("org.eclipse.viatra.examples.bpm.queries.operation.ChecklistEntryTaskCorrespondence"))
    assertNotNull(QuerySpecificationRegistry::getQuerySpecification("org.eclipse.viatra.examples.bpm.queries.operation.ChecklistProcessCorrespondence"))
    
  }

  @Test
  def simpleGetterTest(){
    
    val rs = new ResourceSetImpl
    val resource = loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.system")
    
    val sys = resource.contents.get(0) as System
    sys.contains.forEach[
      assertTrue(it.tasks.empty)
    ]
    sys.data.forEach[
        assertTrue(it.readingTask.empty)
        assertTrue(it.writingTask.empty)
    ]

    loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.process")
    sys.contains.forEach[
      val job = it as Job
      assertTrue(job.taskIds.size == job.tasks.size)
      // all elements resolved
      assertTrue(job.taskIds.filter[tid |
        job.tasks.filter[
          val task = it as Task
          task.id == tid
        ].empty
      ].empty)
      assertTrue(job.tasks.filter[
        val task = it as Task
        !job.taskIds.contains(task.id)
      ].empty)
    ]
    sys.data.forEach[
      val data = it as Data
      assertTrue(data.readingTaskIds.size == data.readingTask.size)
      // all elements resolved
      assertTrue(data.readingTask.filter[
        val task = it as Task
        !data.readingTaskIds.contains(task.id)
      ].empty)
      assertTrue(data.writingTaskIds.size == data.writingTask.size)
      // all elements resolved
      assertTrue(data.writingTask.filter[
        val task = it as Task
        !data.writingTaskIds.contains(task.id)
      ].empty)
    ]
        
  }
  
  @Test
  def simpleModifyTest(){
    val rs = new ResourceSetImpl
    val resource = loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.system")
    loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.process")
    
    val sys = resource.contents.get(0) as System
    sys.contains.forEach[
      assertTrue(it.tasks.size == it.taskIds.size)
    ]
    
    val job = sys.contains.get(0)
    
    val engine = AdvancedViatraQueryEngine::on(new EMFScope(rs))
    val matcher = ProcessTasksMatcher::on(engine)
    matcher.forEachMatch[
      if(!job.tasks.contains(task)){
        job.taskIds += task.id
      }
      assertTrue(job.tasks.contains(task))
    ]
    
    val data = sys.data.get(0)
    data.readingTaskIds.clear
    assertTrue(data.readingTask.empty)
  }
  
  @Test
  def singleFeatureTest(){
    val rs = new ResourceSetImpl
    val resource = loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.operation")
    
    val checklist = resource.contents.get(0) as Checklist
    
    checklist.entries.forEach[
      assertNull(it.task)
    ]
    
    loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.process")
    val engine = AdvancedViatraQueryEngine::on(new EMFScope(rs))
    val procMatcher = ProcessTasksMatcher::on(engine)
    
    checklist.entries.forEach[
      if(it.taskId != null){
        assertTrue(it.task.id == it.taskId)
      }
      
      val t = it.task
      t.id = "changedID"
      assertNull(it.task)
      
      it.taskId = "changedID"
      assertTrue(it.task  == t)
      
      it.taskId = null
      assertNull(it.task)
      
      val entry = it
      
      procMatcher.forEachMatch[
        entry.taskId = task.id
      ]
    ]
  }
  
  @Test
  def violateSingleFeatureTest(){
    
    val rs = new ResourceSetImpl
    val resource = loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.operation")
    val resource2 = loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.process")
    
    val checklist = resource.contents.get(0) as Checklist
    val proc = resource2.contents.get(0) as Process
    val entry  =  checklist.entries.get(0)
    val tid = entry.taskId
    proc.contents.forEach[
      it.id = tid
    ]
    
    val engine = ViatraQueryEngine::on(new EMFScope(rs))
    engine.registerLogger
    
    entry.task
    
    val logOut = engine.retrieveLoggerOutput
    assertTrue(logOut.contains("[QueryBasedFeature] Space-time continuum breached (should never happen): multiple values for single feature!"));
  }
  
  @Test
  def initDuringMatcherBuilding(){
    
    val rs = new ResourceSetImpl
    loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.system")
    loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.process")
    
    val engine = AdvancedViatraQueryEngine::on(new EMFScope(rs))
    engine.registerLogger
    
    val matcher = JobTasksMatcher::on(engine)
    
    assertTrue(matcher.countMatches > 0);
    matcher.forEachMatch[
      job.taskIds.clear
      assertTrue(job.tasks.empty)
    ]
  }
  
  @Test
  def uncachedSingleFeatureTest() {
    
    val rs = new ResourceSetImpl
    loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.system")
    loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.process")
    val resource = loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.operation")
    
    val singleqbf = QueryBasedFeatureHelper::getQueryBasedFeatureHandler(rs,
       OperationPackage::eINSTANCE.checklistEntry_Task,
       "org.eclipse.viatra.examples.bpm.queries.operation.ChecklistEntryTaskCorrespondence",
       "CLE", "Task",
       QueryBasedFeatureKind::SINGLE_REFERENCE, false);
    
    
    val checklist = resource.contents.get(0) as Checklist
    
    checklist.entries.forEach[
      assertEquals(singleqbf.getValue(it),it.task)
    ]
  }
  
  @Test
  def uncachedManyFeatureTest() {
    val rs = new ResourceSetImpl
    loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.system")
    loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.process")
    val resource = loadAdditionalResourceFromUri(rs,"org.eclipse.viatra.examples.bpm.tests/model/Simple.operation")
    
    val manyqbf = QueryBasedFeatureHelper::getQueryBasedFeatureHandler(
          rs, OperationPackage::eINSTANCE.checklistEntry_Jobs,
          "org.eclipse.viatra.examples.bpm.queries.operation.ChecklistEntryJobCorrespondence", "CLE",
          "Job", QueryBasedFeatureKind::MANY_REFERENCE, false);
    
    val checklist = resource.contents.get(0) as Checklist
    
    checklist.entries.forEach[
      assertEquals(manyqbf.getValue(it),it.jobs)
    ]
  }
}
