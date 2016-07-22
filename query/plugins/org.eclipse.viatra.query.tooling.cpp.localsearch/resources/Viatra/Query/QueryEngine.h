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

namespace Viatra {
namespace Query {

template<class ModelRoot>
class QueryEngine {
public:
	static QueryEngine<ModelRoot> of(const ModelRoot *model);
	static QueryEngine<ModelRoot> empty();

	template<template <typename> class QuerySpecification>
	typename QuerySpecification<ModelRoot>::Matcher matcher();

private:
	QueryEngine(const ModelRoot *model);
	
	const ModelRoot *_model;
};

template<class ModelRoot>
QueryEngine<ModelRoot> QueryEngine<ModelRoot>::of(const ModelRoot *model) {
	return QueryEngine<ModelRoot>(model);
}

template<class ModelRoot>
QueryEngine<ModelRoot> QueryEngine<ModelRoot>::empty() {
	return QueryEngine<ModelRoot>(nullptr);
}

template<class ModelRoot>
template<template <class> class QuerySpecification>
typename QuerySpecification<ModelRoot>::Matcher QueryEngine<ModelRoot>::matcher() {
	return typename QuerySpecification<ModelRoot>::Matcher(_model, QuerySpecification<ModelRoot>::QueryGroup::instance()->context());
}

template<class ModelRoot>
QueryEngine<ModelRoot>::QueryEngine(const ModelRoot* model)
	: _model(model) {
	
}

} /* namespace Query */
} /* namespace Viatra */
