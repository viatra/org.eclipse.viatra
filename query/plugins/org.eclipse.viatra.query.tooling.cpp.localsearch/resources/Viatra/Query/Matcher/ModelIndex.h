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

#include <list>

namespace Viatra {
namespace Query {

template<class Class, class ModelRoot>
struct ModelIndex {

	static const std::list<Class*>& instances(const ModelRoot* modelroot) {
		static_assert(false, "Please specialize a model indexer for this type!");
	}
};

}  /* namespace Query*/
}  /* namespace Viatra*/
