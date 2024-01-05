package algo.tree;

import org.junit.Test;
import algo.trees.TreeWithList;


public class TreeWithListTest {

    @Test
    public void addValueOnTree() {
        TreeWithList G = new TreeWithList("G");
        TreeWithList H = new TreeWithList("H");
        TreeWithList I = new TreeWithList("I");
        TreeWithList F = new TreeWithList("F");
        F.getChilds().add(G);
        F.getChilds().add(H);
        F.getChilds().add(I);

        TreeWithList D = new TreeWithList("D");
        TreeWithList E = new TreeWithList("E");
        TreeWithList B = new TreeWithList("B");
        B.getChilds().add(D);
        B.getChilds().add(E);

        TreeWithList C = new TreeWithList("C");
        C.getChilds().add(F);

        TreeWithList A = new TreeWithList("A");
        A.getChilds().add(B);
        A.getChilds().add(C);
        System.out.println(A.size());
        System.out.println(A.height());
        System.out.println(A.getAllValues());
    }
}
