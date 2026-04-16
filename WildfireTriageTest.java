import java.util.List;

public class WildfireTriageTest {
    private static int testsRun = 0;

    private static void assertTrue(boolean condition, String message) {
        testsRun++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void testSeverePatientTreatedFirst() {
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

        WildfireTriage.Patient next = app.treatNextPatient();
        assertTrue(next != null && next.name.equals("Bob"),
                "SEVERE patient should be treated before MINIMAL patient");
    }

    private static void testArrivalOrderBreaksTie() {
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

        WildfireTriage.Patient first = app.treatNextPatient();
        WildfireTriage.Patient second = app.treatNextPatient();

        assertTrue(first != null && first.name.equals("Carla"),
                "Earlier DELAYED patient should be treated first");
        assertTrue(second != null && second.name.equals("David"),
                "Second DELAYED patient should be treated second");
    }

    private static void testPeekDoesNotRemove() {
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

        assertTrue(peeked != null && peeked.name.equals("Ella"),
                "peek should show the next patient");
        assertTrue(app.size() == 1,
                "peek should not remove the patient");
    }

    private static void testTreatRemovesPatient() {
        WildfireTriage app = new WildfireTriage();

        app.addPatient(new WildfireTriage.Patient(
                "Finn",
                WildfireTriage.TriageLevel.SEVERE,
                true,
                true,
                false,
                "smoke inhalation and burns"
        ));

        WildfireTriage.Patient treated = app.treatNextPatient();

        assertTrue(treated != null && treated.name.equals("Finn"),
                "treat should return the patient being treated");
        assertTrue(app.size() == 0,
                "treat should remove the patient from the queue");
    }

    private static void testRemovePatientById() {
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

        boolean removed = app.removePatientById(p1.arrivalNumber);

        assertTrue(removed, "removePatientById should return true for an existing patient");
        assertTrue(app.size() == 1, "Queue size should decrease after removal");

        WildfireTriage.Patient next = app.peekNextPatient();
        assertTrue(next != null && next.name.equals("Henry"),
                "Remaining patient should still be in the queue");
    }

    private static void testRemoveMissingPatient() {
        WildfireTriage app = new WildfireTriage();

        app.addPatient(new WildfireTriage.Patient(
                "Ivy",
                WildfireTriage.TriageLevel.SEVERE,
                true,
                false,
                true,
                "serious bleeding"
        ));

        boolean removed = app.removePatientById(99999);

        assertTrue(!removed, "removePatientById should return false for missing patient");
        assertTrue(app.size() == 1, "Queue should stay unchanged when removal fails");
    }

    private static void testEmptyQueueBehavior() {
        WildfireTriage app = new WildfireTriage();

        assertTrue(app.peekNextPatient() == null,
                "peek on empty queue should return null");
        assertTrue(app.treatNextPatient() == null,
                "treat on empty queue should return null");
        assertTrue(app.size() == 0,
                "empty queue should have size 0");
    }

    private static void testSnapshotOrder() {
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

        assertTrue(list.size() == 3, "snapshot should contain all patients");
        assertTrue(list.get(0).name.equals("Kira"), "SEVERE should come first");
        assertTrue(list.get(1).name.equals("Liam"), "MINIMAL should come before EXPECTANT");
        assertTrue(list.get(2).name.equals("Jack"), "EXPECTANT should come last");
    }

    public static void main(String[] args) {
        testSeverePatientTreatedFirst();
        testArrivalOrderBreaksTie();
        testPeekDoesNotRemove();
        testTreatRemovesPatient();
        testRemovePatientById();
        testRemoveMissingPatient();
        testEmptyQueueBehavior();
        testSnapshotOrder();
        
        System.out.println("All tests passed. Tests run: " + testsRun);
    }
}