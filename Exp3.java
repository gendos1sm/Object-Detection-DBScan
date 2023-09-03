import java.util.List;
//import java.util.Random;
import java.util.ArrayList;
import java.io.*;  
//import java.util.Scanner;


public class Exp3 {
    // reads a csv file of 3D points (rethrow exceptions!)
 /*  public static List<Point3D> read(String filename) throws Exception {
	  
    List<Point3D> points= new ArrayList<Point3D>(); 
	double x,y,z;
	
	Scanner sc = new Scanner(new File(filename));  
	// sets the delimiter pattern to be , or \n \r
	sc.useDelimiter(",|\n|\r");  

	// skipping the first line x y z
	sc.next(); sc.next(); sc.next();
	
	// read points
	while (sc.hasNext())  
	{  
		x= Double.parseDouble(sc.next());
		y= Double.parseDouble(sc.next());
		z= Double.parseDouble(sc.next());
		points.add(new Point3D(x,y,z));  
	}   
	
	sc.close();  //closes the scanner  
	
	return points;
  }
   */

  /** 
   * @param filename
   * @return List<Point3D>
   * @throws IOException
  */
  public static List<Point3D> read(String filename) throws IOException
  {   
      List<Point3D> points = new ArrayList<Point3D>();

      File file = new File(filename);

      BufferedReader br = new BufferedReader(new FileReader(file));

      // Read in and parse each line of the file and store data in the points list
      String line;
      String [] coords = new String[3];
      int i = 0;
      while ((line = br.readLine()) != null)
      {
          if (i > 0)
          {
              coords = line.split(",");
              points.add(new Point3D(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2])));
          }

          i++;
      }

      br.close();
      return points;
  }

  /** 
   * @param filename
   * @throws IOException
  */
  public static void save(String filename, List<Point3D> neighbors) throws IOException
  {
    FileWriter fWriter = new FileWriter(filename);
    
    String line = "x,y,z\n";
    fWriter.write(line);

    for (int i = 0; i < neighbors.size(); i++)
    {
        Point3D p = neighbors.get(i);
        
        // Write each indivdual point to the file
        line = p.toString() + "\n";
         
        fWriter.write(line);
    }

    fWriter.close();
  }

  public static void main(String[] args) throws Exception {  
  
    long startTime = System.nanoTime();

    List<Point3D> points = DBScan.read(args[0]);
        
    DBScan scan = new DBScan(points);

    double eps = scan.setEps(Double.parseDouble(args[1]));
    double minPts = scan.setMinPts(Double.parseDouble(args[2]));

    String nnType = "kd"; 
    scan.findClusters(nnType);
        
    String filename = args[0] + "_" + String.valueOf(eps) + "_" + String.valueOf(minPts) + "_" + String.valueOf(scan.numClusters) + ".csv";

    scan.save(filename);

    scan.printOutputFileData();
    long endTime = System.nanoTime();

    long duration = (endTime - startTime)/1000000000; // in seconds.

    System.out.println(nnType + " total time to run DBScan on " + args[0] + " = " + duration + " seconds");   
  }   
}
