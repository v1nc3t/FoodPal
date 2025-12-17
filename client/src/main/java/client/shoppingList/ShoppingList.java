package client.shoppingList;

import java.util.ArrayList;
import java.util.List;

public class ShoppingList {
    private List<ShoppingListItem> items = new ArrayList<>();

    public void addItem(ShoppingListItem item) {
        items.add(item);
    }

    public List<ShoppingListItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "ShoppingList{" +
                "items=" + items +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ShoppingList that = (ShoppingList) obj;
        return items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }

    public void clear() {
        items.clear();
    }
}
