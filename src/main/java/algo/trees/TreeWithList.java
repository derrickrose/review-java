package algo.trees;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class TreeWithList {
    private String name;
    private int value;
    private List<TreeWithList> childs = new ArrayList<>();

    public TreeWithList(String name) {
        this.name = name;
    }

    public int size() {
        int size = 1;
        for (TreeWithList child : childs) {
            size += child.size();
        }
        return size;
    }

    public int height() {
        int maxChildHeight = 0;
        int currentChildHeight = 0;
        for (TreeWithList child : childs) {
            currentChildHeight = child.height();
            if (currentChildHeight > maxChildHeight) {
                maxChildHeight = currentChildHeight;
            }
        }
        return maxChildHeight + 1;
    }

    public List<String> getAllValues() {
        List<String> all = new ArrayList<>();
        all.add(name);
        for (TreeWithList tree : childs) {
            all.addAll(tree.getAllValues());
        }
        return all;
    }
}
