package client.tools;

import java.util.Iterator;

public class ResizingList<Item> implements Iterable {
    private Item[] items;
    private int N;
    public ResizingList(){
        items = (Item[]) new Object[1];
    }
    public void add(Item item){
        if (N == items.length)
            resize(2 * N);
        items[N++] = item;
    }
    public int count(){
        return N;
    }
    public boolean isEmpty(){
        return N == 0;
    }
    private void resize(int size){
        Item[] old = items;
        items = (Item[]) new Object[size];
        for (int i=0; i<N; i++)
            items[i] = old[i];
    }
    public Item getItem(int i){
        return items[i];
    }

    @Override
    public Iterator iterator() {
        return new Iterator() {
            int count = 0;
            @Override
            public boolean hasNext() {
                return count < N;
            }

            @Override
            public Item next() {
                return items[count++];
            }
        };
    }
}
