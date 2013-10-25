package library.gen

import org.eclipse.viatra2.emf.runtime.rules.batch.BatchTransformationRuleFactory
import org.eclipse.viatra2.emf.runtime.transformation.batch.BatchTransformation
import org.eclipse.viatra2.emf.runtime.rules.batch.BatchTransformationStatements
import org.eclipse.viatra2.emf.runtime.modelmanipulation.IModelManipulations
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.viatra2.emf.runtime.modelmanipulation.SimpleModelManipulations
import org.apache.log4j.Level
import com.google.common.base.Preconditions
import java.util.Random
import org.eclipse.incquery.runtime.evm.specific.resolver.FairRandomConflictResolver
import library.base.BasePackage
import library.base.Library
import library.base.Book
import library.base.BookCategory
import library.base.Writer

class LibraryGenerator {
	extension BatchTransformationRuleFactory factory = new BatchTransformationRuleFactory
    extension BatchTransformation transformation
    extension BatchTransformationStatements statements
    extension IModelManipulations manipulation
    
    extension BasePackage libPackage = BasePackage::eINSTANCE   
    extension GenQueriesMatchers genMatchers
    
    val Resource trgResource
    var Library libraryInstance   
    
    val Random rnd = new Random    
    
    new(Resource trgResource) {
        this.trgResource = trgResource
        
        transformation = new BatchTransformation(trgResource.resourceSet)
        statements = new BatchTransformationStatements(transformation)
        manipulation = new SimpleModelManipulations(transformation.iqEngine)
        
        genMatchers = new GenQueriesMatchers(transformation.iqEngine)
        
        transformation.ruleEngine.logger.level = Level::DEBUG
    } 
    
    
    /**
     * Creates the library and fills with random content of the specified size. 
     * Always creates an entirely new library instance.
     */
    def generateLibrary(int numBooks, int numAuthors, int numCitations, int numAuthorships) {
            trgResource.contents.clear
            
            // check arguments
    		Preconditions::checkArgument(numAuthors <= writerCandidateMatcher.countMatches,
    			'''Can generate at most «writerCandidateMatcher.countMatches» Writers''')
    		Preconditions::checkArgument(numBooks <= titleCandidateMatcher.countMatches,
    			'''Can generate at most «titleCandidateMatcher.countMatches» Books''')
    		val maxCitations = numBooks * (numBooks - 1) /2
    		Preconditions::checkArgument(numCitations <= maxCitations,
    			'''Can generate at most «maxCitations» citations''')
    		val maxAuthorships = numBooks * numAuthors
    		Preconditions::checkArgument(numAuthorships <= maxAuthorships,
    			'''Can generate at most «maxAuthorships» authorships''')
    			
            //Initialize library root
            libraryInstance = trgResource.create(library) as Library
            libraryInstance.baseName = "Random generated library"
            
            // Set random resolver
            ruleEngine.conflictResolver = new FairRandomConflictResolver

            //Create all transitions between states
            createBookRule.fireUntil[bookWithTitleMatcher.countMatches >= numBooks]
            createWriterRule.fireUntil[writerWithNamesMatcher.countMatches >= numAuthors]
            createCitationRule.fireUntil[citationMatcher.countMatches >= numCitations]
            createPriorityAuthorshipRule.fireUntil[authorshipMatcher.countMatches >= numAuthorships]
            createAuthorshipRule.fireUntil[authorshipMatcher.countMatches >= numAuthorships]            
    }            	
    
    val createBookRule = createRule.precondition(BookCandidateMatcher::querySpecification).action [
    	val book = libraryInstance.createChild(library_Books, book) as Book
        book.title = title as String
        book.pages = 100 + rnd.nextInt(600)
        book.bookCategory = 
        	bookCategory.getEEnumLiteral(rnd.nextInt(bookCategory.ELiterals.size)).instance as BookCategory
    ].build
    val createWriterRule = createRule.precondition(WriterCandidateMatcher::querySpecification).action [
    	val writer = libraryInstance.createChild(library_Writers, writer) as Writer
    	writer.firstName = first as String
    	writer.lastName = last  	
    ].build
    val createPriorityAuthorshipRule = createRule.precondition(AuthorshipPriorityCandidateMatcher::querySpecification).action [
    	book.writers += author	
    ].build
    val createAuthorshipRule = createRule.precondition(AuthorshipCandidateMatcher::querySpecification).action [
    	book.writers += author	
    ].build
    val createCitationRule = createRule.precondition(CitationCandidateMatcher::querySpecification).action [
    	source.citations += target	
    ].build
}
