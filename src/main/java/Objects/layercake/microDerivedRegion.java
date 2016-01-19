package Objects.layercake;

import Objects.layercake.microRegion;
import ij.*;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

public class microDerivedRegion extends microRegion {

    public static final int INCLUDED = 1;
    public static final int EXCLUDED = 2;
    public static final int GROW = 0;
    public static final int MASK = 2;
    public static final int ERODE = 1;

    /**
     * Variables
     */
    private int[] x = new int[1000];			//x coordinates
    private int[] y = new int[1000];			//y coordinates
    private int z;				//z position
    private int n;				//number of pixels
    private int type;			//what kind of analysis
    private int subtype;			//analysis modifier

    private String name;			//based on Roi name
    private int volume;			//volume membership if defined
    private int include = INCLUDED;		//flag for including region in analysis

    private float mean = 0;
    private int nThreshold = 0;
    private float thresholdedmean = 0;
    private double thresholdformean = 0.8;
    private float thresholdedid = 0;
    private float integrated_density = 0;
    private double[] deviation;
    private double min = 0;
    private double max = 0;
    private float stdev = 0;

    private double[] FeretValues = new double[5];  //1, maximum caliper width; 1 , FeretAngle; 3, minimum caliper width; 4, FeretX; 5, FeretY. 

    private float centroid_x = 0;
    private float centroid_y = 0;

    private int centerBoundX = 0;
    private int centerBoundY = 0;

    private Object[] analysisResultsRegion;
//[0, count, 1, mean, 2, integrated density, 3, min, 4, max, 5 standard deviation, 6 Feret_AR, 7 Feret_Min, 8 Feret_Max]

    /**
     * Constructors
     */
    //empty constructor
    public microDerivedRegion() {
    }

    //start with a microregion and derive per type
    public microDerivedRegion(microRegion Region, int type) {
    }

    //type is limited to GROW_1 and GROW_2 for number of pixels
    public microDerivedRegion(int[] x, int[] y, int n, int z, String name) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.n = n;
        this.z = z;
    }

    public microDerivedRegion(int[] x, int[] y, int n, int z, int type, String name) {

        this.x = x;
        this.y = y;
        this.n = n + 1;
        this.z = z;
        this.type = type;

        switch (type) {  //new functions may be added later
            default:
                this.name = name;
                break;
        }
    }

    public microDerivedRegion(int[] x, int[] y, int n, int z, int type, int subtype, String name) {
        
        
        

        IJ.log("microDerivedRegion::<init>                     ...making region from " + n + " pixels");
        this.x = x;
        this.y = y;
        this.n = n;

        this.z = z;
        this.type = type;
        this.subtype = subtype;

        switch (type) {  //new functions may be added later
            case microVolume.GROW:
                //IJ.log("Grow region: " + subtype);
                this.growRegion(subtype);
                this.name = name + "_GROW_" + subtype + "px";
                
                break;
            case microVolume.MASK:
                this.name = name + "_MASK_" + subtype + "px";
                break;
            default:
                this.name = name;
                break;
        }
    }

    /**
     * Methods
     */
    //processing methods
    
    //grow region is 8-connected, very slow.  On^3...
    //add remove duplicates another On^2...
    //total of On^5...  Yikes!
    //N for 8-way connectedness
    //N for 
    
    private void growRegion(int growPixels) {

        //no neighbor as criteria with 8-connected
        //int[] x = this.x; int[] y = this.y; 
        int n_local = this.n;
        
        
        
        

        //could use ArrayList here instead of dynamically reseting array size...
        
        //for now lets use the circumferance of a circle with 
        //area n_local as an estimate.
        
        
        
        //double r = Math.sqrt(n_local/Math.PI);
        double primary = 2*Math.PI*(Math.sqrt(n_local/Math.PI));
        double secondary = 2*Math.PI*(Math.sqrt(n_local/Math.PI) + growPixels);
        
        //IJ.log("microDerivedRegions::<init>                     original region");

        //for(int a = 0; a <= this.n-1; a++){IJ.log("                                    (" + this.x[a] + ", " + this.y[a] + ")");}
        

        
        
        
        int maxsize = 5000;
        //maxsize = (int)secondary;
        
        IJ.log("MaxSize for microRegion: " + maxsize);
        IJ.log("Region size: " + this.n);
        
        
        
        int[] x_local = new int[this.n*100];
        int[] y_local = new int[this.n*100];

        System.arraycopy(this.x, 0, x_local, 0, this.n);
        System.arraycopy(this.y, 0, y_local, 0, this.n);

        int[] xGrow = new int[maxsize];
        int[] yGrow = new int[maxsize];
        int nGrow = 0;

        //int start_count = n_local;

        //IJ.log("microDerivedRegion::growRegion               starting...");
        //IJ.log("microDerivedRegion::growRegion                " + n + " pixels to test");

        int xCurrent = 0;
        int yCurrent = 0;
        int xTest = 0;
        int yTest = 0;
        boolean NW = false;
        boolean N = false;
        boolean NE = false;
        boolean E = false;
        boolean SE = false;
        boolean S = false;
        boolean SW = false;
        boolean W = false;

//loop for number of pixels to grow 
        for (int p = 1; p <= growPixels; p++) {

            xCurrent = 0;
            yCurrent = 0;
            xTest = 0;
            yTest = 0;
            NW = false;
            N = false;
            NE = false;
            E = false;
            SE = false;
            S = false;
            SW = false;
            W = false;

//cycle through the existing pixels
            for (int i = 0; i <= n_local-1; i++) {
                //parse regions pixels
                xCurrent = x_local[i];
                yCurrent = y_local[i];
                for (int m = 0; m <= n_local-1; m++) {
                    //check by 8-pt connectedness, if not in existing array, needs to be flipped.
                    xTest = x_local[m];
                    yTest = y_local[m];

                    //IJ.log("                           Testing... " + xTest + " ," + yTest + ", against " + xCurrent + " ," + yCurrent);	
                    //true is when a point in the array is there
                    //false is when a point is not in the array

                    if (xCurrent - 1 == xTest) {
                        if (yCurrent - 1 == yTest) {
                            NW = (true || NW);
                        }
                    }
                    if (xCurrent == xTest) {
                        if (yCurrent - 1 == yTest) {
                            N = (true || N);
                        }
                    }
                    if (xCurrent + 1 == xTest) {
                        if (yCurrent - 1 == yTest) {
                            NE = (true || NE);
                        }
                    }
                    if (xCurrent + 1 == xTest) {
                        if (yCurrent == yTest) {
                            E = (true || E);
                        }
                    }
                    if (xCurrent + 1 == xTest) {
                        if (yCurrent + 1 == yTest) {
                            SE = (true || SE);
                        }
                    }
                    if (xCurrent == xTest) {
                        if (yCurrent + 1 == yTest) {
                            S = (true || S);
                        }
                    }
                    if (xCurrent - 1 == xTest) {
                        if (yCurrent + 1 == yTest) {
                            SW = (true || SW);
                        }
                    }
                    if (xCurrent - 1 == xTest) {
                        if (yCurrent == yTest) {
                            W = (true || W);
                        }
                    }
                }

                NW = !(NW);
                N = !(N);
                NE = !(NE);
                E = !(E);
                SE = !(SE);
                S = !(S);
                SW = !(SW);
                W = !(W);

                //the Not effectively makes the combined positions for all checked pixels a NOR, or if any pixel is false, return true.
                if ((NW || N || NE || E || SE || S || SW || W)) {
			//if any position contains a false then this is an edge pixel,  add pixels to result array

                    //NEED TO REMOVE OVERLAP....OR REMOVE COPYS FROM ARRAY...
                    
                    //Could work off of a copy array.
                    //IJ.log("                              Found edge pixel...  " + xCurrent + " ," + yCurrent); 
                    if (xCurrent != 0 && yCurrent != 0){
                    
                    if (NW == true) {
                        xGrow[nGrow] = xCurrent - 1;
                        yGrow[nGrow] = yCurrent - 1;
                        nGrow++;
                    }
                    if (N == true) {
                        xGrow[nGrow] = xCurrent;
                        yGrow[nGrow] = yCurrent - 1;
                        nGrow++;
                    }
                    if (NE == true) {
                        xGrow[nGrow] = xCurrent + 1;
                        yGrow[nGrow] = yCurrent - 1;
                        nGrow++;
                    }
                    if (E == true) {
                        xGrow[nGrow] = xCurrent + 1;
                        yGrow[nGrow] = yCurrent;
                        nGrow++;
                    }
                    if (SE == true) {
                        xGrow[nGrow] = xCurrent + 1;
                        yGrow[nGrow] = yCurrent + 1;
                        nGrow++;
                    }
                    if (S == true) {
                        xGrow[nGrow] = xCurrent;
                        yGrow[nGrow] = yCurrent + 1;
                        nGrow++;
                    }
                    if (SW == true) {
                        xGrow[nGrow] = xCurrent - 1;
                        yGrow[nGrow] = yCurrent + 1;
                        nGrow++;
                    }
                    if (W == true) {
                        xGrow[nGrow] = xCurrent - 1;
                        yGrow[nGrow] = yCurrent;
                        nGrow++;
                    }
                    }
                    //IJ.log("number of pixels in grow: " + nGrow); 
                    //if this is a grow by x add to parent and result array and repeat x		
                }
                NW = false;
                N = false;
                NE = false;
                E = false;
                SE = false;
                S = false;
                SW = false;
                W = false;
            }
            //if looping multiple times for more than 1 pixel at 8 connected grow add grow to start region
            removeDuplicates(xGrow, yGrow, nGrow);
//            if (growPixels > 1) {
//                for (int a = 0; a <= nGrow - 1; a++) {
//                    n_local++;
//                    x_local[n_local-1] = xGrow[a];
//                    y_local[n_local-1] = yGrow[a];
//
//                }
//            }
        }
//		IJ.log("microDerivedRegions::<init>                     original region");
//
//        for(int a = 0; a <= this.n-1; a++){IJ.log("                                    (" + this.x[a] + ", " + this.y[a] + ")");}
        //IJ.log("microDerivedRegions::<init>                     derived region");
       // for(int b = 0; b <= nGrow-1; b++){IJ.log("                                    (" + xGrow[b] + ", " + yGrow[b] + ")"); }
        

        //IJ.log("microDerivedRegions::<init>              parsed derived region");
       // for(int c = 0; c <= this.n-1; c++){IJ.log("                                    (" + this.x[c] + ", " + this.y[c] + ")");}
        this.x = xGrow;
        this.y = yGrow;
        this.n = nGrow-1;
        //IJ.log("                               Start size...               " + (start_count));
        //IJ.log("                               Grow size...               " + (this.n));

    }

    private void dilateRegion(int numberTimes){       
    Polygon shape = new Polygon(x, y, n);
    Rectangle bounds = shape.getBounds();   
    bounds.grow(numberTimes+1, numberTimes+1); 
    }

    //calculated values-inherited from microRegion
    @Override
    public void calculateMeasurements(ImageStack stack) {
        int[] x_local = this.x;
        int[] y_local = this.y;
        int z_local = this.z;
        int n_local = this.n;

        long total = 0;
        double local_min = 0;
        double local_max = 0;
        double[] deviation = new double[n];
        
        for (int i = 0; i <= n_local - 1; i++) {
            total = total + (long) stack.getVoxel(x_local[i], y_local[i], z_local);
            if (stack.getVoxel(x_local[i], y_local[i], z_local) < local_min) {
                local_min = stack.getVoxel(x_local[i], y_local[i], z_local);
            }
            if (stack.getVoxel(x_local[i], y_local[i], z_local) > local_max) {
                local_max = stack.getVoxel(x_local[i], y_local[i], z_local);
            deviation[i] = stack.getVoxel(x[i], y[i], z);
        }
        this.deviation = deviation;
            //IJ.log("DerivedRegions max: " + local_max + "getVoxel: x " + x_local[i] + ", y " + y_local[i] + ", z" + z_local + " value: " + stack.getVoxel(x_local[i], y_local[i], z_local));
        }
        this.max = local_max;
        this.min = local_min;
        this.mean = total / n_local;
        this.integrated_density = total;      
        total = 0;
        int thresholdcount = 0;        
        for(int j = 0; j <= n_local - 1; j++) {
            if (stack.getVoxel(x_local[j], y_local[j], z_local) > local_max*this.thresholdformean){
                total = total + (long) stack.getVoxel(x_local[j], y_local[j], z_local);
                thresholdcount++;
            }  
        }   
        this.thresholdedid = total;
        this.thresholdedmean = total/thresholdcount;
        this.nThreshold = thresholdcount;
        calculateCenter();
    }

    private void calculateCenter() {

        int[] x = this.x;
        int[] y = this.y;
        int n = this.n;

        double[] FeretValues = new double[5]; //1, maximum caliper width; 1 , FeretAngle; 3, minimum caliper width; 4, FeretX; 5, FeretY. 

        PolygonRoi polygon = new PolygonRoi(x, y, n, Roi.FREEROI);

        Rectangle bounds = polygon.getBounds();

        int xCenter = (int) (bounds.getWidth()) / 2;
        int yCenter = (int) (bounds.getHeight()) / 2;

        FeretValues = polygon.getFeretValues();

        this.FeretValues = FeretValues;
        this.centerBoundX = xCenter + bounds.x;
        this.centerBoundY = yCenter + bounds.y;

        //IJ.log("FERET diameter: " + FeretValues[0]);
    }

    private void removeDuplicates(int[] x, int[] y, int n) {

        int counter = 0;
        int removeflag = 1;
        int testX;
        int testY;
        int currentX;
        int currentY;
        int[] resultX = new int[n];
        int[] resultY = new int[n];

        for (int i = 0; i <= n - 1; i++) {
            currentX = x[i];
            currentY = y[i];
            //IJ.log("Current Pixel                                    (" + currentX + ", " + currentY + ")");
            for (int j = 0; j <= n - 1; j++) {
                testX = x[j];
                testY = y[j];
                //IJ.log("Test Pixel                                    (" + testX + ", " + testY + ")");
                if (currentX == testX) {
                    if (currentY == testY) {
                        if (i != j) {
                            x[j] = 0;
                            y[j] = 0;
                        }
                    }
                }
            }
        }

        for (int k = 0; k <= n-1; k++) {
            if ((x[k] + y[k]) != 0) {
                resultX[counter] = x[k];
                resultY[counter] = y[k];
                //IJ.log("Parsed pixels                                    (" + resultX[counter] + ", " + resultY[counter] + ")");
                counter++;

            }
        }
        this.x = resultX;
        this.y = resultY;
        this.n = counter;
    }

    private void setDerivedConstants() {

        Object[] result = new Object[9];

        for (int i = 0; i <= 3; i++) {

        }

//[0, count, 1, mean, 2, integrated density, 3, min, 4, max, 5 standard deviation, 6 Feret_AR, 7 Feret_Min, 8 Feret_Max]
        analysisResultsRegion = result;
    }

    //private retrival functions
    @Override
    public int isRegionIncluded() {
        return this.include;
    }

    @Override
    public int getPixelCount() {
        return this.n;
    }

    @Override
    public int[] getPixelsX() {
        return this.x;
    }

    @Override
    public int[] getPixelsY() {
        return this.y;
    }

    @Override
    public int getZPosition() {
        return this.z;
    }

//    @Override
//    public String getName() {
//        return this.name;
//    }

    @Override
    public double getMaxIntensity() {
        return this.max;
    }

    @Override
    public double getMinIntensity() {
        return this.min;
    }

    @Override
    public double getIntegratedIntensity() {
        return this.integrated_density;
    }

    @Override
    public double getMeanIntensity() {
        return this.mean;
    }

    public int getType() {
        return this.type;
    }

    public Object[] getDerivedConstants() {
        return this.analysisResultsRegion;
    }
    
    @Override
            public double[] getDeviations() {
        return this.deviation;
    }
            
    @Override
    public double getThresholdedIntegratedIntensity() {
        return this.thresholdedid;
    }
    
    @Override
    public double getThresholdedMeanIntensity() {
        return this.thresholdedmean;
    }
    @Override
    public void setThreshold(double threshold) {
        this.thresholdformean = threshold;
    }
    @Override
    public int getThresholdPixelCount() {
        return this.nThreshold;
    }
            


}
