class BillSplitterException extends Exception {
    public BillSplitterException(String message) {
        super(message);
    }
}

class InvalidAmountException extends BillSplitterException {
    public InvalidAmountException(String value) {
        super("Invalid amount: \"" + value + "\". Please enter a valid positive number.");
    }
}

class ZeroDivisionException extends BillSplitterException {
    public ZeroDivisionException() {
        super("Number of people cannot be zero. You need at least 1 person.");
    }
}

class InvalidPeopleCountException extends BillSplitterException {
    public InvalidPeopleCountException(String value) {
        super("Invalid number of people: \"" + value + "\". Please enter a positive whole number.");
    }
}

class InvalidTipException extends BillSplitterException {
    public InvalidTipException(String value) {
        super("Invalid tip: \"" + value + "\". Please enter 0 or a positive number.");
    }
}

class EmptyItemListException extends BillSplitterException {
    public EmptyItemListException() {
        super("No items added! Please add at least one item before calculating.");
    }
}
