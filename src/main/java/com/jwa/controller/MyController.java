package com.jwa.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jwa.dto.InvoiceDto;
import com.jwa.dto.TotalInvoiceDto;
import com.jwa.dto.WhoOwesDto;
import com.jwa.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MyController {
    private final InvoiceService service;

    /**
     *
     * @param dto json body to create invoice.
     * @return "SUCCESS" if everything is okay
     */
    @PutMapping("/create_invoice")
    public ResponseEntity<String> createInvoice(@RequestBody @Valid InvoiceDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    /**
     *
     * @return total spending of all users and number of users
     */
    @GetMapping("/total")
    public ResponseEntity<TotalInvoiceDto> getTotal() {
        return ResponseEntity.ok(service.getTotal());
    }

    /**
     *
     * @return total spending of each user
     */
    @GetMapping("/user_invoices")
    public ResponseEntity<List<InvoiceDto>> getUsersInvoices() {
        ResponseEntity<List<InvoiceDto>> response = null;
        try {
            response = ResponseEntity.ok(service.getUsersInvoices());
        } catch (JsonProcessingException e) {
            response = ResponseEntity.internalServerError().build();
        }
        return response;
    }

    /**
     *
     * @param username username of the user who wants to identify who owes them
     * @return people who owe to the user
     * @throws JsonProcessingException
     */
    @GetMapping("/who_owes_me")
    public ResponseEntity<List<WhoOwesDto>> whoOwesMe(@RequestParam String username) throws JsonProcessingException {
        return ResponseEntity.ok(service.getWhoOwesMe(username));
    }

    /**
     *
     * @param username username of the user who wants to know whom to pay
     * @return people whom user owe to
     * @throws JsonProcessingException
     */
    @GetMapping("/to_whom_i_owe")
    public ResponseEntity<List<WhoOwesDto>> toWhomIOwe(@RequestParam String username) throws JsonProcessingException {
        return ResponseEntity.ok(service.getToWhomIOwe(username));
    }
}
