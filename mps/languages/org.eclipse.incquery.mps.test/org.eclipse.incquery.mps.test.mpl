<?xml version="1.0" encoding="UTF-8"?>
<language namespace="org.eclipse.incquery.mps.test" uuid="76f64958-d3c3-440b-9b5e-58dc117e6c00">
  <models>
    <modelRoot contentPath="${module}" type="default">
      <sourceRoot location="languageModels" />
    </modelRoot>
  </models>
  <accessoryModels />
  <generators>
    <generator generatorUID="org.eclipse.incquery.mps.test#7224892637325260695" uuid="d645efad-7ba2-43a6-a2a6-4855067488e1">
      <models>
        <modelRoot contentPath="${module}" type="default">
          <sourceRoot location="generator/template" />
        </modelRoot>
      </models>
      <external-templates />
      <usedLanguages>
        <usedLanguage>b401a680-8325-4110-8fd3-84331ff25bef(jetbrains.mps.lang.generator)</usedLanguage>
        <usedLanguage>d7706f63-9be2-479c-a3da-ae92af1e64d5(jetbrains.mps.lang.generator.generationContext)</usedLanguage>
        <usedLanguage>76f64958-d3c3-440b-9b5e-58dc117e6c00(org.eclipse.incquery.mps.test)</usedLanguage>
      </usedLanguages>
      <usedDevKits>
        <usedDevKit>fbc25dd2-5da4-483a-8b19-70928e1b62d7(jetbrains.mps.devkit.general-purpose)</usedDevKit>
      </usedDevKits>
      <mapping-priorities />
    </generator>
  </generators>
  <sourcePath />
  <dependencies>
    <dependency reexport="false">6354ebe7-c22a-4a0f-ac54-50b52ab9b065(JDK)</dependency>
    <dependency reexport="false">f3061a53-9226-4cc5-a443-f952ceaf5816(jetbrains.mps.baseLanguage)</dependency>
    <dependency reexport="false">ef5ea086-f248-4019-bdc4-4a594cfbdd2e(org.eclipse.incquery.mps)</dependency>
    <dependency reexport="false">d78ad636-1087-4a2a-8147-0f6b287011c2(org.eclipse.incquery.mps.runtime)</dependency>
    <dependency reexport="false">f7796ebe-912c-40ce-8a40-bf58c7ee548f(org.eclipse.incquery.mps.test.runtime)</dependency>
  </dependencies>
  <usedLanguages>
    <usedLanguage>ed6d7656-532c-4bc2-81d1-af945aeb8280(jetbrains.mps.baseLanguage.blTypes)</usedLanguage>
    <usedLanguage>9ded098b-ad6a-4657-bfd9-48636cfe8bc3(jetbrains.mps.lang.traceable)</usedLanguage>
    <usedLanguage>ef5ea086-f248-4019-bdc4-4a594cfbdd2e(org.eclipse.incquery.mps)</usedLanguage>
  </usedLanguages>
  <usedDevKits>
    <usedDevKit>2677cb18-f558-4e33-bc38-a5139cee06dc(jetbrains.mps.devkit.language-design)</usedDevKit>
  </usedDevKits>
  <runtime>
    <dependency reexport="false">f7796ebe-912c-40ce-8a40-bf58c7ee548f(org.eclipse.incquery.mps.test.runtime)</dependency>
  </runtime>
  <extendedLanguages />
</language>

