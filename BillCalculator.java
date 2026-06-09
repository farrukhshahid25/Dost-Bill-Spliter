import java.util.ArrayList;

public class BillCalculator {

    private static final double GST_RATE = 0.16;

    public double validateAmount(String input) throws InvalidAmountException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidAmountException(input == null ? "null" : input);
        }

        try {
            double amount = Double.parseDouble(input.trim());

            if (amount <= 0) {
                throw new InvalidAmountException(input.trim());
            }

            return amount;
        } catch (NumberFormatException e) {
            throw new InvalidAmountException(input.trim());
        }
    }

    public int validatePeopleCount(String input)
            throws ZeroDivisionException, InvalidPeopleCountException {

        if (input == null || input.trim().isEmpty()) {
            throw new InvalidPeopleCountException(input == null ? "null" : input);
        }

        try {
            int count = Integer.parseInt(input.trim());

            if (count == 0) {
                throw new ZeroDivisionException();
            }

            if (count < 0) {
                throw new InvalidPeopleCountException(input.trim());
            }

            return count;
        } catch (NumberFormatException e) {
            throw new InvalidPeopleCountException(input.trim());
        }
    }

    public double validateTip(String input) throws InvalidTipException {
        if (input == null || input.trim().isEmpty()) {
            return 0.0;
        }

        try {
            double tip = Double.parseDouble(input.trim());

            if (tip < 0) {
                throw new InvalidTipException(input.trim());
            }

            return tip;
        } catch (NumberFormatException e) {
            throw new InvalidTipException(input.trim());
        }
    }

    public double calculateGST(double subtotal) {
        double gst = subtotal * GST_RATE;
        return roundToTwo(gst);
    }

    public double calculateGrandTotal(double subtotal, double tipAmount) {
        double gst = calculateGST(subtotal);
        double total = subtotal + gst + tipAmount;
        return roundToTwo(total);
    }

    public double splitEqually(double grandTotal, int people) {
        double perPerson = grandTotal / people;
        return roundToTwo(perPerson);
    }

    public double calculateSubtotal(ArrayList<Double> prices) throws EmptyItemListException {
        if (prices == null || prices.isEmpty()) {
            throw new EmptyItemListException();
        }

        double sum = 0;
        for (int i = 0; i < prices.size(); i++) {
            sum = sum + prices.get(i);
        }
        return roundToTwo(sum);
    }

    public static class PersonTotal {
        private String personName;
        private double subtotal;
        private double gstShare;
        private double tipShare;
        private double grandTotal;

        public PersonTotal(String personName, double subtotal, double gstShare, double tipShare, double grandTotal) {
            this.personName = personName;
            this.subtotal = subtotal;
            this.gstShare = gstShare;
            this.tipShare = tipShare;
            this.grandTotal = grandTotal;
        }

        public String getPersonName() { return personName; }
        public double getSubtotal() { return subtotal; }
        public double getGstShare() { return gstShare; }
        public double getTipShare() { return tipShare; }
        public double getGrandTotal() { return grandTotal; }
    }

    public static class SplitResult {
        private ArrayList<ItemEntry> items;
        private double subtotal;
        private double gstAmount;
        private double tipAmount;
        private double grandTotal;
        private int people;
        private double perPerson;
        private ArrayList<PersonTotal> personTotals;

        public SplitResult(ArrayList<ItemEntry> items, double subtotal, double gstAmount,
                           double tipAmount, double grandTotal, 
                           int people, double perPerson, ArrayList<PersonTotal> personTotals) {
            this.items = items;
            this.subtotal = subtotal;
            this.gstAmount = gstAmount;
            this.tipAmount = tipAmount;
            this.grandTotal = grandTotal;
            this.people = people;
            this.perPerson = perPerson;
            this.personTotals = personTotals;
        }

        public ArrayList<ItemEntry> getItems()  { return items; }
        public double getSubtotal()             { return subtotal; }
        public double getGstAmount()            { return gstAmount; }
        public double getTipAmount()            { return tipAmount; }
        public double getGrandTotal()           { return grandTotal; }
        public int getPeople()                  { return people; }
        public double getPerPerson()            { return perPerson; }
        public ArrayList<PersonTotal> getPersonTotals() { return personTotals; }
    }

    public static class ItemEntry {
        private String personName;
        private String name;
        private double price;

        public ItemEntry(String personName, String name, double price) {
            this.personName = personName;
            this.name = name;
            this.price = price;
        }

        public String getPersonName() { return personName; }
        public String getName()  { return name; }
        public double getPrice() { return price; }
    }

    public SplitResult performEqualSplit(String amountInput, String peopleInput,
                                         String tipInput)
            throws InvalidAmountException, ZeroDivisionException,
                   InvalidPeopleCountException, InvalidTipException {

        double subtotal = validateAmount(amountInput);
        int people = validatePeopleCount(peopleInput);
        double tipAmount = validateTip(tipInput);

        double gstAmount = calculateGST(subtotal);
        double grandTotal = calculateGrandTotal(subtotal, tipAmount);
        double perPerson = splitEqually(grandTotal, people);

        return new SplitResult(null, subtotal, gstAmount, tipAmount, grandTotal, people, perPerson, null);
    }

    public SplitResult performItemizedSplit(ArrayList<ItemEntry> items, String tipInput)
            throws EmptyItemListException, InvalidTipException {

        if (items == null || items.isEmpty()) {
            throw new EmptyItemListException();
        }

        ArrayList<Double> prices = new ArrayList<Double>();
        for (int i = 0; i < items.size(); i++) {
            prices.add(items.get(i).getPrice());
        }

        double subtotal = calculateSubtotal(prices);
        double tipAmount = validateTip(tipInput);

        double gstAmount = calculateGST(subtotal);
        double grandTotal = calculateGrandTotal(subtotal, tipAmount);

        ArrayList<String> uniqueNames = new ArrayList<String>();
        for (int i = 0; i < items.size(); i++) {
            String pName = items.get(i).getPersonName();
            if (!uniqueNames.contains(pName)) {
                uniqueNames.add(pName);
            }
        }

        ArrayList<PersonTotal> personTotals = new ArrayList<PersonTotal>();
        for (int i = 0; i < uniqueNames.size(); i++) {
            String currentName = uniqueNames.get(i);
            double personSubtotal = 0;

            for (int j = 0; j < items.size(); j++) {
                if (items.get(j).getPersonName().equals(currentName)) {
                    personSubtotal += items.get(j).getPrice();
                }
            }

            double ratio = personSubtotal / subtotal;
            double pGst = roundToTwo(gstAmount * ratio);
            double pTip = roundToTwo(tipAmount * ratio);
            double pTotal = roundToTwo(personSubtotal + pGst + pTip);

            personTotals.add(new PersonTotal(currentName, personSubtotal, pGst, pTip, pTotal));
        }

        int people = uniqueNames.size();
        double perPerson = 0;

        return new SplitResult(items, subtotal, gstAmount, tipAmount, grandTotal, people, perPerson, personTotals);
    }

    private double roundToTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
