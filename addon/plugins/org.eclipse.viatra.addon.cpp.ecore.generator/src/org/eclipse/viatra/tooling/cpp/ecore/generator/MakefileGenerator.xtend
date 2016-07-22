/*******************************************************************************
 * Copyright (c) 2014-2016 Robert Doczi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Robert Doczi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.tooling.cpp.ecore.generator

import org.eclipse.viatra.query.tooling.cpp.localsearch.util.fs.FileSystemAccess
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource

/**
 * @author Robert Doczi
 */
class MakefileGenerator {

	val Resource ecoreModel

	new(Resource ecoreModel) {
		this.ecoreModel = ecoreModel
	}

	def generate(FileSystemAccess fsa) {
		fsa.generateFile("Makefile", compileMakefile())
		ecoreModel.generateRule(fsa.createInSubfolder(ecoreModel.resourceName))
	}
	
	private def resourceName(Resource res) {
		val fullName = res.getURI.segment(res.getURI.segmentCount - 1)
		fullName.substring(0, fullName.lastIndexOf('.'))
	}

	private def dispatch void generateRule(Resource model, FileSystemAccess fsa) {
		val rootPackages = ecoreModel.allContents.filter(EPackage).toList
		fsa.generateFile("Rules.mk", compileRule(model.resourceName, rootPackages.map[name]))
		rootPackages.forEach[generateRule(fsa.createInSubfolder(name))]
	}

	private def dispatch void generateRule(EPackage pack, FileSystemAccess fsa) {
		val packages = pack.eAllContents.filter(EPackage)
		fsa.generateFile("Rules.mk", compileRule(pack.name, packages.map[name].toIterable))
		packages.forEach[generateRule(fsa.createInSubfolder(name))]
	}

	private def compileMakefile() '''
		##############################################################################
		# M A K E F I L E
		#
		# NAME: «ecoreModel.resourceName»
		#
		##############################################################################
		
		CXX=g++
		CXXFLAGS=-O3 -std=c++11 -Wall -Wextra -I. -I../../Localsearch_Runtime/
		LFLAGS=
		MAKE=make
		LIBPATH=-L"../../Localsearch_Runtime/Debug" -Wl,-rpath,"../../Localsearch_Runtime/Debug"
		LIBS=-lLocalsearch_Runtime
		
		COMPILE=$(CXX) $(CXXFLAGS) -c -o $@ $<
		LINK=$(CXX) $(LFLAGS) -o $@ $^ $(LIBPATH) $(LIBS)
		
		.SUFFIXES:
		.SUFFIXES: .cpp .h .o
		
		SOURCES := $(wildcard *.cpp)
		OBJECTS := $(SOURCES:%.cpp=%.o)
		BINARY := «ecoreModel.resourceName».out
		
		d :=	.
		dir := 	$(d)/«ecoreModel.resourceName»
		include $(dir)/Rules.mk
		
		%.o:	%.cpp
				$(COMPILE)
				
		$(BINARY): $(OBJECTS)
				$(LINK)
				
		.PHONY: all
		all: $(SOURCES) $(OBJECTS) $(BINARY)
		
		.PHONY: clean
		clean:
			rm -f $(OBJECTS) $(BINARY)
	'''

	private def compileRule(String name, Iterable<String> subfolders) '''
		##############################################################################
		# M O D E L   M A K E   R U L E S
		#
		# NAME: «name»
		#
		##############################################################################
		
		sp				:= $(sp).x
		dirstack_$(sp)	:= $(d)
		d				:= $(dir)
		
		«FOR sf : subfolders» 
			dir	:= $(d)/«sf»
			include $(dir)/Rules.mk
		«ENDFOR»
		
		SOURCES_$(d)	:= $(wildcard $(d)/*.cpp)
		OBJECTS_$(d)	:= $(SOURCES_$(d):%.cpp=%.o)
		
		SOURCES	:= $(SOURCES) $(SOURCES_$(d))
		OBJECTS	:= $(OBJECTS) $(OBJECTS_$(d))
		
		d	:= $(dirstack_$(sp))
		sp	:= $(basename $(sp))
	'''

}
