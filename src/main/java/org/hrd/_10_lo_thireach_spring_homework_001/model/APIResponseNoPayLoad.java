package org.hrd._10_lo_thireach_spring_homework_001.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIResponseNoPayLoad {
    private boolean success;
    private String message;
    private HttpStatus status;
    private LocalDateTime timestamp;
}
