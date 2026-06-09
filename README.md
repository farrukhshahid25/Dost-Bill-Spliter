# Dost Bill Splitter

Java: 17+ | GUI: Java Swing | Testing: JUnit 5

## Project Description
Dost Bill Splitter is a seamless, two-column desktop application designed to accurately and fairly divide restaurant bills among friends. It provides a clean, modern interface for both equal and itemized splitting, automatically handling 16% GST and flat PKR tip distributions based on individual consumption. 

This project was developed as part of the SCD (Software Construction and Design) semester requirements, focusing strictly on the Single Responsibility Principle (SRP), robust exception handling, clean code practices, and comprehensive unit testing.

## Key Features
- **Equal Splitting:** Quickly divide a total bill equally among a specified number of friends.
- **Itemized Splitting:** Add individual items with prices. The application distributes tips and taxes proportionally based strictly on what each person ordered.
- **Realistic Thermal Receipt:** A dynamic, visually accurate receipt generated on the right-hand panel for immediate visual feedback.
- **Automatic Tax Calculation:** Seamlessly calculates and adds the standard 16% GST to the subtotal.
- **Flat PKR Tip Support:** Allows custom tip amounts in local currency (PKR) rather than confusing percentages.
- **Parchi Generator:** Generates a formatted text receipt and securely copies it directly to the system clipboard for easy sharing via messaging apps.

## Tech Stack
- **Language:** Java 17+
- **GUI Framework:** Java Swing
- **Testing Framework:** JUnit 5
- **Version Control:** Git & GitHub

## SCD Requirements Coverage

This project explicitly implements all mandatory concepts outlined in the SCD Lab requirements:

1. **Event Handling**
   - ActionListeners: Attached to core interaction points including item addition, item removal, calculating the split, and clipboard generation.

2. **Exception Handling**
   - Custom Exceptions: Implemented 5 specific custom exceptions in `BillExceptions.java` to handle specific business logic failures and invalid inputs gracefully.
   - User Feedback: All exceptions (e.g., zero division, negative amounts, empty fields) are caught and displayed to the user via clean `JOptionPane` error dialogs instead of raw terminal stack traces.

3. **Code Refactoring & OOP Principles**
   - Single Responsibility Principle (SRP): The application is cleanly divided into four distinct classes: UI (`BillSplitterGUI`), Business Logic (`BillCalculator`), Output Formatting (`ParchiGenerator`), and Exceptions (`BillExceptions`). 
   - Clean Code: Meaningful variable naming conventions, modular method design, and avoidance of overly complex streams/lambdas to maintain student-friendly readability.

4. **Unit Testing**
   - JUnit 5 Integration: Comprehensive automated test suite featuring 26 distinct tests.
   - Test Coverage:
     - Input Validation: 14 tests ensuring edge cases and bad inputs are caught.
     - Math Calculation: 7 tests verifying exact proportional distribution of flat tips and GST.
     - Full Workflow: 5 tests confirming end-to-end split logic.

5. **Git & GitHub**
   - Maintained a consistent development history with meaningful, descriptive commit messages tracking feature additions, GUI refactoring, and test implementations.

## Setup & Installation Instructions

**Prerequisites:**
- Java Development Kit (JDK 17 or higher)
- An IDE like IntelliJ IDEA or Eclipse
- JUnit 5 library (included in most modern IDEs by default)

**How to Run:**
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/farrukhshahid25/Dost-Bill-Spliter.git](https://github.com/farrukhshahid25/Dost-Bill-Spliter.git)
2. **Open in IDE:**
Open the project folder in your preferred Java IDE. Mark the src folder as your Sources Root.

3. **Add Dependencies:**
Ensure JUnit 5 is added to your project's build path/module path to enable testing.

4. **Run the Application:**
Execute the main method located in src/BillSplitterGUI.java.

**How to Use:**

1. Choose between the "Equal Split" or "Itemized Split" tabs on the left panel.

2. For Itemized, enter the Person Name, Item Name, and Price, then click "+ Add".

3. Configure the Split Settings by adding the Number of People (for Equal Split) and an optional Tip Amount in PKR.

4. Click "Calculate Split" to view the detailed breakdown on the thermal receipt on the right.

5. Click "Copy Parchi Text" to copy the formatted receipt to your clipboard.
