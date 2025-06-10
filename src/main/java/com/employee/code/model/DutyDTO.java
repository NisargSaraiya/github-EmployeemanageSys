package com.employee.code.model;

public class DutyDTO {
    private int id;
    private String title;
    private String description;
    private Long employeeId;
    private String employeeName;

    public DutyDTO(Duty duty) {
        this.id = duty.getId();
        this.title = duty.getTitle();
        this.description = duty.getDescription();
        if (duty.getEmployee() != null) {
            this.employeeId = duty.getEmployee().getId();
            this.employeeName = duty.getEmployee().getName();
        }
    }

    public DutyDTO() {
        // Default constructor for Jackson
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
} 