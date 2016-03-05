package org.eclipse.viatra.dse.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.viatra.dse.objectives.IObjective;
import org.eclipse.viatra.dse.objectives.LeveledObjectivesHelper;
import org.eclipse.viatra.dse.objectives.impl.ModelQueriesHardObjective;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class LeveledObjectivesHelperTest {

    @Parameters(name = "Test {index} - {0}, expectedLevels:{2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { 
                { "EmptyList", new ArrayList<IObjective>(), 0 },
                { "OneObjective", Arrays.asList(
                        new ModelQueriesHardObjective().withLevel(0)), 1 },
                { "ThreeAtSameLevel", Arrays.asList(
                        new ModelQueriesHardObjective().withLevel(0),
                        new ModelQueriesHardObjective().withLevel(0),
                        new ModelQueriesHardObjective().withLevel(0)), 1 },
                { "TwoLevels", Arrays.asList(
                        new ModelQueriesHardObjective().withLevel(0),
                        new ModelQueriesHardObjective().withLevel(1)), 2 },
                { "TwoLevelsReverseOrder", Arrays.asList(
                        new ModelQueriesHardObjective().withLevel(1),
                        new ModelQueriesHardObjective().withLevel(0)), 2 },
                { "TwoLevelsDoubleLast", Arrays.asList(
                        new ModelQueriesHardObjective().withLevel(0),
                        new ModelQueriesHardObjective().withLevel(1),
                        new ModelQueriesHardObjective().withLevel(1)), 2 },
                { "TwoLevelsDoubleFirst", Arrays.asList(
                        new ModelQueriesHardObjective().withLevel(0),
                        new ModelQueriesHardObjective().withLevel(0),
                        new ModelQueriesHardObjective().withLevel(1)), 2 },
                { "TwoLevelsDifferentOrder", Arrays.asList(
                        new ModelQueriesHardObjective().withLevel(1),
                        new ModelQueriesHardObjective().withLevel(0),
                        new ModelQueriesHardObjective().withLevel(1)), 2 },
                { "OneInEachLevel", Arrays.asList(
                        new ModelQueriesHardObjective().withLevel(1),
                        new ModelQueriesHardObjective().withLevel(3),
                        new ModelQueriesHardObjective().withLevel(2),
                        new ModelQueriesHardObjective().withLevel(4)), 4 },
                { "TwoInEachLEvel", Arrays.asList(
                        new ModelQueriesHardObjective().withLevel(3),
                        new ModelQueriesHardObjective().withLevel(2),
                        new ModelQueriesHardObjective().withLevel(3),
                        new ModelQueriesHardObjective().withLevel(1),
                        new ModelQueriesHardObjective().withLevel(2),
                        new ModelQueriesHardObjective().withLevel(0),
                        new ModelQueriesHardObjective().withLevel(0),
                        new ModelQueriesHardObjective().withLevel(1)), 4 },
                { "WierdNumbers", Arrays.asList(
                        new ModelQueriesHardObjective().withLevel(42),
                        new ModelQueriesHardObjective().withLevel(-3),
                        new ModelQueriesHardObjective().withLevel(-3),
                        new ModelQueriesHardObjective().withLevel(42),
                        new ModelQueriesHardObjective().withLevel(22),
                        new ModelQueriesHardObjective().withLevel(-22),
                        new ModelQueriesHardObjective().withLevel(1)), 5 },
           });
    }

    @Parameter(value = 0)
    public String testName;

    @Parameter(value = 1)
    public List<IObjective> objectives;

    @Parameter(value = 2)
    public int expectedNumberOfLevels;

    @Test
    public void test() {

        LeveledObjectivesHelper helper = new LeveledObjectivesHelper(objectives);
        IObjective[][] leveledObjectives = helper.initLeveledObjectives();

        Assert.assertEquals(expectedNumberOfLevels, leveledObjectives.length);

        Integer prevLevel = null;
        for (IObjective[] iObjectives : leveledObjectives) {
            Integer actualLevel = null;
            for (IObjective objective : iObjectives) {
                Assert.assertNotNull(objective);
                int objectiveLevel = objective.getLevel();
                if (actualLevel == null) {
                    actualLevel = objectiveLevel;
                } else {
                    Assert.assertEquals(actualLevel.intValue(), objectiveLevel);
                }
            }
            if (prevLevel != null) {
                Assert.assertTrue(prevLevel < actualLevel);
            }
            prevLevel = actualLevel;
        }
    }

}
