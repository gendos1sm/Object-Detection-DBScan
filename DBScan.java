import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.Map.Entry;

public class DBScan {
   
    List<Point3D> points;
    int numClusters, numNoisePoints;
    static String fileName;
    double eps, minPts;

    public DBScan(List<Point3D> points)
    {
        this.points = points;

        numClusters = numNoisePoints = 0;
    }

    
    /** 
     * @param eps
     * @return double
     */
    public double setEps(double eps)
    {
        this.eps = eps;

        return this.eps;
    }

    
    /** 
     * @param minPts
     * @return double
     */
    public double setMinPts(double minPts)
    {
        this.minPts = minPts;

        return this.minPts;
    }
    
    public void findClusters(String nnType)
    {   
        // Used to store processed points.
        List<Point3D> newPoints = new ArrayList<>();
        
        NearestNeighbors nearestNeighbors = null;
        if (nnType.equals("lin"))
        {
            nearestNeighbors = new NearestNeighbors(points);
        }
        else if (nnType.equals("kd"))
        {
            nearestNeighbors = new NearestNeighborsKD(points);
        }

        Stack<Point3D> neighborStack = new Stack<Point3D>();

        for (int i = 0; i < points.size(); i++)
        {
            Point3D p = points.get(i);

            // Already processed so store in newPoints
            if (p.getClusterLabel() != Point3D.UNDEFINED)
            { 
                newPoints.add(p);
                continue;
            }

            // Find the neighbors
            List<Point3D> pNeighbors = nearestNeighbors.rangeQuery(p, eps); 
            
            // Density check 
            if (pNeighbors.size() < minPts)
            {
                // Label as noise, track it and store in newPoints
                p.setClusterLabel(Point3D.NOISE);
                numNoisePoints++;

                // To ensure 0,0,0 is stored as Noise point (label == 0)
                if (i == 0)
                    newPoints.add(p);
                    
                continue;
            }

            // Set next cluster label
            numClusters++;

            // Label initial point and store in newPoints
            p.setClusterLabel(numClusters);
            newPoints.add(p);
           
            // Push neigbbors on stack
            for (var pNeighbor : pNeighbors)
                neighborStack.push(pNeighbor);
        
            while (!neighborStack.isEmpty())
            {
                // Process point q (top of Stack)
                Point3D q = neighborStack.pop();

                // Noise becoms border point and stored in newPoints
                if (q.getClusterLabel() == Point3D.NOISE)
                {
                    q.setClusterLabel(numClusters);
                   // numNoisePoints++; // Count noise point twice?
                    newPoints.add(q);
                }

                // Point is already processed
                // Point is already processed
                if (q.getClusterLabel() != Point3D.UNDEFINED)
                {                     
                    continue;
                }

                // Point q is label 
                q.setClusterLabel(numClusters);
                          
                // Find neighbors of Point Q
                List<Point3D> qNeighbors = nearestNeighbors.rangeQuery(q, eps);
                
                // Density check and push Point q's neighbors on stack
                if (qNeighbors.size() >= minPts)
                {
                    for (var qNeighbor : qNeighbors)
                        neighborStack.push(qNeighbor);
                }
            }
        }

        points = newPoints;
    }

    public int getNumberOfClusters() { return numClusters; }

    public int getNumberOfNoisePoints() { return numNoisePoints; }

    public List<Point3D> getPoints() { return points; }
    
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
    public void save(String filename) throws IOException
    {
        FileWriter fWriter = new FileWriter(filename);
        Random random = new Random();
        
        double red = 0, green = 0, blue = 0;

        // Rint ehe header to the file
        String line = "x,y,z,C,R,G,B\n";
        fWriter.write(line);

        // Track the cluster
        int clusterNo = 0;
    
        for (int i = 0; i < points.size(); i++)
        {
            Point3D p = points.get(i);

            // Create a random color for each cluster
            if (p.getClusterLabel() == clusterNo + 1)
            {                
                red = random.nextDouble();
                green = random.nextDouble();
                blue = random.nextDouble();
                
                clusterNo++;
            }
            
            // Write each indivdual point to the file
            line = p.toString() + "," + p.getClusterLabel() + "," + String.valueOf(red) + "," + String.valueOf(green) + "," + String.valueOf(blue) + "\n"; 
            fWriter.write(line);
        }

        fWriter.close();
    }


    public void printOutputFileData()
    {   
        // Used to store the cluster and its size
        Map<Integer,Integer> clusterMap = new HashMap<Integer,Integer>();

        // Calculate the size of each cluster
        int clusterNo = 1, count = 0;
        for (int i = 1; i < points.size(); i++)
        {
            Point3D p = points.get(i);

            if (p.getClusterLabel() == clusterNo + 1)
            {
                // Insert the last cluster (p.getClusterLabel()-1)
                // and its count into the map.
                clusterMap.put(clusterNo, count);

                // Increase clusterNo to match current cluster and
                // reset count to track the this current cluster
                clusterNo++;
                count = 1;
                
                continue;
            }
            else if (p.getClusterLabel() == clusterNo)
            {
                count++;
            }
        } 

        // Insert last cluster label's count.
        clusterMap.put(numClusters, count);
        
        // Sort clusters by value (smallest to largest).
        // Using a map to keep clusterNo (key) and cluster's size (value) together.
        List<Entry<Integer,Integer>> mapList = new ArrayList<Entry<Integer,Integer>>(clusterMap.entrySet());
        mapList.sort(Entry.comparingByValue());

        // Print clusters by size (largest to smallest)
        for (int i = mapList.size()-1; i >= 0; i--)
        {
            String str = "Size of cluster " + mapList.get(i).getKey() + " = " + mapList.get(i).getValue();

            System.out.println(str);
        }

        // Print out the number of noise points
        String str = "Number of Noise Points = " + numNoisePoints;
        System.out.println(str);
    }
}
