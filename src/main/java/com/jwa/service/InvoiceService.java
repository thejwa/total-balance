package com.jwa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwa.dto.InvoiceDto;
import com.jwa.dto.TotalInvoiceDto;
import com.jwa.dto.WhoOwesDto;
import com.jwa.entity.Invoice;
import com.jwa.mapper.InvoiceMapper;
import com.jwa.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository repository;
    private final InvoiceMapper mapper;

    public String create(InvoiceDto dto) {
        if (dto.getAmount() <= 0) return "AMOUNT CANNOT BE LESS THAN OR EQUAL TO ZERO";
        Invoice invoice = mapper.fromDto(dto);
        repository.save(invoice);
        return "SUCCESS";
    }

    public TotalInvoiceDto getTotal() {
        List<Invoice> invoices = repository.findAll();
        int numberOfUsers = (int) invoices.stream()
                .map(Invoice::getUsername).distinct().count();
        double totalAmount = invoices.stream()
                .mapToDouble(Invoice::getAmount).sum();

        return new TotalInvoiceDto(numberOfUsers, totalAmount);
    }

    public List<InvoiceDto> getUsersInvoices() throws JsonProcessingException {
        String json = repository.getUsersInvoices();
        ObjectMapper objectMapper = new ObjectMapper();
        List<InvoiceDto> invoiceDtos = objectMapper.readValue(json, new TypeReference<List<InvoiceDto>>() {
        });
        return invoiceDtos;
    }

    public List<WhoOwesDto> getWhoOwesMe(String username) throws JsonProcessingException {
        List<WhoOwesDto> result = getWhoOwesDtos(username);
        result = result.stream().filter(res -> res.getReceiver().equals(username))
                .collect(Collectors.toList());

        return result;
    }


    public List<WhoOwesDto> getToWhomIOwe(String username) throws JsonProcessingException {
        List<WhoOwesDto> result = getWhoOwesDtos(username);
        result = result.stream().filter(res -> res.getPayer().equals(username))
                .collect(Collectors.toList());

        return result;
    }

    private List<WhoOwesDto> getWhoOwesDtos(String username) throws JsonProcessingException {
        if (!repository.existsByUsername(username)) {
            throw new RuntimeException("USER WITH GIVEN USERNAME NOT FOUND");
        }
        String totalSpending = repository.getTotalSpendingOfUsers();
        ObjectMapper objectMapper = new ObjectMapper();
        List<InvoiceDto> invoices = objectMapper.readValue(totalSpending,
                new TypeReference<List<InvoiceDto>>() {
                });

        double average = invoices.stream()
                .mapToDouble(InvoiceDto::getAmount).average().orElse(0);


        List<InvoiceDto> payers = invoices.stream()
                .filter(invoice -> invoice.getAmount() <= average)
                .collect(Collectors.toList());

        List<WhoOwesDto> result = new ArrayList<>();

        for (InvoiceDto invoice : invoices) {
            if (invoice.getAmount() > average) {
                double amountNeeded = invoice.getAmount() - average;
                for (InvoiceDto payer : payers) {
                    if (payer.getAmount() == 0) continue;
                    double difference = average - payer.getAmount();
                    if (difference > amountNeeded) {
                        result.add(new WhoOwesDto(invoice.getUsername(),
                                payer.getUsername(),
                                difference - amountNeeded));
                        payer.setAmount(payer.getAmount() - (difference - amountNeeded));
                        amountNeeded = 0;
                    } else {
                        result.add(new WhoOwesDto(invoice.getUsername(),
                                payer.getUsername(),
                                difference));
                        payer.setAmount(0);
                        amountNeeded -= difference;
                    }
                    if (amountNeeded == 0) break;
                }
            }
        }
        return result;
    }
}
