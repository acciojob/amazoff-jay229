package com.driver;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Repository
public class OrderRepository {
    HashMap<String,Order>orderMap;
    HashMap<String,DeliveryPartner>partnerMap;
    HashMap<String, List<String>>pair;
    HashSet<String>orderNotAssigned;

    public OrderRepository() {
        orderMap=new HashMap<>();
        partnerMap=new HashMap<>();
        pair=new HashMap<>();
        orderNotAssigned=new HashSet<>();
    }
    public void addOrder(Order order){
        String id=order.getId();
        orderMap.put(id,order);
        orderNotAssigned.add(id);

    }
    public void addPartner( String partnerId){
        partnerMap .put(partnerId,new DeliveryPartner(partnerId));

    }

    public void addOrderPartnerPair( String orderId,  String partnerId){
        List<String>orders=new ArrayList<>();
        if (pair.containsKey(partnerId)) {
            orders = pair.get(partnerId);
        }
        orders.add(orderId);
        pair.put(partnerId,orders);
        orderNotAssigned.remove(orderId);

    }
    public Order getOrderById(String orderId){
        return orderMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return partnerMap.get(partnerId);
    }
    public int getOrderCountByPartnerId(String partnerId){
        return pair.get(partnerId).size();
    }
    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> orderList =new ArrayList<>();
        List<String> orderIdList = pair.get(partnerId);
        for(String order : orderIdList){
            orderList.add(orderMap.get(order).getId());
        }
        return orderList;
    }
    public List<String> getAllOrders(){
        Collection<Order> values = orderMap.values();
        List<String> orderList = new ArrayList<>();
        for(Order order : values){
            orderList.add(order.getId());
        }
        return orderList;
    }
    public int getCountOfUnassignedOrders() {
        return orderNotAssigned.size();
    }
    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){
        int numericalTime = Integer.parseInt(time.substring(0,2))*60 + Integer.parseInt(time.substring(3,5));
        int count = 0;
        for(String orderId : pair.get(partnerId)){
            if(orderMap.get(orderId).getDeliveryTime()>numericalTime){
                count++;
            }
        }
        return count;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        int latestTime = 0;
        if(pair.containsKey(partnerId)){
            for(String currOrderId : pair.get(partnerId)){
                if(orderMap.get(currOrderId).getDeliveryTime()>latestTime){
                    latestTime = orderMap.get(currOrderId).getDeliveryTime();
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
        if(!pair.isEmpty()){
            orderNotAssigned.addAll(pair.get(partnerId));
        }
        pair.remove(partnerId);
        partnerMap.remove(partnerId);

    }
    public void deleteOrderById(String orderId){
        orderMap.remove(orderId);
        if(orderNotAssigned.contains(orderId)){
            orderNotAssigned.remove(orderId);
        }
        else {
            for(List<String> listofOrderIds : pair.values()){
                listofOrderIds.remove(orderId);
            }
        }

    }


}
