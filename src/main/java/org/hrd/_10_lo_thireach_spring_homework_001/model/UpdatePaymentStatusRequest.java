package org.hrd._10_lo_thireach_spring_homework_001.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdatePaymentStatusRequest {
    private int[] ticketIds;
    private boolean paymentStatus;
}
