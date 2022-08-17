package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final int sizeMax;
    private final boolean isNormalOrder;
    private final CustomLinkedList<Task> lastViewedTasks;

    public InMemoryHistoryManager(int sizeMax, boolean isNormalOrder) {
        this.sizeMax = sizeMax;
        this.isNormalOrder = isNormalOrder;
        this.lastViewedTasks = new CustomLinkedList<>(this.sizeMax, this.isNormalOrder);
    }

    @Override
    public void add(Task task) {
        lastViewedTasks.addTask(task);
    }

    @Override
    public void remove(int id) {
        lastViewedTasks.removeTaskById(id);
    }

    public boolean isNormalOrder() {
        return isNormalOrder;
    }

    public int getSizeMax() {
        return sizeMax;
    }

    @Override
    public List<Task> getHistory() {
        return lastViewedTasks.getTasks();
    }

    @Override
    public String toString() {
        final String[] str = {""};
        lastViewedTasks.getTasks().forEach(task -> str[0] += "\n\t\t" + task);
        return "InMemoryHistoryManager{" +
                "\n\tsizeMax=" + sizeMax +
                "\n\tisNormalOrder=" + isNormalOrder +
                "\n\tlastViewedTasks=" + str[0] + "\n" +
                '}';
    }

    public class CustomLinkedList<E extends Task> {
        private Node<E> head;
        private Node<E> tail;
        private int size;
        private final int sizeMax; // ограничение на максимальное кол-во элементов
        private final boolean isNormalOrder;
        private final Map<Integer, Node<E>> nodes; // key = taskId, value = Node of CustomLinkedList

        public CustomLinkedList(int sizeMax, boolean isNormalOrder) {
            this.head = null;
            this.tail = null;
            this.size = 0;
            this.sizeMax = sizeMax <= 0 ? 10 : sizeMax; // Если пришло некорректное число - установим 10
            this.isNormalOrder = isNormalOrder;
            nodes = new HashMap<>(this.sizeMax);
        }

        private void linkFirst(E e) {
            final Node<E> oldHead = head; // запомним хвост
            final Node<E> newNode = new Node<>(null, e, oldHead);
            head = newNode;
            if (oldHead == null) { // значит список был пуст и это будет единственный узел в списке
                tail = newNode;
            } else {
                oldHead.prev = newNode;
            }
            size++;
        }

        private void linkLast(E e) {
            final Node<E> tl = tail; // запомним хвост
            final Node<E> newNode = new Node<>(tl, e, null);
            tail = newNode;
            if (tl == null) { // значит список был пуст и это будет единственный узел в списке
                head = newNode;
            } else {
                tl.next = newNode;
            }
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
            if (x == null)
                return null;
            return unlink(x);
        }

        /**
         * Добавляет элемент "task" в список. Не добавит, если task == null, так как требуются поле объекта id.
         * @param task Элемент.
         */
        public void addTask(E task) {
            if (task == null)
                return;
            // Если такая задача уже есть - удалим ее из списка
            removeTaskById(task.getId());

            // Проверка на максимальный размер
            if (size >= sizeMax) {
                int id = isNormalOrder ? head.data.getId() : tail.data.getId();
                removeTaskById(id);
            }

            // Добавим элемент и запишем узел в HashMap
            if (isNormalOrder) {
                linkLast(task);
                nodes.put(task.getId(), tail);
            } else {
                linkFirst(task);
                nodes.put(task.getId(), head);
            }
        }

        public E removeTaskById(int taskId) {
            if (!nodes.containsKey(taskId))
                return null;
            Node<E> x = nodes.get(taskId);
            nodes.remove(taskId);
            return removeNode(x);
        }

        public int size() {
            return size;
        }

        public List<E> getTasks() {
            List<E> result = new ArrayList<>(size);
            Node<E> x = head;
            while (x != null) {
                result.add(x.data);
                x = x.next;
            }
            return result;
        }

        private class Node<T> {
            public T data;
            public Node<T> next;
            public Node<T> prev;

            Node(Node<T> prev, T data, Node<T> next) {
                this.data = data;
                this.next = next;
                this.prev = prev;
            }
        }
    }
}





