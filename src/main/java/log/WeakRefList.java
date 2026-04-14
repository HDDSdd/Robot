package log;

import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Список, хранящий элементы через WeakReference.
 * Автоматически удаляет ссылки, на которые больше нет сильных ссылок.
 * Подходит для хранения слушателей, чтобы не мешать их сборке мусором.
 */
public class WeakRefList<E> extends AbstractList<E> {
    private final List<WeakReference<E>> delegates;

    /**
     * Конструктор класса
     */
    public WeakRefList() {
        delegates = new ArrayList<>();
    }

    /**
     * Конструктор класса принимающий в качестве аргумента List<E> и добавляет его
     * @param listeners
     */
    public WeakRefList(List<E> listeners) {
        delegates = new ArrayList<>(listeners.size());
        for(E e : listeners){
            delegates.add(new WeakReference<>(e));
        }
    }

    @Override
    public boolean add(E e) {
        cleanup();
        return delegates.add(new WeakReference<>(e));
    }

    @Override
    public E get(int index) {
        return delegates.get(index).get();
    }

    @Override
    public int size() {
        return delegates.size();
    }


    @Override
    public boolean remove(Object o) {
        cleanup();
        for(WeakReference<E> item : delegates) {
            if (item.get() == o) {
                delegates.remove(item);
                return true;
            }
        }
        return false;
    }

    /**
     * Удаляет все WeakReference, у которых target уже был собран GC.
     */
    private void cleanup() {
        delegates.removeIf(ref -> ref.get() == null);
    }
}