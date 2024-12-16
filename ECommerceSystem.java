import java.util.LinkedList;
import java.util.Queue;
public class ECommerceSystem {
    static class Inventory {
        private int stock;

        public Inventory(int initialStock) {
            this.stock = initialStock;
        }

        public synchronized boolean processOrder(String customerName, int quantity) {
            if (quantity <= stock) {
                stock -= quantity;
                System.out.println("Order processed for " + customerName + " | Quantity: " + quantity + " | Remaining Stock: " + stock);
                return true;
            } else {
                System.out.println("Order for " + customerName + " cannot be processed due to insufficient stock.");
                return false;
            }
        }
    }

    // REPRESENT INDIVIDUAL ORDER
    static class Order {
        private final String customerName;
        private final int quantity;

        public Order(String customerName, int quantity) {
            this.customerName = customerName;
            this.quantity = quantity;
        }

        public String getCustomerName() {
            return customerName;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    // ORDER QUEUE AND SYNCHRONIZATION
    static class OrderQueue {
        private final Queue<Order> orders = new LinkedList<>();

        // SYNCHRONIZATION METHOD TO ADD ORDER IN QUEUE
        public synchronized void addOrder(Order order) {
            orders.add(order);
            System.out.println("Order placed by " + order.getCustomerName() + " for Quantity: " + order.getQuantity());
            notify(); // NOTIFY THE PROCESSOR THAT A NEW ORDER IS AVAILABLE 
        }

        public synchronized Order fetchOrder() {
            while (orders.isEmpty()) {
                try {
                    wait(); // WAIT UNTIL NEW ORDERS ARE ADDED
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Order processing thread interrupted.");
                }
            }
            return orders.poll();
        }
    }

    // ORDERPROCESSOR TO PROCESS ORDERS IN BACKGROUND 
    static class OrderProcessor extends Thread {
        private final Inventory inventory;
        private final OrderQueue orderQueue;

        public OrderProcessor(Inventory inventory, OrderQueue orderQueue) {
            this.inventory = inventory;
            this.orderQueue = orderQueue;
        }

        @Override
        public void run() {
            while (true) {
                Order order = orderQueue.fetchOrder();
                if (order != null) {
                    inventory.processOrder(order.getCustomerName(), order.getQuantity());
                    try {
                        Thread.sleep(1000); // SLEEP IS USED TO SIMULATE PROCESS TIME
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println("Processor thread interrupted.");
                    }
                }
            }
        }
    }

    //  IN THIS  MJULTIPLE USER PLACINING ORDER 
    static class User extends Thread {
        private final String userName;
        private final OrderQueue orderQueue;
        private final int quantity;

        public User(String userName, OrderQueue orderQueue, int quantity) {
            this.userName = userName;
            this.orderQueue = orderQueue;
            this.quantity = quantity;
        }
        @Override
        public void run() {
            try {
                Thread.sleep((int) (Math.random() * 2000)); //  SIMULATE DELAY BEFORE PLACING AN ORDER 
                orderQueue.addOrder(new Order(userName, quantity));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(userName + " was interrupted while placing an order.");
            }
        }
    }

    public static void main(String[] args) {
        Inventory inventory = new Inventory(60); // IT MEANS THERE ARE TOTAL  60 ITEMS IN STOCK
        OrderQueue orderQueue = new OrderQueue();
       // START THE ORDER PROCESSOR THREAD 
        OrderProcessor processor = new OrderProcessor(inventory, orderQueue);
        processor.start();
        //USERS PLACING ORDERS 
        Thread user1 = new User("aliyan", orderQueue, 10);
        Thread user2 = new User("Kanwal", orderQueue, 20);
        Thread user3 = new User("akbar", orderQueue, 15);
        Thread user4 = new User("ishaal", orderQueue, 8);

        user1.start();
        user2.start();
        user3.start();
        user4.start();
    }
}
