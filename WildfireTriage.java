import java.util.*;

//Charles Cautibar
//Alfredo Diaz

public class WildfireTriage {
    enum TriageLevel {
        SEVERE(1),
        DELAYED(2),
        MINIMAL(3),
        EXPECTANT(4);

        private final int priority;

        TriageLevel(int priority) {
             this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }

        public static TriageLevel fromString(String text) {
            return TriageLevel.valueOf(text.trim().toUpperCase(Locale.ROOT));
        }
    }

    static class Patient {
        private static long nextArrivalNumber = 1;

        final String name;
        final TriageLevel level;
        final boolean smokeInhalation;
        final boolean severeBurns;
        final boolean majorBleeding;
        final String notes;
        final long arrivalNumber;

        Patient(String name, TriageLevel level, boolean smokeInhalation,
                boolean severeBurns, boolean majorBleeding, String notes) {
            this.name = name;
            this.level = level;
            this.smokeInhalation = smokeInhalation;
            this.severeBurns = severeBurns;
            this.majorBleeding = majorBleeding;
            this.notes = notes;
            this.arrivalNumber = nextArrivalNumber++;
        }

        @Override
        public String toString() {
            return String.format(
                    "#%d %s [%s] smokeInhalation=%s severeBurns=%s majorBleeding=%s notes=%s",
                    arrivalNumber, name, level, smokeInhalation, severeBurns, majorBleeding, notes
            );
        }
    }

    private final PriorityQueue<Patient> queue = new PriorityQueue<>(
            Comparator.comparingInt((Patient p) -> p.level.getPriority())
                    .thenComparingLong(p -> p.arrivalNumber)
    );

    public void addPatient(Patient patient) {
        queue.add(patient);
    }

    public Patient peekNextPatient() {
        return queue.peek();
    }

    public Patient treatNextPatient() {
        return queue.poll();
    }

    public boolean removePatientById(long arrivalNumber) {
        Iterator<Patient> iterator = queue.iterator();
        while (iterator.hasNext()) {
            Patient patient = iterator.next();
            if (patient.arrivalNumber == arrivalNumber) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public int size() {
        return queue.size();
    }

    public List<Patient> snapshotInTreatmentOrder() {
        List<Patient> patients = new ArrayList<>(queue);
        patients.sort(
                Comparator.comparingInt((Patient p) -> p.level.getPriority())
                        .thenComparingLong(p -> p.arrivalNumber)
        );
        return patients;
    }

    private static boolean parseYesNo(String value) {
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals("y") || normalized.equals("yes") || normalized.equals("true")) {
            return true;
        }
        if (normalized.equals("n") || normalized.equals("no") || normalized.equals("false")) {
            return false;
        }
        throw new IllegalArgumentException("Expected yes/no value but got: " + value);
    }

    private static void printHelp() {
        System.out.println("Wildfire Field Hospital Triage Commands:");
        System.out.println("  add <name> <level> <smokeInhalation yes/no> <severeBurns yes/no> <majorBleeding yes/no> [notes...]");
        System.out.println("  remove <arrivalNumber>");
        System.out.println("  next");
        System.out.println("  treat");
        System.out.println("  list");
        System.out.println("  count");
        System.out.println("  help");
        System.out.println("  quit");
        System.out.println();
        System.out.println("Levels: SEVERE, DELAYED, MINIMAL, EXPECTANT");
        System.out.println("Use the number shown before the patient's name (for example, #3) as the arrivalNumber.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        WildfireTriage app = new WildfireTriage();

        System.out.println("Wildfire Field Hospital Triage System");
        printHelp();

        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }

            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+");
            String command = parts[0].toLowerCase(Locale.ROOT);

            try {
                switch (command) {
                    case "add" -> {
                        if (parts.length < 6) {
                            throw new IllegalArgumentException(
                                    "Usage: add <name> <level> <smokeInhalation> <severeBurns> <majorBleeding> [notes...]"
                            );
                        }

                        String name = parts[1];
                        TriageLevel level = TriageLevel.fromString(parts[2]);
                        boolean smokeInhalation = parseYesNo(parts[3]);
                        boolean severeBurns = parseYesNo(parts[4]);
                        boolean majorBleeding = parseYesNo(parts[5]);
                        String notes = parts.length > 6
                                ? String.join(" ", Arrays.copyOfRange(parts, 6, parts.length))
                                : "-";

                        Patient patient = new Patient(
                                name, level, smokeInhalation, severeBurns, majorBleeding, notes
                        );
                        app.addPatient(patient);
                        System.out.println("Added: " + patient);
                    }

                    case "remove" -> {
                        if (parts.length < 2) {
                            throw new IllegalArgumentException("Usage: remove <arrivalNumber>");
                        }

                        long id;
                        try {
                            id = Long.parseLong(parts[1]);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("arrivalNumber must be a number.");
                        }

                        boolean removed = app.removePatientById(id);
                        if (removed) {
                            System.out.println("Removed patient with ID: " + id);
                        } else {
                            System.out.println("No patient found with ID: " + id);
                        }
                    }

                    case "next" -> {
                        Patient next = app.peekNextPatient();
                        System.out.println(next == null ? "No patients waiting." : "Next patient: " + next);
                    }

                    case "treat" -> {
                        Patient treated = app.treatNextPatient();
                        System.out.println(treated == null ? "No patients waiting." : "Treating: " + treated);
                    }

                    case "list" -> {
                        List<Patient> patients = app.snapshotInTreatmentOrder();
                        if (patients.isEmpty()) {
                            System.out.println("No patients waiting.");
                        } else {
                            for (Patient patient : patients) {
                                System.out.println(patient);
                            }
                        }
                    }

                    case "count" -> System.out.println("Patients waiting: " + app.size());
                    case "help" -> printHelp();

                    case "quit", "exit" -> {
                        System.out.println("Goodbye.");
                        return;
                    }

                    default -> System.out.println("Unknown command. Type 'help' for a list of commands.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }
}