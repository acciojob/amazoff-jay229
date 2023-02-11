package com.driver;


import org.apache.logging.log4j.message.Message;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {
    //First we will create databases
    //We require total 3 databases

    //First one is order database named as orderDatabase
    private Map<String,Order> orderDatabase;

    //Second one is delivery partner database named as partnerDatabase
    private Map<String,DeliveryPartner> partnerDatabase;

    //Third one is order-partner pair database named as partnerOrderMap
    private Map<String, List<String>> partnerOrderMap;

    private Set<String> orderNotAssigned;


    public OrderRepository() {
        this.orderDatabase = new HashMap<>();
        this.partnerDatabase = new HashMap<>();
        this.partnerOrderMap = new HashMap<>();
        this.orderNotAssigned =  new HashSet<>();
    }

    public void addOrder(Order order){
        orderDatabase.put(order.getId(),order);
        orderNotAssigned.add(order.getId());
    }

    public void addPartner(String partnerId){
        partnerDatabase.put(partnerId,new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId){
        partnerDatabase.get(partnerId).setNumberOfOrders(partnerDatabase.get(partnerId).getNumberOfOrders()+1);
        if(partnerOrderMap.containsKey(partnerId)){
            List<String> orderList = partnerOrderMap.get(partnerId);
            orderList.add(orderId);
            orderNotAssigned.remove(orderId);
            return;
        }

        partnerOrderMap.put(partnerId,new ArrayList<>(Arrays.asList(orderId)));
        orderNotAssigned.remove(orderId);
    }

    public Order getOrderById(String orderId){
        return orderDatabase.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return partnerDatabase.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId){
        return partnerOrderMap.get(partnerId).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> orderList = new ArrayList<>(); //This list has to be returned after filling

        //Let's first fetch the list (of Strings) of all the orderIds from the partnerOrderMap database
        List<String> orderIdList = partnerOrderMap.get(partnerId);
        for(String order : orderIdList){
            orderList.add(orderDatabase.get(order).getId());
        }
        return orderList;
    }

    public List<String> getAllOrders(){
        //Lets fetch all the values from the orderDatabase
        Collection<Order> values = orderDatabase.values();

        //Now fill all these values in a list and return it
        List<String> orderList = new ArrayList<>();
        for(Order o : values){
            orderList.add(o.getId());
        }
        return orderList;
    }

    public int getCountOfUnassignedOrders(){
        return orderNotAssigned.size();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){
        int numericalTime = Integer.parseInt(time.substring(0,2))*60 + Integer.parseInt(time.substring(3,5));
        int count = 0;
        for(String orderId : partnerOrderMap.get(partnerId)){
            if(orderDatabase.get(orderId).getDeliveryTime()>numericalTime){
                count++;
            }
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId){
        int latestTime = 0;
        if(partnerOrderMap.containsKey(partnerId)){
            for(String currOrderId : partnerOrderMap.get(partnerId)){
                if(orderDatabase.get(currOrderId).getDeliveryTime()>latestTime){
                    latestTime = orderDatabase.get(currOrderId).getDeliveryTime();
                }
            }
        }
        int hours = latestTime/60;
        int minute = latestTime%60;

        String strhours = Integer.toString(hours);
        if(strhours.length()==1){
            strhours = "0"+strhours;
        }

        String minutes = Integer.toString(minute);
        if(minutes.length()==1){
            minutes = "0" + minutes;
        }
        return strhours + ":" + minutes;

    }

    public void deletePartnerById(String partnerId){
        if(!partnerOrderMap.isEmpty()){
            orderNotAssigned.addAll(partnerOrderMap.get(partnerId));
        }
        partnerOrderMap.remove(partnerId);
        partnerDatabase.remove(partnerId);
    }

    public void deleteOrderById(String orderId){
        orderDatabase.remove(orderId);
        if(orderNotAssigned.contains(orderId)){
            orderNotAssigned.remove(orderId);
        }
        else {
            for(List<String> listofOrderIds : partnerOrderMap.values()){
                listofOrderIds.remove(orderId);
            }
        }
    }

}