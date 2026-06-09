import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BillCalculator Tests")
class BillCalculatorTest {

    private BillCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new BillCalculator();
    }

    @Nested
    @DisplayName("Input Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid amount should be parsed correctly")
        void testValidAmount() throws InvalidAmountException {
            assertEquals(1500.0, calculator.validateAmount("1500"));
            assertEquals(99.99, calculator.validateAmount("  99.99  "));
        }

        @Test
        @DisplayName("Null amount should throw InvalidAmountException")
        void testNullAmount() {
            assertThrows(InvalidAmountException.class, new org.junit.jupiter.api.function.Executable() {
                public void execute() throws Throwable {
                    calculator.validateAmount(null);
                }
            });
        }

        @Test
        @DisplayName("Empty amount should throw InvalidAmountException")
        void testEmptyAmount() {
            assertThrows(InvalidAmountException.class, new org.junit.jupiter.api.function.Executable() {
                public void execute() throws Throwable {
                    calculator.validateAmount("");
                }
            });
        }

        @Test
        @DisplayName("Negative amount should throw InvalidAmountException")
        void testNegativeAmount() {
            assertThrows(InvalidAmountException.class, new org.junit.jupiter.api.function.Executable() {
                public void execute() throws Throwable {
                    calculator.validateAmount("-100");
                }
            });
        }

        @Test
        @DisplayName("Zero amount should throw InvalidAmountException")
        void testZeroAmount() {
            assertThrows(InvalidAmountException.class, new org.junit.jupiter.api.function.Executable() {
                public void execute() throws Throwable {
                    calculator.validateAmount("0");
                }
            });
        }

        @Test
        @DisplayName("Text input should throw InvalidAmountException")
        void testTextAmount() {
            assertThrows(InvalidAmountException.class, new org.junit.jupiter.api.function.Executable() {
                public void execute() throws Throwable {
                    calculator.validateAmount("abc");
                }
            });
        }

        @Test
        @DisplayName("Valid people count should be parsed correctly")
        void testValidPeople() throws ZeroDivisionException, InvalidPeopleCountException {
            assertEquals(4, calculator.validatePeopleCount("4"));
            assertEquals(1, calculator.validatePeopleCount("  1  "));
        }

        @Test
        @DisplayName("Zero people should throw ZeroDivisionException")
        void testZeroPeople() {
            assertThrows(ZeroDivisionException.class, new org.junit.jupiter.api.function.Executable() {
                public void execute() throws Throwable {
                    calculator.validatePeopleCount("0");
                }
            });
        }

        @Test
        @DisplayName("Negative people should throw InvalidPeopleCountException")
        void testNegativePeople() {
            assertThrows(InvalidPeopleCountException.class, new org.junit.jupiter.api.function.Executable() {
                public void execute() throws Throwable {
                    calculator.validatePeopleCount("-3");
                }
            });
        }

        @Test
        @DisplayName("Text input for people should throw InvalidPeopleCountException")
        void testTextPeople() {
            assertThrows(InvalidPeopleCountException.class, new org.junit.jupiter.api.function.Executable() {
                public void execute() throws Throwable {
                    calculator.validatePeopleCount("four");
                }
            });
        }

        @Test
        @DisplayName("Valid tip should be parsed correctly as flat PKR amount")
        void testValidTip() throws InvalidTipException {
            assertEquals(150.0, calculator.validateTip("150"));
        }

        @Test
        @DisplayName("Empty tip should return 0 because tip is optional")
        void testEmptyTip() throws InvalidTipException {
            assertEquals(0.0, calculator.validateTip(""));
            assertEquals(0.0, calculator.validateTip(null));
        }

        @Test
        @DisplayName("Negative tip should throw InvalidTipException")
        void testNegativeTip() {
            assertThrows(InvalidTipException.class, new org.junit.jupiter.api.function.Executable() {
                public void execute() throws Throwable {
                    calculator.validateTip("-50");
                }
            });
        }

        @Test
        @DisplayName("Text tip should throw InvalidTipException")
        void testTextTip() {
            assertThrows(InvalidTipException.class, new org.junit.jupiter.api.function.Executable() {
                public void execute() throws Throwable {
                    calculator.validateTip("ten");
                }
            });
        }
    }

    @Nested
    @DisplayName("Math Calculation Tests")
    class CalculationTests {

        @Test
        @DisplayName("GST should be correctly calculated as 16% of subtotal")
        void testGSTCalculation() {
            assertEquals(160.0, calculator.calculateGST(1000));
            assertEquals(80.0, calculator.calculateGST(500));
            assertEquals(0.0, calculator.calculateGST(0));
        }

        @Test
        @DisplayName("Grand total should be subtotal + GST + flat tip amount")
        void testGrandTotal() {
            assertEquals(1360.0, calculator.calculateGrandTotal(1000, 200));
        }

        @Test
        @DisplayName("Grand total with no tip")
        void testGrandTotalNoTip() {
            assertEquals(1160.0, calculator.calculateGrandTotal(1000, 0));
        }

        @Test
        @DisplayName("Split should divide equally among people")
        void testEqualSplit() {
            assertEquals(250.0, calculator.splitEqually(1000, 4));
            assertEquals(500.0, calculator.splitEqually(1000, 2));
            assertEquals(1000.0, calculator.splitEqually(1000, 1));
        }

        @Test
        @DisplayName("Split should round to 2 decimal places")
        void testSplitRounding() {
            assertEquals(333.33, calculator.splitEqually(1000, 3));
        }

        @Test
        @DisplayName("Subtotal should sum all item prices correctly")
        void testSubtotal() throws EmptyItemListException {
            ArrayList<Double> prices = new ArrayList<Double>();
            prices.add(250.0);
            prices.add(350.0);
            prices.add(400.0);
            assertEquals(1000.0, calculator.calculateSubtotal(prices));
        }

        @Test
        @DisplayName("Empty item list should throw EmptyItemListException")
        void testEmptyItemList() {
            assertThrows(EmptyItemListException.class, new org.junit.jupiter.api.function.Executable() {
                public void execute() throws Throwable {
                    calculator.calculateSubtotal(new ArrayList<Double>());
                }
            });
        }
    }

    @Nested
    @DisplayName("Full Workflow Tests")
    class WorkflowTests {

        @Test
        @DisplayName("Equal split: PKR 5000, 4 people, PKR 500 tip")
        void testEqualSplitFull() throws Exception {
            BillCalculator.SplitResult result = calculator.performEqualSplit("5000", "4", "500");

            assertEquals(5000.0, result.getSubtotal());
            assertEquals(800.0, result.getGstAmount());
            assertEquals(500.0, result.getTipAmount());
            assertEquals(6300.0, result.getGrandTotal());
            assertEquals(4, result.getPeople());
            assertEquals(1575.0, result.getPerPerson());
        }

        @Test
        @DisplayName("Equal split with no tip")
        void testEqualSplitNoTip() throws Exception {
            BillCalculator.SplitResult result = calculator.performEqualSplit("2000", "2", "");

            assertEquals(2000.0, result.getSubtotal());
            assertEquals(320.0, result.getGstAmount());
            assertEquals(0.0, result.getTipAmount());
            assertEquals(2320.0, result.getGrandTotal());
            assertEquals(1160.0, result.getPerPerson());
        }
        @Test
        @DisplayName("Itemized split: 3 items, 2 people (Ali and Sara), PKR 200 tip")
        void testItemizedSplitFull() throws Exception {
            ArrayList<BillCalculator.ItemEntry> items = new ArrayList<BillCalculator.ItemEntry>();
            items.add(new BillCalculator.ItemEntry("Ali", "Biryani", 450));
            items.add(new BillCalculator.ItemEntry("Sara", "Naan", 50));
            items.add(new BillCalculator.ItemEntry("Sara", "Drinks", 500));

            BillCalculator.SplitResult result = calculator.performItemizedSplit(items, "200");

            assertEquals(1000.0, result.getSubtotal());
            assertEquals(160.0, result.getGstAmount());
            assertEquals(200.0, result.getTipAmount());
            assertEquals(1360.0, result.getGrandTotal());
            assertEquals(2, result.getPeople());

            BillCalculator.PersonTotal aliTotal = result.getPersonTotals().get(0);
            assertEquals("Ali", aliTotal.getPersonName());
            assertEquals(450.0, aliTotal.getSubtotal());
            assertEquals(72.0, aliTotal.getGstShare());
            assertEquals(90.0, aliTotal.getTipShare());
            assertEquals(612.0, aliTotal.getGrandTotal());

            BillCalculator.PersonTotal saraTotal = result.getPersonTotals().get(1);
            assertEquals("Sara", saraTotal.getPersonName());
            assertEquals(550.0, saraTotal.getSubtotal());
            assertEquals(88.0, saraTotal.getGstShare());
            assertEquals(110.0, saraTotal.getTipShare());
            assertEquals(748.0, saraTotal.getGrandTotal());
        }

        @Test
        @DisplayName("Itemized split with empty items should throw EmptyItemListException")
        void testItemizedSplitEmptyItems() {
            assertThrows(EmptyItemListException.class, new org.junit.jupiter.api.function.Executable() {
                public void execute() throws Throwable {
                    calculator.performItemizedSplit(
                            new ArrayList<BillCalculator.ItemEntry>(), "100");
                }
            });
        }

        @Test
        @DisplayName("Equal split with zero people should throw ZeroDivisionException")
        void testEqualSplitZeroPeople() {
            assertThrows(ZeroDivisionException.class, new org.junit.jupiter.api.function.Executable() {
                public void execute() throws Throwable {
                    calculator.performEqualSplit("1000", "0", "100");
                }
            });
        }
    }
}
