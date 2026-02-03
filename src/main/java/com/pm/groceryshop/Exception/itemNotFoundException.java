package com.pm.groceryshop.Exception;


public class itemNotFoundException extends RuntimeException{
    public itemNotFoundException(String s) {
        System.out.println("Item not found!");
    }
}
