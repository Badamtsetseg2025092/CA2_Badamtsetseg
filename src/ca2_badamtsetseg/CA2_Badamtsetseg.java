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
private static final Scanner SCANNER = new Scanner(System.in);

    // Main list of employees loaded from file + additions
    private static final List<Employee> employees = new ArrayList<>();

    // Track newly added employees in this session
    private static final List<Employee> newlyAddedEmployees = new ArrayList<>();

    // Comparator for sorting/searching (alphabetical by full name: "First Last")
    private static final Comparator<Employee> NAME_COMPARATOR =
            Comparator.comparing(Employee::getFullName, String.CASE_INSENSITIVE_ORDER);

    public static void main(String[] args) {

    boolean loaded = false;

    // KEEP ASKING until file is successfully read
    while (!loaded) {
        System.out.print("Please enter the filename to read: ");
        String fileName = SCANNER.nextLine().trim();

        loaded = loadEmployeesFromFile(fileName);

        if (!loaded) {
            System.out.println("Error: Could not read file.");
            System.out.println("Please try again.\n");
        }
    }

    System.out.println("File read successfully\n");

    boolean running = true;
    while (running) {
        MenuOption choice = readMenuChoice();
        switch (choice) {
            case SORT:
                handleSort();
                break;
            case SEARCH:
                handleSearch();
                break;
            case ADD_RECORDS:
                handleAddRecord();
                break;
            case CREATE_BINARY_TREE:
                handleCreateBinaryTree();
                break;
            case EXIT:
                System.out.println("Exiting program. Thank you!");
                running = false;
                break;
        }
    }
}
  // main menu

    private static MenuOption readMenuChoice() {
        while (true) {
            System.out.println("Please choose one of following options!");
            for (MenuOption option : MenuOption.values()) {
                System.out.printf("%d. %s%n", option.getCode(), option.getLabel());
            }
            System.out.print("Enter your choice: ");
            String input = SCANNER.nextLine().trim();
            try {
                int code = Integer.parseInt(input);
                MenuOption option = MenuOption.fromCode(code);
                if (option != null) {
                    System.out.println();
                    System.out.println(option.getLabel() + " selected");
                    System.out.println();
                    return option;
                } else {
                    System.out.println("Invalid option number. Please try again.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    } 
    // file reader

    private static boolean loadEmployeesFromFile(String fileName) {
    employees.clear();
    newlyAddedEmployees.clear();

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
        String line;
        boolean firstLine = true;

        while ((line = br.readLine()) != null) {
            if (firstLine) {
                firstLine = false;
                continue;
            }
            if (line.trim().isEmpty()) continue;

            Employee e = parseEmployee(line);
            if (e != null) {
                employees.add(e);
            }
        }
        return true; // success!

    } catch (IOException e) {
        return false; // failed to read file
    }
}
    // Parses one CSV line into an Employee
    private static Employee parseEmployee(String line) {
        // Very simple CSV split; your dummy data has no embedded commas
        String[] parts = line.split(",");
        if (parts.length < 9) {
            System.out.println("Skipping malformed line: " + line);
            return null;
        }

        String firstName = parts[0].trim();
        String lastName = parts[1].trim();
        String gender = parts[2].trim();
        String email = parts[3].trim();
        double salary;
        try {
            salary = Double.parseDouble(parts[4].trim());
        } catch (NumberFormatException ex) {
            salary = 0.0;
        }
        String deptRaw = parts[5].trim();
        String positionRaw = parts[6].trim();
        String jobTitle = parts[7].trim();
        String company = parts[8].trim();

        DepartmentName deptName = DepartmentName.fromString(deptRaw);
        Department department = new Department(deptName);

        ManagerType managerType = ManagerType.fromStrings(positionRaw, jobTitle);

        // Manager object not heavily used here, but created to satisfy class structure
        Manager manager = new Manager(managerType, department);

        return new Employee(firstName, lastName, gender, email, salary,
                department, manager, jobTitle, company);
    }
    // ========== SORTING (MERGE SORT, RECURSIVE) ==========

    private static void handleSort() {
        if (employees.isEmpty()) {
            System.out.println("No employees loaded.");
            return;
        }

        // We use a recursive Merge Sort here.
        // Justification (high level, not algorithm definition):
        // - It has predictable performance on all inputs (no worst-case degradation).
        // - It is naturally recursive, which matches the requirement.
        // - It handles large lists reliably and is stable (keeps equal names in order).
        System.out.println("Sorting employees by full name (alphabetical) using recursive Merge Sort...");
        mergeSort(employees, NAME_COMPARATOR);

        System.out.println("First 20 names (or fewer if list is smaller):");
        for (int i = 0; i < employees.size() && i < 20; i++) {
            Employee e = employees.get(i);
            System.out.printf("%2d. %s%n", i + 1, e.getFullName());
        }
        System.out.println();
    }

    private static void mergeSort(List<Employee> list, Comparator<Employee> comparator) {
        if (list.size() <= 1) {
            return;
        }
        Employee[] arr = list.toArray(new Employee[0]);
        mergeSort(arr, 0, arr.length - 1, comparator);
        list.clear();
        list.addAll(Arrays.asList(arr));
    }

    private static void mergeSort(Employee[] arr, int left, int right, Comparator<Employee> cmp) {
        if (left >= right) {
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSort(arr, left, mid, cmp);
        mergeSort(arr, mid + 1, right, cmp);
        merge(arr, left, mid, right, cmp);
    }

    private static void merge(Employee[] arr, int left, int mid, int right, Comparator<Employee> cmp) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Employee[] L = new Employee[n1];
        Employee[] R = new Employee[n2];

        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);

        int i = 0, j = 0;
        int k = left;

        while (i < n1 && j < n2) {
            if (cmp.compare(L[i], R[j]) <= 0) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }
        while (i < n1) {
            arr[k++] = L[i++];
        }
        while (j < n2) {
            arr[k++] = R[j++];
        }
    }
    private static void mergeSort(List<Employee> list, Comparator<Employee> comparator) {
        if (list.size() <= 1) {
            return;
        }
        Employee[] arr = list.toArray(new Employee[0]);
        mergeSort(arr, 0, arr.length - 1, comparator);
        list.clear();
        list.addAll(Arrays.asList(arr));
    }

    private static void mergeSort(Employee[] arr, int left, int right, Comparator<Employee> cmp) {
        if (left >= right) {
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSort(arr, left, mid, cmp);
        mergeSort(arr, mid + 1, right, cmp);
        merge(arr, left, mid, right, cmp);
    }

    private static void merge(Employee[] arr, int left, int mid, int right, Comparator<Employee> cmp) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Employee[] L = new Employee[n1];
        Employee[] R = new Employee[n2];

        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);

        int i = 0, j = 0;
        int k = left;

        while (i < n1 && j < n2) {
            if (cmp.compare(L[i], R[j]) <= 0) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }
        while (i < n1) {
            arr[k++] = L[i++];
        }
        while (j < n2) {
            arr[k++] = R[j++];
        }
    }

    // search employee data

    private static void handleSearch() {
        if (employees.isEmpty()) {
            System.out.println("No employees loaded.");
            return;
        }

        // Ensure list is sorted before binary search
        mergeSort(employees, NAME_COMPARATOR);

        System.out.print("Enter full name to search (e.g: John Smith): ");
        String query = SCANNER.nextLine().trim();

        // We use Binary Search on the sorted list here.
        // Justification (high level):
        // - It is very efficient on sorted lists (logarithmic time).
        // - It greatly reduces the number of comparisons versus a linear scan.
        // - It matches naturally with our sorted structure.
        int index = binarySearchByFullName(employees, query);

        if (index >= 0) {
            Employee e = employees.get(index);
            System.out.println("Record found:");
            System.out.println("Full Name    : " + e.getFullName());
            System.out.println("Email        : " + e.getEmail());
            System.out.println("Gender       : " + e.getGender());
            System.out.println("Salary       : " + e.getSalary());
            System.out.println("Company      : " + e.getCompany());
            System.out.println("Department   : " + e.getDepartment().getName());
            System.out.println("Manager Type : " + e.getManager().getManagerType());
            System.out.println("Job Title    : " + e.getJobTitle());
            System.out.println();
        } else {
            System.out.println("No record found for: " + query);
            System.out.println();
        }
    }
   private static int binarySearchByFullName(List<Employee> list, String targetName) {
        int left = 0;
        int right = list.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Employee midEmp = list.get(mid);
            int cmp = midEmp.getFullName().compareToIgnoreCase(targetName);
            if (cmp == 0) {
                return mid;
            } else if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    // add new records                   

    private static void handleAddRecord() {
        System.out.println("Add a new employee record.");
        System.out.print("First name: ");
        String firstName = readNonEmptyString();

        System.out.print("Last name: ");
        String lastName = readNonEmptyString();

        String gender = "";
while (true) {
    System.out.println("Select Gender:");
    System.out.println("1. Female");
    System.out.println("2. Male");
    

    String gInput = SCANNER.nextLine().trim();

    if (gInput.equals("1")) {
        gender = "Female";
        break;
    } else if (gInput.equals("2")) {
        gender = "Male";
        break;
    } else {
        System.out.println("Invalid choice! Please enter 1 or 2.\n");
    }
}

        System.out.print("Email: ");
        String email = readNonEmptyString();

        double salary = readDoubleWithPrompt("Salary: ");

        DepartmentName deptName = readDepartmentFromUser();
        Department department = new Department(deptName);

        ManagerType managerType = readManagerTypeFromUser();
        Manager manager = new Manager(managerType, department);

        System.out.print("Job Title: ");
        String jobTitle = readNonEmptyString();

        System.out.print("Company: ");
        String company = readNonEmptyString();

        Employee newEmp = new Employee(firstName, lastName, gender, email, salary,
                department, manager, jobTitle, company);

        employees.add(newEmp);
        newlyAddedEmployees.add(newEmp);

        // After insertion, keep list sorted for consistency
        mergeSort(employees, NAME_COMPARATOR);

        System.out.println("New employee added successfully.");
        System.out.println("Newly added records this session:");
        for (Employee e : newlyAddedEmployees) {
            System.out.println(" - " + e);
        }
        System.out.println();
    }

    private static String readNonEmptyString() {
        while (true) {
            String input = SCANNER.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.print("Input cannot be empty. Try again: ");
        }
    }

    private static double readDoubleWithPrompt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    } 
    
    private static DepartmentName readDepartmentFromUser() {
        while (true) {
            System.out.println("Choose a Department:");
            DepartmentName[] values = DepartmentName.values();
            for (int i = 0; i < values.length; i++) {
                System.out.printf("%d. %s%n", i + 1, values[i]);
            }
            System.out.print("Enter option: ");
            String input = SCANNER.nextLine().trim();
            try {
                int idx = Integer.parseInt(input);
                if (idx >= 1 && idx <= values.length) {
                    return values[idx - 1];
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid department choice. Try again.");
        }
    }

    private static ManagerType readManagerTypeFromUser() {
        while (true) {
            System.out.println("Choose a Manager Type:");
            ManagerType[] values = ManagerType.values();
            for (int i = 0; i < values.length; i++) {
                System.out.printf("%d. %s%n", i + 1, values[i]);
            }
            System.out.print("Enter option: ");
            String input = SCANNER.nextLine().trim();
            try {
                int idx = Integer.parseInt(input);
                if (idx >= 1 && idx <= values.length) {
                    return values[idx - 1];
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid manager type choice. Try again.");
        }
    }

    // binary tree

    private static void handleCreateBinaryTree() {
        if (employees.size() < 28) {
            System.out.println("Not enough employee records to build the tree.");
            System.out.println("You currently have " + employees.size() + " records; need at least 28.");
            System.out.println();
            return;
        }

        // Build a simple complete binary tree using level-order insertion
        EmployeeBinaryTree tree = new EmployeeBinaryTree();

        // Insert the first 20 employees. You can change this to employees.size() if desired.
        int count = Math.min(28, employees.size());
        System.out.println("Creating employee hierarchy binary tree with " + count + " records...");
        for (int i = 0; i < count; i++) {
            tree.insert(employees.get(i));
        }

        System.out.println("Level-order traversal of hierarchy:");
        tree.printLevelOrder();
        System.out.println();

        int height = tree.height();
        int nodeCount = tree.countNodes();
        System.out.println("Tree height     : " + height);
        System.out.println("Total node count: " + nodeCount);
        System.out.println();
    }
}
    
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
