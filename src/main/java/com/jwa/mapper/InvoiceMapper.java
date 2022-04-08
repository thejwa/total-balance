package com.jwa.mapper;

import com.jwa.dto.InvoiceDto;
import com.jwa.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvoiceMapper {
    Invoice fromDto(InvoiceDto dto);

    List<InvoiceDto> toListDto(List<Invoice> invoices);
}
