package com.taller.mecanico.Persistence.CRUDs;

import org.springframework.data.jpa.repository.JpaRepository;
import com.taller.mecanico.Persistence.Model.Procedure;
import java.util.List;

public interface ProceduresCRUD extends JpaRepository<Procedure, Long> {
    List<Procedure> findByOrderId(Long orderId);
}
