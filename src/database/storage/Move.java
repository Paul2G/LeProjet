package database.storage;

import java.util.Date;

public class Move {
    private int id;
    private Employee employee;
    private Item item;
    private int qty;
    private Date date;

    //Constuctores
    public Move(int id, Employee employee, Item item, int qty)
    {
        this.id = id;
        this.employee = employee;
        this.item = item;
        this.qty = qty;
        this.date = new Date();
    }

    public Move(int id, Employee employee, Item item, int qty, Date date)
    {
        this.id = id;
        this.employee = employee;
        this.item = item;
        this.qty = qty;
        this.date = date;
    }

    //Getters
    public int getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Item getItem() {
        return item;
    }

    public int getQty(){
        return qty;
    }

    public Date getDate() {
        return date;
    }

    //Setters
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setQty(int qty){
        this.qty = qty;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
