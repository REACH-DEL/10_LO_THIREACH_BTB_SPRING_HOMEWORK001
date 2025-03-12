package org.hrd._10_lo_thireach_spring_homework_001.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.hrd._10_lo_thireach_spring_homework_001.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {
    ArrayList<Ticket> tickets = new ArrayList<Ticket>();
    static int id = 4;
    public TicketController(){
        tickets.add(new Ticket(1, "Johny", LocalDate.of(2025, 3, 15), "New York", "Los Angeles", 250.50, false, TicketStatus.BOOKED, "A1"));
        tickets.add(new Ticket(2, "Emma", LocalDate.of(2025, 3, 20), "Chicago", "San Francisco", 180.75, true, TicketStatus.COMPLETED, "B7"));
        tickets.add(new Ticket(3, "Michael", LocalDate.of(2025, 3, 25), "Boston", "Seattle", 300.00, false, TicketStatus.CANCELLED, "C9"));
        tickets.add(new Ticket(4, "Sophia", LocalDate.of(2025, 4, 5), "Miami", "Houston", 150.25, true, TicketStatus.BOOKED, "D5"));
    }
    @GetMapping()
    @Operation(summary = "Get all tickets")
    public ResponseEntity<APIResponseListTicket<PageResponseListTicket>> getTickets(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size){
        ArrayList<Ticket> selectedTickets = new ArrayList<>();
        size = (size > tickets.size())? tickets.size():size;
        int allPage = (tickets.size()%size==0)?tickets.size()/size:(tickets.size()/size)+1;
        int startIndex = (page-1) * size;
        int lastIndex = (startIndex+size > tickets.size()-1)?tickets.size():startIndex+size;
        if ( page <= allPage && page > 0){
            for (int i = startIndex; i < lastIndex; i++){
                selectedTickets.add(tickets.get(i));
            }
        }
        PaginationInfo pagination = new PaginationInfo(tickets.size(), page, size, allPage);
        PageResponseListTicket pageResponseListTicket = new PageResponseListTicket(selectedTickets, pagination);
        APIResponseListTicket<PageResponseListTicket> response = new APIResponseListTicket<>(true, "All tickets retrieved successfully.", HttpStatus.OK, pageResponseListTicket, LocalDateTime.now());

        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PostMapping()
    @Operation(summary = "Create a new ticket")
    public ResponseEntity<APIResponseListTicket<Ticket>> createTicket(@RequestBody TicketRequest ticketRequest){
        if (ticketRequest.getPassengerName().trim().isEmpty() || ticketRequest.getSourceStation().trim().isEmpty() || ticketRequest.getDestinationStation().trim().isEmpty() || ticketRequest.getPrice() <= 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        id++;
        Ticket ticket = new Ticket(id, ticketRequest.getPassengerName(), ticketRequest.getTravelDate(), ticketRequest.getSourceStation(), ticketRequest.getDestinationStation(), ticketRequest.getPrice(), ticketRequest.isPaymentStatus(), ticketRequest.getTicketStatus(), ticketRequest.getSeatNumber());
        tickets.add(ticket);
        APIResponseListTicket<Ticket> response = new APIResponseListTicket<>(true, "Ticket created successfully.", HttpStatus.OK, ticket, LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping()
    @Operation(summary = "Bulk update payment status for multiple tickets")
    public ResponseEntity<APIResponseListTicket<ArrayList<Ticket>>> updateStatusMultipleTickets(@RequestBody UpdatePaymentStatusRequest updateStatus){
        ArrayList<Ticket> updatedTickets = new ArrayList<>();
        for (int id: updateStatus.getTicketIds()) {
            tickets.forEach(e-> {
                if (e.getTicketId() == id){
                    e.setPaymentStatus(updateStatus.isPaymentStatus());
                    updatedTickets.add(e);
                }
            });
        }
        APIResponseListTicket<ArrayList<Ticket>> response = new APIResponseListTicket<>(true, "Payment status updated successfully.", HttpStatus.OK, updatedTickets, LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{ticket-id}")
    @Operation(summary = "Get a ticket by ID")
    public ResponseEntity<?> getTicketByID(@PathVariable("ticket-id") Integer ticketId){
        Ticket findedFind = tickets.stream().filter(e -> e.getTicketId() == ticketId).findFirst().orElse(null);
        APIResponseListTicket<Ticket> response = new APIResponseListTicket<>(true, "Ticket retrieved successfully.",  HttpStatus.OK, findedFind, LocalDateTime.now());
        if  (findedFind == null) {
            APIResponseNoPayLoad responseError = new APIResponseNoPayLoad(false, "No ticket found with ID: " + ticketId, HttpStatus.NOT_FOUND, LocalDateTime.now());
            return new ResponseEntity<>(responseError, HttpStatus.NOT_FOUND);
        };
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("/{ticket-id}")
    @Operation(summary = "Update an existing ticket by ID")
    public ResponseEntity<?> updateTicketByID(@PathVariable("ticket-id") Integer ticketId, @RequestBody TicketRequest ticketRequest){
        Ticket updatedTicket = new Ticket();
        boolean isMatch = false;
        for (Ticket ticket: tickets){
            if (ticket.getTicketId() == ticketId){
                isMatch = true;
                if (ticketRequest.getPassengerName().trim().isEmpty() || ticketRequest.getSourceStation().trim().isEmpty() || ticketRequest.getDestinationStation().trim().isEmpty() || ticketRequest.getPrice() <= 0){
                    APIResponseNoPayLoad response = new APIResponseNoPayLoad(false, "Price must greater than 0 and no null field", HttpStatus.BAD_REQUEST, LocalDateTime.now());
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                ticket.setPassengerName(ticketRequest.getPassengerName());
                ticket.setTravelDate(ticketRequest.getTravelDate());
                ticket.setSourceStation(ticketRequest.getSourceStation());
                ticket.setDestinationStation(ticketRequest.getDestinationStation());
                ticket.setPrice(ticketRequest.getPrice());
                ticket.setPaymentStatus(ticketRequest.isPaymentStatus());
                ticket.setSeatNumber(ticketRequest.getSeatNumber());
                ticket.setTicketStatus(ticketRequest.getTicketStatus());
                updatedTicket = ticket;
            }
        }

        APIResponseListTicket<Ticket> response = new APIResponseListTicket<>(true, "Ticket created successfully.", HttpStatus.OK, updatedTicket, LocalDateTime.now());
        if (!isMatch){
            APIResponseNoPayLoad responseError = new APIResponseNoPayLoad(false, "No ticket found with ID: " + ticketId, HttpStatus.NOT_FOUND, LocalDateTime.now());
            return new ResponseEntity<>(responseError, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @DeleteMapping("/{ticket-id}")
    @Operation(summary = "Delete a ticket by ID")
    public ResponseEntity<APIResponseNoPayLoad> deleteTicketByID(@PathVariable("ticket-id") Integer ticketId){
        APIResponseNoPayLoad response = new APIResponseNoPayLoad(true, "Ticket deleted successfully.", HttpStatus.NOT_FOUND, LocalDateTime.now());
        boolean isMatch = false;
        for(Ticket ticket: tickets){
            if (ticket.getTicketId() == ticketId){
                isMatch = true;
            }
        }
        if (!isMatch){
            response.setSuccess(false);
            response.setMessage("No ticket found with ID: " + ticketId);
            response.setStatus(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        tickets.removeIf(e -> e.getTicketId() == ticketId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @PostMapping("/bulk")
//    @Operation(summary = "Bulk create tickets")

    @GetMapping("/search")
    @Operation(summary = "Search ticket by passenger name")
    public ResponseEntity<APIResponseListTicket<ArrayList<Ticket>>> searchTicketByPassengerName(@RequestParam("passengerName") String pName){
        ArrayList<Ticket> findedTicket = new ArrayList<>();
        for (Ticket ticket: tickets){
            if (ticket.getPassengerName().toLowerCase().equals(pName)){
                findedTicket.add(ticket);
            }
        }
        APIResponseListTicket<ArrayList<Ticket>> response = new APIResponseListTicket<>(true, "Ticket searched successfully.", HttpStatus.OK, findedTicket, LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter tickets by status and travel date")
    public ResponseEntity<APIResponseListTicket<ArrayList<Ticket>>> filterTicket(@RequestParam("ticketStatus") TicketStatus ticketStatus, @RequestParam("travelDate") LocalDate travelDate){
        ArrayList<Ticket> findedTicket = new ArrayList<>();
        tickets.stream().filter(e -> e.getTicketStatus().equals(ticketStatus)).filter(e -> e.getTravelDate().equals(travelDate)).forEach(e -> findedTicket.add(e));
        APIResponseListTicket<ArrayList<Ticket>> response = new APIResponseListTicket<>(true, "Ticket searched successfully.", HttpStatus.OK, findedTicket, LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
