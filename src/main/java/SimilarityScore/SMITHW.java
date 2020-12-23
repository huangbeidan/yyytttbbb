package SimilarityScore;

public class SMITHW {

    float[][] d;
    float max;

    public SMITHW(float[][] d, float max) {
        this.d = d;
        this.max = max;
    }

    public float[][] getMatrix(){
        return this.d;
    }

    public float getMax(){
        return this.max;
    }
}
