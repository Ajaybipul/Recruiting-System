package com.example.recruiting_system.service;

import com.example.recruiting_system.model.Employee;
import com.example.recruiting_system.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Create a new employee from an accepted offer candidate
     */
    public Employee createEmployee(String applicationId, String firstName, String lastName, 
                                   String email, String phone, String position, 
                                   String department, Double salary) {
        String employeeId = generateEmployeeId();
        
        Employee employee = new Employee(applicationId, employeeId, firstName, lastName, email, position, department, salary);
        employee.setPhone(phone);
        employee.setCreatedDate(new Date());
        employee.setStatus("Active");
        employee.setOnboardingStatus("Not Started");
        
        return employeeRepository.save(employee);
    }

    /**
     * Generate unique employee ID (format: EMP-YYYY-XXXXX)
     */
    private String generateEmployeeId() {
        int year = java.time.LocalDate.now().getYear();
        String randomPart = String.format("%05d", new java.util.Random().nextInt(100000));
        return "EMP-" + year + "-" + randomPart;
    }

    /**
     * Activate employee profile (after offer acceptance)
     */
    public Employee activateEmployee(String employeeId) {
        Optional<Employee> emp = employeeRepository.findByEmployeeId(employeeId);
        if (emp.isPresent()) {
            Employee employee = emp.get();
            employee.setActivationDate(new Date());
            employee.setStatus("Active");
            return employeeRepository.save(employee);
        }
        return null;
    }

    /**
     * Update onboarding status
     */
    public Employee updateOnboardingStatus(String employeeId, String status) {
        Optional<Employee> emp = employeeRepository.findByEmployeeId(employeeId);
        if (emp.isPresent()) {
            Employee employee = emp.get();
            employee.setOnboardingStatus(status);
            if ("In Progress".equals(status)) {
                employee.setOnboardingStartDate(new Date());
            } else if ("Completed".equals(status)) {
                employee.setOnboardingCompletedDate(new Date());
            }
            return employeeRepository.save(employee);
        }
        return null;
    }

    /**
     * Get employee by employee ID
     */
    public Employee getEmployeeById(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId).orElse(null);
    }

    /**
     * Get employee by application ID
     */
    public Employee getEmployeeByApplicationId(String applicationId) {
        return employeeRepository.findByApplicationId(applicationId).orElse(null);
    }

    /**
     * Get all employees in a department
     */
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }

    /**
     * Get all active employees
     */
    public List<Employee> getActiveEmployees() {
        return employeeRepository.findByStatus("Active");
    }

    /**
     * Get employees by onboarding status
     */
    public List<Employee> getEmployeesByOnboardingStatus(String status) {
        return employeeRepository.findByOnboardingStatus(status);
    }

    /**
     * Count active employees
     */
    public long countActiveEmployees() {
        return employeeRepository.countByStatus("Active");
    }

    /**
     * Terminate employee
     */
    public Employee terminateEmployee(String employeeId) {
        Optional<Employee> emp = employeeRepository.findByEmployeeId(employeeId);
        if (emp.isPresent()) {
            Employee employee = emp.get();
            employee.setStatus("Terminated");
            return employeeRepository.save(employee);
        }
        return null;
    }

    /**
     * Update employee information
     */
    public Employee updateEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    /**
     * Get all employees
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Delete employee
     */
    public void deleteEmployee(String employeeId) {
        Optional<Employee> emp = employeeRepository.findByEmployeeId(employeeId);
        emp.ifPresent(employee -> employeeRepository.deleteById(employee.getId()));
    }
}
