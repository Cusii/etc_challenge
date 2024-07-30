package com.challenge.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskDTO {
    private Long taskId;
    private String taskName;
    private String status;
    private String description;
    private String image;
}
