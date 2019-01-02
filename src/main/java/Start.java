import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import static com.github.davidmoten.rtree.geometry.Geometries.point;

public class Start {
    private static final String PRE_PATH = "/home/hamid/IdeaProjects/Clustering/src/main/resources/";
    private static final int MAX_DISTANCE = Integer.MAX_VALUE;
    private static RTree<Integer, Point> rTree;
    private static int n = 0;
    private static MutableGraph<Point> graph = GraphBuilder.undirected().build();
    private static HashMap<Point, HashMap<Point, Double>> nodeEdges = new HashMap<Point, HashMap<Point, Double>>();

    public static void main(String[] args) throws Exception {
        readPoint(PRE_PATH + "input1");
        connectNode(5);
        calculateEdgeBetweenness(10);
        rTree.visualize(6000, 6000)
                .save("target/mytree.png");
    }

    public static void calculateEdgeBetweenness(int depth) {
        LinkedList<Point> seen = new LinkedList<Point>();
        HashMap<Point, Integer> distance = new HashMap<Point, Integer>();
        HashMap<Point, Double> value = new HashMap<Point, Double>();

        for (Point point : graph.nodes()) {
            seen.clear();
            value.clear();
            distance.clear();
            seen.add(point);
            value.put(point, 0.0);
            distance.put(point, 0);

            int i = 0;
            while (distance.get(seen.getLast()) <= depth) {
                Point temp = seen.get(i);

                for (Point point2 : graph.predecessors(temp))
                    if (distance.get(point2) == null) {
                        distance.put(point2, distance.get(temp) + 1);
                        seen.add(point2);
                        value.put(point2, 1.0);
                    }
                i++;
                if (i == seen.size()) break;

            }


            Collections.reverse(seen);
            seen.removeLast();
            for (Point point2 : seen) {
                if (distance.get(point2) <= depth) {
                    Set<Point> neighborPoint2 = graph.predecessors(point2);
                    double totalDis = 0;
                    for (Point point3 : neighborPoint2)
                        if (distance.get(point3) != null && distance.get(point3) < distance.get(point2))
                            totalDis += point2.distance(point3);
                    for (Point point3 : neighborPoint2)
                        if (distance.get(point3) != null && distance.get(point3) < distance.get(point2)) {
                            double score = (point2.distance(point3) / totalDis) * value.get(point2);
                            value.put(point3, value.get(point3) + score);
                            nodeEdges.get(point2).put(point3, nodeEdges.get(point2).get(point3) + score);
                            nodeEdges.get(point3).put(point2, nodeEdges.get(point3).get(point2) + score);
                        }
                }
            }
        }
    }

    public static void connectNode(int k) {
        for (Point point : graph.nodes()) nodeEdges.put(point, new HashMap<Point, Double>());

        for (Point node : graph.nodes()) {
            List<Point> neighbors = kNearestItem(node, k);
            for (Point neighbor : neighbors) {
                if (node != neighbor) {
                    graph.putEdge(node, neighbor);
                    nodeEdges.get(node).put(neighbor, 0.0);
                    nodeEdges.get(neighbor).put(node, 0.0);
                }
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
            Point temp = point(Integer.parseInt(dim[1]), Integer.parseInt(dim[2]));
            n++;
            rTree = rTree.add(n, temp);
            graph.addNode(temp);
        }
    }

    public static void export(String path, String Type){
        switch (Type){

        }
    }
    
}
