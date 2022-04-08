package com.jwa.repository;

import com.jwa.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query(value = "select cast(( " +
            "    select array_to_json(array_agg(row_to_json(ta))) " +
            "    from (select i.username, sum(i.amount) as amount " +
            "          from invoice i " +
            "          group by i.username) ta) as text)",
            nativeQuery = true)
    String getUsersInvoices();

    @Query(value = "select avg(t.average) " +
            "from (select avg(i.amount) as average " +
            "      from invoice i " +
            "      group by i.username) t",
            nativeQuery = true)
    double getAverageSpending();

    @Query(value = "select cast(( " +
            "    select array_to_json(array_agg(row_to_json(t))) " +
            "    from (select username, sum(amount) as amount " +
            "          from invoice " +
            "          group by username) t) as text)",
            nativeQuery = true)
    String getTotalSpendingOfUsers();

    boolean existsByUsername(String username);
}
