package Utilities;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static Utilities.Math.max;


import SimilarityScore.MatchMismatchWords;
import SimilarityScore.SMITHW;
import SimilarityScore.StringMetricWords;
import SimilarityScore.SubstitutionWords;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.functions.AffineGap;
import org.simmetrics.metrics.functions.Gap;


import org.simmetrics.metrics.functions.Substitution;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public final class SmithWatermanWords implements StringMetricWords {

    private final Gap gap;
    private final SubstitutionWords substitutionWords;
    private final int windowSize;
    float[][] d;
    int[] track;
    String[] wordSet1;
    String[] wordSet2;

    /**
     * Constructs a new Smith Waterman metric. Uses an affine gap of
     * <code>-5.0 - gapLength</code> a <code>-3.0</code> substitution penalty
     * for mismatches, <code>5.0</code> for matches.
     *
     */
    public SmithWatermanWords() {
        this(new AffineGap(-3.0f, -1.0f), new MatchMismatchWords(3.0f, -3.0f),
                Integer.MAX_VALUE);
    }

    /**
     * Constructs a new Smith Waterman metric.
     *
     * @param gap
     *            a gap function to score gaps by
     * @param substitutionWords
     *            a substitution function to score substitutions by
     * @param windowSize
     *            a non-negative window in which
     */
    public SmithWatermanWords(Gap gap, SubstitutionWords substitutionWords, int windowSize) {
        checkNotNull(gap);
        checkNotNull(substitutionWords);
        checkArgument(windowSize >= 0);
        this.gap = gap;
        this.substitutionWords = substitutionWords;
        this.windowSize = windowSize;
    }

    @Override
    public float compare(String[] a, String[] b) {

        if (a.length==0 && b.length==0) {
            return 1.0f;
        }
        if (a.length==0 || b.length==0) {
            return 0.0f;
        }
        float maxDistance = min(a.length, b.length)
                * max(substitutionWords.max(), gap.min());
        return smithWaterman(a, b).getMax() / maxDistance;

        /** the score is measured by smithWaterman score (e.g. 16) / maxDistance (min length * max(penalty or matchvalue) */

    }



    public SMITHW smithWaterman(String[] a, String[] b) {
        final int n = a.length;
        final int m = b.length;

        wordSet1 = a;
        wordSet2 = b;


         d = new float[n][m];

        // Initialize corner
        float max = d[0][0] = max(0, substitutionWords.compare(a, 0, b, 0));

        // Initialize edge
        for (int i = 0; i < n; i++) {

            // Find most optimal deletion
            float maxGapCost = 0;
            for (int k = max(1, i - windowSize); k < i; k++) {
                maxGapCost = max(maxGapCost, d[i - k][0] + gap.value(i - k, i));
            }

            d[i][0] = max(0, maxGapCost, substitutionWords.compare(a, i, b, 0));

            max = max(max, d[i][0]);

        }

        // Initialize edge
        for (int j = 1; j < m; j++) {

            // Find most optimal insertion
            float maxGapCost = 0;
            for (int k = max(1, j - windowSize); k < j; k++) {
                maxGapCost = max(maxGapCost, d[0][j - k] + gap.value(j - k, j));
            }

            d[0][j] = max(0, maxGapCost, substitutionWords.compare(a, 0, b, j));

            max = max(max, d[0][j]);

        }

        // Build matrix
        for (int i = 1; i < n; i++) {

            for (int j = 1; j < m; j++) {

                float maxGapCost = 0;
                // Find most optimal deletion
                for (int k = max(1, i - windowSize); k < i; k++) {
                    maxGapCost = max(maxGapCost,
                            d[i - k][j] + gap.value(i - k, i));
                }
                // Find most optimal insertion
                for (int k = max(1, j - windowSize); k < j; k++) {
                    maxGapCost = max(maxGapCost,
                            d[i][j - k] + gap.value(j - k, j));
                }

                // Find most optimal of insertion, deletion and substitution
                d[i][j] = max(0, maxGapCost,
                        d[i - 1][j - 1] + substitutionWords.compare(a, i, b, j));

                max = max(max, d[i][j]);
            }

        }


//        for (int i = 0; i < d.length; i++) {
//            for (int j = 0; j < d[i].length; j++) {
//                System.out.print(d[i][j] + " ");
//            }
//            System.out.println();
//        }

        return new SMITHW(d,max);
    }

    /** get the optimized route
     *
     * @return
     */

    public LinkedList<int[]> getRoute(float[][]d, float max){

        LinkedList<int[]> route = new LinkedList<>();

        int m  = d.length, n=d[0].length;

        int row = m-1, col = n-1;
        //search for the max



        for(int i=row;i>=0;i--){
            for(int j=col;j>=0;j--){

                searchMax(d,max,i,j);
            }
        }


        System.out.println("row is" + track[0] + "col is: "+track[1]);

       //search neighbor to find the next to go
        searchNeighbor(route,d,track[0],track[1]);

        return route;
    }

    public int[] getAlignment(LinkedList<int[]> route){

        int[] res = new int[route.size()];


        for(int i=0; i<route.size()-1;i++){

            int[] temp = route.get(i);
            int[] temp_next = route.get(i+1);

            if(temp[0] == temp_next[0] || temp[1] == temp_next[1]){
                res[i] = 0;
            }else{
                res[i] = 1;
            }

        }
        res[route.size()-1] = 1;
        return res;

    }

    public void searchMax(float[][] d, float max, int row, int col){



        if(row <0 || col<0) return;

        if(d[row][col] == max){

            track =  new int[]{row,col};
            System.out.println("Row is: "+row + "Col is: "+col + "track is " + track[0]);
            return;
        }

//        searchMax(d,max,row-1,col);
//        searchMax(d,max,row,col-1);
//        searchMax(d,max,row-1,col-1);



    }

    public void searchNeighbor(LinkedList<int[]> map, float[][]d, int row, int col){


        if(row<0 || col<0) return;

        map.addFirst(new int[]{row,col});

        if(row==0 && col==0) return;

        int[] temp = (row>0 && col>0) ? new int[]{row-1,col-1} : (row>0) ? new int[]{row-1,col} : (col>0) ? new int[]{row,col-1} : new int[]{0,0};


        if(col-1>=0 && d[row][col-1]>d[temp[0]][temp[1]]){
            temp = new int[]{row,col-1};
        }

        if(row-1>=0 && d[row-1][col] > d[temp[0]][temp[1]]){
            temp = new int[]{row-1,col};
        }

        searchNeighbor(map,d,temp[0],temp[1]);

    }

    public String getStringReport(LinkedList<int[]> route){


        //String curr = "", comparedAgainst = "";

       // String common = "", insertion = "", deletion = "";
        StringBuilder common = new StringBuilder(),
                insertion = new StringBuilder(),
                deletion = new StringBuilder(),
                change = new StringBuilder();
        for(int i=0; i<route.size()-1; i++) {

            int[] pair = route.get(i);
            int[] pair_next = route.get(i + 1);

            if(route.get(i + 1)[0] != route.get(i)[0] && route.get(i + 1)[1] != route.get(i)[1]){


                if(common.toString().isEmpty()){
                    common.append(wordSet1[route.get(i)[0]]).append(" ");
                }


                while (i<route.size()-1 && route.get(i + 1)[0] != route.get(i)[0] && route.get(i + 1)[1] != route.get(i)[1]) {
//                curr = curr + wordSet1[pair[0]] + " ";
//                comparedAgainst = comparedAgainst + wordSet2[pair[1]] + " ";

                    int[] p = route.get(i);
                    int[] p_next = route.get(i + 1);


                    if (d[p_next[0]][p_next[1]] < d[p[0]][p[1]]) {
                        change.append(wordSet1[p_next[0]]).append(" to ").append(wordSet2[p_next[1]]).append(" / ");

                    } else{
                        //common.append(wordSet1[p[0]]).append(" ");
                        common.append(wordSet1[p_next[0]]).append(" ");
                    }

                    i++;


                }

                  i--;
                //common.append(wordSet1[route.get(i + 1)[0]]).append(" ");



                if(i!=route.size()-2){
                    common.append("/");
                }


            }


                if (pair_next[0] == pair[0] && pair_next[1] != pair[1]) {
//                curr = curr + wordSet1[pair[0]].replaceAll("^[\\s]"," ") + " ";
//                comparedAgainst = comparedAgainst + wordSet2[pair[1]] + " ";
                    insertion.append(wordSet2[pair_next[1]]).append(" ");


                }


                if (pair_next[1] == pair[1] && pair_next[0] != pair[0]) {
//                curr = curr + wordSet1[pair[0]] + " ";
//                comparedAgainst = comparedAgainst + wordSet2[pair[1]].replaceAll("^[\\s]"," ") + " ";
                    deletion.append(wordSet1[pair[0]]).append(" ");


                }

            }



        int[] last = route.get(route.size()-1);

//        if(wordSet1[last[0]].equals(wordSet2[last[1]])){
//
//            common.append(wordSet1[last[0]]).append(" ").append("/");
//        }

        //if the highest score stops at the middle, then do the rest.
        int smithLength1 = last[0], smithLength2 = last[1];
        int diffLength1 = wordSet1.length - 1 - smithLength1, diffLength2 = wordSet2.length - 1 - smithLength2;


        if(diffLength1 > 0){
            for (int i=smithLength1+1; i<wordSet1.length; i++){
                change.append(wordSet1[i]).append(" ");
            }

            change.append("to ");
        }

        if(diffLength2 > 0){
            for(int i=smithLength2+1; i<wordSet2.length; i++){

                change.append(wordSet2[i]).append(" ");
            }

        }



        return "common words: "+common.toString() + "\n"
                + "insertion: "+insertion.toString() + "\n"
                + "deletion: " + deletion.toString() + "\n"
                + "change: " + change.toString();

    }






    public void getRoutehelper (LinkedList<int[]> map, float[][]d, float max, int i, int j){


        int m = d.length, n=d[0].length;

        if(i>=m || j>=n || i<0 || j<0) return;

        map.add(new int[]{i,j});

        if(d[i][j]==max)return;

        if(i<m-1 && j<n-1 && d[i+1][j+1]>=d[i][j]){
            getRoutehelper(map,d,max,i+1,j+1);

        }else
        {
            if(i<m-1 && d[i+1][j] !=0){
                getRoutehelper(map,d,max,i+1,j);
                map.remove(map.size()-1);
            }
            if( j<n-1 && d[i][j+1] !=0)
               { getRoutehelper(map,d,max,i,j+1);
               map.remove(map.size()-1);
               }
           }

        }



    @Override
    public String toString() {
        return "SmithWatermanWords [gap=" + gap + ", substitution=" + substitutionWords
                + ", windowSize=" + windowSize + "]";
    }

    public static void main(String[] args){
        SmithWatermanWords smithWatermanWords = new SmithWatermanWords();
        String one = "The circumstance referred to in subparagraph A is that the person was killed";
        String two = "The shootings that killed at least 49 people at two mosques in New Zealand on Friday have placed new scrutiny on New Zealand’s gun laws and sparked a serious debate about whether they were a factor in the gunman’s decision to carry out his attack there";
        String three = "Gun Laws In New Zealand Draw Scrutiny After Mosque Shootings yes";
        String four = "The shootings that murdured at least 49 people at two sites in New Zealand on Friday have placed new scrutiny on New Zealand’s gun laws and sparked a fervent debate about whether they were a factor in the gunman’s decision to carry out his attack there";
        String five = "the circumstance referred to in subparagraph A is that the person was killed or targeted";
        String[] a = one.split(" ");
        String[] b = five.split(" ");
//        System.out.println(smithWatermanWords.compare(a,b));
//        System.out.println(smithWatermanWords.smithWaterman(a,b));
//        System.out.println(smithWatermanWords.toString());
        SMITHW smithw = smithWatermanWords.smithWaterman(a,b);
        float[][] d = smithw.getMatrix();
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[i].length; j++) {
                System.out.print(d[i][j] + " ");
            }
            System.out.println();
        }
        float max = (smithw.getMax());
                System.out.println("max is: "+max);

        LinkedList<int[]> route = smithWatermanWords.getRoute(d,max);
//        for(int[] pair: route){
//            System.out.println(pair[0] + "  " + pair[1]);
//        }

        int[] res = smithWatermanWords.getAlignment(route);
//        for (int r : res){
//            System.out.println(r);
//        }
        System.out.println("the length of res is: "+res.length);

        String report = smithWatermanWords.getStringReport(route);
        System.out.println(report);
    }

}