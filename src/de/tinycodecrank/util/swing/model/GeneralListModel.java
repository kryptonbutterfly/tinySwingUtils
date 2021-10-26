package de.tinycodecrank.util.swing.model;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.tinycodecrank.math.utils.limit.LimitInt;

public class GeneralListModel<E> implements ListModel<E>
{
	protected ArrayList<E>				list		= new ArrayList<>();
	protected HashSet<ListDataListener>	listener	= new HashSet<>();
	
	@Override
	public int getSize()
	{
		return list.size();
	}
	
	@Override
	public E getElementAt(int index)
	{
		if (LimitInt.inRange(0, index, list.size() - 1))
		{
			return list.get(index);
		}
		else
		{
			return null;
		}
	}
	
	public void remove(int index)
	{
		if (LimitInt.inRange(0, index, list.size() - 1))
		{
			list.remove(index);
			listener
				.forEach(l -> l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index)));
		}
	}
	
	public int add(E e)
	{
		int index = list.size();
		list.add(e);
		listener.forEach(l -> l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index)));
		return index;
	}
	
	public void setContent(ArrayList<E> content)
	{
		int end = Math.max(list.size(), content.size()) - 1;
		list = content;
		listener.forEach(l -> l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, end)));
	}
	
	@Override
	public void addListDataListener(ListDataListener l)
	{
		listener.add(l);
	}
	
	@Override
	public void removeListDataListener(ListDataListener l)
	{
		listener.remove(l);
	}
}