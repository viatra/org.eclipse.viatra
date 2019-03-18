/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.validation;

/**
 * @author Mark Czotter
 * @since 2.0
 */
public final class IssueCodes {

    private IssueCodes() {
    }

    protected static final String ISSUE_CODE_PREFIX = "org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes.";

    public static final String DUPLICATE_PATTERN_PARAMETER_NAME = ISSUE_CODE_PREFIX
            + "duplicate_pattern_parameter_name";
    public static final String DUPLICATE_PATTERN_DEFINITION = ISSUE_CODE_PREFIX + "duplicate_pattern_definition";
    public static final String WRONG_NUMBER_PATTERNCALL_PARAMETER = ISSUE_CODE_PREFIX
            + "wrong_number_pattern_parameter";
    public static final String TRANSITIVE_PATTERNCALL_NOT_APPLICABLE = ISSUE_CODE_PREFIX
            + "transitive_patterncall_not_applicable";
    public static final String TRANSITIVE_PATTERNCALL_ARITY = ISSUE_CODE_PREFIX + "transitive_patterncall_wrong_arity";
    public static final String TRANSITIVE_PATTERNCALL_TYPE = ISSUE_CODE_PREFIX
            + "transitive_patterncall_incompatibletypes";
    public static final String PATTERN_BODY_EMPTY = ISSUE_CODE_PREFIX + "patternbody_empty";
    
    public static final String PRIVATE_PATTERN_CALLED = ISSUE_CODE_PREFIX + "private_pattern_call";
    
    public static final String UNKNOWN_ANNOTATION = ISSUE_CODE_PREFIX + "unknown_annotation";
    public static final String UNKNOWN_ANNOTATION_PARAMETER = ISSUE_CODE_PREFIX + "unknown_annotation_attribute";
    public static final String MISSING_REQUIRED_ANNOTATION_PARAMETER = ISSUE_CODE_PREFIX
            + "missing_annotation_parameter";
    public static final String MISTYPED_ANNOTATION_PARAMETER = ISSUE_CODE_PREFIX + "mistyped_annotation_parameter";
    /**
     * @since 1.5
     */
    public static final String MISTYPED_PARAMETER = ISSUE_CODE_PREFIX + "mistyped_parameter";

    public static final String CONSTANT_COMPARE_CONSTRAINT = ISSUE_CODE_PREFIX + "constant_compare_constraint";
    public static final String SELF_COMPARE_CONSTRAINT = ISSUE_CODE_PREFIX + "self_compare_constraint";

    public static final String PACKAGE_NAME_MISMATCH = ISSUE_CODE_PREFIX + "package_name_mismatch";
    public static final String PACKAGE_NAME_EMPTY = ISSUE_CODE_PREFIX + "package_name_empty";
    

    public static final String LOWERCASE_PATTERN_NAME = ISSUE_CODE_PREFIX + "lowercase_pattern_name";
    public static final String UNUSED_PRIVATE_PATTERN = ISSUE_CODE_PREFIX + "unused_private_pattern";
    public static final String MISSING_PATTERN_PARAMETERS = ISSUE_CODE_PREFIX + "missing_pattern_parameters";

    public static final String CHECK_MUST_BE_BOOLEAN = ISSUE_CODE_PREFIX + "check_boolean";
    public static final String CHECK_WITH_IMPURE_JAVA_CALLS = ISSUE_CODE_PREFIX + "check_with_impure_java_calls";
    /**
     * @since 1.6
     */
    public static final String EVAL_INCORRECT_RETURNVALUE = ISSUE_CODE_PREFIX + "eval_incorrect_returnvalue";
    
    public static final String SYMBOLIC_VARIABLE_NEVER_REFERENCED = ISSUE_CODE_PREFIX
            + "symbolic_variable_never_referenced";
    public static final String SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE = ISSUE_CODE_PREFIX
            + "symbolic_variable_no_positive_reference";
    public static final String LOCAL_VARIABLE_REFERENCED_ONCE = ISSUE_CODE_PREFIX + "local_variable_referenced_once";
    public static final String LOCAL_VARIABLE_READONLY = ISSUE_CODE_PREFIX + "local_variable_no_quantifying_reference";
    public static final String LOCAL_VARIABLE_QUANTIFIED_REFERENCE = ISSUE_CODE_PREFIX
            + "local_variable_quantified_reference";
    public static final String LOCAL_VARIABLE_NO_POSITIVE_REFERENCE = ISSUE_CODE_PREFIX
            + "local_variable_no_positive_reference";
    public static final String ANONYM_VARIABLE_MULTIPLE_REFERENCE = ISSUE_CODE_PREFIX
            + "anonym_variable_multiple_reference";
    public static final String DUBIUS_VARIABLE_NAME = ISSUE_CODE_PREFIX + "dubius_variable_name";
    public static final String NEGATIVE_PATTERN_CALL_WITH_ONLY_SINGLE_USE_VARIABLES = ISSUE_CODE_PREFIX + "negative_pattern_call_with_only_single_use_variables";

    public static final String RECURSIVE_PATTERN_CALL = ISSUE_CODE_PREFIX + "recursive_pattern_call";
    
    /**
     * @since 1.4
     */
    public static final String INVALID_AGGREGATE_CONTEXT = ISSUE_CODE_PREFIX + "invalid_aggregate_context";
    /**
     * @since 1.4
     */
    public static final String INVALID_AGGREGATOR = ISSUE_CODE_PREFIX + "invalid_aggregator";
    /**
     * @since 1.4
     */
    public static final String INVALID_AGGREGATOR_PARAMETER = ISSUE_CODE_PREFIX + "invalid_aggregator_parameter";
    /**
     * @since 1.4
     */
    public static final String UNEXPECTED_AGGREGATE = ISSUE_CODE_PREFIX + "unexpected_aggregate";

    public static final String DUPLICATE_IMPORT = ISSUE_CODE_PREFIX + "duplicate_import";
    public static final String MISSING_PACKAGE_IMPORT = ISSUE_CODE_PREFIX + "missing_import";
    public static final String IMPORT_WITH_GENERATEDCODE = ISSUE_CODE_PREFIX + "missing_imported_code";
    public static final String IMPORT_DEPENDENCY_MISSING = ISSUE_CODE_PREFIX + "missing_import_dependency";
    public static final String INVALID_ENUM_LITERAL = ISSUE_CODE_PREFIX + "invalid_enum";

    public static final String SINGLEUSE_PARAMETER = ISSUE_CODE_PREFIX + "singleuse_parameter";

    public static final String PARAMETER_TYPE_INVALID = ISSUE_CODE_PREFIX + "parameter_type_invalid";
    /**
     * @since 1.3
     */
    public static final String PARAMETER_TYPE_AMBIGUOUS = ISSUE_CODE_PREFIX + "parameter_type_ambiguous";
    public static final String VARIABLE_TYPE_INVALID_ERROR = ISSUE_CODE_PREFIX + "variable_type_invalid_error";
    public static final String VARIABLE_TYPE_INVALID_WARNING = ISSUE_CODE_PREFIX + "variable_type_invalid_warning";
    public static final String VARIABLE_TYPE_MULTIPLE_DECLARATION = ISSUE_CODE_PREFIX
            + "variable_type_multiple_declaration";
    public static final String LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_COMPARE = ISSUE_CODE_PREFIX
            + "literal_and_computation_type_mismatch_in_compare";
    public static final String LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATH_EXPRESSION = ISSUE_CODE_PREFIX
            + "literal_or_computation_type_mismatch_in_path_expression";
    public static final String LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATTERN_CALL = ISSUE_CODE_PREFIX
            + "literal_or_computation_type_mismatch_in_pattern_call";
    public static final String CARTESIAN_SOFT_WARNING = ISSUE_CODE_PREFIX + "cartesian_soft_warning";
    public static final String CARTESIAN_STRICT_WARNING = ISSUE_CODE_PREFIX + "cartesian_strict_warning";
    public static final String CHECK_CONSTRAINT_SCALAR_VARIABLE_ERROR = ISSUE_CODE_PREFIX
            + "check_constraint_scalar_variable_error";

    public static final String IDENTIFIER_AS_KEYWORD = ISSUE_CODE_PREFIX + "identifier_as_keyword";

    public static final String FEATURE_NOT_REPRESENTABLE = ISSUE_CODE_PREFIX + "feature_not_representable";
    public static final String SURROGATE_QUERY_EXISTS = ISSUE_CODE_PREFIX + "surrogate_query_exists";

    public static final String MISSING_PARAMETER_TYPE = ISSUE_CODE_PREFIX + "missing_parameter_type";

    public static final String JDK_NOT_ON_CLASSPATH = ISSUE_CODE_PREFIX + "no_jdk_on_classpath";
    public static final String IQR_NOT_ON_CLASSPATH = ISSUE_CODE_PREFIX + "no_iq_runtime_on_classpath";
    /**
     * @since 1.3
     */
    public static final String TYPE_NOT_ON_CLASSPATH = ISSUE_CODE_PREFIX + "type_not_on_classpath";
    
    /**
     * @since 2.0
     */
    public static final String DEPRECATION = ISSUE_CODE_PREFIX + "deprecated";
    /**
     * @since 2.0
     */
    public static final String AGGREGATED_FEATURE_CHAIN = ISSUE_CODE_PREFIX + "aggregated_feature_chain"; 
    
    public static final String OTHER_ISSUE = ISSUE_CODE_PREFIX + "other_issue";

    
    
    /**
     * This prefix is used to distinguish between Java and EMF parameter type proposals
     * @since 1.4
     */
    public static final String JAVA_TYPE_PREFIX = "java:";
}