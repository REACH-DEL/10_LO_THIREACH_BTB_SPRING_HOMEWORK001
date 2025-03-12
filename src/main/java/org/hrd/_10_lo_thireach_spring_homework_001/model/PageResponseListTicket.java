package org.hrd._10_lo_thireach_spring_homework_001.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class PageResponseListTicket {
    ArrayList<Ticket> items;
    PaginationInfo pagination;
}
