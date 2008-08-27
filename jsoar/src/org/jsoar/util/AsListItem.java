/*
 * Copyright (c) 2008 Dave Ray <daveray@gmail.com>
 */
package org.jsoar.util;

import java.util.Iterator;


/**
 * @author ray
 */
public class AsListItem <T> implements Iterable<T>
{
    private T item;
    public AsListItem<T> next;
    public AsListItem<T> previous;
    
    public AsListItem(T item)
    {
        Arguments.checkNotNull(item, "item");
        this.item = item;
        this.next = null;
        this.previous = null;
    }
    
    public T get()
    {
        return item;
    }
    
    public void insertAtHead(ListHead<T> head)
    {
        Arguments.checkNotNull(head, "head");
        
        next = head.first;
        previous = null;
        if(head.first != null)
        {
            head.first.previous = this;
        }
        head.first = this;
    }
    
//    public void insertBefore(ListHead<T> head, AsListItem<T> other)
//    {
//        Arguments.checkNotNull(head, "head");
//        if(other.previous != null)
//        {
//            other.previous.next = this;
//        }
//        else
//        {
//            head.first = this;
//        }
//        previous = other.previous;
//        other.previous = this;
//        next = other;
//    }
//    
    public void insertAfter(ListHead<T> head, AsListItem<T> other)
    {
        Arguments.checkNotNull(head, "head");
        
        this.previous = other;
        if(other == null)
        {
            head.first = this;
            this.next = null;
        }
        else
        {
            this.next = other.next;
            other.next = this;
            if(this.next != null)
            {
                this.next.previous = this;
            }
        }
    }
    
    public void remove(ListHead<T> head)
    {
        Arguments.checkNotNull(head, "head");

        if(next != null)
        {
            next.previous = previous;
        }
        if(previous != null)
        {
            previous.next = next;
        }
        else
        {
            head.first = next;
        }
    }
    
    /**
     * Count the number of list items in this list in the range [this, end),
     * i.e. including this item, but not including end.
     * 
     * @param end The item to stop counting at
     * @return Number of items in list
     */
    public int count(AsListItem<T> end)
    {
        int n = 0;
        for(AsListItem<T> start = this; start != end; start = start.next, ++n)
        {
        }
        return n;
    }
    
    /**
     * @return The number of items from this item to the end of the list
     */
    public int count()
    {
        return count(null);
    }
    
    public AsListItem<T> find(T item)
    {
        for(AsListItem<T> m = this; m != null; m = m.next)
        {
            if(item == m.get())
            {
                return m;
            }
        }
        return null;
    }
//  public AsListItem<T> getTail()
//  {
//      AsListItem<T> tail = this;
//      for(AsListItem<T> m = next; m != null; m = m.next)
//      {
//          tail = m;
//      }
//      return tail;
//  }
//  
//  public AsListItem<T> getHead()
//  {
//      AsListItem<T> head = this;
//      for(AsListItem<T> m = previous; m != null; m = m.previous)
//      {
//          head = m;
//      }
//      return head;
//  }
  
//  public AsListItem<T> next(int offset) { return byOffset(offset); }
//  
//  public AsListItem<T> previous(int offset) { return byOffset(-offset); }
//  
//  public AsListItem<T> byOffset(int offset)
//  {
//      AsListItem<T> start = this;
//      if(offset >= 0)
//      {
//          for(int i = 0; 
//              start != null && i != offset; 
//              ++i, start = start.next)
//          {
//          }
//      }
//      else
//      {
//          for(int i = 0; 
//              start != null && i != offset; 
//              --i, start = start.previous)
//          {
//          }            
//      }
//      return start;
//  }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<T> iterator()
    {
        return new ItemIterator<T>(this);
    }

    private static class ItemIterator <T> implements Iterator<T>
    {
        private AsListItem<T> current;
        
        /**
         * @param current
         */
        public ItemIterator(AsListItem<T> current)
        {
            this.current = current;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext()
        {
            return current != null;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public T next()
        {
            T r = current != null ? current.item : null;
            if(current != null)
            {
                current = current.next;
            }
            return r;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
        
    }
}
