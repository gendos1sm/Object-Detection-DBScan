import java.util.List;
import java.util.ArrayList;

public class NearestNeighborsKD extends NearestNeighbors{

    private KDtree kdTree;
   
    public NearestNeighborsKD(List<Point3D> points)
    {
        super(points);

        kdTree = new KDtree();

        for (var p : points)
        {
            kdTree.add(p);
        }

    }

    public List<Point3D> rangeQuery(Point3D p, double eps) {

        List<Point3D> neighbors = new ArrayList<>();

        rangeQuery(p, eps, neighbors, kdTree.root());
        
        return neighbors;
    }

    public void rangeQuery(Point3D p, double eps, List<Point3D> neigbors, KDtree.KDnode node) {
    
        if (node == null)
            return;
        
        if (p.distance(node.point) < eps)
            neigbors.add(node.point);
        
        if (p.get(node.axis) - eps <= node.value)
            rangeQuery(p, eps, neigbors, node.left);
        
        if (p.get(node.axis) + eps > node.value)
            rangeQuery(p, eps, neigbors, node.right);

    }

}