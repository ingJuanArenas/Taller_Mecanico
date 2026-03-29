package com.taller.mecanico.Domain.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.taller.mecanico.Domain.DTOs.CreateProcedureDTO;
import com.taller.mecanico.Domain.DTOs.ProcedureResponseDTO;
import com.taller.mecanico.Domain.DTOs.UpdateProcedurePriceDTO;
import com.taller.mecanico.Domain.Exceptions.NotFoundException;
import com.taller.mecanico.Persistence.CRUDs.OrdersCRUD;
import com.taller.mecanico.Persistence.CRUDs.ProceduresCRUD;
import com.taller.mecanico.Persistence.Mapper.ProcedureMapper;

@Service
public class ProcedureService {

    private final ProceduresCRUD proceduresCRUD;
    private final OrdersCRUD ordersCRUD;
    private final ProcedureMapper procedureMapper;

    public ProcedureService(ProceduresCRUD proceduresCRUD, OrdersCRUD ordersCRUD, ProcedureMapper procedureMapper) {
        this.proceduresCRUD = proceduresCRUD;
        this.ordersCRUD = ordersCRUD;
        this.procedureMapper = procedureMapper;
    }

    public ProcedureResponseDTO create(CreateProcedureDTO dto) {
        var order = ordersCRUD.findById(dto.orderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));
        
        var entity = procedureMapper.toEntity(dto);
        entity.setOrder(order);
        var saved = proceduresCRUD.save(entity);
        return procedureMapper.toDTO(saved);
    }

    public ProcedureResponseDTO updatePrice(Long id, UpdateProcedurePriceDTO dto) {
        var entity = proceduresCRUD.findById(id)
                .orElseThrow(() -> new NotFoundException("Procedure not found"));
        procedureMapper.updateEntityFromDTO(dto, entity);
        var saved = proceduresCRUD.save(entity);
        return procedureMapper.toDTO(saved);
    }

    public List<ProcedureResponseDTO> getByOrderId(Long orderId) {
        var procedures = proceduresCRUD.findByOrderId(orderId);
        return procedureMapper.toDTOs(procedures);
    }

    public void delete(Long id) {
        var entity = proceduresCRUD.findById(id)
                .orElseThrow(() -> new NotFoundException("Procedure not found"));
        proceduresCRUD.delete(entity);
    }
}
