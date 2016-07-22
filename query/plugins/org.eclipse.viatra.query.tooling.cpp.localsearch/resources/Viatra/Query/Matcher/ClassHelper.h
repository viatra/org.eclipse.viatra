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
#pragma once

#include <stddef.h>
#include <map>
#include <set>
#include <list>

#include "IClassHelper.h"
#include "../Util/Defs.h"

namespace Viatra {
namespace Query {
namespace Matcher {

/**
 * @brief Helper for tracking class inheritance.
 *
 * This class helps keep track of inheritance relationships using an inheritance matrix.
 * For example if the inheritance is as follows:
 *
 * @dot
 * digraph inheritance {
 *  node [shape=record, fontname=Helvetica, fontsize=10 ];
 *  Foo [ label="class Foo" ];
 *  Bar [ label="class Bar" ];
 *  SpecFoo [ label="class SpecFoo" ];
 *  SpecFoo -> Foo [ arrowhead="empty", style="solid" ];
 * }
 * @enddot
 *
 * the inheritance matrix will look like this:
 * |    -      |    Foo    |    Bar    | SpecFoo  |
 * |-----------|-----------|-----------|----------|
 * |    Foo    |   true    |   false   |   false  |
 * |    Bar    |   false   |   true    |   false  |
 * | SpecFoo   |   true    |   false   |   true   |
 */
class ClassHelper: public IClassHelper {
public:
    bool is_super_type(const EClass& child, const EClass& parent) const;

    /**
     * Builder for creating the inheritance matrix for the ClassHelper.
     */
    class ClassHelperBuilder {
    public:

        /**
         * Create the actual ClassHelper instance.
         * @return The ClassHelper instance.
         */
        IClassHelper* build();

        /**
         * Sets the currently configured type.
         *
         * @param current The type to be configured.
         *
         * @return The builder instance.
         */
        ClassHelperBuilder& forClass(EClass current);

        /**
         * Sets the current type to have no super type.
         *
         * @return The builder instance.
         */
        ClassHelperBuilder& noSuper();

        /**
         * Sets the specified type to be the super type of the current type.
         *
         * @param super The super type.
         *
         * @return The builder instance.
         */
        ClassHelperBuilder& setSuper(EClass super);

        /**
         * Sets the specified types to be the super type of the current type.
         *
         * @param super A list of types.
         *
         * @return The builder instance.
         */
        ClassHelperBuilder& setSuper(const std::list<EClass>& super);

        friend class ClassHelper;

    private:
        ClassHelperBuilder();

        std::map<EClass, std::set<EClass> > _classRelationshipMap;
        int _current;
    };

    /**
     * Creates a builder instance.
     *
     * @return The builder instance.
     */
    static ClassHelperBuilder builder();

private:
    ClassHelper(std::map<EClass, std::map<EClass, bool> > inheritanceMatrix);

    std::map<EClass, std::map<EClass, bool> > _inheritanceMatrix;
};

bool ClassHelper::is_super_type(const EClass& child, const EClass& parent) const {
	return (*(*_inheritanceMatrix.find(child)).second.find(parent)).second;
}

IClassHelper* ClassHelper::ClassHelperBuilder::build() {
	size_t nrOfClasses = _classRelationshipMap.size();
	std::map<EClass, std::map<EClass, bool> > inheritanceMatrix;
	for (size_t i = 0; i < nrOfClasses; i++) {
		for (size_t j = 0; j < nrOfClasses; j++) {
			if (i == j) {
				inheritanceMatrix[i][j] = true;
			}
			else {
				auto currentClassParents = _classRelationshipMap[i];
				inheritanceMatrix[i][j] = currentClassParents.find(j) != currentClassParents.end();
			}
		}
	}
	return new ClassHelper(inheritanceMatrix);
}

ClassHelper::ClassHelperBuilder& ClassHelper::ClassHelperBuilder::forClass(EClass current) {
	_current = current;
	return *this;
}

ClassHelper::ClassHelperBuilder& ClassHelper::ClassHelperBuilder::noSuper() {
	_classRelationshipMap[_current];
	return *this;
}

ClassHelper::ClassHelperBuilder& ClassHelper::ClassHelperBuilder::setSuper(EClass super) {
	_classRelationshipMap[_current].insert(super);
	return *this;
}

ClassHelper::ClassHelperBuilder& ClassHelper::ClassHelperBuilder::setSuper(const std::list<EClass>& super) {
	_classRelationshipMap[_current].insert(super.begin(), super.end());
	return *this;
}

ClassHelper::ClassHelperBuilder::ClassHelperBuilder() :
	_current(0) {
}

ClassHelper::ClassHelperBuilder ClassHelper::builder() {
	return ClassHelperBuilder();
}

ClassHelper::ClassHelper(std::map<EClass, std::map<EClass, bool> > inheritanceMatrix) :
	_inheritanceMatrix(inheritanceMatrix) {
}

} /* namespace Matcher */
} /* namespace Query */
} /* namespace Viatra */
