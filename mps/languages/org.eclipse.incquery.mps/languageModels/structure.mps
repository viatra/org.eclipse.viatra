<?xml version="1.0" encoding="UTF-8"?>
<model modelUID="r:8c420ee7-5605-40f1-8ffd-968aa96940f0(org.eclipse.incquery.mps.structure)" version="35">
  <persistence version="8" />
  <language namespace="c72da2b9-7cce-4447-8389-f407dc1158b7(jetbrains.mps.lang.structure)" />
  <devkit namespace="fbc25dd2-5da4-483a-8b19-70928e1b62d7(jetbrains.mps.devkit.general-purpose)" />
  <import index="tpee" modelUID="r:00000000-0000-4000-0000-011c895902ca(jetbrains.mps.baseLanguage.structure)" version="5" />
  <import index="tpce" modelUID="r:00000000-0000-4000-0000-011c89590292(jetbrains.mps.lang.structure.structure)" version="0" implicit="yes" />
  <import index="tpck" modelUID="r:00000000-0000-4000-0000-011c89590288(jetbrains.mps.lang.core.structure)" version="0" implicit="yes" />
  <import index="auek" modelUID="r:8c420ee7-5605-40f1-8ffd-968aa96940f0(org.eclipse.incquery.mps.structure)" version="35" implicit="yes" />
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024449103" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="Pattern" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="pattern" />
    <property name="conceptShortDescription" nameId="tpce.4628067390765907488" value="Pattern" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="content" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992024500594" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="parameters" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="0..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024500592" resolveInfo="Parameter" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992024500596" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="bodies" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024500593" resolveInfo="PatternBody" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="8396102296983102185" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="tpck.1169194658468" resolveInfo="INamedConcept" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="5610578079075490834" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="2281067221948992820" resolveInfo="IGenNameProvider" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="8545270286772915932" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="8545270286772207149" resolveInfo="PatternModelContent" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024500587" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="PatternModel" />
    <property name="rootable" nameId="tpce.1096454100552" value="true" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="pattern model" />
    <property name="conceptShortDescription" nameId="tpce.4628067390765907488" value="Pattern Model" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="996292992024500590" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="package" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983041843" resolveInfo="string" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992024500588" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="contents" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="0..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="8545270286772207149" resolveInfo="PatternModelContent" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="8396102296983119739" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="tpck.1169194658468" resolveInfo="INamedConcept" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024500592" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="Parameter" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="content" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="996292992025675760" resolveInfo="Variable" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024500593" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="PatternBody" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Pattern Body" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="content" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992024530408" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="constraints" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="0..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="7705667014569610872" resolveInfo="Constraint" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="8396102296983102188" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="tpck.1169194658468" resolveInfo="INamedConcept" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024530426" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="CheckConstraint" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="constraints" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="check" />
    <property name="conceptShortDescription" nameId="tpce.4628067390765907488" value="Check Constraint" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992025680416" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="expression" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="tpee.1068431790191" resolveInfo="Expression" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="7705667014569671913" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="7705667014569610872" resolveInfo="Constraint" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024530443" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="CompareConstraint" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="constraints" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="compare" />
    <property name="conceptShortDescription" nameId="tpce.4628067390765907488" value="Compare Constraint" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="8396102296983865703" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="feature" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="996292992025672694" resolveInfo="CompareFeature" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="8396102296983865626" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="leftOperand" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024565907" resolveInfo="ValueReference" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="8396102296983865629" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="rightOperand" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024565907" resolveInfo="ValueReference" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="7705667014569736229" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="7705667014569610872" resolveInfo="Constraint" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024530460" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="PatternCompositionConstraint" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="constraints" />
    <property name="conceptShortDescription" nameId="tpce.4628067390765907488" value="Pattern Composition Constraint" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="find" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992028393460" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="call" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024566952" resolveInfo="PatternCall" />
    </node>
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="996292992028393457" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="neg" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983657063" resolveInfo="boolean" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="7705667014569736232" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="7705667014569610872" resolveInfo="Constraint" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024530493" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="constraints" />
    <property name="name" nameId="tpck.1169194664001" value="PathExpressionConstraint" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="false" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <property name="conceptShortDescription" nameId="tpce.4628067390765907488" value="Path Expression Constraint" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="path" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992027769733" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="head" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992025662526" resolveInfo="PathExpressionHead" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="7705667014569736235" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="7705667014569610872" resolveInfo="Constraint" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024565907" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="ValueReference" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="values" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Value Reference" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="2281067221948993334" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="2281067221948992820" resolveInfo="IGenNameProvider" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024565924" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="LiteralValueReference" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="values" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Literal Value Reference" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="996292992024565907" resolveInfo="ValueReference" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024565941" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="IntValue" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="values" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Int Value" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="996292992024565924" resolveInfo="LiteralValueReference" />
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="996292992024565942" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="value" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983657062" resolveInfo="integer" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024566054" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="StringValue" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="values" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="String Value" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="996292992024565924" resolveInfo="LiteralValueReference" />
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="996292992024566055" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="value" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983041843" resolveInfo="string" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024566533" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="values" />
    <property name="name" nameId="tpck.1169194664001" value="BoolValue" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Bool Value" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="996292992024565924" resolveInfo="LiteralValueReference" />
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="996292992024566534" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="value" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983657063" resolveInfo="boolean" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024566673" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="values" />
    <property name="name" nameId="tpck.1169194664001" value="VariableValue" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Variable Value" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="996292992024565907" resolveInfo="ValueReference" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992024566793" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="value" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="2281067221947980594" resolveInfo="VariableReference" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024566889" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="values" />
    <property name="name" nameId="tpck.1169194664001" value="ComputationValue" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Computation Value" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="996292992024565907" resolveInfo="ValueReference" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024566935" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="values" />
    <property name="name" nameId="tpck.1169194664001" value="AggregatedValue" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Aggregated Value" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="996292992024566889" resolveInfo="ComputationValue" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992024566984" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="aggregator" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024567002" resolveInfo="AggregatorExpression" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992024567003" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="call" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024566952" resolveInfo="PatternCall" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024566952" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="PatternCall" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="constraints" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992028507462" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="parameters" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024565907" resolveInfo="ValueReference" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992028507459" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="patternRef" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024449103" resolveInfo="Pattern" />
    </node>
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="996292992028507456" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="transitive" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983657063" resolveInfo="boolean" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024567002" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="values" />
    <property name="name" nameId="tpck.1169194664001" value="AggregatorExpression" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Aggregator Expression" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024567022" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="values" />
    <property name="name" nameId="tpck.1169194664001" value="CountAggregatorExpression" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="count" />
    <property name="conceptShortDescription" nameId="tpce.4628067390765907488" value="Count Aggregator Expression" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="996292992024567002" resolveInfo="AggregatorExpression" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992024567127" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="values" />
    <property name="name" nameId="tpck.1169194664001" value="FunctionEvaluationValue" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Function Evaluation Value" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="996292992024566889" resolveInfo="ComputationValue" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992024567128" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="expression" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="tpee.1068431790191" resolveInfo="Expression" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992025662526" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="constraints" />
    <property name="name" nameId="tpck.1169194664001" value="PathExpressionHead" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Path Expression Head" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="5589093812001602540" resolveInfo="PathExpressionElement" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992025662592" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="src" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="2281067221947980594" resolveInfo="VariableReference" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992025662599" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="trg" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024565907" resolveInfo="ValueReference" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992025662567" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="type" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="tpce.1169125787135" resolveInfo="AbstractConceptDeclaration" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992025662591" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="constraints" />
    <property name="name" nameId="tpck.1169194664001" value="PathExpressionTail" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Path Expression Tail" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="5589093812001602540" resolveInfo="PathExpressionElement" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992028124127" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="type" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992025675491" resolveInfo="LinkDeclarationType" />
    </node>
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="996292992025662611" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="index" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983657062" resolveInfo="integer" />
    </node>
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="996292992025662616" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="closure" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983657063" resolveInfo="boolean" />
    </node>
  </root>
  <root type="tpce.EnumerationDataTypeDeclaration" typeId="tpce.1082978164219" id="996292992025672694" nodeInfo="ng">
    <property name="name" nameId="tpck.1169194664001" value="CompareFeature" />
    <property name="hasNoDefaultMember" nameId="tpce.1212080844762" value="false" />
    <property name="noValueText" nameId="tpce.1212087449254" value="==" />
    <property name="memberIdentifierPolicy" nameId="tpce.1197591154882" value="derive_from_internal_value" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="constraints" />
    <link role="memberDataType" roleId="tpce.1083171729157" targetNodeId="tpck.1082983041843" resolveInfo="string" />
    <node role="member" roleId="tpce.1083172003582" type="tpce.EnumerationMemberDeclaration" typeId="tpce.1083171877298" id="996292992025672695" nodeInfo="ig">
      <property name="externalValue" nameId="tpce.1083923523172" value="==" />
      <property name="internalValue" nameId="tpce.1083923523171" value="equality" />
    </node>
    <node role="member" roleId="tpce.1083172003582" type="tpce.EnumerationMemberDeclaration" typeId="tpce.1083171877298" id="996292992025672696" nodeInfo="ig">
      <property name="externalValue" nameId="tpce.1083923523172" value="!=" />
      <property name="internalValue" nameId="tpce.1083923523171" value="inequality" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992025672789" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="ConceptReferenceType" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="content" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="7241148409041409499" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="concept" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="tpce.1169125787135" resolveInfo="AbstractConceptDeclaration" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992025675491" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="LinkDeclarationType" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="content" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="6888142545404296242" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="value" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="tpck.1319728274784973096" resolveInfo="InterfacePart" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="996292992025675760" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="Variable" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="content" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="996292992025675764" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="type" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992025672789" resolveInfo="ConceptReferenceType" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="4429560607464811059" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="tpck.1169194658468" resolveInfo="INamedConcept" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="2281067221948999923" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="2281067221948992820" resolveInfo="IGenNameProvider" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="6584777387165985962" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="values" />
    <property name="name" nameId="tpck.1169194664001" value="DoubleValue" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Double Value" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="996292992024565924" resolveInfo="LiteralValueReference" />
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="6584777387166676421" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="value" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983041843" resolveInfo="string" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="7802504792141552484" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="TemporaryVariable" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="content" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="996292992025675760" resolveInfo="Variable" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="7241148409043933760" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="usage.matcher" />
    <property name="name" nameId="tpck.1169194664001" value="MPSIncQueryMatcherInstantiation" />
    <property name="conceptShortDescription" nameId="tpce.4628067390765907488" value="Pattern Matcher Instantiation" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="get matcher" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpee.1068431790191" resolveInfo="Expression" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="8066520122896896506" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="model" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="tpee.1068431790191" resolveInfo="Expression" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="7241148409043933812" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="pattern" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024449103" resolveInfo="Pattern" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="5589093812001602540" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="constraints" />
    <property name="name" nameId="tpck.1169194664001" value="PathExpressionElement" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="5589093812001602541" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="tail" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992025662591" resolveInfo="PathExpressionTail" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="5589093812003084634" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="constraints" />
    <property name="name" nameId="tpck.1169194664001" value="ConceptConstraint" />
    <property name="conceptShortDescription" nameId="tpce.4628067390765907488" value="Concept Constraint" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="concept" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="5589093812003084950" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="var" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="2281067221947980594" resolveInfo="VariableReference" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="5589093812003084769" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="type" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="tpce.1169125787135" resolveInfo="AbstractConceptDeclaration" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="7705667014569736238" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="7705667014569610872" resolveInfo="Constraint" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="5589093812003326776" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="values" />
    <property name="name" nameId="tpck.1169194664001" value="EnumValue" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Enum Value" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="996292992024565924" resolveInfo="LiteralValueReference" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="5589093812003326863" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="value" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="tpee.1083260308424" resolveInfo="EnumConstantReference" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="2281067221947980594" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="VariableReference" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="false" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="reference" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpee.1068431790191" resolveInfo="Expression" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="768444928085405086" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="variable" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992025675760" resolveInfo="Variable" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="2281067221948995260" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="2281067221948992820" resolveInfo="IGenNameProvider" />
    </node>
  </root>
  <root type="tpce.InterfaceConceptDeclaration" typeId="tpce.1169125989551" id="2281067221948992820" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="IGenNameProvider" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="content" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="768444928086151186" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="values" />
    <property name="name" nameId="tpck.1169194664001" value="TemporaryVariableValue" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Temporary Variable Value" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="996292992024565907" resolveInfo="ValueReference" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="768444928086151187" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="variable" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="7802504792141552484" resolveInfo="TemporaryVariable" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="3770201403572950169" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="EmptyPatternModelContent" />
    <property name="conceptShortDescription" nameId="tpce.4628067390765907488" value="Empty Content" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="content" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="8545270286772207883" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="8545270286772207149" resolveInfo="PatternModelContent" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="8545270286772207894" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="tpck.1835621062190663819" resolveInfo="IDontSubstituteByDefault" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="7705667014569069526" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="constraints" />
    <property name="name" nameId="tpck.1169194664001" value="EmptyConstraint" />
    <property name="conceptShortDescription" nameId="tpce.4628067390765907488" value="Empty Constraint" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="7705667014569736241" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="7705667014569610872" resolveInfo="Constraint" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="7705667014570351438" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="tpck.1835621062190663819" resolveInfo="IDontSubstituteByDefault" />
    </node>
  </root>
  <root type="tpce.InterfaceConceptDeclaration" typeId="tpce.1169125989551" id="7705667014569610872" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="constraints" />
    <property name="name" nameId="tpck.1169194664001" value="Constraint" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="8650544432873189951" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="usage.matcher" />
    <property name="name" nameId="tpck.1169194664001" value="GetAllMatchesOperation" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="getAllMatches" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="7447605944637160349" resolveInfo="MPSIncQueryMatcherOperation" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="8650544432874543686" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="partialMatch" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="tpee.1068431790191" resolveInfo="Expression" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="8650544432873311946" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="usage.matcher" />
    <property name="name" nameId="tpck.1169194664001" value="GetAllValuesOperation" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="getAllValues" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="7447605944637160349" resolveInfo="MPSIncQueryMatcherOperation" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="8202482261175133153" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="parameter" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024500592" resolveInfo="Parameter" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="8650544432874558885" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="partialMatch" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="tpee.1068431790191" resolveInfo="Expression" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="8650544432874604370" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="usage.matcher" />
    <property name="name" nameId="tpck.1169194664001" value="MPSIncQueryMatcher" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="MPSIncQueryMatcher" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpee.1068431790189" resolveInfo="Type" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="8650544432874609807" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="pattern" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024449103" resolveInfo="Pattern" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="8650544432877557562" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="usage.matcher" />
    <property name="name" nameId="tpck.1169194664001" value="CountMatchesOperation" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="countMatches" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="7447605944637160349" resolveInfo="MPSIncQueryMatcherOperation" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="8650544432877559371" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="partialMatch" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="tpee.1068431790191" resolveInfo="Expression" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="8650544432877880724" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="usage.matcher" />
    <property name="name" nameId="tpck.1169194664001" value="GetPatternNameOperation" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="getPatternName" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="7447605944637160349" resolveInfo="MPSIncQueryMatcherOperation" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="8650544432877888542" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="usage.matcher" />
    <property name="name" nameId="tpck.1169194664001" value="GetParameterNamesOperation" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="getParameterNames" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="7447605944637160349" resolveInfo="MPSIncQueryMatcherOperation" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="8202482261173552240" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="usage.matcher" />
    <property name="name" nameId="tpck.1169194664001" value="GetPositionOfPatameterOperation" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="getPositionOfParameter" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="7447605944637160349" resolveInfo="MPSIncQueryMatcherOperation" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="8202482261173893671" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="parameter" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024500592" resolveInfo="Parameter" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="7447605944634188286" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="usage.matcher" />
    <property name="name" nameId="tpck.1169194664001" value="NewMatchOperation" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="newMatch" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="7447605944637160349" resolveInfo="MPSIncQueryMatcherOperation" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="7447605944634333569" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="bindings" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="0..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="7447605944634311434" resolveInfo="PartialMatchParameterBinding" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="7447605944634311434" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="usage.matcher" />
    <property name="name" nameId="tpck.1169194664001" value="PartialMatchParameterBinding" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="7447605944634512798" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="parameter" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024500592" resolveInfo="Parameter" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="7447605944634317224" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="value" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="tpee.1068431790191" resolveInfo="Expression" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="7447605944636584242" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="usage.match" />
    <property name="name" nameId="tpck.1169194664001" value="MPSIncQueryMatch" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="MPSIncQueryMatch" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpee.1068431790189" resolveInfo="Type" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="7447605944636589388" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="pattern" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024449103" resolveInfo="Pattern" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="7447605944636587895" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="tpee.7405920559687237502" resolveInfo="IClassifierType" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="7447605944637160349" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="usage.matcher" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <property name="name" nameId="tpck.1169194664001" value="MPSIncQueryMatcherOperation" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="7447605944637163254" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="tpee.1197027803184" resolveInfo="IOperation" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="7447605944640681971" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="usage.match" />
    <property name="name" nameId="tpck.1169194664001" value="MPSIncQueryMatchOperation" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="7447605944640687085" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="tpee.1197027803184" resolveInfo="IOperation" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="7447605944641594150" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="usage.match" />
    <property name="name" nameId="tpck.1169194664001" value="GetParameterOperation" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="get" />
    <property name="conceptShortDescription" nameId="tpce.4628067390765907488" value="Get Value Of Parameter" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="7447605944640681971" resolveInfo="MPSIncQueryMatchOperation" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="7447605944641598694" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="parameter" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="996292992024500592" resolveInfo="Parameter" />
    </node>
  </root>
  <root type="tpce.InterfaceConceptDeclaration" typeId="tpce.1169125989551" id="8545270286772207149" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="PatternModelContent" />
    <property name="virtualPackage" nameId="tpck.1193676396447" value="content" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="8545270286773251084" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="content" />
    <property name="name" nameId="tpck.1169194664001" value="CommentContent" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="//" />
    <property name="conceptShortDescription" nameId="tpce.4628067390765907488" value="Comment" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="8545270286773251089" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="text" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983041843" resolveInfo="string" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="8545270286773251085" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="8545270286772207149" resolveInfo="PatternModelContent" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="972335058771816096" nodeInfo="ig">
    <property name="virtualPackage" nameId="tpck.1193676396447" value="constraints" />
    <property name="name" nameId="tpck.1169194664001" value="CommentConstraint" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="//" />
    <property name="conceptShortDescription" nameId="tpce.4628067390765907488" value="Comment" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="972335058771816152" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="text" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983041843" resolveInfo="string" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="972335058771816148" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="7705667014569610872" resolveInfo="Constraint" />
    </node>
  </root>
</model>

