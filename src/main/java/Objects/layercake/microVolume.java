package Objects.layercake;

import ij.*;
import java.awt.Color;
import java.util.ArrayList;
//

//new class for defining a cell object for microFLOW analysis
public class microVolume extends Object implements Cloneable, java.io.Serializable {

    public static final int INCLUDED = 1;
    public static final int EXCLUDED = 2;

    public static final int X_VALUES = 1;
    public static final int Y_VALUES = 2;
    
    public static final int MASK = 0;
    public static final int GROW = 1;  //use subtype to determine how much
    public static final int FILL = 2;

    private int n;    //total pixels

    private String name;

    private int nChannels;

    private int nRegions = 0;		//number of member regions
    private int nDerivedRegions = 0;	//number of member derived regions by array location
    private int sizeRegions = 0;

//regions for the mask volume
    private microRegion[] Regions;

//derived regions for all dependent channels,  
    private microDerivedRegion[][] DerivedRegions;
//[channel{0-3}][regions]

//private ImageStack[] Stacks;
//calculated variables
    private float mean = 0;
    private float integrated_density = 0;
    private double min = 0;
    private double max = 0;
    private float stdev = 0;
    private double FeretMaxCaliperMax = 0;
    private double FeretMinCaliperMax = 0;
    private double FeretAspectRatio = 0;

    private Object[][] derivedConstants;
    private Object[][] maskConstants;

    private Object[][] analysisResultsVolume = new Object[4][11];
    private Object[] analysisMaskVolume = new Object[11];

    static public String[] Analytics = {"#pixels", "mean", "sum", "min", "max", "SD", "AR", "F_min", "F_max", "mean_th", "mean_sq"};
    private Color[][] Colorized = new Color[4][9];
    private ArrayList ResultsPointer;

    microVolume Original; 
    

   //   Volume measurements; 297, 1663.5555, 558321, 0.0, 4095.0
//[channel][0, count, 1, mean, 2, integrated density, 3, min, 4, max, 5 standard deviation, 6 Feret_AR, 7 Feret_Min, 8 Feret_Max]
//derived regions for regions, 2D array, [Regions][Derived Regions],  or each region may have different derived regions.
//use GROW_1 and GROW_2 for intial lookup of Derived Regions-add analyses as required.  nDerived is the counter
    /**
     * Default constructor
     */
    public microVolume() {
    }

    public microVolume(int n, int count) {

        makeRegions(n, count);

    }

    /**
     * Methods
     */
//make regions 
    public void makeRegions(int n, int count) {
        microRegion[] localRegions = new microRegion[n];
        this.Regions = localRegions;
        this.sizeRegions = n;

        this.name = "v_" + count;
        //IJ.log("microVolume::<init>                      microRegions instantiated...  ");
        //calculateVolumeMeasurements();
    }

    public void makeDerivedRegions(int[][] derivedRegionType, int channels, ImageStack[] Stacks, ArrayList ResultsPointers) {

        this.nChannels = channels;
        DerivedRegions = new microDerivedRegion[nChannels][nRegions];
        //IJ.log("                                                   ");
        //IJ.log("microVolume...          making derived regions for volume from, " + nChannels + " channels and " + nRegions + " Regions");

        this.ResultsPointer = ResultsPointers;
         
         //IJ.log("microVolume Volume measurements, primary object: " + analysisMaskVolume[0] + ", " + analysisMaskVolume[1] + ", " + analysisMaskVolume[2] + ", " + analysisMaskVolume[3] + ", " + analysisMaskVolume[4]);
         
        //System.out.println("Results Pointer, microVolume: " + ResultsPointer);
        //System.out.println("Number of channels: " + nChannels);
         
         
        for (int i = 0; i < nChannels; i++) {
            IJ.log("microVolume...          Deriving channel," + (i + 1));
            switch (derivedRegionType[i][0]) {
                case microVolume.GROW:
                    IJ.log("microVolume...          calculating Grow.");

                    calculateGrow(i, derivedRegionType[i][1], Stacks[i]);
                   // IJ.log("microVolume...          calculating Derived measurements.");
                    calculateDerivedVolumeMeasurements(i);
                    break;
                case microVolume.MASK:
                    IJ.log("microVolume...          calculating Mask.");
                    calculateMask(i, Stacks[i]);
                    calculateDerivedVolumeMeasurements(i);

                    break;
                case microVolume.FILL:
                    calculateFill();
                    break;
                default:

                    break;
            }
        }

    }

//calculate volume statistics from mask volume and derived volumes
    public void calculateDerivedVolumeMeasurements(int Channel) {

        int localnDerivedRegions = this.nRegions;
        microDerivedRegion[][] localDerivedRegions = this.DerivedRegions;

        int countPixelsD = 0;
        int countThresholdD = 0;
        long totalD = 0;
        long totalThresholdD = 0;
        double localminD = 0;
        double localmaxD = 0;
        long localmeanD = 0;
        long localmeanThresholdD = 0;
        double standardDeviation = 0;
        double meanFeretAR = 0;
        double meanFeretMaxCaliperLocal = 0;
        double meanFeretMinCaliperLocal = 0;
        double[] FeretValues = new double[5];  //0, maximum caliper width; 1 , FeretAngle; 2, minimum caliper width; 3, FeretX; 4, FeretY. 
        //IJ.log ("microVolume::calculateDerivedMeasurements                Regions to analyze: " + n);
        //for(int c = 0; c <= nChannels-1; c++){
        for (int i = 0; i <= localnDerivedRegions - 1; i++) {
            double[] deviations = localDerivedRegions[Channel][i].getDeviations();
            FeretValues = localDerivedRegions[Channel][i].getFeretValues();
            meanFeretAR = meanFeretAR + (FeretValues[0]) / (FeretValues[2]);
            meanFeretMaxCaliperLocal = meanFeretMaxCaliperLocal + FeretValues[0];
            meanFeretMinCaliperLocal = meanFeretMinCaliperLocal + FeretValues[2];
            countPixelsD = countPixelsD + localDerivedRegions[Channel][i].getPixelCount();
            countThresholdD = countThresholdD + localDerivedRegions[Channel][i].getThresholdPixelCount();
            totalD = totalD + (long) localDerivedRegions[Channel][i].getIntegratedIntensity();
            totalThresholdD = totalThresholdD + (long) localDerivedRegions[Channel][i].getThresholdedIntegratedIntensity();       
            localmeanD = localmeanD + (long) localDerivedRegions[Channel][i].getMeanIntensity();

            if (localDerivedRegions[Channel][i].getMinIntensity() < localminD) {
                localminD = localDerivedRegions[Channel][i].getMinIntensity();
            }
            if (localDerivedRegions[Channel][i].getMaxIntensity() > localmaxD) {
                //IJ.log("localmaxD:  " + localmaxD + "localDerivedRegions max:  " + localDerivedRegions[Channel][i].getMaxIntensity());
                localmaxD = localDerivedRegions[Channel][i].getMaxIntensity();
            }
            for(int j = 0; j <= localDerivedRegions[Channel][i].getPixelCount()-1; j++){
                standardDeviation = standardDeviation + Math.pow((deviations[j]-(totalD/countPixelsD)),2);
            }
        }
        analysisResultsVolume[Channel][0] = countPixelsD;
        analysisResultsVolume[Channel][1] = totalD/countPixelsD;
        analysisResultsVolume[Channel][2] = totalD;
        analysisResultsVolume[Channel][3] = localminD;
        analysisResultsVolume[Channel][4] = localmaxD;
        analysisResultsVolume[Channel][5] = Math.sqrt(standardDeviation/countPixelsD);
        analysisResultsVolume[Channel][8] = meanFeretMaxCaliperLocal / (localnDerivedRegions);
        analysisResultsVolume[Channel][7] = meanFeretMinCaliperLocal / (localnDerivedRegions);
        analysisResultsVolume[Channel][6] = (meanFeretMaxCaliperLocal / (localnDerivedRegions)) / (meanFeretMinCaliperLocal / (localnDerivedRegions));
        //analysisResultsVolume[Channel][9] = (totalD/countPixelsD)*(totalD/countPixelsD);
        analysisResultsVolume[Channel][9] = totalThresholdD/countThresholdD;
        analysisResultsVolume[Channel][10] = (totalD/countPixelsD)*(totalD/countPixelsD);
        //[channel][0, count, 1, mean, 2, integrated density, 3, min, 4, max, 5 standard deviation, 6 Feret_AR, 7 Feret_Min, 8 Feret_Max]
        IJ.log("microVolume Derived Volume measurements, channel " + Channel + ", " + analysisResultsVolume[Channel][9] + ", " + analysisResultsVolume[Channel][10]);

    }
  
    public void calculateVolumeMeasurements(int Channel) {

        int nRegionsLocal = this.nRegions;
        microRegion[] RegionsLocal = this.Regions;

        int countPixels = 0;
        
        long total = 0;
        long totalThreshold = 0;
        int nThreshold = 0;
        
        double minLocal = 0;//why doubles?
        double maxLocal = 0;//why doubles?
        double standardDeviation = 0;
        double[] FeretValues = new double[5];
        double meanFeretAR = 0;
        double meanFeretMaxCaliperLocal = 0;
        double meanFeretMinCaliperLocal = 0;

        //IJ.log ("microVolume::calculateMeasurements                Regions to analyze: " + nRegions);
        
        //Standard deviation is the square root of the 
        for (int i = 0; i <= nRegionsLocal - 1; i++) {

            FeretValues = RegionsLocal[i].getFeretValues();
            
            double[] deviations = RegionsLocal[i].getDeviations();

            meanFeretAR = meanFeretAR + (FeretValues[0]) / (FeretValues[2]);
            meanFeretMaxCaliperLocal = meanFeretMaxCaliperLocal + FeretValues[0];
            meanFeretMinCaliperLocal = meanFeretMinCaliperLocal + FeretValues[2];
            countPixels = countPixels + RegionsLocal[i].getPixelCount();
            total = total + (long) RegionsLocal[i].getIntegratedIntensity();
            mean = mean + (long) RegionsLocal[i].getMeanIntensity();
            totalThreshold = totalThreshold + (long) RegionsLocal[i].getThresholdedIntegratedIntensity();
            nThreshold = nThreshold + RegionsLocal[i].getThresholdPixelCount();
            
            if (RegionsLocal[i].getMinIntensity() < minLocal) {
                minLocal = RegionsLocal[i].getMinIntensity();
            }
            if (RegionsLocal[i].getMaxIntensity() > maxLocal) {
                maxLocal = RegionsLocal[i].getMaxIntensity();
            }
            for(int j = 0; j <= RegionsLocal[i].getPixelCount()-1; j++){
                standardDeviation = standardDeviation + Math.pow(deviations[j]-(total/countPixels), 2);
            }
                
        }
        this.n = countPixels;

        
        analysisResultsVolume[Channel][0] = countPixels;
        analysisResultsVolume[Channel][1] = total/countPixels; //changed from averaging regions to all pixels
        analysisResultsVolume[Channel][2] = total;
        analysisResultsVolume[Channel][3] = minLocal;
        analysisResultsVolume[Channel][4] = maxLocal;
        analysisResultsVolume[Channel][5] = Math.sqrt(standardDeviation/countPixels);
        analysisResultsVolume[Channel][8] = meanFeretMaxCaliperLocal / (nRegionsLocal);
        analysisResultsVolume[Channel][7] = meanFeretMinCaliperLocal / (nRegionsLocal);
        //analysisResultsVolume[Channel][9] = (total/countPixels)*(total/countPixels);
        analysisResultsVolume[Channel][9] = totalThreshold/nThreshold;
        analysisResultsVolume[Channel][10] = (total/countPixels)*(total/countPixels);

        if (meanFeretMinCaliperLocal / (nRegionsLocal) != 0) {
            analysisMaskVolume[6] = (meanFeretMaxCaliperLocal / (nRegionsLocal)) / (meanFeretMinCaliperLocal / (nRegionsLocal));
        }

        IJ.log("microVolume Original Volume measurements: " + analysisMaskVolume[9] + ", " + analysisMaskVolume[10]);

    }
    
    public void calculateVolumeMeasurements() {

        int nRegionsLocal = this.nRegions;
        microRegion[] RegionsLocal = this.Regions;
        
        

        int countPixels = 0;
        int countPixelsThreshold = 0;

        long total = 0;
        long totalThreshold = 0;
        double minLocal = 0;//why doubles?
        double maxLocal = 0;//why doubles?
        double standardDeviation = 0;
        double[] FeretValues = new double[5];
        double meanFeretAR = 0;
        double meanFeretMaxCaliperLocal = 0;
        double meanFeretMinCaliperLocal = 0;

        //IJ.log ("microVolume::calculateMeasurements                Regions to analyze: " + nRegions);
        
        //Standard deviation is the square root of the 
        for (int i = 0; i <= nRegionsLocal - 1; i++) {

            FeretValues = RegionsLocal[i].getFeretValues();
            
            double[] deviations = RegionsLocal[i].getDeviations();

            meanFeretAR = meanFeretAR + (FeretValues[0]) / (FeretValues[2]);
            meanFeretMaxCaliperLocal = meanFeretMaxCaliperLocal + FeretValues[0];
            meanFeretMinCaliperLocal = meanFeretMinCaliperLocal + FeretValues[2];
            countPixels = countPixels + RegionsLocal[i].getPixelCount();
            total = total + (long) RegionsLocal[i].getIntegratedIntensity();
            totalThreshold = totalThreshold + (long) RegionsLocal[i].getThresholdedIntegratedIntensity();
            countPixelsThreshold = countPixelsThreshold + RegionsLocal[i].getThresholdPixelCount();
            mean = mean + (long) RegionsLocal[i].getMeanIntensity();
            
            if (RegionsLocal[i].getMinIntensity() < minLocal) {
                minLocal = RegionsLocal[i].getMinIntensity();
            }
            if (RegionsLocal[i].getMaxIntensity() > maxLocal) {
                maxLocal = RegionsLocal[i].getMaxIntensity();
            }
            for(int j = 0; j <= RegionsLocal[i].getPixelCount()-1; j++){
                standardDeviation = standardDeviation + Math.pow(deviations[j]-(total/countPixels), 2);
            }
                
        }
        this.n = countPixels;

        
        analysisMaskVolume[0] = countPixels;
        analysisMaskVolume[1] = total/countPixels; //changed from averaging regions to all pixels
        analysisMaskVolume[2] = total;
        analysisMaskVolume[3] = minLocal; 
        analysisMaskVolume[4] = maxLocal;
        analysisMaskVolume[5] = Math.sqrt(standardDeviation/countPixels);
        analysisMaskVolume[8] = meanFeretMaxCaliperLocal / (nRegionsLocal);
        analysisMaskVolume[7] = meanFeretMinCaliperLocal / (nRegionsLocal);
        analysisMaskVolume[9] = totalThreshold/countPixelsThreshold;
        analysisMaskVolume[10] = Math.pow(totalThreshold/countPixelsThreshold,2);

        if (meanFeretMinCaliperLocal / (nRegionsLocal) != 0) {
            analysisMaskVolume[6] = (meanFeretMaxCaliperLocal / (nRegionsLocal)) / (meanFeretMinCaliperLocal / (nRegionsLocal));
        }

        //("microVolume Original Volume measurements: " + analysisMaskVolume[0] + ", " + analysisMaskVolume[1] + ", " + analysisMaskVolume[2] + ", " + analysisMaskVolume[3] + ", " + analysisMaskVolume[4]);

    }

//redefine derived region --> may upgrade to multiple derived states, depends upon utility
//public void rederiveRegion(int n, int type){DerivedRegions[n] = new derivedRegion(Regions[n], type);}
//private methods for making derived regions
    private void calculateGrow(int Channel, int amountGrow, ImageStack is) {

        int localn = this.nRegions;
        microRegion[] localRegions = this.Regions;
        //microDerivedRegion[][] localDerivedRegions = new microDerivedRegion[nChannels][localn];
        
        

        for (int i = 0; i <= localn - 1; i++) {
            //(int[] x, int[] y, int n, int z, int type, int subtype, String name)
            DerivedRegions[Channel][i] = new microDerivedRegion(localRegions[i].getPixelsX(), localRegions[i].getPixelsY(), localRegions[i].getPixelCount(), localRegions[i].getZPosition(), microVolume.GROW, amountGrow, localRegions[i].getName());
            DerivedRegions[Channel][i].calculateMeasurements(is);
            //DerivedRegions localDerivedRegions[c][i];
        }
    }

    private void calculateFill() {
    }

    ;
    

    
    
    
private void calculateMask(int Channel, ImageStack is) {
    
        int localn = this.nRegions;
        microRegion[] localRegions = this.Regions;
    
for (int i = 0; i <= localn - 1; i++) {
            //(int[] x, int[] y, int n, int z, int type, int subtype, String name)
            DerivedRegions[Channel][i] = new microDerivedRegion(localRegions[i].getPixelsX(), localRegions[i].getPixelsY(), localRegions[i].getPixelCount(), localRegions[i].getZPosition(), microVolume.MASK, 0, localRegions[i].getName());
            
             //public microDerivedRegion(int[] x, int[] y, int n, int z, int type, int subtype, String name)
            DerivedRegions[Channel][i].calculateMeasurements(is);
            //DerivedRegions localDerivedRegions[c][i];
        }

};



//region manipulation
public void addRegion(int[] x, int[] y, int n, int z) {
        this.Regions[this.nRegions + 1] = new microRegion(x, y, n, z);
        this.nRegions++;

    }

    public void addRegion(microRegion Region) {
        this.Regions[this.nRegions] = Region;
        this.nRegions++;
    }

    public void setRegion(microRegion Region, int nRegions) {
        this.Regions[nRegions] = Region;
    }

//data set usage
    public void excludeRegion(int n) {
        this.Regions[n].setExclude();
    }

    public void includeRegion(int n) {
        this.Regions[n].setInclude();
    }

    public boolean isRegionIncluded(int n) {
        if (Regions[n].isRegionIncluded() == EXCLUDED) return false;
        return true;
    }

    public microRegion getRegion(int n) {return this.Regions[n];}

    public ArrayList getRegions() {
        
        ArrayList<microRegion> mral = new ArrayList<microRegion>();
        //System.out.println("Getting regions for overlay: " + nRegions + " regions to get.");
        for (int i = 0; i <= nRegions - 1; i++) {
            mral.add(Regions[i]);
            //System.out.println("Getting region: " + i);
        }
        return mral;
    }

//Analysis functions
    public int getNRegions() {
        return this.nRegions;
    }

    public float getMean() {
        return this.mean;
    }

    public float getIntDen() {
        return this.integrated_density;
    }

    public double getFeretMax() {
        return this.FeretMaxCaliperMax;
    }

    public double getFeretMin() {
        return this.FeretMinCaliperMax;
    }

    public double getFeretAR() {
        return this.FeretAspectRatio;
    }

    public Object[][] getderivedConstants() {
        return this.derivedConstants;
    }

    public double getMax() {
        return this.max;
    }

    public int getPixelCount() {
        return this.n;
    }

    public double getMin() {
        return this.min;
    }

    public ArrayList getVolumePixels(int dim) {

        int countRegion = this.nRegions;
        int countPixel;
        microRegion[] localRegions = this.Regions;
        //int[] pixels = new int[1];  
        int[] pixels;
        ArrayList Dpixels = new ArrayList();

        switch (dim) {

            case X_VALUES:

                for (int c = 0; c <= countRegion; c++) {
                    countPixel = localRegions[c].getPixelCount();
                    pixels = localRegions[c].getPixelsX();

                    for (int m = 0; m <= countPixel; m++) {
                        Dpixels.add(pixels[m]);
                    }
                }
                return Dpixels;

            case Y_VALUES:

                for (int d = 0; d <= countRegion; d++) {
                    countPixel = localRegions[d].getPixelCount();
                    pixels = localRegions[d].getPixelsY();

                    for (int m = 0; m <= countPixel; m++) {
                        Dpixels.add(pixels[m]);
                    }
                }
                return Dpixels;

            default:
                return Dpixels;
        }
    }

    public int getParticleCount(microRegion Region) {
        return Region.getPixelCount();
    }

    public Object[][] getAnalysisResultsVolume() {
        return this.analysisResultsVolume;
    }

    public Object[] getAnalysisMaskVolume() {
        return this.analysisMaskVolume;
    }

    public ArrayList getResultPointer() {
        return this.ResultsPointer;
    }
    
    public Color getAnalyticColor(int channel, int analytic) {
        try{return Colorized[channel][analytic];}
        catch(NullPointerException np){}
        return Color.BLACK;
    }
    
    public void setAnalyticColor(Color clr,int channel, int analytic){
        this.Colorized[channel][analytic] = clr;
    }
};
