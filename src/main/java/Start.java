import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import rx.Observable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.github.davidmoten.rtree.geometry.Geometries.point;

public class Start {
    private static final String PRE_PATH = "/home/hamid/IdeaProjects/Clustering/src/main/resources/";
    private static final int MAX_DISTANCE = Integer.MAX_VALUE;
    private static RTree<Integer, Point> rTree;
    private static int n = 0;
    private static MutableGraph<Point> graph = GraphBuilder.undirected().build();

    public static void main(String[] args) throws Exception {
        readPoint(PRE_PATH + "input1");
        connectNode(4);
        rTree.visualize(6000,6000)
                .save("target/mytree.png");
    }

    public static void connectNode(int k) {
        for (Point node : graph.nodes()) {
            List<Point> neighbors=  kNearestItem(node, k);
            for (Point neighbor : neighbors) {
               if (node!=neighbor){ graph.putEdge(node,neighbor);}
            }
        }
    }

    public static List<Point> kNearestItem(Point point, int k) {
        Object temp = rTree.nearest(point, MAX_DISTANCE, k).toBlocking().toIterable();
        Iterator it = ((Iterable) temp).iterator();
        LinkedList<Point> res = new LinkedList<Point>();
        while (it.hasNext()) {
            res.add(((Entry<Integer, Point>) it.next()).geometry());
        }
        return res;
    }

    public static void readPoint(String path) throws Exception {
        rTree = RTree.create();
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null) {
            String[] dim = st.split(" ");
            Point temp = point(Integer.parseInt(dim[0]), Integer.parseInt(dim[1]));
            n++;
            rTree = rTree.add(n, temp);
            graph.addNode(temp);
        }
    }
}
