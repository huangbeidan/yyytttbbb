package SimilarityScore;

import com.google.common.base.Preconditions;
import org.simmetrics.metrics.functions.Substitution;

public class MatchMismatchWords implements SubstitutionWords {

    private final float matchValue;
    private final float mismatchValue;

    public MatchMismatchWords(float matchValue, float mismatchValue) {
        Preconditions.checkArgument(matchValue > mismatchValue);
        this.matchValue = matchValue;
        this.mismatchValue = mismatchValue;
    }

    public float compare(String[] a, int aIndex, String[] b, int bIndex) {
        return a[aIndex].equals(b[bIndex]) ? this.matchValue : this.mismatchValue;
    }

    public float max() {
        return this.matchValue;
    }

    public float min() {
        return this.mismatchValue;
    }

    public String toString() {
        return "MatchMismatch [matchCost=" + this.matchValue + ", mismatchCost=" + this.mismatchValue + "]";
    }
}

