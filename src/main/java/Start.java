import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static com.github.davidmoten.rtree.geometry.Geometries.point;

public class Start {
    private static final String PRE_PATH = "/home/hamid/IdeaProjects/Clustering/src/main/resources/";

    public static void main(String[] args) throws Exception {
        readPoint(PRE_PATH + "input1");
    }

    public static RTree<Integer, Point> readPoint(String path) throws Exception {
        RTree<Integer, Point> tree = RTree.create();
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        int n = 0;
        String st;
        while ((st = br.readLine()) != null) {
            String[] dim = st.split(" ");
            tree=tree.add(++n, point(Integer.parseInt(dim[0]), Integer.parseInt(dim[0])));
        }
        tree.visualize(600,600)
                .save(PRE_PATH+"/mytree.png");
        return tree;
    }
}
