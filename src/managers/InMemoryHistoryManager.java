package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
//    private final List<Task> lastViewedTasks = new ArrayList<>();
    private final CustomLinkedList<Task> lastViewedTasks = new CustomLinkedList<>();

    @Override
    public void add(Task task) {
        lastViewedTasks.addTask(task);





//        lastViewedTasks.add(task);
        if (lastViewedTasks.size() > 10) {
            lastViewedTasks.removeTaskById(0); // TODO: 03.07.2022  change to removeFirst() as in a LinkedList
        }
    }

    @Override
    public void remove(int id) {
        lastViewedTasks.removeTaskById(id); // TODO: 03.07.2022  Повторяющиеся тоже удалить
    }

    @Override
    public List<Task> getHistory() {
//        return lastViewedTasks; // TODO: 03.07.2022 Return with the method of CustomLinkedList : List<E> getTasks()
        return lastViewedTasks.getTasks();
    }

    @Override
    public String toString() {
        final String[] str = {""};
        lastViewedTasks.forEach(task -> str[0] += "\n\t\t" + task);
        return "InMemoryHistoryManager{" +
                "\n\tlastViewedTasks=" + str[0] + "\n" +
                '}';
    }


    class CustomLinkedList<E extends Task>{
        private Node<E> head;
        private Node<E> tail;
        private int size = 0;
        private final Map<Integer, Node<E>> nodes = new HashMap<>(); // key= taskId, value= Node of CustomLinkedList

        private void linkLast(E e) {
            final Node<E> tl = tail; // запомним хвост
            final Node<E> newNode = new Node<>(tl, e, null);
            tail = newNode;
            if (tl == null) // значит список был пуст и это будет единственный узел в списке
                head = newNode;
            else
                tl.next = newNode;
            size++;
        }

        private E unlink(Node<E> x) {
            // assert x != null;
            final E element = x.data;
            final Node<E> next = x.next;
            final Node<E> prev = x.prev;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                x.prev = null;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                x.next = null;
            }

            x.data = null;
            size--;
            return element;
        }

        private E removeNode(Node<E> x) {
            return unlink(x);
        }

        public void addTask(E task) {
            linkLast(task);
            nodes.put(task.getId(), tail);
        }


        public E removeTaskById(int taskId) {
            Node<E> x = nodes.get(taskId);
            nodes.remove(taskId);
            return removeNode(x);
        }

        public int size() {
            return size;
        }

        public List<E> getTasks() {
            List<E> result = new ArrayList<>(size); // ?? (size);
            Node<E> x = head;
            while (x != null) {
                result.add(x.data);
                x = x.next;
            }
            return result;
        }

//        @Override
//        public void accept(E e) {
//
//        }

//        public void forEach(Consumer<? super E> action) {
//            Objects.requireNonNull(action);
//            for (E t : this) {
//                action.accept(t);
//            }
//        }


    }
}





