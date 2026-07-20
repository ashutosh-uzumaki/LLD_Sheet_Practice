package org.example.solid.srp.bad_codes;

import java.util.List;

public class Invoice {
    public void saveInvoice(List<String> items, double price, int quantity ){
        System.out.println("Saving the invoice");
    }

    public double calculateAmount(double price, int quantity){
        return price * quantity;
    }

    public void printInvoice(List<String> items, double price, int quantity){
        System.out.println("Printed Invoice");
    }

    public void saveInvoiceToFile(){
        System.out.println("Invoice saved to file");
    }

}
