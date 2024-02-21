package old.data_structure.tab_and_list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TabAndLinkedList {

    public static List<String> tabToLinkedListWithSize(String[] strings) {
        //Arrays.asList(strings.clone());
        List<String> strList = new LinkedList<>();
        //LinkedList or ArrayList

        int size = strings.length;
        for (int index = 0; index < size; index++) {
            strList.add(strings[index]);
        }
        return strList;
    }

    public static List<String> tabToLinkedListWithoutSize(String[] strings) {
        //Arrays.asList(strings.clone());
        List<String> strList = new LinkedList<>();
        //LinkedList or ArrayList

        for (int index = 0; index < strings.length; index++) {
            strList.add(strings[index]);
        }
        return strList;
    }


    public static List<String> tabToArrayListWithSize(String[] strings) {
        //Arrays.asList(strings.clone());
        List<String> strList = new ArrayList<>();
        //LinkedList or ArrayList

        int size = strings.length;
        for (int index = 0; index < size; index++) {
            strList.add(strings[index]);
        }
        return strList;
    }

    public static String[] linkedListToTabWithDefaultIterator(LinkedList<String> stringList) {
        String[] strings = new String[stringList.size()];
        int index = 0;
        for (String str : stringList) {
            strings[index] = str;
            index++;
        }
        return strings;
    }

    public static String[] linkedListToTabWithIterator(LinkedList<String> stringList) {
        String[] strings = new String[stringList.size()];
        Iterator<String> iterator = stringList.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            strings[index] = iterator.next();
            index++;
        }
        return strings;
    }

    public static String[] linkedListToTabWithoutIteratorWithSize(LinkedList<String> stringList) {
        int size = stringList.size();
        String[] strings = new String[size];
        for (int index = 0; index < size; index++) {
            strings[index] = stringList.get(index);
        }
        return strings;
    }

    public static String[] linkedListToTabWithoutIteratorWithoutSize(LinkedList<String> stringList) {
        String[] strings = new String[stringList.size()];
        for (int index = 0; index < stringList.size(); index++) {
            strings[index] = stringList.get(index);
        }
        return strings;
    }
}
