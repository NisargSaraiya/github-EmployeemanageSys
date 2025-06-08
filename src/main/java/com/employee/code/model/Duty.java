package com.employee.code.model;

import jakarta.persistence.*;

@Entity
@Table(name="dutytable")
public class Duty {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY )
    private int id ;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false,length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name="manager_id")
    private Manager manager;

    @ManyToOne
    @JoinColumn(name="employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name= "assignedByManager")
    private Manager assignedByManager;
    @ManyToOne
    @JoinColumn(name= "assignedByAdmin")
    private Admin assignedByAdmin ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Manager getAssignedByManager() {
        return assignedByManager;
    }

    public void setAssignedByManager(Manager assignedByManager) {
        this.assignedByManager = assignedByManager;
    }

    public Admin getAssignedByAdmin() {
        return assignedByAdmin;
    }

    public void setAssignedByAdmin(Admin assignedByAdmin) {
        this.assignedByAdmin = assignedByAdmin;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    @Override
    public String toString() {
        return "Duty{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", manager=" + manager +
                ", employee=" + employee +
                ", assignedByManager=" + assignedByManager +
                ", assignedByAdmin=" + assignedByAdmin +
                '}';
    }
}
