<?xml version="1.0" encoding="UTF-8"?>
<model modelUID="r:37273d5e-6a69-4e4b-9ab4-c9f2214cfd7a(org.eclipse.incquery.mps.test.runtime.school)">
  <persistence version="8" />
  <language namespace="76f64958-d3c3-440b-9b5e-58dc117e6c00(org.eclipse.incquery.mps.test)" />
  <language namespace="ef5ea086-f248-4019-bdc4-4a594cfbdd2e(org.eclipse.incquery.mps)" />
  <import index="ipj7" modelUID="r:72b2626f-5a0a-40af-a2b4-fbc2ae1b60c1(org.eclipse.incquery.mps.test.structure)" version="0" />
  <import index="tpck" modelUID="r:00000000-0000-4000-0000-011c89590288(jetbrains.mps.lang.core.structure)" version="0" implicit="yes" />
  <root type="ipj7.School" typeId="ipj7.7224892637325261564" id="7224892637325518001" nodeInfo="ng">
    <property name="name" nameId="tpck.1169194664001" value="Budapest University of Technology and Economics" />
    <property name="address" nameId="ipj7.7224892637325261571" value="Magyar Tudosok Korutja" />
    <node role="courses" roleId="ipj7.7224892637325263848" type="ipj7.Course" typeId="ipj7.7224892637325261573" id="7224892637325518003" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Formal methods" />
      <property name="weight" nameId="ipj7.7224892637325261627" value="10" />
      <link role="school" roleId="ipj7.7224892637325261631" targetNodeId="7224892637325518001" resolveInfo="Budapest University of Technology and Economics" />
      <link role="teacher" roleId="ipj7.7224892637325261633" targetNodeId="7224892637325561298" resolveInfo="Majzik Istvan" />
    </node>
    <node role="courses" roleId="ipj7.7224892637325263848" type="ipj7.Course" typeId="ipj7.7224892637325261573" id="7224892637325561267" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="MDSD" />
      <property name="weight" nameId="ipj7.7224892637325261627" value="20" />
      <link role="school" roleId="ipj7.7224892637325261631" targetNodeId="7224892637325518001" resolveInfo="Budapest University of Technology and Economics" />
      <link role="teacher" roleId="ipj7.7224892637325261633" targetNodeId="7224892637325561298" resolveInfo="Majzik Istvan" />
    </node>
    <node role="courses" roleId="ipj7.7224892637325263848" type="ipj7.Course" typeId="ipj7.7224892637325261573" id="7224892637325561272" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Graph transformations" />
      <property name="weight" nameId="ipj7.7224892637325261627" value="30" />
      <link role="school" roleId="ipj7.7224892637325261631" targetNodeId="7224892637325518001" resolveInfo="Budapest University of Technology and Economics" />
      <link role="teacher" roleId="ipj7.7224892637325261633" targetNodeId="7224892637325561309" resolveInfo="Varro Daniel" />
    </node>
    <node role="students" roleId="ipj7.7224892637325263851" type="ipj7.Student" typeId="ipj7.7224892637325261508" id="7224892637325561283" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Szabo Tamas" />
      <node role="directFriends" roleId="ipj7.7224892637325261610" type="ipj7.StudentReference" typeId="ipj7.7224892637325261607" id="2139054008193209754" nodeInfo="ng">
        <link role="student" roleId="ipj7.7224892637325261608" targetNodeId="7224892637325561286" resolveInfo="Jambor Attila" />
      </node>
    </node>
    <node role="students" roleId="ipj7.7224892637325263851" type="ipj7.Student" typeId="ipj7.7224892637325261508" id="7224892637325561286" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Jambor Attila" />
      <node role="directFriends" roleId="ipj7.7224892637325261610" type="ipj7.StudentReference" typeId="ipj7.7224892637325261607" id="7224892637325561339" nodeInfo="ng">
        <link role="student" roleId="ipj7.7224892637325261608" targetNodeId="7224892637325561291" resolveInfo="Karai Tamas" />
      </node>
    </node>
    <node role="students" roleId="ipj7.7224892637325263851" type="ipj7.Student" typeId="ipj7.7224892637325261508" id="7224892637325561291" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Karai Tamas" />
    </node>
    <node role="teachers" roleId="ipj7.7224892637325263846" type="ipj7.Teacher" typeId="ipj7.7224892637325261574" id="7224892637325561298" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Majzik Istvan" />
      <link role="school" roleId="ipj7.7224892637325261592" targetNodeId="7224892637325518001" resolveInfo="Budapest University of Technology and Economics" />
      <node role="courses" roleId="ipj7.7224892637325261590" type="ipj7.CourseReference" typeId="ipj7.7224892637325261587" id="1981263388925648143" nodeInfo="ng">
        <link role="course" roleId="ipj7.7224892637325261588" targetNodeId="7224892637325518003" resolveInfo="Formal methods" />
      </node>
    </node>
    <node role="teachers" roleId="ipj7.7224892637325263846" type="ipj7.Teacher" typeId="ipj7.7224892637325261574" id="7224892637325561309" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Varro Daniel" />
      <link role="school" roleId="ipj7.7224892637325261592" targetNodeId="7224892637325518001" resolveInfo="Budapest University of Technology and Economics" />
      <node role="courses" roleId="ipj7.7224892637325261590" type="ipj7.CourseReference" typeId="ipj7.7224892637325261587" id="1981263388925648160" nodeInfo="ng">
        <link role="course" roleId="ipj7.7224892637325261588" targetNodeId="7224892637325561272" resolveInfo="Graph transformations" />
      </node>
    </node>
    <node role="teachers" roleId="ipj7.7224892637325263846" type="ipj7.Teacher" typeId="ipj7.7224892637325261574" id="7224892637325561314" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Szeredi Peter" />
      <link role="school" roleId="ipj7.7224892637325261592" targetNodeId="7224892637325518001" resolveInfo="Budapest University of Technology and Economics" />
      <node role="courses" roleId="ipj7.7224892637325261590" type="ipj7.CourseReference" typeId="ipj7.7224892637325261587" id="1981263388925648163" nodeInfo="ng">
        <link role="course" roleId="ipj7.7224892637325261588" targetNodeId="7224892637325561267" resolveInfo="MDSD" />
      </node>
    </node>
    <node role="teachers" roleId="ipj7.7224892637325263846" type="ipj7.Teacher" typeId="ipj7.7224892637325261574" id="8779509957323902598" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Rath Istvan" />
      <link role="school" roleId="ipj7.7224892637325261592" targetNodeId="7224892637325518001" resolveInfo="Budapest University of Technology and Economics" />
    </node>
  </root>
  <root type="ipj7.School" typeId="ipj7.7224892637325261564" id="1981263388925648324" nodeInfo="ng">
    <property name="name" nameId="tpck.1169194664001" value="Eotvos Lorand Tudomanyegyetem" />
  </root>
</model>

