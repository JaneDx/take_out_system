package com.dxx.takeOut.dto;

import com.dxx.takeOut.entity.OrderDetail;
import com.dxx.takeOut.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;
}
