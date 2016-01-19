package Objects.layercake;

import Objects.layercake.microRegion;
import Objects.layercake.microVolume;
import ij.*;
import ij.process.*;
import java.util.*;
import ij.ImagePlus;
import ij.plugin.filter.ParticleAnalyzer;
import ij.measure.ResultsTable;
import ij.plugin.filter.BackgroundSubtracter;

public class RegionFactory extends Object implements Cloneable, java.io.Serializable, Runnable {

    /**
     * Constants
     */
//public static final int POSITIVE = 1;
//public static final int NEGATIVE = 2;
//public static final int GROW = 0;  //use subtype to determine how much
//ublic static final int SHRINK = 1;
    /**
     * Variables
     */
    private static ImagePlus imageOriginal;
    private static ImagePlus imageResult;
    private static ImageStack stackOriginal;
    protected static ImageStack stackResult;
    private static microRegion[] Regions;
    private static int nRegions;

    private static float[] minConstants; // 0: minObjectSize, 1: maxObjectSize, 2: minOverlap, 3: minThreshold

    private static microVolume[] Volumes;
    private static int nVolumes;

    private int[][] derivedRegionType;
//derivedRegionType[][], [Channel][0, type, 1, subtype];

    /**
     * Constructors
     */
//empty cosntructor
    public RegionFactory() {
    }

//constructor for volume building
    @Deprecated
    public RegionFactory(microRegion[] Regions, int nRegions, float[] minConstants, int size, int[][] localderivedRegionType) {
        this.minConstants = minConstants;
        this.derivedRegionType = localderivedRegionType;
        int counter = 1;
        int region_counter = 0;
        int volume_counter = 0;
        microVolume volume = new microVolume(size, volume_counter);
        microVolume[] buildVolume = new microVolume[nRegions];
        int[] analyzedRegions = new int[nRegions + 1];
        double comparator;
        double[] testRegion = new double[2];
        double[] startRegion = new double[2];

        int current_z;
        int last_z;
        //IJ.log("microBuilder::<init>		Initializing volume search...  " + nRegions + " regions to process.");
        for (int n = 0; n <= nRegions - 1; n++) {
            if (analyzedRegions[n] == 0) {
                volume = new microVolume(size, volume_counter);
                startRegion[0] = Regions[n].getBoundCenterX();
                startRegion[1] = Regions[n].getBoundCenterY();
                region_counter = 0;
                last_z = Regions[n].getZPosition();
                analyzedRegions[n] = 1;
                //IJ.log("microBuilder::<init>		Creating volume...  counter #" + counter + " and volume_counter #" + volume_counter);
                //IJ.log("          	Adding Parent...  #" + n + " at  (" + startRegion[0] + ", " + startRegion[1] + ") the # " + region_counter + " in volume.");

                volume.addRegion(Regions[n]);
                //IJ.log("                                                                                        Number of regions logged: " + volume.getNRegions());

                for (int m = 0; m <= nRegions - 1; m++) {
                    if (analyzedRegions[m] == 0) {

                        //IJ.log("microBuilder                         # " + m +" Center:" + Regions[m].getBoundCenterX() + ", " + Regions[m].getBoundCenterX());
                        testRegion[0] = Regions[m].getBoundCenterX();
                        testRegion[1] = Regions[m].getBoundCenterY();
                        current_z = Regions[m].getZPosition();
                        comparator = lengthCart(startRegion, testRegion);
                        if (comparator <= minConstants[2] && Math.abs(current_z - last_z) == 1) {
                            Regions[m].setMembership(volume_counter);
                            volume.addRegion(Regions[m]);
                            analyzedRegions[m] = 1;
                            last_z = Regions[m].getZPosition();
                            region_counter++;
                            //IJ.log("          	Adding Region...  #" + m + " at  (" + testRegion[0] + ", " + testRegion[1] + ")" + comparator + " pixels away, the # " + region_counter + " in volume.");		
                            //IJ.log("                                                                                        Number of regions logged: " + volume.getNRegions());
                        }
                    }
                }

                volume.calculateVolumeMeasurements(0);

                region_counter = 0;
                counter++;

                //IJ.log("microBuilder::<init>                               volume size: " + volume.getPixelCount());
                if (volume.getPixelCount() >= minConstants[0]) {
                    buildVolume[volume_counter] = volume;
                    //IJ.log("microBuilder::<init>                               volume# " + volume_counter + " size: " + buildVolume[volume_counter].getPixelCount());
                    volume_counter++;
                }
            }
        }

        this.nVolumes = volume_counter;
//this.nVolumes = counter-1;
        this.Volumes = buildVolume;
        IJ.log("microBuilder::<init>                               Total volumes found: " + (this.nVolumes));
//IJ.log("microBuilder::<init>           Test calculated results, volume[2] mean: " + this.Volumes[2].getMean());
    }

//constructor for volume building
    public RegionFactory(microRegion[] Regions, int nRegions, float[] minConstants, int size, List secondaryDetails) {
        this.minConstants = minConstants;
        //this.derivedRegionType = localderivedRegionType;
        int counter = 1;
        int region_counter = 0;
        int volume_counter = 0;
        microVolume volume = new microVolume(size, volume_counter);
        microVolume[] buildVolume = new microVolume[nRegions];
        int[] analyzedRegions = new int[nRegions + 1];
        double comparator;
        double[] testRegion = new double[2];
        double[] startRegion = new double[2];

        int current_z;
        int last_z;
        //IJ.log("microBuilder::<init>		Initializing volume search...  " + nRegions + " regions to process.");
        for (int n = 0; n <= nRegions - 1; n++) {
            if (analyzedRegions[n] == 0) {
                volume = new microVolume(size, volume_counter);
                startRegion[0] = Regions[n].getBoundCenterX();
                startRegion[1] = Regions[n].getBoundCenterY();
                region_counter = 0;
                last_z = Regions[n].getZPosition();
                analyzedRegions[n] = 1;
                //IJ.log("microBuilder::<init>		Creating volume...  counter #" + counter + " and volume_counter #" + volume_counter);
                //IJ.log("          	Adding Parent...  #" + n + " at  (" + startRegion[0] + ", " + startRegion[1] + ") the # " + region_counter + " in volume.");

                volume.addRegion(Regions[n]);
                //IJ.log("                                                                                        Number of regions logged: " + volume.getNRegions());

                for (int m = 0; m <= nRegions - 1; m++) {
                    if (analyzedRegions[m] == 0) {

                        //IJ.log("microBuilder                         # " + m +" Center:" + Regions[m].getBoundCenterX() + ", " + Regions[m].getBoundCenterX());
                        testRegion[0] = Regions[m].getBoundCenterX();
                        testRegion[1] = Regions[m].getBoundCenterY();
                        current_z = Regions[m].getZPosition();
                        comparator = lengthCart(startRegion, testRegion);
                        if (comparator <= minConstants[2] && Math.abs(current_z - last_z) == 1) {
                            Regions[m].setMembership(volume_counter);
                            volume.addRegion(Regions[m]);
                            analyzedRegions[m] = 1;
                            last_z = Regions[m].getZPosition();
                            region_counter++;
                            //IJ.log("          	Adding Region...  #" + m + " at  (" + testRegion[0] + ", " + testRegion[1] + ")" + comparator + " pixels away, the # " + region_counter + " in volume.");		
                            //IJ.log("                                                                                        Number of regions logged: " + volume.getNRegions());
                        }
                    }
                }

                volume.calculateVolumeMeasurements();

                region_counter = 0;
                counter++;

                //IJ.log("microBuilder::<init>                               volume size: " + volume.getPixelCount());
                if (volume.getPixelCount() >= minConstants[0]) {
                    buildVolume[volume_counter] = volume;
                    //IJ.log("microBuilder::<init>                               volume# " + volume_counter + " size: " + buildVolume[volume_counter].getPixelCount());
                    volume_counter++;
                }
            }
        }

        this.nVolumes = volume_counter;
//this.nVolumes = counter-1;
        this.Volumes = buildVolume;
        IJ.log("RegionFactory::<init>                               Total volumes found: " + (this.nVolumes));
//IJ.log("microBuilder::<init>           Test calculated results, volume[2] mean: " + this.Volumes[2].getMean());
    }

//constructor for region building
    public RegionFactory(ImageStack stack, float[] minConstants, boolean imageOptimize) {

        this.minConstants = minConstants;
        this.stackOriginal = new ImageStack();
        this.stackOriginal = stack;
        ImagePlus imp = new ImagePlus("Mask", stack);
        this.imageOriginal = imp;
        double maxIntensity = Math.pow(2, imp.getBitDepth());
        //IJ.log("microBuilder min constants: " + minConstants[0] + ", " + minConstants[1] + ", " + minConstants[3]);
        //imp.show("for Particle Analyzer");

        if (imageOptimize) {
            stackOriginal = optimizeMask(stackOriginal);
        }

        ResultsTable rtRegions = new ResultsTable();
        //required for ParticleAnalyzer -> NOT used here
        ImageStack stackResult = new ImageStack(stackOriginal.getWidth(), stackOriginal.getHeight());
        //particle analyzer for generating mask image.
        ParticleAnalyzer maskParticles = new ParticleAnalyzer(ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES | ParticleAnalyzer.SHOW_PROGRESS | ParticleAnalyzer.SHOW_MASKS, 0, rtRegions, minConstants[0], minConstants[1]);
        maskParticles.setHideOutputImage(true);
        for (int n = 1; n <= stackOriginal.getSize(); n++) {
            ImageProcessor ipStack = stackOriginal.getProcessor(n);
            ipStack.setThreshold(minConstants[3], maxIntensity, ImageProcessor.RED_LUT);
            maskParticles.analyze(imp, ipStack);
            ImagePlus maskImage = maskParticles.getOutputImage();
            ImageProcessor maskProcessor = maskImage.getProcessor();
            stackResult.addSlice(maskProcessor);
        }
        //IJ.log("microBuilder::<init>          Making image stack.");
        //IJ.log("                              Stack height:  " + stackResult.getSize());
        //This could be replaced with a direct watershed calculation...//
        ImagePlus impStackResult = new ImagePlus("Particle Analyzer Result", stackResult);
        IJ.run(impStackResult, "Watershed", "stack");
        this.imageResult = impStackResult;
        this.stackResult = stackResult;
        //ImageStack stackWorking = new ImageStack(stackOriginal.getWidth(),stackOriginal.getHeight());
        //stackWorking = stackResult;
        //impStackResult.show();	
        ImagePlus stackresult = (new ImagePlus("mask result", stackResult)).duplicate();
        //stackresult.show();

        makeRegions(stackResult, stack);
        //IJ.log("microBuilder::<init>                               Making image stack.");
//        IJ.log("RegionFactory::<init>                               Regions found:  " + (nRegions));
        //IJ.log("microBuilder::<init>          Regions found:  " + nRegions);

    }

//constructor for region building with algorithmic threshold setting
    
    public RegionFactory(ImageStack stack, ArrayList<String> threshold){
        this.minConstants = minConstants;
        this.stackOriginal = new ImageStack();
        this.stackOriginal = stack;
        ImagePlus imp = new ImagePlus("Mask", stack);
        this.imageOriginal = imp;
        
        
//                this.imageResult = impStackResult;
//        this.stackResult = stackResult;
    }
    
    /**
     * Methods
     */
    private void makeRegions(ImageStack stack, ImageStack original) {

        //defines regions from ParticleAnalyzer mask output and determines the perimeter pixels
        //both are added to an array of microRegion objects.
        //Could have used imageJ classes such as Roi, PolygonRoi and Polygon.
        //I found the pixel by pixel extraction difficult with this approach
        //Furthermore, minimizing dependencies on ImageJ classes will allow for 
        //better portability.  The approach given here could be used above to replace
        //the dependency on the AnalyzeParticle class used in the microBuilder
        //constructor
        //overall approach is to find region starts, then perform a linescan flood-fill
        //to identify member pixels for a given region.  I used end-of-line and beginning-of-line
        //(EOL and BOL respectively) to direct the linescan operation as bi-directional searching
        //around the identified start pixel.  This approach has the advantage that it can be used 
        //to easily implement the grow algorithm as well.  I'll also extract the perimeter data from
        //the EOL and BOL datapoints. May leave in the microBuilder class or
        //use similar code in microDerive.  TBD.
        //new ImagePlus("mask result", stack).show();
        int nRegions = 0;
        ImageProcessor ip;
        int maxsize = stack.getSize() * stack.getWidth() * stack.getHeight();
        microRegion[] Regions = new microRegion[(int) (maxsize / minConstants[0])];
        //byte[] pixels = new byte[stack.getWidth()*stack.getHeight()];
        int[] start_pixel = new int[3];
        int x;
        int y;
        int z;
        int[] x_positions = new int[(int) minConstants[1]];
        int[] y_positions = new int[(int) minConstants[1]];
        int n_positions = 0;
        int[] BOL = new int[(int) minConstants[1]];  //start of line position
        int[] EOL = new int[(int) minConstants[1]];  //end of line position
        int[] row = new int[(int) minConstants[1]];  //line position
//        int[] xPerimeter = new int[(int) minConstants[1]];
//        int[] yPerimeter = new int[(int) minConstants[1]];
//        int nPerimeter = 0;
        int count = 0;
	//int count2 = 0;
        //IJ.log("microBuilder::makeRegion          Getting start pixel...");

        //IJ.log("        stack height:  " + stack.getSize());
        //loop through stacks and analyze a pixel array to define starts. Reset pixel array 
        for (int n = 0; n <= stack.getSize() - 1; n++) {
            //IJ.log("        ...analyzing slice number:  " + (n + 1));
            //loop through pixels to find starts		
            for (int p = 0; p <= stack.getWidth() - 1; p++) {
                for (int q = 0; q <= stack.getHeight() - 1; q++) {
                    //start pixel selected if 255
                    if (stack.getVoxel(p, q, n) == 255) {
                        start_pixel[0] = p;
                        start_pixel[1] = q;
                        start_pixel[2] = n;
                        //IJ.log("Making regions, x: " + p + ", y: " + q + ", z: " + n);

                        //position being analyzed
                        x = start_pixel[0];
                        y = start_pixel[1];
                        z = start_pixel[2];

                        //052814 while(stack.getVoxel(x-1,y,z) == 255) {x--;}
                        //start pixel is left most in first row
                        BOL[count] = x;
                        row[count] = y;
                        //run until 0, end of first row
                        while (stack.getVoxel(x, y, z) == 255) {
                            x++;
                        }
                        EOL[count] = x - 1;

                        //IJ.log("      ...added row  " +  row[count] +  ", BOL: "  + BOL[count] + " EOL: "+ EOL[count]);
                        count++;
                        //second row start, search up
                        x = BOL[count - 1];

                        while (stack.getVoxel(x, y, z) == 255) {
                            if (stack.getVoxel(x, y + 1, z) == 255) {
                                while (stack.getVoxel(x - 1, y + 1, z) == 255) {
                                    x--;
                                }
                                BOL[count] = x;
                                row[count] = y + 1;
                                while (stack.getVoxel(x + 1, y + 1, z) == 255) {
                                    x++;
                                }
                                EOL[count] = x;
//                                IJ.log("      ...added row  " +  row[count] +  ", BOL: "  + BOL[count] + " EOL: "+ EOL[count]);
                                y++;
                                x = BOL[count];
                                count++;
                            }
                            x++;
                        }
                        //reset start pixel and search down
                        x = BOL[0];
                        y = start_pixel[1];
                        while (stack.getVoxel(x, y, z) == 255) {
                            if (stack.getVoxel(x, y - 1, z) == 255) {
                                while (stack.getVoxel(x - 1, y - 1, z) == 255) {
                                    x--;
                                }
                                BOL[count] = x;
                                row[count] = y - 1;
                                while (stack.getVoxel(x + 1, y - 1, z) == 255) {
                                    x++;
                                }
                                EOL[count] = x;
//                                IJ.log("rev   ...added row  " +  row[count] +  ", BOL: "  + BOL[count] + " EOL: "+ EOL[count]);
                                y--;
                                x = BOL[count];
                                count++;
                                //count2++;
                            }
                            x++;
                        }
                        //parse tables
//                        IJ.log("microBuilder::makeRegion                Parsing SOL and EOL tables...");
                        //get whole region					
                        for (int a = 0; a <= count - 1; a++) {				//loop rows
                            for (int c = BOL[a]; c <= EOL[a]; c++) {			//loop x or columns
                                x_positions[n_positions] = c;
                                y_positions[n_positions] = row[a];
//                                IJ.log("Getting pixels, x:" + c + " y:" + row[a]);
                                n_positions++;
                            }
                        }
                        //add region to array
                        if (n_positions > 0) {
                            Regions[nRegions] = new microRegion(x_positions, y_positions, n_positions, n, original);
                            //set Perimeter-easier hear then trying to rederive by region pixels, new perimeters may need to be calculated
                            //microDerive...  TBD.
                            //Regions[nRegions].calculatePerimeter();
//                            IJ.log("microBuilder::makeRegion  		Clearing stack and logging pixels...");
//                            IJ.log("microBuilder::makeRegion  		Pixels found: " + n_positions);	
//                            IJ.log("microBuilder::makeRegion  		Region added: " + nRegions);
                            nRegions++;
                        }
                        //remove pixels from stack
                        for (int i = 0; i <= n_positions - 1; i++) {

                            //IJ.log("          logged pixels  " + x_positions[i] + ", " + y_positions[i] + " pixel: " + stack.getVoxel(x_positions[i],y_positions[i],n));
                            stack.setVoxel(x_positions[i], y_positions[i], n, 0);
                            //IJ.log("            pixel  " + x_positions[i] + ", " + y_positions[i] + " pixel: " + stack.getVoxel(x_positions[i],y_positions[i],n-1));
                        }
                        //reset	arrays

                        row = new int[(int) minConstants[1]];
                        x_positions = new int[(int) minConstants[1]];
                        y_positions = new int[(int) minConstants[1]];
                        n_positions = 0;
                        count = 0;
                    }
                }
            }
        }
        this.Regions = Regions;
        this.nRegions = nRegions;
    }

    private void makeRegions2(ImageStack stack, ImageStack original) {

//refining connected component labeling
        ImageStack resultstack = stack;

        double c = 256;

        for (int n = 0; n <= stack.getSize() - 1; n++) {
            //IJ.log("        ...analyzing slice number:  " + (n+1));		
            //loop through pixels to find starts		
            for (int p = 0; p <= stack.getWidth() - 1; p++) {
                for (int q = 0; q <= stack.getHeight() - 1; q++) {
                    //start pixel selected if 255
                    if (stack.getVoxel(p, q, n) == 0) {
                        resultstack.setVoxel(p, q, n, 0);
                    }
                    if (stack.getVoxel(p, q, n) == 255) {

                        int[] point = new int[4];
                        point[0] = p;
                        point[1] = q;
                        point[2] = n;

                        check8Neighbors(stack, point, c);

                        resultstack.setVoxel(p, q, n, c);
                        //resultstack = duplicate
                    }

//					//add region to array
//					if(n_positions > 0){Regions[nRegions] = new microRegion(x_positions, y_positions, n_positions, n, original);					
//
//					nRegions++;}
//					//remove pixels from stack
//					for(int i = 0; i <=n_positions-1; i++)
//						{
//							
//							//IJ.log("          logged pixels  " + x_positions[i] + ", " + y_positions[i] + " pixel: " + stack.getVoxel(x_positions[i],y_positions[i],n));
//							stack.setVoxel(x_positions[i],y_positions[i],n,0);
//							//IJ.log("            pixel  " + x_positions[i] + ", " + y_positions[i] + " pixel: " + stack.getVoxel(x_positions[i],y_positions[i],n-1));
//						}
//					//reset	arrays
//						
//					row = new int[(int)minConstants[1]];
//					x_positions = new int[(int)minConstants[1]];
//					y_positions = new int[(int)minConstants[1]];
//					n_positions = 0;
//					count = 0;					
//					} 
//				}
//		}	}		
//	this.Regions = Regions;
//	this.nRegions = nRegions;
                }

            }
        }
    }

    private double[] check8Neighbors(ImageStack stack, int[] point, double counter) {
        double[] result = new double[2];
        int x = point[0];
        int y = point[1];
        int z = point[2];
        double[] neighbors = new double[10];

        //N
        try {
            neighbors[0] = stack.getVoxel(x, y + 1, z);
        } catch (NullPointerException e) {
            neighbors[0] = 0;
        }
        //NE
        try {
            neighbors[1] = stack.getVoxel(x + 1, y + 1, z);
        } catch (NullPointerException e) {
            neighbors[1] = 0;
        }
        //E
        try {
            neighbors[2] = stack.getVoxel(x, y + 1, z);
        } catch (NullPointerException e) {
            neighbors[2] = 0;
        }
        //SE
        try {
            neighbors[3] = stack.getVoxel(x - 1, y + 1, z);
        } catch (NullPointerException e) {
            neighbors[3] = 0;
        }
        //S
        try {
            neighbors[4] = stack.getVoxel(x, y - 1, z);
        } catch (NullPointerException e) {
            neighbors[4] = 0;
        }
        //SW
        try {
            neighbors[5] = stack.getVoxel(x - 1, y - 1, z);
        } catch (NullPointerException e) {
            neighbors[5] = 0;
        }
        //W
        try {
            neighbors[6] = stack.getVoxel(x - 1, y, z);
        } catch (NullPointerException e) {
            neighbors[6] = 0;
        }
        //NW
        try {
            neighbors[7] = stack.getVoxel(x - 1, y + 1, z);
        } catch (NullPointerException e) {
            neighbors[7] = 0;
        }
        //up
        try {
            neighbors[8] = stack.getVoxel(x, y, z + 1);
        } catch (NullPointerException e) {
            neighbors[7] = 0;
        }
        //down
        try {
            neighbors[9] = stack.getVoxel(x, y, z - 1);
        } catch (NullPointerException e) {
            neighbors[7] = 0;
        }

        //parse neighbors array
        double tag = counter;
        for (int i = 0; i <= 10; i++) {
            if (neighbors[i] > 255) {
                tag = neighbors[i];
            }
            if (neighbors[i] == 255) {
                tag = counter++;
            }
        }

        result[0] = tag;
        result[1] = counter;

        return result;
    }

    public void makeDerivedRegions(int[][] localDerivedRegionTypes, int channels, ImageStack[] Stack, ArrayList ResultsPointers) {

//loop through all Volumes, call makeDerivedVolumes
        microVolume[] localVolumes = this.Volumes;
        int localnVolumes = this.nVolumes;

        this.derivedRegionType = localDerivedRegionTypes;

        //System.out.println("The results pointers" + ResultsPointers);
        //System.out.println("For " + channels + " channels.");
        //derivedRegionType[][], [Channel][0, type, 1, subtype];

        for (int i = 0; i <= localnVolumes - 1; i++) {
            //IJ.log("microBuilder...          making derived volume for volume#, " + i);

            localVolumes[i].makeDerivedRegions(derivedRegionType, channels, Stack, ResultsPointers);
        }
        this.Volumes = localVolumes;
    }

    private float maxPixel(ImageStack stack) {

        float max = this.minConstants[0];
        //IJ.log("microPref::maxPixel           Searching for max pixel.");	
        for (int n = 0; n <= stack.getSize() - 1; n++) {
            ImageProcessor ipStack = stack.getProcessor(n + 1);
            if (ipStack.getMax() > max) {
                max = (float) ipStack.getMax();
            }
        }
        //IJ.log("        Max pixel:  " + max);
        return max;
    }

    private boolean containsPoint(int x1, int y1, int[] x, int[] y, int n) {

        for (int i = 0; i <= n; i++) {
            if (x1 == x[n]) {
                if (y1 == y[n]) {
                    return true;
                }
            }
        }

        return false;
    }

    private ImageStack optimizeMask(ImageStack inputStack) {

        //IJ.log("Optimizing mask stack for region ID...                  ");

        int localsize = inputStack.getSize();
        int localwidth = inputStack.getWidth();
        int localheight = inputStack.getHeight();

        double bgValue = 0.0;

        ImagePlus localImp = new ImagePlus(null, inputStack);

        IJ.run(localImp, "Subtract Background...", "rolling=50 stack");

        StackProcessor sp = new StackProcessor(localImp.getStack());
        ImageStack s1 = sp.resize(localwidth * 2, localheight * 2, true);
        //int newSize = s1.getSize();
        localImp.setStack(null, s1);

        IJ.run(localImp, "Median...", "radius=2 stack");

        sp = new StackProcessor(localImp.getStack());
        ImageStack s2 = sp.resize(localwidth, localheight, true);

        localImp.setStack("Mask Channel-Optimized", s2);
        localImp.show();

        return s2;
    }

    private int[] calculateCartesian(int pixel, int width, int slice) {
        int[] result = new int[3];
        result[1] = (int) Math.ceil(pixel / width);
        result[0] = pixel - (result[1] * width);
        result[2] = slice - 1;
        return result;
    }

    private int calculateLinear(int x, int y, int width) {
        int result = (width * y) - (width - x);
        return result;
    }

    private double lengthCart(double[] position, double[] reference_pt) {
        double distance;
        double part0 = position[0] - reference_pt[0];
        double part1 = position[1] - reference_pt[1];
        distance = Math.sqrt((part0 * part0) + (part1 * part1));
        return distance;
    }

    public microRegion[] getRegions() {
        return this.Regions;
    }

    public int getRegionsCount() {
        //returns the 0-end value, +1 for total count
        return this.nRegions;
    }

    public microVolume[] getVolumes() {
        return this.Volumes;
    }

    public int getVolumesCount() {
        return this.nVolumes;
    }

    public ImagePlus getMaskImage() {
        return this.imageResult;
    }

    public ImagePlus getOriginalImage() {
        return this.imageOriginal;
    }

    public ArrayList getVolumesAsArrayList() {
        ArrayList alVolumes = new ArrayList(this.nVolumes);
        for (int n = 0; n <= nVolumes; n++) {
            alVolumes.add(this.Volumes[n]);
        }
        IJ.log("microBuilder.getVolumesAsArrayList...             volumes found: " + alVolumes.size());
        return alVolumes;
    }
//public ImageStack getProcessedImageStacks()

    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
