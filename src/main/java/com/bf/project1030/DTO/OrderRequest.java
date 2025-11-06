package com.bf.project1030.DTO;

import com.bf.project1030.service.OrderService.OrderItemRequest;
import java.util.List;

public record OrderRequest(List<OrderItemRequest> order) {}
