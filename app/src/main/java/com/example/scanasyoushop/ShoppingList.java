package com.example.scanasyoushop;

import java.util.ArrayList;

public class ShoppingList {
    private String listName;
    ArrayList<Item> list = new ArrayList<Item>();

    public ShoppingList(String listName){
        this.listName = listName;
    }
    public void addItem(Item shoppingItem){
        list.add(shoppingItem);
    }

}
