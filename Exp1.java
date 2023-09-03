import java.util.List;
//import java.util.Random;
import java.util.ArrayList;
import java.io.*;  
//import java.util.Scanner;  

public class Exp1 {
  
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
  
    // not reading args[0]
    double eps = Double.parseDouble(args[1]);
    
    // reads the csv file
    List<Point3D> points= Exp1.read(args[2]);
    
    NearestNeighbors nn = null;
    if (args[0].equals("lin"))
    {
      nn = new NearestNeighbors(points);
    }
    else if (args[0].equals("kd"))
    {
      nn = new NearestNeighborsKD(points);
    }

      Point3D query = new Point3D(Double.parseDouble(args[3]),
                  Double.parseDouble(args[4]),
                  Double.parseDouble(args[5]));
    
    // creates the NearestNeighbor instance
    List<Point3D> neighbors = nn.rangeQuery(query,eps);
    
    Point3D [] ptArray = new Point3D[] {
                                         new Point3D( -5.42985, 0.807567048, -0.398216823), 
                                         new Point3D( -12.97637373, 5.09061138, 0.762238889), 
                                         new Point3D( -36.10818686, 14.2416184, 4.293473762),
                                         new Point3D( 3.107437007, 0.032869335, 0.428397562), 
                                         new Point3D( 11.58047393, 2.990601868, 1.865463342), 
                                         new Point3D( 14.15982089, 4.680702457, -0.133791584) 
                                       }; 

    int i = -1;
    for (i = 0; i < ptArray.length; i++)
    {
      if (query.equals(ptArray[i]))
        break;
    }

    if (i != -1)
    {
      String filename = "pt" + (i+1) + "_" + args[0] + ".txt";

      save(filename, neighbors);
    }
    else
    {
      System.out.println("number of neighbors = "+ neighbors.size());
      System.out.println(neighbors);
    }
  }   
}
