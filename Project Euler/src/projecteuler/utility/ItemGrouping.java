package projecteuler.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemGrouping<Type> {

	private List<Type> list;
	
	
	
	public ItemGrouping(Type... items) {
		list = Arrays.asList(items);
	}
	
	public ItemGrouping(List<Type> items, Type item) {
		list = new ArrayList<Type>(items.size() + 1);
		list.addAll(items);
		list.add(item);
	}
	
	public ItemGrouping(List<Type> items) {
		list = new ArrayList<Type>(items.size());
		list.addAll(items);
	}
	
	public List<Type> getList() {
		return list;
	}
	
	public ItemGrouping<Type> copy() {
		return new ItemGrouping<Type>(list);
	}
	
}
