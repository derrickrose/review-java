package algo.tab_and_list;

import org.junit.Test;
import old.data_structure.tab_and_list.TabAndLinkedList;

import java.util.LinkedList;

public class TadAndListTest {

    private static final int VALUES_COUNT = 200000;

    @Test
    public void linkedListToTabWithIterator() {
        LinkedList<String> stringList = new LinkedList<>();
        for (int index = 0; index < VALUES_COUNT; index++) {
            stringList.add(Integer.toString(index));
        }
        long t1 = System.currentTimeMillis();
        TabAndLinkedList.linkedListToTabWithIterator(stringList);
        long t2 = System.currentTimeMillis();
        System.out.println("convertion time linkedlist ==> tab with iterator " + (t2 - t1));
    }

    @Test
    public void linkedListToTabWithoutIteratorWithSize() {
        LinkedList<String> stringList = new LinkedList<>();
        for (int index = 0; index < VALUES_COUNT; index++) {
            stringList.add(Integer.toString(index));
        }
        long t1 = System.currentTimeMillis();
        TabAndLinkedList.linkedListToTabWithoutIteratorWithSize(stringList);
        long t2 = System.currentTimeMillis();
        System.out.println("convertion time linkedlist ==> tab without iterator with size " + (t2 - t1));
    }

    @Test
    public void linkedListToTabWithoutIteratorWithoutSize() {
        LinkedList<String> stringList = new LinkedList<>();
        for (int index = 0; index < VALUES_COUNT; index++) {
            stringList.add(Integer.toString(index));
        }
        long t1 = System.currentTimeMillis();
        TabAndLinkedList.linkedListToTabWithoutIteratorWithoutSize(stringList);
        long t2 = System.currentTimeMillis();
        System.out.println("convertion time linkedlist ==> tab without iterator without size " + (t2 - t1));
    }

    @Test
    public void linkedListToTabWithDefaultIterator() {
        LinkedList<String> stringList = new LinkedList<>();
        for (int index = 0; index < VALUES_COUNT; index++) {
            stringList.add(Integer.toString(index));
        }
        long t1 = System.currentTimeMillis();
        TabAndLinkedList.linkedListToTabWithDefaultIterator(stringList);
        long t2 = System.currentTimeMillis();
        System.out.println("convertion time linkedlist ==> tab with default iterator" + (t2 - t1));
    }

    @Test
    public void tabToLinkedListWithSize() {
        String[] strings = new String[VALUES_COUNT];
        LinkedList<String> stringList;
        for (int index = 0; index < VALUES_COUNT; index++) {
            strings[index] = Integer.toString(index);
        }
        long t1 = System.currentTimeMillis();
        TabAndLinkedList.tabToLinkedListWithSize(strings);
        long t2 = System.currentTimeMillis();
        System.out.println("convertion time tab ==> linkedlist with size " + (t2 - t1));
    }

    @Test
    public void tabToArrayListWithSize() {
        String[] strings = new String[VALUES_COUNT];
        LinkedList<String> stringList;
        for (int index = 0; index < VALUES_COUNT; index++) {
            strings[index] = Integer.toString(index);
        }
        long t1 = System.currentTimeMillis();
        TabAndLinkedList.tabToArrayListWithSize(strings);
        long t2 = System.currentTimeMillis();
        System.out.println("convertion time tab ==> arraylist with size " + (t2 - t1));
    }

    @Test
    public void tabToLinkedListWithoutSize() {
        String[] strings = new String[VALUES_COUNT];
        LinkedList<String> stringList;
        for (int index = 0; index < VALUES_COUNT; index++) {
            strings[index] = Integer.toString(index);
        }
        long t1 = System.currentTimeMillis();
        TabAndLinkedList.tabToLinkedListWithoutSize(strings);
        long t2 = System.currentTimeMillis();
        System.out.println("convertion time tab ==> linkedlist without size " + (t2 - t1));
    }


}
