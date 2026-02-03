package com.pm.groceryshop.repo;

import com.pm.groceryshop.model.Order;
import com.pm.groceryshop.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    List<Order> findAllByOrderByOrderDateDesc();

    // Get orders between two dates
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    // Get orders by status
    List<Order> findByStatus(OrderStatus status);

    // Combined: status + date range
    List<Order> findByStatusAndOrderDateBetween(
            OrderStatus status,
            LocalDateTime start,
            LocalDateTime end
    );

    // With pagination
    Page<Order> findAllByOrderByOrderDateDesc(Pageable pageable);
}
