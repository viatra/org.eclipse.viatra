<?xml version="1.0" encoding="UTF-8"?>
<model modelUID="r:e401582b-897c-491f-b35e-f470843d6644(org.eclipse.incquery.mps.test.base.playground)">
  <persistence version="8" />
  <language namespace="ef5ea086-f248-4019-bdc4-4a594cfbdd2e(org.eclipse.incquery.mps)" />
  <language namespace="ed6d7656-532c-4bc2-81d1-af945aeb8280(jetbrains.mps.baseLanguage.blTypes)" />
  <language namespace="9ded098b-ad6a-4657-bfd9-48636cfe8bc3(jetbrains.mps.lang.traceable)" />
  <language namespace="f3061a53-9226-4cc5-a443-f952ceaf5816(jetbrains.mps.baseLanguage)" />
  <language namespace="63650c59-16c8-498a-99c8-005c7ee9515d(jetbrains.mps.lang.access)" />
  <language namespace="fd392034-7849-419d-9071-12563d152375(jetbrains.mps.baseLanguage.closures)" />
  <language namespace="f2801650-65d5-424e-bb1b-463a8781b786(jetbrains.mps.baseLanguage.javadoc)" />
  <language namespace="83888646-71ce-4f1c-9c53-c54016f6ad4f(jetbrains.mps.baseLanguage.collections)" />
  <language namespace="7866978e-a0f0-4cc7-81bc-4d213d9375e1(jetbrains.mps.lang.smodel)" />
  <language namespace="a247e09e-2435-45ba-b8d2-07e93feba96a(jetbrains.mps.baseLanguage.tuples)" />
  <language namespace="8585453e-6bfb-4d80-98de-b16074f1d86c(jetbrains.mps.lang.test)" />
  <import index="tpee" modelUID="r:00000000-0000-4000-0000-011c895902ca(jetbrains.mps.baseLanguage.structure)" version="5" />
  <import index="auek" modelUID="r:8c420ee7-5605-40f1-8ffd-968aa96940f0(org.eclipse.incquery.mps.structure)" version="35" />
  <import index="tp25" modelUID="r:00000000-0000-4000-0000-011c89590301(jetbrains.mps.lang.smodel.structure)" version="16" />
  <import index="tp2q" modelUID="r:00000000-0000-4000-0000-011c8959032e(jetbrains.mps.baseLanguage.collections.structure)" version="7" />
  <import index="f1uo" modelUID="r:326fd4ed-5369-4cc8-8788-92145c4d8911(org.eclipse.incquery.mps.runtime)" version="-1" />
  <import index="fxg7" modelUID="f:java_stub#6354ebe7-c22a-4a0f-ac54-50b52ab9b065#java.io(JDK/java.io@java_stub)" version="-1" />
  <import index="tpck" modelUID="r:00000000-0000-4000-0000-011c89590288(jetbrains.mps.lang.core.structure)" version="0" />
  <import index="gyg9" modelUID="f:java_stub#d78ad636-1087-4a2a-8147-0f6b287011c2#org.eclipse.incquery.runtime.rete.matcher(org.eclipse.incquery.mps.runtime/org.eclipse.incquery.runtime.rete.matcher@java_stub)" version="-1" />
  <import index="ozr7" modelUID="f:java_stub#d78ad636-1087-4a2a-8147-0f6b287011c2#org.eclipse.incquery.runtime.matchers.context(org.eclipse.incquery.mps.runtime/org.eclipse.incquery.runtime.matchers.context@java_stub)" version="-1" />
  <import index="67jt" modelUID="f:java_stub#d78ad636-1087-4a2a-8147-0f6b287011c2#org.eclipse.incquery.runtime.api(org.eclipse.incquery.mps.runtime/org.eclipse.incquery.runtime.api@java_stub)" version="-1" />
  <import index="e2lb" modelUID="f:java_stub#6354ebe7-c22a-4a0f-ac54-50b52ab9b065#java.lang(JDK/java.lang@java_stub)" version="-1" implicit="yes" />
  <import index="tp2c" modelUID="r:00000000-0000-4000-0000-011c89590338(jetbrains.mps.baseLanguage.closures.structure)" version="3" implicit="yes" />
  <root type="auek.PatternModel" typeId="auek.996292992024500587" id="996292992024598956" nodeInfo="ng">
    <property name="package" nameId="auek.996292992024500590" value="a.b.c" />
    <property name="name" nameId="tpck.1169194664001" value="PatternModel_a_b_c" />
    <node role="contents" roleId="auek.996292992024500588" type="auek.Pattern" typeId="auek.996292992024449103" id="996292992025100282" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="checkExpression" />
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="7802504792141939032" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="a" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="8800263097516742820" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
        </node>
      </node>
      <node role="bodies" roleId="auek.996292992024500596" type="auek.PatternBody" typeId="auek.996292992024500593" id="996292992025100284" nodeInfo="ng">
        <node role="constraints" roleId="auek.996292992024530408" type="auek.PathExpressionConstraint" typeId="auek.996292992024530493" id="768444928087546543" nodeInfo="ng">
          <node role="head" roleId="auek.996292992027769733" type="auek.PathExpressionHead" typeId="auek.996292992025662526" id="768444928087546545" nodeInfo="ng">
            <link role="type" roleId="auek.996292992025662567" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
            <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="768444928087547066" nodeInfo="ng">
              <property name="index" nameId="auek.996292992025662611" value="-1" />
              <property name="closure" nameId="auek.996292992025662616" value="false" />
              <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="768444928087547346" nodeInfo="ng">
                <link role="value" roleId="auek.6888142545404296242" targetNodeId="auek.996292992024500596" />
              </node>
              <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="768444928087547621" nodeInfo="ng">
                <property name="index" nameId="auek.996292992025662611" value="-1" />
                <property name="closure" nameId="auek.996292992025662616" value="false" />
                <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="2815025855839732772" nodeInfo="ng">
                  <property name="index" nameId="auek.996292992025662611" value="-1" />
                  <property name="closure" nameId="auek.996292992025662616" value="false" />
                  <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="2815025855839732873" nodeInfo="ng">
                    <link role="value" roleId="auek.6888142545404296242" targetNodeId="tpck.5169995583184591170" />
                  </node>
                  <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="2815025855839732818" nodeInfo="ng">
                    <property name="index" nameId="auek.996292992025662611" value="-1" />
                    <property name="closure" nameId="auek.996292992025662616" value="false" />
                    <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="2815025855839732892" nodeInfo="ng">
                      <link role="value" roleId="auek.6888142545404296242" targetNodeId="tpck.5169995583184591170" />
                    </node>
                  </node>
                </node>
                <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="2815025855839732855" nodeInfo="ng">
                  <link role="value" roleId="auek.6888142545404296242" targetNodeId="auek.996292992024530408" />
                </node>
              </node>
            </node>
            <node role="src" roleId="auek.996292992025662592" type="auek.VariableReference" typeId="auek.2281067221947980594" id="768444928087549195" nodeInfo="ng">
              <link role="variable" roleId="auek.768444928085405086" targetNodeId="7802504792141939032" resolveInfo="a" />
            </node>
            <node role="trg" roleId="auek.996292992025662599" type="auek.TemporaryVariableValue" typeId="auek.768444928086151186" id="594138331666415982" nodeInfo="ng">
              <node role="variable" roleId="auek.768444928086151187" type="auek.TemporaryVariable" typeId="auek.7802504792141552484" id="594138331666415984" nodeInfo="ng">
                <property name="name" nameId="tpck.1169194664001" value="body" />
              </node>
            </node>
          </node>
        </node>
        <node role="constraints" roleId="auek.996292992024530408" type="auek.PathExpressionConstraint" typeId="auek.996292992024530493" id="768444928088374339" nodeInfo="ng">
          <node role="head" roleId="auek.996292992027769733" type="auek.PathExpressionHead" typeId="auek.996292992025662526" id="768444928088374340" nodeInfo="ng">
            <link role="type" roleId="auek.996292992025662567" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
            <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="768444928088374341" nodeInfo="ng">
              <property name="index" nameId="auek.996292992025662611" value="-1" />
              <property name="closure" nameId="auek.996292992025662616" value="false" />
              <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="768444928088374342" nodeInfo="ng">
                <link role="value" roleId="auek.6888142545404296242" targetNodeId="tpck.1169194664001" resolveInfo="name" />
              </node>
            </node>
            <node role="src" roleId="auek.996292992025662592" type="auek.VariableReference" typeId="auek.2281067221947980594" id="768444928088374343" nodeInfo="ng">
              <link role="variable" roleId="auek.768444928085405086" targetNodeId="7802504792141939032" resolveInfo="a" />
            </node>
            <node role="trg" roleId="auek.996292992025662599" type="auek.TemporaryVariableValue" typeId="auek.768444928086151186" id="768444928088374344" nodeInfo="ng">
              <node role="variable" roleId="auek.768444928086151187" type="auek.TemporaryVariable" typeId="auek.7802504792141552484" id="768444928088374345" nodeInfo="ng">
                <property name="name" nameId="tpck.1169194664001" value="name2" />
              </node>
            </node>
          </node>
        </node>
        <node role="constraints" roleId="auek.996292992024530408" type="auek.PathExpressionConstraint" typeId="auek.996292992024530493" id="768444928083648205" nodeInfo="ng">
          <node role="head" roleId="auek.996292992027769733" type="auek.PathExpressionHead" typeId="auek.996292992025662526" id="768444928083648207" nodeInfo="ng">
            <link role="type" roleId="auek.996292992025662567" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
            <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="768444928083648600" nodeInfo="ng">
              <property name="index" nameId="auek.996292992025662611" value="-1" />
              <property name="closure" nameId="auek.996292992025662616" value="false" />
              <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="768444928087539283" nodeInfo="ng">
                <link role="value" roleId="auek.6888142545404296242" targetNodeId="tpck.1169194664001" resolveInfo="name" />
              </node>
            </node>
            <node role="src" roleId="auek.996292992025662592" type="auek.VariableReference" typeId="auek.2281067221947980594" id="768444928086124419" nodeInfo="ng">
              <link role="variable" roleId="auek.768444928085405086" targetNodeId="7802504792141939032" resolveInfo="a" />
            </node>
            <node role="trg" roleId="auek.996292992025662599" type="auek.TemporaryVariableValue" typeId="auek.768444928086151186" id="768444928086893283" nodeInfo="ng">
              <node role="variable" roleId="auek.768444928086151187" type="auek.TemporaryVariable" typeId="auek.7802504792141552484" id="768444928086893286" nodeInfo="ng">
                <property name="name" nameId="tpck.1169194664001" value="name1" />
              </node>
            </node>
          </node>
        </node>
        <node role="constraints" roleId="auek.996292992024530408" type="auek.CheckConstraint" typeId="auek.996292992024530426" id="996292992026424821" nodeInfo="ng">
          <node role="expression" roleId="auek.996292992025680416" type="tpee.DotExpression" typeId="tpee.1197027756228" id="6085038789413069910" nodeInfo="nn">
            <node role="operand" roleId="tpee.1197027771414" type="tp2c.ClosureLiteral" typeId="tp2c.1199569711397" id="6085038789413025519" nodeInfo="nn">
              <node role="body" roleId="tp2c.1199569916463" type="tpee.StatementList" typeId="tpee.1068580123136" id="6085038789413025521" nodeInfo="sn">
                <node role="statement" roleId="tpee.1068581517665" type="tpee.ExpressionStatement" typeId="tpee.1068580123155" id="6085038789413025984" nodeInfo="nn">
                  <node role="expression" roleId="tpee.1068580123156" type="tpee.DotExpression" typeId="tpee.1197027756228" id="6085038789413027045" nodeInfo="nn">
                    <node role="operand" roleId="tpee.1197027771414" type="tpee.StaticFieldReference" typeId="tpee.1070533707846" id="6085038789413025983" nodeInfo="nn">
                      <link role="classifier" roleId="tpee.1144433057691" targetNodeId="e2lb.~System" resolveInfo="System" />
                      <link role="variableDeclaration" roleId="tpee.1068581517664" targetNodeId="e2lb.~System%dout" resolveInfo="out" />
                    </node>
                    <node role="operation" roleId="tpee.1197027833540" type="tpee.InstanceMethodCallOperation" typeId="tpee.1202948039474" id="6085038789413028956" nodeInfo="nn">
                      <link role="baseMethodDeclaration" roleId="tpee.1068499141037" targetNodeId="fxg7.~PrintStream%dprintln()%cvoid" resolveInfo="println" />
                    </node>
                  </node>
                </node>
                <node role="statement" roleId="tpee.1068581517665" type="tpee.IfStatement" typeId="tpee.1068580123159" id="6085038789413030351" nodeInfo="nn">
                  <node role="ifTrue" roleId="tpee.1068580123161" type="tpee.StatementList" typeId="tpee.1068580123136" id="6085038789413030354" nodeInfo="sn">
                    <node role="statement" roleId="tpee.1068581517665" type="tpee.ReturnStatement" typeId="tpee.1068581242878" id="6085038789413042168" nodeInfo="nn">
                      <node role="expression" roleId="tpee.1068581517676" type="tpee.BooleanConstant" typeId="tpee.1068580123137" id="6085038789413042174" nodeInfo="nn">
                        <property name="value" nameId="tpee.1068580123138" value="true" />
                      </node>
                    </node>
                  </node>
                  <node role="condition" roleId="tpee.1068580123160" type="tpee.GreaterThanExpression" typeId="tpee.1081506762703" id="6085038789413039869" nodeInfo="nn">
                    <node role="rightExpression" roleId="tpee.1081773367579" type="tpee.IntegerConstant" typeId="tpee.1068580320020" id="6085038789413041006" nodeInfo="nn">
                      <property name="value" nameId="tpee.1068580320021" value="1" />
                    </node>
                    <node role="leftExpression" roleId="tpee.1081773367580" type="tpee.DotExpression" typeId="tpee.1197027756228" id="6085038789413032076" nodeInfo="nn">
                      <node role="operand" roleId="tpee.1197027771414" type="auek.VariableReference" typeId="auek.2281067221947980594" id="6085038789413031148" nodeInfo="ng">
                        <link role="variable" roleId="auek.768444928085405086" targetNodeId="768444928086893286" resolveInfo="name1" />
                      </node>
                      <node role="operation" roleId="tpee.1197027833540" type="tpee.InstanceMethodCallOperation" typeId="tpee.1202948039474" id="6085038789413036270" nodeInfo="nn">
                        <link role="baseMethodDeclaration" roleId="tpee.1068499141037" targetNodeId="e2lb.~String%dlength()%cint" resolveInfo="length" />
                      </node>
                    </node>
                  </node>
                  <node role="ifFalseStatement" roleId="tpee.1082485599094" type="tpee.BlockStatement" typeId="tpee.1082485599095" id="6085038789413043450" nodeInfo="nn">
                    <node role="statements" roleId="tpee.1082485599096" type="tpee.StatementList" typeId="tpee.1068580123136" id="6085038789413043451" nodeInfo="sn">
                      <node role="statement" roleId="tpee.1068581517665" type="tpee.ReturnStatement" typeId="tpee.1068581242878" id="6085038789413064884" nodeInfo="nn">
                        <node role="expression" roleId="tpee.1068581517676" type="tpee.GreaterThanExpression" typeId="tpee.1081506762703" id="6085038789413054486" nodeInfo="nn">
                          <node role="rightExpression" roleId="tpee.1081773367579" type="tpee.IntegerConstant" typeId="tpee.1068580320020" id="6085038789413056418" nodeInfo="nn">
                            <property name="value" nameId="tpee.1068580320021" value="2" />
                          </node>
                          <node role="leftExpression" roleId="tpee.1081773367580" type="tpee.DotExpression" typeId="tpee.1197027756228" id="6085038789413047462" nodeInfo="nn">
                            <node role="operand" roleId="tpee.1197027771414" type="auek.VariableReference" typeId="auek.2281067221947980594" id="6085038789413046317" nodeInfo="ng">
                              <link role="variable" roleId="auek.768444928085405086" targetNodeId="768444928088374345" resolveInfo="name2" />
                            </node>
                            <node role="operation" roleId="tpee.1197027833540" type="tpee.InstanceMethodCallOperation" typeId="tpee.1202948039474" id="6085038789413050831" nodeInfo="nn">
                              <link role="baseMethodDeclaration" roleId="tpee.1068499141037" targetNodeId="e2lb.~String%dlength()%cint" resolveInfo="length" />
                            </node>
                          </node>
                        </node>
                      </node>
                    </node>
                  </node>
                </node>
              </node>
            </node>
            <node role="operation" roleId="tpee.1197027833540" type="tp2c.InvokeFunctionOperation" typeId="tp2c.1225797177491" id="6085038789413075273" nodeInfo="nn" />
          </node>
        </node>
      </node>
    </node>
    <node role="contents" roleId="auek.996292992024500588" type="auek.Pattern" typeId="auek.996292992024449103" id="2815025855840515200" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="testPattern" />
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="2815025855840516085" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="a" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="2815025855840714776" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
        </node>
      </node>
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="2815025855840516099" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="b" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="2815025855840714927" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="auek.996292992024500593" resolveInfo="PatternBody" />
        </node>
      </node>
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="2815025855840516108" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="c" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="2815025855840714960" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="auek.996292992024500592" resolveInfo="Parameter" />
        </node>
      </node>
      <node role="bodies" roleId="auek.996292992024500596" type="auek.PatternBody" typeId="auek.996292992024500593" id="2815025855840515202" nodeInfo="ng">
        <node role="constraints" roleId="auek.996292992024530408" type="auek.ConceptConstraint" typeId="auek.5589093812003084634" id="2815025855840517058" nodeInfo="ng">
          <link role="type" roleId="auek.5589093812003084769" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
          <node role="var" roleId="auek.5589093812003084950" type="auek.VariableReference" typeId="auek.2281067221947980594" id="2815025855840517970" nodeInfo="ng">
            <link role="variable" roleId="auek.768444928085405086" targetNodeId="2815025855840516085" resolveInfo="a" />
          </node>
        </node>
        <node role="constraints" roleId="auek.996292992024530408" type="auek.ConceptConstraint" typeId="auek.5589093812003084634" id="2815025855840709383" nodeInfo="ng">
          <link role="type" roleId="auek.5589093812003084769" targetNodeId="auek.996292992024500593" resolveInfo="PatternBody" />
          <node role="var" roleId="auek.5589093812003084950" type="auek.VariableReference" typeId="auek.2281067221947980594" id="2815025855840710297" nodeInfo="ng">
            <link role="variable" roleId="auek.768444928085405086" targetNodeId="2815025855840516099" resolveInfo="b" />
          </node>
        </node>
        <node role="constraints" roleId="auek.996292992024530408" type="auek.ConceptConstraint" typeId="auek.5589093812003084634" id="2815025855840710763" nodeInfo="ng">
          <link role="type" roleId="auek.5589093812003084769" targetNodeId="auek.996292992024500592" resolveInfo="Parameter" />
          <node role="var" roleId="auek.5589093812003084950" type="auek.VariableReference" typeId="auek.2281067221947980594" id="2815025855840711683" nodeInfo="ng">
            <link role="variable" roleId="auek.768444928085405086" targetNodeId="2815025855840516108" resolveInfo="c" />
          </node>
        </node>
        <node role="constraints" roleId="auek.996292992024530408" type="auek.PathExpressionConstraint" typeId="auek.996292992024530493" id="2815025855840712167" nodeInfo="ng">
          <node role="head" roleId="auek.996292992027769733" type="auek.PathExpressionHead" typeId="auek.996292992025662526" id="2815025855840712169" nodeInfo="ng">
            <link role="type" roleId="auek.996292992025662567" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
            <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="2815025855840712170" nodeInfo="ng">
              <property name="index" nameId="auek.996292992025662611" value="-1" />
              <property name="closure" nameId="auek.996292992025662616" value="false" />
              <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="2815025855840712669" nodeInfo="ng">
                <link role="value" roleId="auek.6888142545404296242" targetNodeId="auek.996292992024500596" />
              </node>
            </node>
            <node role="src" roleId="auek.996292992025662592" type="auek.VariableReference" typeId="auek.2281067221947980594" id="2815025855840713154" nodeInfo="ng">
              <link role="variable" roleId="auek.768444928085405086" targetNodeId="2815025855840516085" resolveInfo="a" />
            </node>
            <node role="trg" roleId="auek.996292992025662599" type="auek.VariableValue" typeId="auek.996292992024566673" id="2815025855840713828" nodeInfo="ng">
              <node role="value" roleId="auek.996292992024566793" type="auek.VariableReference" typeId="auek.2281067221947980594" id="2815025855840714509" nodeInfo="ng">
                <link role="variable" roleId="auek.768444928085405086" targetNodeId="2815025855840516099" resolveInfo="b" />
              </node>
            </node>
          </node>
        </node>
        <node role="constraints" roleId="auek.996292992024530408" type="auek.PathExpressionConstraint" typeId="auek.996292992024530493" id="2815025855840715499" nodeInfo="ng">
          <node role="head" roleId="auek.996292992027769733" type="auek.PathExpressionHead" typeId="auek.996292992025662526" id="2815025855840715501" nodeInfo="ng">
            <link role="type" roleId="auek.996292992025662567" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
            <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="2815025855840715502" nodeInfo="ng">
              <property name="index" nameId="auek.996292992025662611" value="-1" />
              <property name="closure" nameId="auek.996292992025662616" value="false" />
              <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="2815025855840716070" nodeInfo="ng">
                <link role="value" roleId="auek.6888142545404296242" targetNodeId="auek.996292992024500594" />
              </node>
            </node>
            <node role="src" roleId="auek.996292992025662592" type="auek.VariableReference" typeId="auek.2281067221947980594" id="2815025855840716569" nodeInfo="ng">
              <link role="variable" roleId="auek.768444928085405086" targetNodeId="2815025855840516085" resolveInfo="a" />
            </node>
            <node role="trg" roleId="auek.996292992025662599" type="auek.VariableValue" typeId="auek.996292992024566673" id="2815025855840717710" nodeInfo="ng">
              <node role="value" roleId="auek.996292992024566793" type="auek.VariableReference" typeId="auek.2281067221947980594" id="2815025855840718236" nodeInfo="ng">
                <link role="variable" roleId="auek.768444928085405086" targetNodeId="2815025855840516108" resolveInfo="c" />
              </node>
            </node>
          </node>
        </node>
      </node>
    </node>
    <node role="contents" roleId="auek.996292992024500588" type="auek.Pattern" typeId="auek.996292992024449103" id="6085038789411539218" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="countExpression" />
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="594138331666574549" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="patternModel" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="594138331666619085" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="auek.996292992024500587" resolveInfo="PatternModel" />
        </node>
      </node>
      <node role="bodies" roleId="auek.996292992024500596" type="auek.PatternBody" typeId="auek.996292992024500593" id="6085038789411539219" nodeInfo="ng">
        <node role="constraints" roleId="auek.996292992024530408" type="auek.PathExpressionConstraint" typeId="auek.996292992024530493" id="594138331666573647" nodeInfo="ng">
          <node role="head" roleId="auek.996292992027769733" type="auek.PathExpressionHead" typeId="auek.996292992025662526" id="594138331666573649" nodeInfo="ng">
            <link role="type" roleId="auek.996292992025662567" targetNodeId="auek.996292992024500587" resolveInfo="PatternModel" />
            <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="594138331666574080" nodeInfo="ng">
              <property name="index" nameId="auek.996292992025662611" value="-1" />
              <property name="closure" nameId="auek.996292992025662616" value="false" />
              <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="594138331666574148" nodeInfo="ng">
                <link role="value" roleId="auek.6888142545404296242" targetNodeId="auek.996292992024500588" />
              </node>
            </node>
            <node role="src" roleId="auek.996292992025662592" type="auek.VariableReference" typeId="auek.2281067221947980594" id="594138331666574922" nodeInfo="ng">
              <link role="variable" roleId="auek.768444928085405086" targetNodeId="594138331666574549" resolveInfo="patternModel" />
            </node>
            <node role="trg" roleId="auek.996292992025662599" type="auek.TemporaryVariableValue" typeId="auek.768444928086151186" id="594138331666576081" nodeInfo="ng">
              <node role="variable" roleId="auek.768444928086151187" type="auek.TemporaryVariable" typeId="auek.7802504792141552484" id="594138331666576083" nodeInfo="ng">
                <property name="name" nameId="tpck.1169194664001" value="p" />
              </node>
            </node>
          </node>
        </node>
        <node role="constraints" roleId="auek.996292992024530408" type="auek.CheckConstraint" typeId="auek.996292992024530426" id="2815025855840419524" nodeInfo="ng">
          <node role="expression" roleId="auek.996292992025680416" type="tpee.GreaterThanExpression" typeId="tpee.1081506762703" id="2815025855840436921" nodeInfo="nn">
            <node role="rightExpression" roleId="tpee.1081773367579" type="tpee.IntegerConstant" typeId="tpee.1068580320020" id="2815025855840437037" nodeInfo="nn">
              <property name="value" nameId="tpee.1068580320021" value="2" />
            </node>
            <node role="leftExpression" roleId="tpee.1081773367580" type="tpee.DotExpression" typeId="tpee.1197027756228" id="2815025855840430356" nodeInfo="nn">
              <node role="operand" roleId="tpee.1197027771414" type="tpee.DotExpression" typeId="tpee.1197027756228" id="2815025855840427542" nodeInfo="nn">
                <node role="operand" roleId="tpee.1197027771414" type="auek.VariableReference" typeId="auek.2281067221947980594" id="2815025855840427203" nodeInfo="ng">
                  <link role="variable" roleId="auek.768444928085405086" targetNodeId="594138331666574549" resolveInfo="patternModel" />
                </node>
                <node role="operation" roleId="tpee.1197027833540" type="tp25.SPropertyAccess" typeId="tp25.1138056022639" id="2815025855840429289" nodeInfo="nn">
                  <link role="property" roleId="tp25.1138056395725" targetNodeId="tpck.1169194664001" resolveInfo="name" />
                </node>
              </node>
              <node role="operation" roleId="tpee.1197027833540" type="tpee.InstanceMethodCallOperation" typeId="tpee.1202948039474" id="2815025855840434885" nodeInfo="nn">
                <link role="baseMethodDeclaration" roleId="tpee.1068499141037" targetNodeId="e2lb.~String%dlength()%cint" resolveInfo="length" />
              </node>
            </node>
          </node>
        </node>
        <node role="constraints" roleId="auek.996292992024530408" type="auek.CompareConstraint" typeId="auek.996292992024530443" id="6085038789412629748" nodeInfo="ng">
          <property name="feature" nameId="auek.8396102296983865703" value="equality" />
          <node role="leftOperand" roleId="auek.8396102296983865626" type="auek.IntValue" typeId="auek.996292992024565941" id="6085038789412629992" nodeInfo="ng">
            <property name="value" nameId="auek.996292992024565942" value="1" />
          </node>
          <node role="rightOperand" roleId="auek.8396102296983865629" type="auek.VariableValue" typeId="auek.996292992024566673" id="594138331666576425" nodeInfo="ng">
            <node role="value" roleId="auek.996292992024566793" type="auek.VariableReference" typeId="auek.2281067221947980594" id="594138331666576599" nodeInfo="ng">
              <link role="variable" roleId="auek.768444928085405086" targetNodeId="594138331666576083" resolveInfo="p" />
            </node>
          </node>
        </node>
      </node>
    </node>
    <node role="contents" roleId="auek.996292992024500588" type="auek.Pattern" typeId="auek.996292992024449103" id="8800263097516744515" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="conceptConstraint" />
      <node role="bodies" roleId="auek.996292992024500596" type="auek.PatternBody" typeId="auek.996292992024500593" id="5589093812002806612" nodeInfo="ng">
        <node role="constraints" roleId="auek.996292992024530408" type="auek.ConceptConstraint" typeId="auek.5589093812003084634" id="768444928081120993" nodeInfo="ng">
          <link role="type" roleId="auek.5589093812003084769" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
          <node role="var" roleId="auek.5589093812003084950" type="auek.VariableReference" typeId="auek.2281067221947980594" id="768444928086148643" nodeInfo="ng">
            <link role="variable" roleId="auek.768444928085405086" targetNodeId="8800263097516744604" resolveInfo="pattern" />
          </node>
        </node>
      </node>
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="8800263097516744604" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="pattern" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="768444928081120978" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
        </node>
      </node>
    </node>
    <node role="contents" roleId="auek.996292992024500588" type="auek.Pattern" typeId="auek.996292992024449103" id="8337440621608806882" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="pathExpressions0" />
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="8337440621608806926" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="p" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="8337440621608806930" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
        </node>
      </node>
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="8337440621608806932" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="b" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="8337440621608806938" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="auek.996292992024500593" resolveInfo="PatternBody" />
        </node>
      </node>
      <node role="bodies" roleId="auek.996292992024500596" type="auek.PatternBody" typeId="auek.996292992024500593" id="8337440621608806883" nodeInfo="ng">
        <node role="constraints" roleId="auek.996292992024530408" type="auek.PathExpressionConstraint" typeId="auek.996292992024530493" id="8337440621608806940" nodeInfo="ng">
          <node role="head" roleId="auek.996292992027769733" type="auek.PathExpressionHead" typeId="auek.996292992025662526" id="8337440621608806941" nodeInfo="ng">
            <link role="type" roleId="auek.996292992025662567" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
            <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="8337440621608806950" nodeInfo="ng">
              <property name="index" nameId="auek.996292992025662611" value="-1" />
              <property name="closure" nameId="auek.996292992025662616" value="false" />
              <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="8337440621608806956" nodeInfo="ng">
                <link role="value" roleId="auek.6888142545404296242" targetNodeId="auek.996292992024500596" />
              </node>
            </node>
            <node role="trg" roleId="auek.996292992025662599" type="auek.VariableValue" typeId="auek.996292992024566673" id="2281067221947060501" nodeInfo="ng">
              <node role="value" roleId="auek.996292992024566793" type="auek.VariableReference" typeId="auek.2281067221947980594" id="768444928086148845" nodeInfo="ng">
                <link role="variable" roleId="auek.768444928085405086" targetNodeId="8337440621608806932" resolveInfo="b" />
              </node>
            </node>
            <node role="src" roleId="auek.996292992025662592" type="auek.VariableReference" typeId="auek.2281067221947980594" id="594138331666231552" nodeInfo="ng">
              <link role="variable" roleId="auek.768444928085405086" targetNodeId="8337440621608806926" resolveInfo="p" />
            </node>
          </node>
        </node>
      </node>
    </node>
    <node role="contents" roleId="auek.996292992024500588" type="auek.Pattern" typeId="auek.996292992024449103" id="8337440621608807428" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="pathExpressions1" />
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="8337440621608807463" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="p" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="8337440621608807467" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
        </node>
      </node>
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="8337440621608807469" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="c" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="3481695433003452016" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="auek.7705667014569610872" resolveInfo="Constraint" />
        </node>
      </node>
      <node role="bodies" roleId="auek.996292992024500596" type="auek.PatternBody" typeId="auek.996292992024500593" id="8337440621608807429" nodeInfo="ng">
        <node role="constraints" roleId="auek.996292992024530408" type="auek.PathExpressionConstraint" typeId="auek.996292992024530493" id="8337440621608807477" nodeInfo="ng">
          <node role="head" roleId="auek.996292992027769733" type="auek.PathExpressionHead" typeId="auek.996292992025662526" id="8337440621608807478" nodeInfo="ng">
            <link role="type" roleId="auek.996292992025662567" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
            <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="8337440621608807487" nodeInfo="ng">
              <property name="index" nameId="auek.996292992025662611" value="-1" />
              <property name="closure" nameId="auek.996292992025662616" value="false" />
              <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="1717385064697109660" nodeInfo="ng">
                <property name="index" nameId="auek.996292992025662611" value="-1" />
                <property name="closure" nameId="auek.996292992025662616" value="false" />
                <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="1717385064697109671" nodeInfo="ng">
                  <link role="value" roleId="auek.6888142545404296242" targetNodeId="auek.996292992024530408" />
                </node>
              </node>
              <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="1717385064697109668" nodeInfo="ng">
                <link role="value" roleId="auek.6888142545404296242" targetNodeId="auek.996292992024500596" />
              </node>
            </node>
            <node role="src" roleId="auek.996292992025662592" type="auek.VariableReference" typeId="auek.2281067221947980594" id="768444928086149022" nodeInfo="ng">
              <link role="variable" roleId="auek.768444928085405086" targetNodeId="8337440621608807463" resolveInfo="p" />
            </node>
            <node role="trg" roleId="auek.996292992025662599" type="auek.VariableValue" typeId="auek.996292992024566673" id="594138331666492781" nodeInfo="ng">
              <node role="value" roleId="auek.996292992024566793" type="auek.VariableReference" typeId="auek.2281067221947980594" id="594138331666492927" nodeInfo="ng">
                <link role="variable" roleId="auek.768444928085405086" targetNodeId="8337440621608807469" resolveInfo="c" />
              </node>
            </node>
          </node>
        </node>
      </node>
    </node>
    <node role="contents" roleId="auek.996292992024500588" type="auek.Pattern" typeId="auek.996292992024449103" id="2281067221946532994" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="pathExpressions2" />
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="2281067221946533063" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="p" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="594138331666231906" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
        </node>
      </node>
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="2281067221946533069" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="c" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="2281067221946533075" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="auek.7705667014569610872" resolveInfo="Constraint" />
        </node>
      </node>
      <node role="bodies" roleId="auek.996292992024500596" type="auek.PatternBody" typeId="auek.996292992024500593" id="2281067221946532995" nodeInfo="ng">
        <node role="constraints" roleId="auek.996292992024530408" type="auek.PathExpressionConstraint" typeId="auek.996292992024530493" id="2281067221946533053" nodeInfo="ng">
          <node role="head" roleId="auek.996292992027769733" type="auek.PathExpressionHead" typeId="auek.996292992025662526" id="2281067221946533054" nodeInfo="ng">
            <link role="type" roleId="auek.996292992025662567" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
            <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="2281067221946533077" nodeInfo="ng">
              <property name="index" nameId="auek.996292992025662611" value="-1" />
              <property name="closure" nameId="auek.996292992025662616" value="false" />
              <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="2281067221946533083" nodeInfo="ng">
                <link role="value" roleId="auek.6888142545404296242" targetNodeId="auek.996292992024500596" />
              </node>
            </node>
            <node role="src" roleId="auek.996292992025662592" type="auek.VariableReference" typeId="auek.2281067221947980594" id="768444928086150136" nodeInfo="ng">
              <link role="variable" roleId="auek.768444928085405086" targetNodeId="2281067221946533063" resolveInfo="p" />
            </node>
            <node role="trg" roleId="auek.996292992025662599" type="auek.TemporaryVariableValue" typeId="auek.768444928086151186" id="768444928086893721" nodeInfo="ng">
              <node role="variable" roleId="auek.768444928086151187" type="auek.TemporaryVariable" typeId="auek.7802504792141552484" id="768444928086893723" nodeInfo="ng">
                <property name="name" nameId="tpck.1169194664001" value="b" />
              </node>
            </node>
          </node>
        </node>
        <node role="constraints" roleId="auek.996292992024530408" type="auek.PathExpressionConstraint" typeId="auek.996292992024530493" id="2281067221947060641" nodeInfo="ng">
          <node role="head" roleId="auek.996292992027769733" type="auek.PathExpressionHead" typeId="auek.996292992025662526" id="2281067221947060643" nodeInfo="ng">
            <link role="type" roleId="auek.996292992025662567" targetNodeId="auek.996292992024500593" resolveInfo="PatternBody" />
            <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="2281067221947060661" nodeInfo="ng">
              <property name="index" nameId="auek.996292992025662611" value="-1" />
              <property name="closure" nameId="auek.996292992025662616" value="false" />
              <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="2281067221947060667" nodeInfo="ng">
                <link role="value" roleId="auek.6888142545404296242" targetNodeId="auek.996292992024530408" />
              </node>
            </node>
            <node role="trg" roleId="auek.996292992025662599" type="auek.VariableValue" typeId="auek.996292992024566673" id="594138331665937042" nodeInfo="ng">
              <node role="value" roleId="auek.996292992024566793" type="auek.VariableReference" typeId="auek.2281067221947980594" id="594138331666414164" nodeInfo="ng">
                <link role="variable" roleId="auek.768444928085405086" targetNodeId="2281067221946533069" resolveInfo="c" />
              </node>
            </node>
            <node role="src" roleId="auek.996292992025662592" type="auek.VariableReference" typeId="auek.2281067221947980594" id="3770201403571887966" nodeInfo="ng">
              <link role="variable" roleId="auek.768444928085405086" targetNodeId="768444928086893723" resolveInfo="b" />
            </node>
          </node>
        </node>
      </node>
    </node>
    <node role="contents" roleId="auek.996292992024500588" type="auek.Pattern" typeId="auek.996292992024449103" id="900061070401130215" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="patternCall0" />
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="900061070401130300" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="pattern" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="768444928080954772" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
        </node>
      </node>
      <node role="bodies" roleId="auek.996292992024500596" type="auek.PatternBody" typeId="auek.996292992024500593" id="900061070401130216" nodeInfo="ng">
        <node role="constraints" roleId="auek.996292992024530408" type="auek.PatternCompositionConstraint" typeId="auek.996292992024530460" id="900061070401137417" nodeInfo="ng">
          <node role="call" roleId="auek.996292992028393460" type="auek.PatternCall" typeId="auek.996292992024566952" id="900061070401137418" nodeInfo="ng">
            <link role="patternRef" roleId="auek.996292992028507459" targetNodeId="8800263097516744515" resolveInfo="conceptConstraint" />
            <node role="parameters" roleId="auek.996292992028507462" type="auek.VariableValue" typeId="auek.996292992024566673" id="8005688629620379522" nodeInfo="ng">
              <node role="value" roleId="auek.996292992024566793" type="auek.VariableReference" typeId="auek.2281067221947980594" id="768444928086150670" nodeInfo="ng">
                <link role="variable" roleId="auek.768444928085405086" targetNodeId="900061070401130300" resolveInfo="pattern" />
              </node>
            </node>
          </node>
        </node>
      </node>
    </node>
    <node role="contents" roleId="auek.996292992024500588" type="auek.Pattern" typeId="auek.996292992024449103" id="6085038789410821094" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="patternCall1" />
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="9087516747608705564" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="a" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="156470876045763826" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="auek.996292992024449103" resolveInfo="Pattern" />
        </node>
      </node>
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="6085038789411032584" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="b" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="594138331666699771" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="auek.7705667014569610872" resolveInfo="Constraint" />
        </node>
      </node>
      <node role="bodies" roleId="auek.996292992024500596" type="auek.PatternBody" typeId="auek.996292992024500593" id="6085038789410821095" nodeInfo="ng">
        <node role="constraints" roleId="auek.996292992024530408" type="auek.PatternCompositionConstraint" typeId="auek.996292992024530460" id="6085038789410842091" nodeInfo="ng">
          <node role="call" roleId="auek.996292992028393460" type="auek.PatternCall" typeId="auek.996292992024566952" id="6085038789410842092" nodeInfo="ng">
            <link role="patternRef" roleId="auek.996292992028507459" targetNodeId="2281067221946532994" resolveInfo="pathExpressions2" />
            <node role="parameters" roleId="auek.996292992028507462" type="auek.VariableValue" typeId="auek.996292992024566673" id="6085038789410842536" nodeInfo="ng">
              <node role="value" roleId="auek.996292992024566793" type="auek.VariableReference" typeId="auek.2281067221947980594" id="6085038789410842694" nodeInfo="ng">
                <link role="variable" roleId="auek.768444928085405086" targetNodeId="9087516747608705564" resolveInfo="a" />
              </node>
            </node>
            <node role="parameters" roleId="auek.996292992028507462" type="auek.VariableValue" typeId="auek.996292992024566673" id="6085038789410842291" nodeInfo="ng">
              <node role="value" roleId="auek.996292992024566793" type="auek.VariableReference" typeId="auek.2281067221947980594" id="6085038789410842415" nodeInfo="ng">
                <link role="variable" roleId="auek.768444928085405086" targetNodeId="6085038789411032584" resolveInfo="b" />
              </node>
            </node>
          </node>
        </node>
      </node>
    </node>
  </root>
  <root type="tpee.ClassConcept" typeId="tpee.1068390468198" id="156470876049023597" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="Instantiation" />
    <node role="member" roleId="tpee.5375687026011219971" type="tpee.PlaceholderMember" typeId="tpee.1465982738277781862" id="156470876049023620" nodeInfo="ngu" />
    <node role="member" roleId="tpee.5375687026011219971" type="tpee.StaticMethodDeclaration" typeId="tpee.1081236700938" id="156470876049023671" nodeInfo="igu">
      <property name="name" nameId="tpck.1169194664001" value="main" />
      <property name="isSynchronized" nameId="tpee.4276006055363816570" value="false" />
      <property name="isFinal" nameId="tpee.1181808852946" value="false" />
      <node role="body" roleId="tpee.1068580123135" type="tpee.StatementList" typeId="tpee.1068580123136" id="156470876049023674" nodeInfo="sn">
        <node role="statement" roleId="tpee.1068581517665" type="tpee.LocalVariableDeclarationStatement" typeId="tpee.1068581242864" id="156470876049630906" nodeInfo="nn">
          <node role="localVariableDeclaration" roleId="tpee.1068581242865" type="tpee.LocalVariableDeclaration" typeId="tpee.1068581242863" id="156470876049630907" nodeInfo="nr">
            <property name="name" nameId="tpck.1169194664001" value="matcher1" />
            <node role="type" roleId="tpee.5680397130376446158" type="tpee.ClassifierType" typeId="tpee.1107535904670" id="156470876049630908" nodeInfo="in">
              <link role="classifier" roleId="tpee.1107535924139" targetNodeId="67jt.~IncQueryMatcher" resolveInfo="IncQueryMatcher" />
            </node>
            <node role="initializer" roleId="tpee.1068431790190" type="auek.MPSIncQueryMatcherInstantiation" typeId="auek.7241148409043933760" id="156470876049886834" nodeInfo="ng">
              <link role="pattern" roleId="auek.7241148409043933812" targetNodeId="8800263097516744515" resolveInfo="conceptConstraint" />
              <node role="model" roleId="auek.8066520122896896506" type="tp25.ModelReferenceExpression" typeId="tp25.559557797393017698" id="156470876050453948" nodeInfo="nn">
                <property name="name" nameId="tp25.559557797393017702" value="org.eclipse.incquery.mps.test.base.playground" />
                <property name="stereotype" nameId="tp25.559557797393021807" value="" />
              </node>
            </node>
          </node>
        </node>
        <node role="statement" roleId="tpee.1068581517665" type="tpee.ExpressionStatement" typeId="tpee.1068580123155" id="156470876049887287" nodeInfo="nn">
          <node role="expression" roleId="tpee.1068580123156" type="tpee.DotExpression" typeId="tpee.1197027756228" id="156470876049887333" nodeInfo="nn">
            <node role="operand" roleId="tpee.1197027771414" type="tpee.VariableReference" typeId="tpee.1068498886296" id="156470876049887285" nodeInfo="nn">
              <link role="variableDeclaration" roleId="tpee.1068581517664" targetNodeId="156470876049630907" resolveInfo="matcher1" />
            </node>
            <node role="operation" roleId="tpee.1197027833540" type="tpee.InstanceMethodCallOperation" typeId="tpee.1202948039474" id="156470876049888169" nodeInfo="nn">
              <link role="baseMethodDeclaration" roleId="tpee.1068499141037" targetNodeId="67jt.~IncQueryMatcher%dcountMatches()%cint" resolveInfo="countMatches" />
            </node>
          </node>
        </node>
      </node>
      <node role="visibility" roleId="tpee.1178549979242" type="tpee.PublicVisibility" typeId="tpee.1146644602865" id="156470876049023639" nodeInfo="nn" />
      <node role="returnType" roleId="tpee.1068580123133" type="tpee.VoidType" typeId="tpee.1068581517677" id="156470876049023661" nodeInfo="in" />
      <node role="parameter" roleId="tpee.1068580123134" type="tpee.ParameterDeclaration" typeId="tpee.1068498886292" id="156470876049023689" nodeInfo="ir">
        <property name="name" nameId="tpck.1169194664001" value="args" />
        <node role="type" roleId="tpee.5680397130376446158" type="tpee.ArrayType" typeId="tpee.1070534760951" id="156470876049023763" nodeInfo="in">
          <node role="componentType" roleId="tpee.1070534760952" type="tpee.StringType" typeId="tpee.1225271177708" id="156470876049023688" nodeInfo="in" />
        </node>
      </node>
    </node>
    <node role="member" roleId="tpee.5375687026011219971" type="tpee.PlaceholderMember" typeId="tpee.1465982738277781862" id="156470876049023625" nodeInfo="ngu" />
    <node role="visibility" roleId="tpee.1178549979242" type="tpee.PublicVisibility" typeId="tpee.1146644602865" id="156470876049023598" nodeInfo="nn" />
  </root>
</model>

