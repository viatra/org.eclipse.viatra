package org.eclipse.viatra.dse.objectives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LeveledObjectivesHelper {

    private List<IObjective> objectives = new ArrayList<IObjective>();
    private IObjective[][] leveledObjectives;

    public LeveledObjectivesHelper(List<IObjective> objectives) {
        this.objectives = objectives;
    }

    public IObjective[][] initLeveledObjectives() {
        if (objectives.size() == 0) {
            leveledObjectives = new IObjective[0][0];
            return leveledObjectives;
        }

        int level = objectives.get(0).getLevel();
        boolean oneLevelOnly = true;
        for (IObjective objective : objectives) {
            if (objective.getLevel() != level) {
                oneLevelOnly = false;
                break;
            }
        }

        if (oneLevelOnly) {
            leveledObjectives = new IObjective[1][objectives.size()];
            for (int i = 0; i < objectives.size(); i++) {
                leveledObjectives[0][i] = objectives.get(i);
            }
            return leveledObjectives;
        }

        IObjective[] objectivesArray = getSortedByLevelObjectives(objectives);

        int numberOfLevels = getNumberOfObjectiveLevels(objectivesArray);

        leveledObjectives = new IObjective[numberOfLevels][];

        fillLeveledObjectives(objectivesArray);

        return leveledObjectives;
    }

    private void fillLeveledObjectives(IObjective[] objectivesArray) {
        int actLevel = objectivesArray[0].getLevel();
        int levelIndex = 0;
        int lastIndex = 0;
        int corrigationForLastLevel = 0;
        boolean oneObjectiveAtLastLevel = false;
        for (int i = 0; i < objectivesArray.length; i++) {
            if (i == objectivesArray.length - 1) {
                corrigationForLastLevel = 1;
                if (objectivesArray[i - 1].getLevel() != objectivesArray[i].getLevel()) {
                    oneObjectiveAtLastLevel = true;
                    corrigationForLastLevel = 0;
                }
            }
            if (objectivesArray[i].getLevel() != actLevel || corrigationForLastLevel == 1 || oneObjectiveAtLastLevel) {
                leveledObjectives[levelIndex] = new IObjective[i - lastIndex + corrigationForLastLevel];
                for (int j = lastIndex; j < i + corrigationForLastLevel; j++) {
                    leveledObjectives[levelIndex][j - lastIndex] = objectivesArray[j];
                }
                if (oneObjectiveAtLastLevel) {
                    leveledObjectives[levelIndex + 1] = new IObjective[1];
                    leveledObjectives[levelIndex + 1][0] = objectivesArray[i];
                }
                actLevel = objectivesArray[i].getLevel();
                levelIndex++;
                lastIndex = i;
            }
        }
    }

    private int getNumberOfObjectiveLevels(IObjective[] objectivesArray) {

        int actLevel = objectivesArray[0].getLevel();
        int numberOfLevels = 1;

        for (int i = 1; i < objectivesArray.length; i++) {
            if (objectivesArray[i].getLevel() != actLevel) {
                numberOfLevels++;
                actLevel = objectivesArray[i].getLevel();
            }
        }

        return numberOfLevels;
    }

    private IObjective[] getSortedByLevelObjectives(List<IObjective> objectives) {
        IObjective[] objectivesArray = objectives.toArray(new IObjective[objectives.size()]);
        Arrays.sort(objectivesArray, new Comparator<IObjective>() {
            @Override
            public int compare(IObjective o1, IObjective o2) {
                return Integer.valueOf(o1.getLevel()).compareTo(o2.getLevel());
            }
        });
        return objectivesArray;
    }

    public IObjective[][] getLeveledObjectives() {
        return leveledObjectives;
    }

}
