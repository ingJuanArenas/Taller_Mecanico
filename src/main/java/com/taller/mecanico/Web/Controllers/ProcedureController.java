package com.taller.mecanico.Web.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taller.mecanico.Domain.DTOs.CreateProcedureDTO;
import com.taller.mecanico.Domain.DTOs.ProcedureResponseDTO;
import com.taller.mecanico.Domain.DTOs.UpdateProcedurePriceDTO;
import com.taller.mecanico.Domain.Service.ProcedureService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/procedures")
public class ProcedureController {

    private final ProcedureService procedureService;

    public ProcedureController(ProcedureService procedureService) {
        this.procedureService = procedureService;
    }

    @PostMapping
    public ResponseEntity<ProcedureResponseDTO> create(@Valid @RequestBody CreateProcedureDTO dto) {
        return ResponseEntity.ok(procedureService.create(dto));
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<ProcedureResponseDTO> updatePrice(@PathVariable Long id, @Valid @RequestBody UpdateProcedurePriceDTO dto) {
        return ResponseEntity.ok(procedureService.updatePrice(id, dto));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ProcedureResponseDTO>> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(procedureService.getByOrderId(orderId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        procedureService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
