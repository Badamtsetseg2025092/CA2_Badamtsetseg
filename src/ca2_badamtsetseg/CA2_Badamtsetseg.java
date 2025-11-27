/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ca2_badamtsetseg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*; 
/**
 *
 * @author badamtsetsegnatsagdorj
 */
public class CA2_Badamtsetseg {

  
class Manager {
   
    private final ManagerType managerType;
    private final Department department;

    public Manager(ManagerType managerType, Department department) {
        this.managerType = managerType;
        this.department = department;
    }

    public ManagerType getManagerType() {
        return managerType;
    }

    public Department getDepartment() {
        return department;
    }

    @Override
    public String toString() {
        return managerType + " in " + department;
    }
}

class Department {
    private final DepartmentName name;

    public Department(DepartmentName name) {
        this.name = name;
    }

    public DepartmentName getName() {
        return name;
    }

    @Override
    public String toString() {
        return name.toString();
    }
}

class Employee {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final double salary;
    private final Department department;
    private final Manager manager;
    private final String company;

    public Employee(String firstName, String lastName, String gender, String email,
                    double salary, Department department, Manager manager,
                    String jobTitle, String company) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.salary = salary;
        this.department = department;
        this.manager = manager;
        this.company = company;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    

    public String getEmail() {
        return email;
    }

    public double getSalary() {
        return salary;
    }

    public Department getDepartment() {
        return department;
    }

    public Manager getManager() {
        return manager;
    }

    public String getCompany() {
        return company;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %s, %s, %s, %.2f)",
                getFullName(),
                email,
                department,
                manager.getManagerType(),
                salary);
    }
}

//Binary tree 

class EmployeeBinaryTree {
    private static class Node {
        Employee data;
        Node left;
        Node right;

        Node(Employee data) {
            this.data = data;
        }
    }

    private Node root;

    public void insert(Employee employee) {
        Node newNode = new Node(employee);
        if (root == null) {
            root = newNode;
            return;
        }

        // Level-order insertion using a queue to keep the tree as complete as possible
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.left == null) {
                current.left = newNode;
                return;
            } else if (current.right == null) {
                current.right = newNode;
                return;
            } else {
                queue.add(current.left);
                queue.add(current.right);
            }
        }
    }

    public void printLevelOrder() {
        if (root == null) {
            System.out.println("(empty tree)");
            return;
        }

        Queue<Node> queue = new LinkedList<>();
        queue.add(root);

        int level = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            System.out.println("Level " + level + ":");
            for (int i = 0; i < size; i++) {
                Node current = queue.poll();
                System.out.println("  - " + current.data.getFullName() +
                        " | Dept: " + current.data.getDepartment().getName() +
                        " | Manager Type: " + current.data.getManager().getManagerType());
                if (current.left != null) queue.add(current.left);
                if (current.right != null) queue.add(current.right);
            }
            level++;
        }
    }

    public int countNodes() {
        return countNodes(root);
    }

    private int countNodes(Node node) {
        if (node == null) return 0;
        return 1 + countNodes(node.left) + countNodes(node.right);
    }

    public int height() {
        return height(root);
    }

    private int height(Node node) {
        if (node == null) return 0;
        int lh = height(node.left);
        int rh = height(node.right);
        return 1 + Math.max(lh, rh);
    }
}   
}
