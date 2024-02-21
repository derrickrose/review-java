package linked_list;

import org.junit.Test;
import old.data_structure.linkedlist.DataLinkedList;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class DataLinkedListTest {

  @Test
  public void testList() {
    List testList = (LinkedList) ((new DataLinkedList()).getList());
    testList.add(12);
    testList.add("toto ! !");
    testList.add(12.20f);
    for (int i = 0; i < testList.size(); i++)
      System.out.println("Élément à l'index " + i + " = " + testList.get(i));
    // --------------lire avec listIterator
    ListIterator listIterator = testList.listIterator();
    while (listIterator.hasNext()) {
      System.out.println("Élément " + listIterator.next());
    }
    // ----------- more example about linkedlist here
    // https://www.codeflow.site/fr/article/java-linked-list-middle-element
  }
}
