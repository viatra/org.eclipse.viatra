<?xml version="1.0" encoding="UTF-8"?>
<model modelUID="r:0975e64c-0ad7-4495-a99a-c275c88d9b86(org.eclipse.incquery.mps.test.behavior)">
  <persistence version="8" />
  <language namespace="af65afd8-f0dd-4942-87d9-63a55f2a9db1(jetbrains.mps.lang.behavior)" />
  <language namespace="ef5ea086-f248-4019-bdc4-4a594cfbdd2e(org.eclipse.incquery.mps)" />
  <devkit namespace="fbc25dd2-5da4-483a-8b19-70928e1b62d7(jetbrains.mps.devkit.general-purpose)" />
  <import index="67jt" modelUID="f:java_stub#d78ad636-1087-4a2a-8147-0f6b287011c2#org.eclipse.incquery.runtime.api(org.eclipse.incquery.mps.runtime/org.eclipse.incquery.runtime.api@java_stub)" version="-1" />
  <import index="k7g3" modelUID="f:java_stub#6354ebe7-c22a-4a0f-ac54-50b52ab9b065#java.util(JDK/java.util@java_stub)" version="-1" />
  <import index="e2lb" modelUID="f:java_stub#6354ebe7-c22a-4a0f-ac54-50b52ab9b065#java.lang(JDK/java.lang@java_stub)" version="-1" />
  <import index="f1uo" modelUID="r:326fd4ed-5369-4cc8-8788-92145c4d8911(org.eclipse.incquery.mps.runtime)" version="-1" />
  <import index="ipj7" modelUID="r:72b2626f-5a0a-40af-a2b4-fbc2ae1b60c1(org.eclipse.incquery.mps.test.structure)" version="0" />
  <import index="fxg7" modelUID="f:java_stub#6354ebe7-c22a-4a0f-ac54-50b52ab9b065#java.io(JDK/java.io@java_stub)" version="-1" />
  <import index="tpcu" modelUID="r:00000000-0000-4000-0000-011c89590282(jetbrains.mps.lang.core.behavior)" version="-1" />
  <import index="auek" modelUID="r:8c420ee7-5605-40f1-8ffd-968aa96940f0(org.eclipse.incquery.mps.structure)" version="35" />
  <import index="tpee" modelUID="r:00000000-0000-4000-0000-011c895902ca(jetbrains.mps.baseLanguage.structure)" version="5" />
  <import index="tpck" modelUID="r:00000000-0000-4000-0000-011c89590288(jetbrains.mps.lang.core.structure)" version="0" implicit="yes" />
  <import index="tp25" modelUID="r:00000000-0000-4000-0000-011c89590301(jetbrains.mps.lang.smodel.structure)" version="16" implicit="yes" />
  <import index="1i04" modelUID="r:3270011d-8b2d-4938-8dff-d256a759e017(jetbrains.mps.lang.behavior.structure)" version="-1" implicit="yes" />
  <import index="tp2q" modelUID="r:00000000-0000-4000-0000-011c8959032e(jetbrains.mps.baseLanguage.collections.structure)" version="7" implicit="yes" />
  <root type="1i04.ConceptBehavior" typeId="1i04.1225194240794" id="7224892637327655353" nodeInfo="ng">
    <link role="concept" roleId="1i04.1225194240799" targetNodeId="ipj7.7224892637325261564" resolveInfo="School" />
    <node role="method" roleId="1i04.1225194240805" type="1i04.ConceptMethodDeclaration" typeId="1i04.1225194472830" id="7224892637327655558" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="getStudents" />
      <node role="visibility" roleId="tpee.1178549979242" type="tpee.PublicVisibility" typeId="tpee.1146644602865" id="7224892637327655559" nodeInfo="nn" />
      <node role="returnType" roleId="tpee.1068580123133" type="tp2q.SetType" typeId="tp2q.1226511727824" id="7224892637327655566" nodeInfo="in">
        <node role="elementType" roleId="tp2q.1226511765987" type="tp25.SNodeType" typeId="tp25.1138055754698" id="7224892637327655572" nodeInfo="in">
          <link role="concept" roleId="tp25.1138405853777" targetNodeId="ipj7.7224892637325261508" resolveInfo="Student" />
        </node>
      </node>
      <node role="body" roleId="tpee.1068580123135" type="tpee.StatementList" typeId="tpee.1068580123136" id="7224892637327655561" nodeInfo="sn">
        <node role="statement" roleId="tpee.1068581517665" type="tpee.ReturnStatement" typeId="tpee.1068581242878" id="7224892637327655938" nodeInfo="nn">
          <node role="expression" roleId="tpee.1068581517676" type="tpee.NullLiteral" typeId="tpee.1070534058343" id="7224892637327655955" nodeInfo="nn" />
        </node>
      </node>
    </node>
    <node role="constructor" roleId="1i04.1225194240801" type="1i04.ConceptConstructorDeclaration" typeId="1i04.1225194413805" id="7224892637327655505" nodeInfo="nn">
      <node role="body" roleId="tpee.1137022507850" type="tpee.StatementList" typeId="tpee.1068580123136" id="7224892637327655506" nodeInfo="sn" />
    </node>
  </root>
  <root type="auek.PatternModel" typeId="auek.996292992024500587" id="7224892637325800664" nodeInfo="ng">
    <property name="package" nameId="auek.996292992024500590" value="org.eclipse.incquery.mps.test" />
    <node role="contents" roleId="auek.996292992024500588" type="auek.Pattern" typeId="auek.996292992024449103" id="7224892637326367999" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="students" />
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="7224892637326368053" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="school" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="7224892637326368081" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="ipj7.7224892637325261564" resolveInfo="School" />
        </node>
      </node>
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="7224892637326368013" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="student" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="7224892637326368043" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="ipj7.7224892637325261508" resolveInfo="Student" />
        </node>
      </node>
      <node role="bodies" roleId="auek.996292992024500596" type="auek.PatternBody" typeId="auek.996292992024500593" id="7224892637326368001" nodeInfo="ng">
        <node role="constraints" roleId="auek.996292992024530408" type="auek.PathExpressionConstraint" typeId="auek.996292992024530493" id="1620063141947311804" nodeInfo="ng">
          <node role="head" roleId="auek.996292992027769733" type="auek.PathExpressionHead" typeId="auek.996292992025662526" id="1620063141947311805" nodeInfo="ng">
            <link role="type" roleId="auek.996292992025662567" targetNodeId="ipj7.7224892637325261564" resolveInfo="School" />
            <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="1620063141947311836" nodeInfo="ng">
              <property name="index" nameId="auek.996292992025662611" value="-1" />
              <property name="closure" nameId="auek.996292992025662616" value="false" />
              <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="1620063141947311892" nodeInfo="ng">
                <link role="value" roleId="auek.6888142545404296242" targetNodeId="ipj7.7224892637325263851" />
              </node>
            </node>
            <node role="src" roleId="auek.996292992025662592" type="auek.VariableReference" typeId="auek.2281067221947980594" id="1620063141947311917" nodeInfo="ng">
              <link role="variable" roleId="auek.768444928085405086" targetNodeId="7224892637326368053" resolveInfo="school" />
            </node>
            <node role="trg" roleId="auek.996292992025662599" type="auek.VariableValue" typeId="auek.996292992024566673" id="1620063141947311944" nodeInfo="ng">
              <node role="value" roleId="auek.996292992024566793" type="auek.VariableReference" typeId="auek.2281067221947980594" id="1620063141947311978" nodeInfo="ng">
                <link role="variable" roleId="auek.768444928085405086" targetNodeId="7224892637326368013" resolveInfo="student" />
              </node>
            </node>
          </node>
        </node>
      </node>
    </node>
    <node role="contents" roleId="auek.996292992024500588" type="auek.Pattern" typeId="auek.996292992024449103" id="1620063141947312092" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="directFriends" />
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="1620063141947312144" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="s1" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="1620063141947312176" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="ipj7.7224892637325261508" resolveInfo="Student" />
        </node>
      </node>
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="1620063141947312187" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="s2" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="1620063141947312225" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="ipj7.7224892637325261508" resolveInfo="Student" />
        </node>
      </node>
      <node role="bodies" roleId="auek.996292992024500596" type="auek.PatternBody" typeId="auek.996292992024500593" id="1620063141947312094" nodeInfo="ng">
        <node role="constraints" roleId="auek.996292992024530408" type="auek.PathExpressionConstraint" typeId="auek.996292992024530493" id="1620063141947312246" nodeInfo="ng">
          <node role="head" roleId="auek.996292992027769733" type="auek.PathExpressionHead" typeId="auek.996292992025662526" id="1620063141947312247" nodeInfo="ng">
            <link role="type" roleId="auek.996292992025662567" targetNodeId="ipj7.7224892637325261508" resolveInfo="Student" />
            <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="1620063141947312278" nodeInfo="ng">
              <property name="index" nameId="auek.996292992025662611" value="-1" />
              <property name="closure" nameId="auek.996292992025662616" value="false" />
              <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="1620063141947312294" nodeInfo="ng">
                <link role="value" roleId="auek.6888142545404296242" targetNodeId="ipj7.7224892637325261610" />
              </node>
              <node role="tail" roleId="auek.5589093812001602541" type="auek.PathExpressionTail" typeId="auek.996292992025662591" id="1620063141947313186" nodeInfo="ng">
                <property name="index" nameId="auek.996292992025662611" value="-1" />
                <property name="closure" nameId="auek.996292992025662616" value="false" />
                <node role="type" roleId="auek.996292992028124127" type="auek.LinkDeclarationType" typeId="auek.996292992025675491" id="1620063141947313203" nodeInfo="ng">
                  <link role="value" roleId="auek.6888142545404296242" targetNodeId="ipj7.7224892637325261608" />
                </node>
              </node>
            </node>
            <node role="src" roleId="auek.996292992025662592" type="auek.VariableReference" typeId="auek.2281067221947980594" id="1620063141947312319" nodeInfo="ng">
              <link role="variable" roleId="auek.768444928085405086" targetNodeId="1620063141947312144" resolveInfo="s1" />
            </node>
            <node role="trg" roleId="auek.996292992025662599" type="auek.VariableValue" typeId="auek.996292992024566673" id="1620063141947312346" nodeInfo="ng">
              <node role="value" roleId="auek.996292992024566793" type="auek.VariableReference" typeId="auek.2281067221947980594" id="1620063141947312606" nodeInfo="ng">
                <link role="variable" roleId="auek.768444928085405086" targetNodeId="1620063141947312187" resolveInfo="s2" />
              </node>
            </node>
          </node>
        </node>
      </node>
    </node>
    <node role="contents" roleId="auek.996292992024500588" type="auek.Pattern" typeId="auek.996292992024449103" id="1620063141947313579" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="allFriends" />
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="1620063141947313874" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="s1" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="8202482261175710114" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="ipj7.7224892637325261508" resolveInfo="Student" />
        </node>
      </node>
      <node role="parameters" roleId="auek.996292992024500594" type="auek.Parameter" typeId="auek.996292992024500592" id="1620063141947313911" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="s2" />
        <node role="type" roleId="auek.996292992025675764" type="auek.ConceptReferenceType" typeId="auek.996292992025672789" id="1620063141947313949" nodeInfo="ng">
          <link role="concept" roleId="auek.7241148409041409499" targetNodeId="ipj7.7224892637325261508" resolveInfo="Student" />
        </node>
      </node>
      <node role="bodies" roleId="auek.996292992024500596" type="auek.PatternBody" typeId="auek.996292992024500593" id="1620063141947313581" nodeInfo="ng">
        <node role="constraints" roleId="auek.996292992024530408" type="auek.PatternCompositionConstraint" typeId="auek.996292992024530460" id="1620063141947313960" nodeInfo="ng">
          <node role="call" roleId="auek.996292992028393460" type="auek.PatternCall" typeId="auek.996292992024566952" id="1620063141947313961" nodeInfo="ng">
            <property name="transitive" nameId="auek.996292992028507456" value="true" />
            <link role="patternRef" roleId="auek.996292992028507459" targetNodeId="1620063141947312092" resolveInfo="directFriends" />
            <node role="parameters" roleId="auek.996292992028507462" type="auek.VariableValue" typeId="auek.996292992024566673" id="1620063141947313980" nodeInfo="ng">
              <node role="value" roleId="auek.996292992024566793" type="auek.VariableReference" typeId="auek.2281067221947980594" id="1620063141947314080" nodeInfo="ng">
                <link role="variable" roleId="auek.768444928085405086" targetNodeId="1620063141947313874" resolveInfo="s1" />
              </node>
            </node>
            <node role="parameters" roleId="auek.996292992028507462" type="auek.VariableValue" typeId="auek.996292992024566673" id="1620063141947314101" nodeInfo="ng">
              <node role="value" roleId="auek.996292992024566793" type="auek.VariableReference" typeId="auek.2281067221947980594" id="1620063141947314177" nodeInfo="ng">
                <link role="variable" roleId="auek.768444928085405086" targetNodeId="1620063141947313911" resolveInfo="s2" />
              </node>
            </node>
          </node>
        </node>
        <node role="constraints" roleId="auek.996292992024530408" type="auek.CompareConstraint" typeId="auek.996292992024530443" id="7705667014571714800" nodeInfo="ng">
          <property name="feature" nameId="auek.8396102296983865703" value="inequality" />
          <node role="leftOperand" roleId="auek.8396102296983865626" type="auek.VariableValue" typeId="auek.996292992024566673" id="7705667014571714824" nodeInfo="ng">
            <node role="value" roleId="auek.996292992024566793" type="auek.VariableReference" typeId="auek.2281067221947980594" id="7705667014571714836" nodeInfo="ng">
              <link role="variable" roleId="auek.768444928085405086" targetNodeId="1620063141947313874" resolveInfo="s1" />
            </node>
          </node>
          <node role="rightOperand" roleId="auek.8396102296983865629" type="auek.VariableValue" typeId="auek.996292992024566673" id="7705667014571714848" nodeInfo="ng">
            <node role="value" roleId="auek.996292992024566793" type="auek.VariableReference" typeId="auek.2281067221947980594" id="7705667014571714860" nodeInfo="ng">
              <link role="variable" roleId="auek.768444928085405086" targetNodeId="1620063141947313911" resolveInfo="s2" />
            </node>
          </node>
        </node>
      </node>
    </node>
  </root>
  <root type="1i04.ConceptBehavior" typeId="1i04.1225194240794" id="1620063141947337653" nodeInfo="ng">
    <link role="concept" roleId="1i04.1225194240799" targetNodeId="ipj7.7224892637325261508" resolveInfo="Student" />
    <node role="method" roleId="1i04.1225194240805" type="1i04.ConceptMethodDeclaration" typeId="1i04.1225194472830" id="1620063141947337656" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="getAllFriends" />
      <node role="visibility" roleId="tpee.1178549979242" type="tpee.PublicVisibility" typeId="tpee.1146644602865" id="1620063141947337657" nodeInfo="nn" />
      <node role="returnType" roleId="tpee.1068580123133" type="tp2q.SetType" typeId="tp2q.1226511727824" id="1620063141947337672" nodeInfo="in">
        <node role="elementType" roleId="tp2q.1226511765987" type="tp25.SNodeType" typeId="tp25.1138055754698" id="1620063141947337682" nodeInfo="in">
          <link role="concept" roleId="tp25.1138405853777" targetNodeId="ipj7.7224892637325261508" resolveInfo="Student" />
        </node>
      </node>
      <node role="body" roleId="tpee.1068580123135" type="tpee.StatementList" typeId="tpee.1068580123136" id="1620063141947337659" nodeInfo="sn">
        <node role="statement" roleId="tpee.1068581517665" type="tpee.LocalVariableDeclarationStatement" typeId="tpee.1068581242864" id="1620063141947337704" nodeInfo="nn">
          <node role="localVariableDeclaration" roleId="tpee.1068581242865" type="tpee.LocalVariableDeclaration" typeId="tpee.1068581242863" id="1620063141947337705" nodeInfo="nr">
            <property name="name" nameId="tpck.1169194664001" value="matcher" />
            <node role="initializer" roleId="tpee.1068431790190" type="auek.MPSIncQueryMatcherInstantiation" typeId="auek.7241148409043933760" id="1620063141947337988" nodeInfo="ng">
              <link role="pattern" roleId="auek.7241148409043933812" targetNodeId="1620063141947313579" resolveInfo="allFriends" />
              <node role="model" roleId="auek.8066520122896896506" type="tpee.DotExpression" typeId="tpee.1197027756228" id="1620063141947338244" nodeInfo="nn">
                <node role="operand" roleId="tpee.1197027771414" type="1i04.ThisNodeExpression" typeId="1i04.1225194691553" id="1620063141947338125" nodeInfo="nn" />
                <node role="operation" roleId="tpee.1197027833540" type="tp25.Node_GetModelOperation" typeId="tp25.1143234257716" id="1620063141947338886" nodeInfo="nn" />
              </node>
            </node>
            <node role="type" roleId="tpee.5680397130376446158" type="auek.MPSIncQueryMatcher" typeId="auek.8650544432874604370" id="7447605944640376085" nodeInfo="ig">
              <link role="pattern" roleId="auek.8650544432874609807" targetNodeId="1620063141947313579" resolveInfo="allFriends" />
            </node>
          </node>
        </node>
        <node role="statement" roleId="tpee.1068581517665" type="tpee.LocalVariableDeclarationStatement" typeId="tpee.1068581242864" id="5880800913036141948" nodeInfo="nn">
          <node role="localVariableDeclaration" roleId="tpee.1068581242865" type="tpee.LocalVariableDeclaration" typeId="tpee.1068581242863" id="5880800913036141951" nodeInfo="nr">
            <property name="name" nameId="tpck.1169194664001" value="partialMatch" />
            <node role="type" roleId="tpee.5680397130376446158" type="auek.MPSIncQueryMatch" typeId="auek.7447605944636584242" id="5880800913036141946" nodeInfo="ig">
              <link role="pattern" roleId="auek.7447605944636589388" targetNodeId="1620063141947313579" resolveInfo="allFriends" />
            </node>
            <node role="initializer" roleId="tpee.1068431790190" type="tpee.DotExpression" typeId="tpee.1197027756228" id="5880800913036651596" nodeInfo="nn">
              <node role="operand" roleId="tpee.1197027771414" type="tpee.VariableReference" typeId="tpee.1068498886296" id="5880800913036651485" nodeInfo="nn">
                <link role="variableDeclaration" roleId="tpee.1068581517664" targetNodeId="1620063141947337705" resolveInfo="matcher" />
              </node>
              <node role="operation" roleId="tpee.1197027833540" type="auek.NewMatchOperation" typeId="auek.7447605944634188286" id="5880800913037132244" nodeInfo="ng">
                <node role="bindings" roleId="auek.7447605944634333569" type="auek.PartialMatchParameterBinding" typeId="auek.7447605944634311434" id="5880800913037132294" nodeInfo="ng">
                  <link role="parameter" roleId="auek.7447605944634512798" targetNodeId="1620063141947313874" resolveInfo="s1" />
                  <node role="value" roleId="auek.7447605944634317224" type="1i04.ThisNodeExpression" typeId="1i04.1225194691553" id="5880800913037132339" nodeInfo="nn" />
                </node>
              </node>
            </node>
          </node>
        </node>
        <node role="statement" roleId="tpee.1068581517665" type="tpee.ReturnStatement" typeId="tpee.1068581242878" id="5880800913037132527" nodeInfo="nn">
          <node role="expression" roleId="tpee.1068581517676" type="tpee.DotExpression" typeId="tpee.1197027756228" id="5880800913037132752" nodeInfo="nn">
            <node role="operand" roleId="tpee.1197027771414" type="tpee.VariableReference" typeId="tpee.1068498886296" id="5880800913037132703" nodeInfo="nn">
              <link role="variableDeclaration" roleId="tpee.1068581517664" targetNodeId="1620063141947337705" resolveInfo="matcher" />
            </node>
            <node role="operation" roleId="tpee.1197027833540" type="auek.GetAllValuesOperation" typeId="auek.8650544432873311946" id="5880800913037133318" nodeInfo="ng">
              <link role="parameter" roleId="auek.8202482261175133153" targetNodeId="1620063141947313911" resolveInfo="s2" />
              <node role="partialMatch" roleId="auek.8650544432874558885" type="tpee.VariableReference" typeId="tpee.1068498886296" id="5880800913037536383" nodeInfo="nn">
                <link role="variableDeclaration" roleId="tpee.1068581517664" targetNodeId="5880800913036141951" resolveInfo="partialMatch" />
              </node>
            </node>
          </node>
        </node>
      </node>
    </node>
    <node role="constructor" roleId="1i04.1225194240801" type="1i04.ConceptConstructorDeclaration" typeId="1i04.1225194413805" id="1620063141947337654" nodeInfo="nn">
      <node role="body" roleId="tpee.1137022507850" type="tpee.StatementList" typeId="tpee.1068580123136" id="1620063141947337655" nodeInfo="sn" />
    </node>
  </root>
  <root type="1i04.ConceptBehavior" typeId="1i04.1225194240794" id="2239108793198785784" nodeInfo="ng">
    <link role="concept" roleId="1i04.1225194240799" targetNodeId="ipj7.7224892637325261607" resolveInfo="StudentReference" />
    <node role="method" roleId="1i04.1225194240805" type="1i04.ConceptMethodDeclaration" typeId="1i04.1225194472830" id="2239108793198786140" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="getPresentation" />
      <link role="overriddenMethod" roleId="1i04.1225194472831" targetNodeId="tpcu.1213877396640" resolveInfo="getPresentation" />
      <node role="body" roleId="tpee.1068580123135" type="tpee.StatementList" typeId="tpee.1068580123136" id="2239108793198786143" nodeInfo="sn">
        <node role="statement" roleId="tpee.1068581517665" type="tpee.ReturnStatement" typeId="tpee.1068581242878" id="2239108793198826027" nodeInfo="nn">
          <node role="expression" roleId="tpee.1068581517676" type="tpee.PlusExpression" typeId="tpee.1068581242875" id="2239108793198826987" nodeInfo="nn">
            <node role="rightExpression" roleId="tpee.1081773367579" type="tpee.DotExpression" typeId="tpee.1197027756228" id="2239108793198829315" nodeInfo="nn">
              <node role="operand" roleId="tpee.1197027771414" type="tpee.DotExpression" typeId="tpee.1197027756228" id="2239108793198827442" nodeInfo="nn">
                <node role="operand" roleId="tpee.1197027771414" type="1i04.ThisNodeExpression" typeId="1i04.1225194691553" id="2239108793198827016" nodeInfo="nn" />
                <node role="operation" roleId="tpee.1197027833540" type="tp25.SLinkAccess" typeId="tp25.1138056143562" id="2239108793198828400" nodeInfo="nn">
                  <link role="link" roleId="tp25.1138056516764" targetNodeId="ipj7.7224892637325261608" />
                </node>
              </node>
              <node role="operation" roleId="tpee.1197027833540" type="tp25.SPropertyAccess" typeId="tp25.1138056022639" id="2239108793198830497" nodeInfo="nn">
                <link role="property" roleId="tp25.1138056395725" targetNodeId="tpck.1169194664001" resolveInfo="name" />
              </node>
            </node>
            <node role="leftExpression" roleId="tpee.1081773367580" type="tpee.StringLiteral" typeId="tpee.1070475926800" id="2239108793198826133" nodeInfo="nn">
              <property name="value" nameId="tpee.1070475926801" value="ref-&gt; " />
            </node>
          </node>
        </node>
      </node>
      <node role="returnType" roleId="tpee.1068580123133" type="tpee.StringType" typeId="tpee.1225271177708" id="2239108793198826023" nodeInfo="in" />
      <node role="visibility" roleId="tpee.1178549979242" type="tpee.PublicVisibility" typeId="tpee.1146644602865" id="2239108793198826024" nodeInfo="nn" />
    </node>
    <node role="constructor" roleId="1i04.1225194240801" type="1i04.ConceptConstructorDeclaration" typeId="1i04.1225194413805" id="2239108793198786087" nodeInfo="nn">
      <node role="body" roleId="tpee.1137022507850" type="tpee.StatementList" typeId="tpee.1068580123136" id="2239108793198786088" nodeInfo="sn" />
    </node>
  </root>
  <root type="1i04.ConceptBehavior" typeId="1i04.1225194240794" id="2239108793198838788" nodeInfo="ng">
    <link role="concept" roleId="1i04.1225194240799" targetNodeId="ipj7.7224892637325261587" resolveInfo="CourseReference" />
    <node role="method" roleId="1i04.1225194240805" type="1i04.ConceptMethodDeclaration" typeId="1i04.1225194472830" id="2239108793198838827" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="getPresentation" />
      <link role="overriddenMethod" roleId="1i04.1225194472831" targetNodeId="tpcu.1213877396640" resolveInfo="getPresentation" />
      <node role="body" roleId="tpee.1068580123135" type="tpee.StatementList" typeId="tpee.1068580123136" id="2239108793198838828" nodeInfo="sn">
        <node role="statement" roleId="tpee.1068581517665" type="tpee.ReturnStatement" typeId="tpee.1068581242878" id="2239108793198838829" nodeInfo="nn">
          <node role="expression" roleId="tpee.1068581517676" type="tpee.PlusExpression" typeId="tpee.1068581242875" id="2239108793198838830" nodeInfo="nn">
            <node role="rightExpression" roleId="tpee.1081773367579" type="tpee.DotExpression" typeId="tpee.1197027756228" id="2239108793198838831" nodeInfo="nn">
              <node role="operand" roleId="tpee.1197027771414" type="tpee.DotExpression" typeId="tpee.1197027756228" id="2239108793198838832" nodeInfo="nn">
                <node role="operand" roleId="tpee.1197027771414" type="1i04.ThisNodeExpression" typeId="1i04.1225194691553" id="2239108793198838833" nodeInfo="nn" />
                <node role="operation" roleId="tpee.1197027833540" type="tp25.SLinkAccess" typeId="tp25.1138056143562" id="2239108793198905954" nodeInfo="nn">
                  <link role="link" roleId="tp25.1138056516764" targetNodeId="ipj7.7224892637325261588" />
                </node>
              </node>
              <node role="operation" roleId="tpee.1197027833540" type="tp25.SPropertyAccess" typeId="tp25.1138056022639" id="2239108793198838835" nodeInfo="nn">
                <link role="property" roleId="tp25.1138056395725" targetNodeId="tpck.1169194664001" resolveInfo="name" />
              </node>
            </node>
            <node role="leftExpression" roleId="tpee.1081773367580" type="tpee.StringLiteral" typeId="tpee.1070475926800" id="2239108793198838836" nodeInfo="nn">
              <property name="value" nameId="tpee.1070475926801" value="ref-&gt; " />
            </node>
          </node>
        </node>
      </node>
      <node role="returnType" roleId="tpee.1068580123133" type="tpee.StringType" typeId="tpee.1225271177708" id="2239108793198838837" nodeInfo="in" />
      <node role="visibility" roleId="tpee.1178549979242" type="tpee.PublicVisibility" typeId="tpee.1146644602865" id="2239108793198838838" nodeInfo="nn" />
    </node>
    <node role="constructor" roleId="1i04.1225194240801" type="1i04.ConceptConstructorDeclaration" typeId="1i04.1225194413805" id="2239108793198838789" nodeInfo="nn">
      <node role="body" roleId="tpee.1137022507850" type="tpee.StatementList" typeId="tpee.1068580123136" id="2239108793198838790" nodeInfo="sn" />
    </node>
  </root>
</model>

