public class KDtree {
    public class KDnode{

        public Point3D point;
        
        //  Splitting axis (x = 0, y = 1, z = 2)
        public int axis; 

       //  Splitting value (the coordinate value of the splitting axis)
        public double value;

        public KDnode left;

        public KDnode right;

        public KDnode(Point3D pt, int axis) { 
            this.point = pt;
            this.axis = axis;
            this.value = pt.get(axis);
            left = right = null;
        }
      
    }

    private KDnode root_;

    public KDtree() {
         root_ = null;
    }

    public void add(Point3D p)
    {
        root_ = insert(p, root_, 0);
    }

    private KDnode insert(Point3D p, KDnode node, int axis)
    {
        final int DIM = 3;

        if (node == null)
            node = new KDnode(p, axis);
        else if (p.get(axis) <= node.value)
            node.left = insert(p, node.left, (axis+1) % DIM);
        else
            node.right = insert(p, node.right, (axis+1) % DIM);
       
        return node;
    }

   public KDnode root() { return root_; }     
}
