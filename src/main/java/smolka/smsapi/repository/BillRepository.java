package smolka.smsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import smolka.smsapi.model.Bill;
import smolka.smsapi.model.CurrentActivation;

import java.time.LocalDateTime;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, String> {
    Bill findBillById(String id);
    @Query(value = "SELECT b FROM Bill b WHERE b.plannedCloseDate <= ?1")
    List<Bill> findAllBillsByPlannedFinishDateLessThanEqual(LocalDateTime dateTime);
}
