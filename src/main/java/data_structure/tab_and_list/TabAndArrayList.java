package data_structure.tab_and_list;

import java.util.ArrayList;
import java.util.List;

public class TabAndArrayList {


    public static List<String> tabToArrayListWithoutSize(String[] strings) {
        //Arrays.asList(strings.clone());
        List<String> strList = new ArrayList<>();
        //LinkedList or ArrayList

        for (int index = 0; index < strings.length; index++) {
            strList.add(strings[index]);
        }
        return strList;
    }

    


}
