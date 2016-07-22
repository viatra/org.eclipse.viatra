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

/**
 * @file This file includes every implemented search operation.
 */

#include "ISearchOperation.h"

// include all operations
// check
#include "Check/InstanceOfCheck.h"
#include "Check/AssociationCheck.h"
#include "Check/CheckExpression.h"
#include "Check/NACOperation.h"

//extend
#include "Extend/IterateOverInstances.h"
#include "Extend/NavigateAssociation.h"
