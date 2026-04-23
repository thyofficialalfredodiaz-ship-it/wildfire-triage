public class WildfireTriageTest {
    private static int testsRun = 0;
    private static int testsPassed = 0;

    private static void assertTrue(boolean condition, String message) {
        testsRun++;
        if (!condition) {
            throw new AssertionError(message);
        }
        testsPassed++;
    }

    private static void printHeader(String testName) {
        System.out.println("\n=== " + testName + " ===");
    }

    private static void testSeverePatientTreatedFirst() {
        printHeader("Test 1: SEVERE patient treated first");

        WildfireTriage app = new WildfireTriage();

        app.addPatient(new WildfireTriage.Patient(
                "Alice",
                WildfireTriage.TriageLevel.MINIMAL,
                false,
                false,
                false,
                "minor smoke irritation"
        ));

        app.addPatient(new WildfireTriage.Patient(
                "Bob",
                WildfireTriage.TriageLevel.SEVERE,
                true,
                true,
                false,
                "severe smoke inhalation"
        ));

        System.out.println("Queue before treating:");
        for (WildfireTriage.Patient p : app.snapshotInTreatmentOrder()) {
            System.out.println(p);
        }

        WildfireTriage.Patient next = app.treatNextPatient();
        System.out.println("Patient treated: " + next);

        assertTrue(next != null && next.name.equals("Bob"),
                "SEVERE patient should be treated before MINIMAL patient");

        System.out.println("Result: PASS");
    }

    private static void testArrivalOrderBreaksTie() {
        printHeader("Test 2: Arrival order breaks tie");

        WildfireTriage app = new WildfireTriage();

        app.addPatient(new WildfireTriage.Patient(
                "Carla",
                WildfireTriage.TriageLevel.DELAYED,
                true,
                false,
                false,
                "moderate burns"
        ));

        app.addPatient(new WildfireTriage.Patient(
                "David",
                WildfireTriage.TriageLevel.DELAYED,
                false,
                false,
                true,
                "bleeding arm wound"
        ));

        System.out.println("Queue before treating:");
        for (WildfireTriage.Patient p : app.snapshotInTreatmentOrder()) {
            System.out.println(p);
        }

        WildfireTriage.Patient first = app.treatNextPatient();
        WildfireTriage.Patient second = app.treatNextPatient();

        System.out.println("First treated: " + first);
        System.out.println("Second treated: " + second);

        assertTrue(first != null && first.name.equals("Carla"),
                "Earlier DELAYED patient should be treated first");
        assertTrue(second != null && second.name.equals("David"),
                "Second DELAYED patient should be treated second");

        System.out.println("Result: PASS");
    }

    private static void testPeekDoesNotRemove() {
        printHeader("Test 3: Peek does not remove");

        WildfireTriage app = new WildfireTriage();

        app.addPatient(new WildfireTriage.Patient(
                "Ella",
                WildfireTriage.TriageLevel.SEVERE,
                true,
                false,
                false,
                "trouble breathing"
        ));

        WildfireTriage.Patient peeked = app.peekNextPatient();

        System.out.println("Peeked patient: " + peeked);
        System.out.println("Queue size after peek: " + app.size());

        assertTrue(peeked != null && peeked.name.equals("Ella"),
                "peek should show the next patient");
        assertTrue(app.size() == 1,
                "peek should not remove the patient");

        System.out.println("Result: PASS");
    }

    private static void testTreatRemovesPatient() {
        printHeader("Test 4: Treat removes patient");

        WildfireTriage app = new WildfireTriage();

        app.addPatient(new WildfireTriage.Patient(
                "Finn",
                WildfireTriage.TriageLevel.SEVERE,
                true,
                true,
                false,
                "smoke inhalation and burns"
        ));

        System.out.println("Queue size before treat: " + app.size());

        WildfireTriage.Patient treated = app.treatNextPatient();

        System.out.println("Patient treated: " + treated);
        System.out.println("Queue size after treat: " + app.size());

        assertTrue(treated != null && treated.name.equals("Finn"),
                "treat should return the patient being treated");
        assertTrue(app.size() == 0,
                "treat should remove the patient from the queue");

        System.out.println("Result: PASS");
    }

    private static void testRemovePatientById() {
        printHeader("Test 5: Remove patient by ID");

        WildfireTriage app = new WildfireTriage();

        WildfireTriage.Patient p1 = new WildfireTriage.Patient(
                "Grace",
                WildfireTriage.TriageLevel.DELAYED,
                false,
                true,
                false,
                "burns on arms"
        );

        WildfireTriage.Patient p2 = new WildfireTriage.Patient(
                "Henry",
                WildfireTriage.TriageLevel.MINIMAL,
                false,
                false,
                false,
                "small scrape"
        );

        app.addPatient(p1);
        app.addPatient(p2);

        System.out.println("Queue before removal:");
        for (WildfireTriage.Patient p : app.snapshotInTreatmentOrder()) {
            System.out.println(p);
        }

        boolean removed = app.removePatientById(p1.arrivalNumber);

        System.out.println("Tried removing ID: " + p1.arrivalNumber);
        System.out.println("Removed? " + removed);

        System.out.println("Queue after removal:");
        for (WildfireTriage.Patient p : app.snapshotInTreatmentOrder()) {
            System.out.println(p);
        }

        assertTrue(removed, "removePatientById should return true for an existing patient");
        assertTrue(app.size() == 1, "Queue size should decrease after removal");

        WildfireTriage.Patient next = app.peekNextPatient();
        assertTrue(next != null && next.name.equals("Henry"),
                "Remaining patient should still be in the queue");

        System.out.println("Result: PASS");
    }

    private static void testRemoveMissingPatient() {
        printHeader("Test 6: Remove missing patient");

        WildfireTriage app = new WildfireTriage();

        app.addPatient(new WildfireTriage.Patient(
                "Ivy",
                WildfireTriage.TriageLevel.SEVERE,
                true,
                false,
                true,
                "serious bleeding"
        ));

        System.out.println("Queue before removal attempt:");
        for (WildfireTriage.Patient p : app.snapshotInTreatmentOrder()) {
            System.out.println(p);
        }

        boolean removed = app.removePatientById(99999);

        System.out.println("Tried removing ID: 99999");
        System.out.println("Removed? " + removed);
        System.out.println("Queue size after failed removal: " + app.size());

        assertTrue(!removed, "removePatientById should return false for missing patient");
        assertTrue(app.size() == 1, "Queue should stay unchanged when removal fails");

        System.out.println("Result: PASS");
    }

    private static void testEmptyQueueBehavior() {
        printHeader("Test 7: Empty queue behavior");

        WildfireTriage app = new WildfireTriage();

        System.out.println("Peek on empty queue: " + app.peekNextPatient());
        System.out.println("Treat on empty queue: " + app.treatNextPatient());
        System.out.println("Empty queue size: " + app.size());

        assertTrue(app.peekNextPatient() == null,
                "peek on empty queue should return null");
        assertTrue(app.treatNextPatient() == null,
                "treat on empty queue should return null");
        assertTrue(app.size() == 0,
                "empty queue should have size 0");

        System.out.println("Result: PASS");
    }

    private static void testSnapshotOrder() {
        printHeader("Test 8: Snapshot order");

        WildfireTriage app = new WildfireTriage();

        app.addPatient(new WildfireTriage.Patient(
                "Jack",
                WildfireTriage.TriageLevel.EXPECTANT,
                true,
                true,
                true,
                "critical burns"
        ));

        app.addPatient(new WildfireTriage.Patient(
                "Kira",
                WildfireTriage.TriageLevel.SEVERE,
                true,
                false,
                true,
                "airway issue and bleeding"
        ));

        app.addPatient(new WildfireTriage.Patient(
                "Liam",
                WildfireTriage.TriageLevel.MINIMAL,
                false,
                false,
                false,
                "small burn"
        ));

        java.util.List<WildfireTriage.Patient> list = app.snapshotInTreatmentOrder();

        System.out.println("Snapshot in treatment order:");
        for (WildfireTriage.Patient p : list) {
            System.out.println(p);
        }

        assertTrue(list.size() == 3, "snapshot should contain all patients");
        assertTrue(list.get(0).name.equals("Kira"), "SEVERE should come first");
        assertTrue(list.get(1).name.equals("Liam"), "MINIMAL should come before EXPECTANT");
        assertTrue(list.get(2).name.equals("Jack"), "EXPECTANT should come last");

        System.out.println("Result: PASS");
    }

    public static void main(String[] args) {
        try {
            testSeverePatientTreatedFirst();
            testArrivalOrderBreaksTie();
            testPeekDoesNotRemove();
            testTreatRemovesPatient();
            testRemovePatientById();
            testRemoveMissingPatient();
            testEmptyQueueBehavior();
            testSnapshotOrder();

            System.out.println("\n=== FINAL SUMMARY ===");
            System.out.println("Tests passed: " + testsPassed);
            System.out.println("Assertions checked: " + testsRun);
            System.out.println("All tests completed successfully.");
        } catch (AssertionError e) {
            System.out.println("\nTEST FAILED");
            System.out.println("Reason: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nUNEXPECTED ERROR");
            System.out.println("Reason: " + e.getMessage());
        }
    }
}