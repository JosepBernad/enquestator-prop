package answer;

import analysis.DistanceCalculator;

import java.util.List;

public class NumericAnswer extends Answer {

    private Double value;

    public static NumericAnswer calculateCentroid(List<NumericAnswer> answers) {
        Double sum = 0D;
        for (NumericAnswer answer : answers)
            sum += answer.getValue();

        NumericAnswer numericAnswer = new NumericAnswer();
        numericAnswer.setValue(sum / answers.size());
        return numericAnswer;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public Double calculateDistance(Answer answer) {
        return DistanceCalculator.calculateDistance(this, (NumericAnswer) answer);
    }

}
