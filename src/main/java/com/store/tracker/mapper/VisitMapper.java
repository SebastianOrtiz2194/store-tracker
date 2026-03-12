package com.store.tracker.mapper;

import com.store.tracker.dto.VisitResponse;
import com.store.tracker.entity.Visit;

/**
 * Mapper para convertir entre la entidad Visit y su DTO de respuesta.
 * En proyectos más grandes se recomienda usar bibliotecas como MapStruct.
 */
public class VisitMapper {

    /**
     * Convierte una entidad Visit a un DTO VisitResponse.
     */
    public static VisitResponse toResponse(Visit entity) {
        if (entity == null) return null;

        VisitResponse response = new VisitResponse();
        response.setId(entity.getId());
        response.setPersonName(entity.getPersonName());
        response.setEntryTime(entity.getEntryTime());
        response.setExitTime(entity.getExitTime());
        response.setPurchasedItems(entity.getPurchasedItems());
        response.setTotalSpent(entity.getTotalSpent());

        return response;
    }
}
